package net.frozenorb.foxtrot.antixray;

import com.comphenix.protocol.ProtocolLibrary;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.AntiXray;
import org.spigotmc.SpigotConfig;
import org.spigotmc.SpigotWorldConfig;

public final class AntiXrayHandler implements Listener {
   private static final List<Integer> ORES = Collections.unmodifiableList(Arrays.asList(14, 15, 16, 21, 56, 73, 129, 153));
   private static final List<Integer> COVERED_BLOCKS = Collections.unmodifiableList(Arrays.asList(1, 3, 13, 14, 15, 16, 21, 56, 73, 74, 87, 121, 129, 153));
   private final JavaPlugin plugin;
   private final Map<SpigotWorldConfig, AntiXrayHandler.OriginalSettings> originals = new IdentityHashMap<>();
   private AntiXrayChunkAdapter packets;
   private boolean started;
   private boolean originalCacheChunkMaps;

   public AntiXrayHandler(JavaPlugin plugin) {
      this.plugin = plugin;
   }

   public void start() {
      if (!this.started && this.plugin.getConfig().getBoolean("anti-xray.enabled", true)) {
         this.packets = new AntiXrayChunkAdapter(this.plugin);
         ProtocolLibrary.getProtocolManager().addPacketListener(this.packets);
         this.originalCacheChunkMaps = SpigotConfig.cacheChunkMaps;
         SpigotConfig.cacheChunkMaps = false;
         this.started = true;
         this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);

         for (World world : this.plugin.getServer().getWorlds()) {
            this.protect(world);
         }

         this.plugin.getLogger().info("[AntiXray] Enabled mode 2 for all worlds (1.7/1.8); buried ores are obfuscated.");
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST)
   public void onWorldInit(WorldInitEvent event) {
      this.protect(event.getWorld());
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onWorldUnload(WorldUnloadEvent event) {
      this.restore(((CraftWorld)event.getWorld()).getHandle().spigotConfig);
   }

   private void protect(World world) {
      SpigotWorldConfig config = ((CraftWorld)world).getHandle().spigotConfig;
      if (!this.originals.containsKey(config)) {
         this.originals.put(config, new AntiXrayHandler.OriginalSettings(config));
      }

      configure(config);
   }

   static void configure(SpigotWorldConfig config) {
      config.engineMode = 2;
      config.hiddenBlocks = ORES;
      config.replaceBlocks = COVERED_BLOCKS;
      config.antiXrayInstance = new AntiXray(config);
      config.antiXray = true;
   }

   public void close() {
      if (this.started) {
         HandlerList.unregisterAll(this);
         ProtocolLibrary.getProtocolManager().removePacketListener(this.packets);

         for (Entry<SpigotWorldConfig, AntiXrayHandler.OriginalSettings> entry : this.originals.entrySet()) {
            entry.getValue().restore(entry.getKey());
         }

         this.originals.clear();
         SpigotConfig.cacheChunkMaps = this.originalCacheChunkMaps;
         this.packets = null;
         this.started = false;
      }
   }

   private void restore(SpigotWorldConfig config) {
      AntiXrayHandler.OriginalSettings original = this.originals.remove(config);
      if (original != null) {
         original.restore(config);
      }
   }

   private static final class OriginalSettings {
      private final boolean enabled;
      private final int mode;
      private final List<Integer> hidden;
      private final List<Integer> replaced;
      private final AntiXray engine;

      private OriginalSettings(SpigotWorldConfig config) {
         this.enabled = config.antiXray;
         this.mode = config.engineMode;
         this.hidden = config.hiddenBlocks;
         this.replaced = config.replaceBlocks;
         this.engine = config.antiXrayInstance;
      }

      private void restore(SpigotWorldConfig config) {
         config.antiXray = this.enabled;
         config.engineMode = this.mode;
         config.hiddenBlocks = this.hidden;
         config.replaceBlocks = this.replaced;
         config.antiXrayInstance = this.engine;
      }
   }
}
