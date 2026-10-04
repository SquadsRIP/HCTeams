package net.frozenorb.foxtrot.listener;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import net.frozenorb.foxtrot.Foxtrot;
import net.minecraft.server.v1_7_R4.AxisAlignedBB;
import net.minecraft.server.v1_7_R4.Entity;
import net.minecraft.server.v1_7_R4.EntityInsentient;
import net.minecraft.server.v1_7_R4.EntityTypes;
import net.minecraft.server.v1_7_R4.EnumDifficulty;
import net.minecraft.server.v1_7_R4.MobSpawnerAbstract;
import net.minecraft.server.v1_7_R4.TileEntity;
import net.minecraft.server.v1_7_R4.TileEntityMobSpawner;
import net.minecraft.server.v1_7_R4.WorldServer;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class SpawnerLimiterListener implements Listener {
   private static Limits defaultLimits;
   private static final Map<String, Limits> typeLimits = new HashMap<>();
   private static int maxPlayerRange;
   private static boolean ignoreLight;

   public SpawnerLimiterListener() {
      FileConfiguration config = Foxtrot.getInstance().getConfig();
      defaultLimits = new Limits(config.getConfigurationSection("spawners"), null);
      maxPlayerRange = defaultLimits.playerRange;
      typeLimits.clear();
      // The live config only gets the jar's defaults when it is first created, so an older config
      // without spawners.types falls back to the defaults bundled in the jar.
      Object liveTypes = config.get("spawners.types", null);
      ConfigurationSection types = liveTypes instanceof ConfigurationSection
         ? (ConfigurationSection)liveTypes
         : (config.getDefaults() == null ? null : config.getDefaults().getConfigurationSection("spawners.types"));
      if (types != null) {
         // Keyed by the spawner's mob id, e.g. LavaSlime (magma cube), Creeper, Blaze, Zombie.
         for (String mobName : types.getKeys(false)) {
            Limits limits = new Limits(types.getConfigurationSection(mobName), defaultLimits);
            typeLimits.put(mobName.toLowerCase(), limits);
            maxPlayerRange = Math.max(maxPlayerRange, limits.playerRange);
         }
      }

      ignoreLight = config.getBoolean("spawners.ignoreLight", true);
      (new BukkitRunnable() {
         @Override
         public void run() {
            for (World world : Foxtrot.getInstance().getServer().getWorlds()) {
               for (Chunk chunk : world.getLoadedChunks()) {
                  for (BlockState state : chunk.getTileEntities()) {
                     if (state instanceof CreatureSpawner) {
                        SpawnerLimiterListener.applySpawnerLimits((CreatureSpawner)state);
                     }
                  }
               }
            }
         }
      }).runTaskTimer(Foxtrot.getInstance(), 200L, 1200L);
      if (ignoreLight) {
         (new BukkitRunnable() {
            @Override
            public void run() {
               SpawnerLimiterListener.assistSpawnersNearPlayers();
            }
         }).runTaskTimer(Foxtrot.getInstance(), 100L, 20L);
      }
   }

   private static void applySpawnerLimits(CreatureSpawner state) {
      TileEntity tile = ((CraftWorld)state.getWorld()).getTileEntityAt(state.getX(), state.getY(), state.getZ());
      if (tile instanceof TileEntityMobSpawner) {
         MobSpawnerAbstract spawner = ((TileEntityMobSpawner)tile).getSpawner();
         String mobName = spawner.getMobName();
         Limits limits = mobName == null ? null : typeLimits.get(mobName.toLowerCase());
         if (limits == null) {
            limits = defaultLimits;
         }

         if (spawner.minSpawnDelay != limits.minDelay
            || spawner.maxSpawnDelay != limits.maxDelay
            || spawner.spawnCount != limits.spawnCount
            || spawner.maxNearbyEntities != limits.maxNearby
            || spawner.requiredPlayerRange != limits.playerRange) {
            spawner.minSpawnDelay = limits.minDelay;
            spawner.maxSpawnDelay = limits.maxDelay;
            spawner.spawnCount = limits.spawnCount;
            spawner.maxNearbyEntities = limits.maxNearby;
            spawner.requiredPlayerRange = limits.playerRange;
            if (spawner.spawnDelay > limits.maxDelay) {
               spawner.spawnDelay = limits.maxDelay;
            }

            tile.update();
         }
      }
   }

   private static void assistSpawnersNearPlayers() {
      Set<TileEntityMobSpawner> spawners = Collections.newSetFromMap(new IdentityHashMap<>());
      int chunkRadius = (maxPlayerRange >> 4) + 1;

      for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
         World world = player.getWorld();
         WorldServer handle = ((CraftWorld)world).getHandle();
         int chunkX = player.getLocation().getBlockX() >> 4;
         int chunkZ = player.getLocation().getBlockZ() >> 4;

         for (int x = chunkX - chunkRadius; x <= chunkX + chunkRadius; x++) {
            for (int z = chunkZ - chunkRadius; z <= chunkZ + chunkRadius; z++) {
               if (world.isChunkLoaded(x, z)) {
                  for (TileEntity tile : handle.getChunkAt(x, z).tileEntities.values()) {
                     if (tile instanceof TileEntityMobSpawner) {
                        spawners.add((TileEntityMobSpawner)tile);
                     }
                  }
               }
            }
         }
      }

      for (TileEntityMobSpawner spawner : spawners) {
         assistSpawner(spawner.getSpawner());
      }
   }

   // Vanilla only spawns monsters in the dark, so a spawner in a lit room (or next to glowstone)
   // sits at delay 0 retrying every tick. Spawn its wave with the same placement and collision
   // checks, minus the light check, then restart its delay.
   private static void assistSpawner(MobSpawnerAbstract spawner) {
      if (spawner.spawnDelay <= 0 && spawner.f()) {
         net.minecraft.server.v1_7_R4.World world = spawner.a();
         if (world.difficulty != EnumDifficulty.PEACEFUL) {
            Entity probe = EntityTypes.createEntityByName(spawner.getMobName(), world);
            if (probe instanceof EntityInsentient) {
               int x = spawner.b();
               int y = spawner.c();
               int z = spawner.d();
               AxisAlignedBB area = AxisAlignedBB.a(x, y, z, x + 1, y + 1, z + 1).grow(spawner.spawnRange * 2, 4.0, spawner.spawnRange * 2);
               int nearby = world.a(probe.getClass(), area).size();
               boolean spawned = false;

               for (int i = 0; i < spawner.spawnCount && nearby < spawner.maxNearbyEntities; i++) {
                  Entity entity = i == 0 ? probe : EntityTypes.createEntityByName(spawner.getMobName(), world);
                  if (entity == null) {
                     break;
                  }

                  double spawnX = x + (world.random.nextDouble() - world.random.nextDouble()) * spawner.spawnRange;
                  double spawnY = y + world.random.nextInt(3) - 1;
                  double spawnZ = z + (world.random.nextDouble() - world.random.nextDouble()) * spawner.spawnRange;
                  entity.setPositionRotation(spawnX, spawnY, spawnZ, world.random.nextFloat() * 360.0F, 0.0F);
                  if (world.b(entity.boundingBox) && world.getCubes(entity, entity.boundingBox).isEmpty() && !world.containsLiquid(entity.boundingBox)) {
                     spawner.a(entity);
                     world.triggerEffect(2004, x, y, z, 0);
                     spawned = true;
                     nearby++;
                  }
               }

               if (spawned) {
                  int minDelay = spawner.minSpawnDelay;
                  spawner.spawnDelay = minDelay + world.random.nextInt(Math.max(0, spawner.maxSpawnDelay - minDelay) + 1);
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onSpawnerPlace(BlockPlaceEvent event) {
      Block block = event.getBlock();
      if (block.getType() == Material.MOB_SPAWNER) {
         BlockState state = block.getState();
         if (state instanceof CreatureSpawner) {
            applySpawnerLimits((CreatureSpawner)state);
         }
      }
   }

   private static final class Limits {
      private final int minDelay;
      private final int maxDelay;
      private final int spawnCount;
      private final int maxNearby;
      private final int playerRange;

      // Missing keys fall back to the parent (the global spawners.* values), then to the built-in defaults.
      private Limits(ConfigurationSection section, Limits parent) {
         this.minDelay = Math.max(1, get(section, "minDelay", parent == null ? 200 : parent.minDelay));
         this.maxDelay = Math.max(this.minDelay, get(section, "maxDelay", parent == null ? 600 : parent.maxDelay));
         this.spawnCount = Math.max(1, get(section, "spawnCount", parent == null ? 4 : parent.spawnCount));
         this.maxNearby = Math.max(1, get(section, "maxNearby", parent == null ? 8 : parent.maxNearby));
         this.playerRange = Math.max(1, get(section, "playerRange", parent == null ? 16 : parent.playerRange));
      }

      private static int get(ConfigurationSection section, String key, int def) {
         return section == null ? def : section.getInt(key, def);
      }
   }
}
