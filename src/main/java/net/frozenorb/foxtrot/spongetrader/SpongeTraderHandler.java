package net.frozenorb.foxtrot.spongetrader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import net.frozenorb.foxtrot.spongetrader.legacy.LegacySpongeVillager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class SpongeTraderHandler implements Listener, TabExecutor {
   private static final String PERMISSION = "hcteams.command.spongetrader";
   private final JavaPlugin plugin;
   private final Path file;
   private final ExecutorService storage;
   private final BukkitTask maintenance;
   private YamlConfiguration settings;
   private TraderPosition position;
   private LegacySpongeVillager villager;
   private boolean ready;
   private boolean saving;
   private volatile boolean stopped;

   public SpongeTraderHandler(JavaPlugin plugin) {
      this.plugin = plugin;
      this.file = plugin.getDataFolder().toPath().resolve("sponge-trader.yml");
      this.storage = Executors.newSingleThreadExecutor(task -> {
         Thread thread = new Thread(task, "HCTeams-SpongeTrader-Storage");
         thread.setDaemon(true);
         return thread;
      });
      plugin.getCommand("spongetrader").setExecutor(this);
      plugin.getCommand("spongetrader").setTabCompleter(this);
      plugin.getServer().getPluginManager().registerEvents(this, plugin);
      this.maintenance = plugin.getServer().getScheduler().runTaskTimer(plugin, this::ensureSpawned, 20L, 20L);
      this.storage.execute(() -> {
         try {
            YamlConfiguration loaded = new YamlConfiguration();
            InputStream input = plugin.getResource("sponge-trader.yml");

            try {
               if (input == null) {
                  throw new IllegalStateException("Missing sponge-trader.yml resource");
               }

               loaded.load(new InputStreamReader(input, StandardCharsets.UTF_8));
            } catch (Throwable t$) {
               if (input != null) {
                  try {
                     input.close();
                  } catch (Throwable x2) {
                     t$.addSuppressed(x2);
                  }
               }

               throw t$;
            }

            if (input != null) {
               input.close();
            }

            if (Files.exists(this.file)) {
               YamlConfiguration saved = new YamlConfiguration();
               saved.load(this.file.toFile());
               loaded.set("location", null);

               for (String key : saved.getKeys(true)) {
                  if (!saved.isConfigurationSection(key)) {
                     loaded.set(key, saved.get(key));
                  }
               }
            } else {
               saveAtomically(this.file, loaded.saveToString());
            }

            if (loaded.contains("location") && !loaded.isConfigurationSection("location")) {
               throw new IllegalArgumentException("Trader location must be a YAML section");
            }

            TraderPosition savedPosition = TraderPosition.read(loaded.getConfigurationSection("location"));
            this.onServerThread(() -> {
               try {
                  LegacySpongeVillager.registerType();
                  this.settings = loaded;
                  this.position = savedPosition;
                  this.ready = true;
                  this.ensureSpawned();
               } catch (ReflectiveOperationException | RuntimeException failurex) {
                  plugin.getLogger().log(Level.SEVERE, "Sponge trader requires the supplied vSpigot v1_7_R4 server", failurex);
               }
            });
         } catch (Exception failure) {
            plugin.getLogger().log(Level.SEVERE, "Could not load sponge-trader.yml; trader commands remain unavailable", failure);
         }
      });
   }

   private void onServerThread(Runnable task) {
      if (!this.stopped) {
         try {
            this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
               if (!this.stopped) {
                  task.run();
               }
            });
         } catch (IllegalPluginAccessException var3) {
         }
      }
   }

   @Override
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!sender.hasPermission("hcteams.command.spongetrader")) {
         sender.sendMessage(ChatColor.RED + "You do not have permission to manage the sponge trader.");
         return true;
      }

      if (this.ready && !this.stopped) {
         if (args.length == 1 && (args[0].equalsIgnoreCase("spawn") || args[0].equalsIgnoreCase("despawn"))) {
            if (this.saving) {
               this.tell(sender, "saving");
               return true;
            }

            if (args[0].equalsIgnoreCase("spawn")) {
               if (!(sender instanceof Player)) {
                  this.tell(sender, "player-only");
               } else if (this.position != null) {
                  this.tell(sender, "already-spawned");
               } else {
                  Location location = ((Player)sender).getLocation();

                  try {
                     this.changePosition(sender, new TraderPosition(location));
                  } catch (IllegalArgumentException invalid) {
                     this.tell(sender, "invalid-location");
                  }
               }
            } else if (this.position == null) {
               this.tell(sender, "not-spawned");
            } else {
               this.changePosition(sender, null);
            }

            return true;
         } else {
            this.tell(sender, "usage");
            return true;
         }
      } else {
         sender.sendMessage(ChatColor.RED + "The sponge trader is loading or unavailable. Check the server log if this persists.");
         return true;
      }
   }

   private void changePosition(CommandSender sender, TraderPosition next) {
      YamlConfiguration snapshot = new YamlConfiguration();

      for (String key : this.settings.getKeys(true)) {
         if (!this.settings.isConfigurationSection(key)) {
            snapshot.set(key, this.settings.get(key));
         }
      }

      snapshot.set("location", null);
      if (next != null) {
         next.write(snapshot.createSection("location"));
      }

      String yaml = snapshot.saveToString();
      this.saving = true;
      this.storage.execute(() -> {
         try {
            saveAtomically(this.file, yaml);
            this.onServerThread(() -> {
               this.removeEntity();
               this.settings = snapshot;
               this.position = next;
               this.saving = false;
               this.ensureSpawned();
               this.tell(sender, next == null ? "despawned" : (this.villager == null ? "spawn-blocked" : "spawned"));
               this.plugin.getLogger().info(sender.getName() + (next == null ? " despawned" : " configured") + " the sponge trader.");
            });
         } catch (Exception failure) {
            this.plugin.getLogger().log(Level.SEVERE, "Could not save sponge trader location", failure);
            this.onServerThread(() -> {
               this.saving = false;
               this.tell(sender, "save-failed");
            });
         }
      });
   }

   static void saveAtomically(Path file, String yaml) throws IOException {
      Files.createDirectories(file.getParent());
      Path temporary = Files.createTempFile(file.getParent(), "sponge-trader-", ".tmp");

      try {
         Files.write(temporary, yaml.getBytes(StandardCharsets.UTF_8));

         try {
            Files.move(temporary, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
         } catch (AtomicMoveNotSupportedException unsupported) {
            Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
         }
      } finally {
         Files.deleteIfExists(temporary);
      }
   }

   private void ensureSpawned() {
      if (this.ready && !this.stopped && this.position != null) {
         if (this.villager == null || !this.villager.getBukkitEntity().isValid()) {
            if (this.villager != null) {
               this.removeEntity();
            }

            Location location = this.position.resolve();
            if (location != null && location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
               this.villager = LegacySpongeVillager.spawn(location, color(this.settings.getString("display-name", "&e&lSponge Trader")));
            }
         }
      }
   }

   private void removeEntity() {
      if (this.villager != null) {
         this.villager.removeTrader();
      }

      this.villager = null;
   }

   private boolean owns(Entity entity) {
      return this.villager != null && entity.getUniqueId().equals(this.villager.getUniqueID());
   }

   private void tell(CommandSender sender, String key) {
      sender.sendMessage(color(this.settings.getString("messages." + key, "&cMissing sponge trader message: " + key)));
   }

   private static String color(String text) {
      return ChatColor.translateAlternateColorCodes('&', text);
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
      if (sender.hasPermission("hcteams.command.spongetrader") && args.length == 1) {
         List<String> matches = new ArrayList<>();

         for (String action : Arrays.asList("spawn", "despawn")) {
            if (action.regionMatches(true, 0, args[0], 0, args[0].length())) {
               matches.add(action);
            }
         }

         return matches;
      } else {
         return Collections.emptyList();
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST)
   public void onDamage(EntityDamageEvent event) {
      if (this.owns(event.getEntity())) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST)
   public void onCombust(EntityCombustEvent event) {
      if (this.owns(event.getEntity())) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST)
   public void onTarget(EntityTargetEvent event) {
      if (this.owns(event.getEntity()) || event.getTarget() != null && this.owns(event.getTarget())) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST)
   public void onPortal(EntityPortalEvent event) {
      if (this.owns(event.getEntity())) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST)
   public void onVehicle(VehicleEnterEvent event) {
      if (this.owns(event.getEntered())) {
         event.setCancelled(true);
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void onInteract(PlayerInteractEntityEvent event) {
      if (this.owns(event.getRightClicked()) && this.villager.cc()) {
         this.tell(event.getPlayer(), "busy");
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onChunkLoad(ChunkLoadEvent event) {
      if (this.position != null && this.position.matches(event.getWorld(), event.getChunk().getX(), event.getChunk().getZ())) {
         this.onServerThread(this::ensureSpawned);
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onWorldLoad(WorldLoadEvent event) {
      this.onServerThread(this::ensureSpawned);
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onWorldUnload(WorldUnloadEvent event) {
      if (this.villager != null && this.villager.getBukkitEntity().getWorld().equals(event.getWorld())) {
         this.removeEntity();
      }
   }

   @EventHandler
   public void onDisable(PluginDisableEvent event) {
      if (event.getPlugin() == this.plugin && !this.stopped) {
         this.stopped = true;
         this.maintenance.cancel();
         this.removeEntity();
         LegacySpongeVillager.unregisterType();
         this.storage.shutdown();

         try {
            if (!this.storage.awaitTermination(5L, TimeUnit.SECONDS)) {
               this.plugin.getLogger().warning("Sponge trader storage is still finishing a write.");
            }
         } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
         }
      }
   }
}
