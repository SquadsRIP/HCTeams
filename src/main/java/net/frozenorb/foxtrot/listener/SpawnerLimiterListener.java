package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.minecraft.server.v1_7_R4.MobSpawnerAbstract;
import net.minecraft.server.v1_7_R4.TileEntity;
import net.minecraft.server.v1_7_R4.TileEntityMobSpawner;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class SpawnerLimiterListener implements Listener {
   public static final int MIN_SPAWN_DELAY = 400;
   public static final int MAX_SPAWN_DELAY = 1200;
   public static final int SPAWN_COUNT = 1;
   public static final int MAX_NEARBY = 4;
   public static final int PLAYER_RANGE = 16;

   public SpawnerLimiterListener() {
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
   }

   private static void applySpawnerLimits(CreatureSpawner state) {
      TileEntity tile = ((CraftWorld)state.getWorld()).getTileEntityAt(state.getX(), state.getY(), state.getZ());
      if (tile instanceof TileEntityMobSpawner) {
         MobSpawnerAbstract spawner = ((TileEntityMobSpawner)tile).getSpawner();
         if (spawner.minSpawnDelay != 400
            || spawner.maxSpawnDelay != 1200
            || spawner.spawnCount != 1
            || spawner.maxNearbyEntities != 4
            || spawner.requiredPlayerRange != 16) {
            spawner.minSpawnDelay = 400;
            spawner.maxSpawnDelay = 1200;
            spawner.spawnCount = 1;
            spawner.maxNearbyEntities = 4;
            spawner.requiredPlayerRange = 16;
            tile.update();
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
}
