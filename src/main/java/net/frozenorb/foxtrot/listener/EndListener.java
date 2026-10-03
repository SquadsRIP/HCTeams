package net.frozenorb.foxtrot.listener;

import cc.fyre.proton.Proton;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.listener.event.TimerEndEvent;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.PortalType;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.World.Environment;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

public class EndListener implements Listener {
   public static boolean endActive = true;
   private static Location endReturn;
   public static EnderDragon ENDER_DRAGON;
   private Map<String, Long> msgCooldown = new HashMap<>();

   @EventHandler
   public void onEntityDeath(EntityDeathEvent event) {
      if (event.getEntity() instanceof EnderDragon) {
         Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getEntity().getKiller());
         String teamName = ChatColor.GOLD + "[" + ChatColor.YELLOW + "-" + ChatColor.GOLD + "]";
         ENDER_DRAGON = null;
         if (team != null) {
            team.setAddedGems(team.getAddedGems() + 100);
            team.setAddedPoints(team.getAddedPoints() + 50);
            teamName = ChatColor.GOLD + "[" + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + "]";
         }

         for (int i = 0; i < 6; i++) {
            Foxtrot.getInstance().getServer().broadcastMessage("");
         }

         Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.BLACK + "████████");
         Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.BLACK + "████████");
         Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.BLACK + "████████" + ChatColor.GOLD + " [Enderdragon]");
         Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.BLACK + "████████" + ChatColor.YELLOW + " killed by");
         Foxtrot.getInstance()
            .getServer()
            .broadcastMessage(
               ChatColor.LIGHT_PURPLE
                  + "█"
                  + ChatColor.DARK_PURPLE
                  + "█"
                  + ChatColor.LIGHT_PURPLE
                  + "█"
                  + ChatColor.BLACK
                  + "██"
                  + ChatColor.LIGHT_PURPLE
                  + "█"
                  + ChatColor.DARK_PURPLE
                  + "█"
                  + ChatColor.LIGHT_PURPLE
                  + "█ "
                  + teamName
            );
         Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.BLACK + "████████ " + event.getEntity().getKiller().getDisplayName());
         Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.BLACK + "████████");
         Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.BLACK + "████████");
         ItemStack dragonEgg = new ItemStack(Material.DRAGON_EGG);
         ItemMeta itemMeta = dragonEgg.getItemMeta();
         DateFormat sdf = new SimpleDateFormat("M/d HH:mm:ss");
         itemMeta.setLore(
            Arrays.asList(
               ChatColor.DARK_PURPLE.toString()
                  + ChatColor.BOLD
                  + "Enderdragon "
                  + ChatColor.WHITE
                  + "slain by "
                  + ChatColor.YELLOW
                  + event.getEntity().getKiller().getName(),
               ChatColor.WHITE + sdf.format(new Date()).replace(" AM", "").replace(" PM", "")
            )
         );
         dragonEgg.setItemMeta(itemMeta);
         Player killer = event.getEntity().getKiller();
         Server server = Foxtrot.getInstance().getServer();
         server.dispatchCommand(server.getConsoleSender(), "cr givekey " + killer.getName() + " Perk 5");
         event.getEntity().getKiller().getInventory().addItem(dragonEgg);
         if (!event.getEntity().getKiller().getInventory().contains(Material.DRAGON_EGG)) {
            event.getDrops().add(dragonEgg);
         }
      }
   }

   @EventHandler
   public void onEntityCreatePortal(EntityCreatePortalEvent event) {
      if (event.getEntity() instanceof Item && event.getPortalType() == PortalType.ENDER) {
         event.getBlocks().clear();
      }
   }

   @EventHandler
   public void onEntityDamage(EntityDamageEvent event) {
      if (event.getEntity() instanceof EnderDragon && event.getEntity().getWorld().getEnvironment() == Environment.THE_END) {
         ((EnderDragon)event.getEntity())
            .setCustomName(
               "Ender Dragon "
                  + ChatColor.YELLOW
                  + ChatColor.BOLD
                  + Math.round(((EnderDragon)event.getEntity()).getHealth() / ((EnderDragon)event.getEntity()).getMaxHealth() * 100.0)
                  + "% Health"
            );
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onTimerEnd(TimerEndEvent event) {
      if (event.getDisplayName().contains("dragon")) {
         World world = Foxtrot.getInstance().getServer().getWorld("world_the_end");
         EnderDragon enderDragon = (EnderDragon)world.spawnEntity(world.getHighestBlockAt(0, 0).getLocation(), EntityType.ENDER_DRAGON);
         enderDragon.setCustomName(
            "Ender Dragon " + ChatColor.YELLOW + ChatColor.BOLD + Math.round(enderDragon.getHealth() / enderDragon.getMaxHealth() * 100.0) + "% Health"
         );
         ENDER_DRAGON = enderDragon;

         for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ENDERDRAGON_GROWL, 1.0F, 1.0F);
            List<String> tooltip = Arrays.asList(
               ChatColor.translate("&4&lKiller Rewards:"),
               ChatColor.translate("&4&l┃ &fDragon Egg"),
               ChatColor.translate("&4&l┃ &f5x Perk Keys"),
               ChatColor.translate("&4&l┃ &f100x Faction Gems"),
               ChatColor.translate("&4&l┃ &f50x Faction Points"),
               "",
               ChatColor.GREEN + "Kill it to receive these rewards!"
            );
            onlinePlayer.sendMessage(ChatColor.BLACK + "████████");
            onlinePlayer.sendMessage(ChatColor.BLACK + "████████" + ChatColor.DARK_PURPLE + " [Ender Dragon]");
            onlinePlayer.sendMessage(ChatColor.BLACK + "████████" + ChatColor.YELLOW + " has spawned!");
            onlinePlayer.sendMessage(ChatColor.BLACK + "████████");
            onlinePlayer.sendMessage(
               ChatColor.LIGHT_PURPLE
                  + "█"
                  + ChatColor.DARK_PURPLE
                  + "█"
                  + ChatColor.LIGHT_PURPLE
                  + "█"
                  + ChatColor.BLACK
                  + "██"
                  + ChatColor.LIGHT_PURPLE
                  + "█"
                  + ChatColor.DARK_PURPLE
                  + "█"
                  + ChatColor.LIGHT_PURPLE
                  + "█"
                  + ChatColor.DARK_PURPLE
                  + "Location:"
            );
            onlinePlayer.sendMessage(
               ChatColor.BLACK
                  + "████████ "
                  + ChatColor.WHITE
                  + enderDragon.getLocation().getBlockX()
                  + ", "
                  + enderDragon.getLocation().getBlockZ()
                  + ChatColor.DARK_PURPLE
                  + " [The End]"
            );
            onlinePlayer.sendMessage(ChatColor.BLACK + "████████" + ChatColor.GREEN + ChatColor.ITALIC + "Hover to view rewards");
            onlinePlayer.sendMessage(ChatColor.BLACK + "████████");
         }
      }
   }

   @EventHandler
   public void onBlockPlace(BlockPlaceEvent event) {
      if (LandBoard.getInstance().getTeam(event.getBlock().getLocation()) != null && event.getBlock().getWorld().getEnvironment().equals(Environment.THE_END)) {
         Team team = LandBoard.getInstance().getTeam(event.getBlock().getLocation());
         if (team.getOwner() != null && team.getOwner().toString().equalsIgnoreCase(event.getPlayer().getUniqueId().toString())) {
            return;
         }

         if (team.isMember(event.getPlayer().getUniqueId())) {
            return;
         }
      }

      if (event.getPlayer().getWorld().getEnvironment() == Environment.THE_END) {
         if (event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
         }

         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onBlockBreak(BlockBreakEvent event) {
      if (LandBoard.getInstance().getTeam(event.getBlock().getLocation()) != null && event.getBlock().getWorld().getEnvironment().equals(Environment.THE_END)) {
         Team team = LandBoard.getInstance().getTeam(event.getBlock().getLocation());
         if (team.getOwner() != null && team.getOwner().toString().equalsIgnoreCase(event.getPlayer().getUniqueId().toString())) {
            return;
         }

         if (team.isMember(event.getPlayer().getUniqueId())) {
            return;
         }
      }

      if (event.getPlayer().getWorld().getEnvironment() == Environment.THE_END) {
         if (event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
         }

         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onPlayerBukkitEmpty(PlayerBucketEmptyEvent event) {
      if (event.getPlayer().getWorld().getEnvironment() == Environment.THE_END) {
         if (event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
         }

         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onPlayerBucketFill(PlayerBucketFillEvent event) {
      if (LandBoard.getInstance().getTeam(event.getPlayer().getLocation()) != null && event.getPlayer().getWorld().getEnvironment().equals(Environment.THE_END)
         )
       {
         Team team = LandBoard.getInstance().getTeam(event.getPlayer().getLocation());
         if (team.getOwner() != null && team.getOwner().toString().equalsIgnoreCase(event.getPlayer().getUniqueId().toString())) {
            return;
         }

         if (team.isMember(event.getPlayer().getUniqueId())) {
            return;
         }
      }

      if (event.getPlayer().getWorld().getEnvironment() == Environment.THE_END) {
         if (event.getPlayer().isOp() && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
         }

         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onCreatePortal(EntityCreatePortalEvent event) {
      if (event.getEntity().getType() == EntityType.ENDER_DRAGON) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onPortal(PlayerPortalEvent event) {
      retargetMapWorldPortal(event);
      if (event.getTo() == null) {
         return;
      }

      if (event.getCause() == TeleportCause.END_PORTAL) {
         Player player = event.getPlayer();
         if ((event.getTo() == null || event.getTo().getWorld() == null) && event.getFrom().getWorld().getName().equalsIgnoreCase("world_miner")) {
            event.setTo(Foxtrot.getInstance().getServer().getWorlds().get(0).getSpawnLocation());
         }

         if (event.getTo().getWorld().getEnvironment() == Environment.NORMAL) {
            if (event.getFrom().getWorld().getEntitiesByClass(EnderDragon.class).size() != 0) {
               event.setCancelled(true);
               if (!this.msgCooldown.containsKey(player.getName()) || this.msgCooldown.get(player.getName()) < System.currentTimeMillis()) {
                  event.getPlayer().sendMessage(ChatColor.RED + "You cannot leave the end before the dragon is killed.");
                  this.msgCooldown.put(player.getName(), System.currentTimeMillis() + 3000L);
               }
            }

            loadEndReturn();
            event.setTo(Foxtrot.getInstance().getServer().getWorld("world_the_end").getSpawnLocation());
         } else if (event.getTo().getWorld().getEnvironment() == Environment.THE_END) {
            if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
               event.setCancelled(true);
               if (!this.msgCooldown.containsKey(player.getName()) || this.msgCooldown.get(player.getName()) < System.currentTimeMillis()) {
                  event.getPlayer().sendMessage(ChatColor.RED + "You cannot enter the end while you have PvP protection.");
                  this.msgCooldown.put(player.getName(), System.currentTimeMillis() + 3000L);
               }

               return;
            }

            if (SpawnTagHandler.isTagged(event.getPlayer())) {
               event.setCancelled(true);
               if (!this.msgCooldown.containsKey(player.getName()) || this.msgCooldown.get(player.getName()) < System.currentTimeMillis()) {
                  event.getPlayer().sendMessage(ChatColor.RED + "You cannot enter the end while you are spawn tagged.");
                  this.msgCooldown.put(player.getName(), System.currentTimeMillis() + 3000L);
               }

               return;
            }

            if (!endActive && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
               event.setCancelled(true);
               if (this.msgCooldown.containsKey(player.getName()) && this.msgCooldown.get(player.getName()) >= System.currentTimeMillis()) {
                  event.setTo(Foxtrot.getInstance().getServer().getWorld("world_the_end").getSpawnLocation());
               } else {
                  event.getPlayer().sendMessage(ChatColor.RED + "The End is currently disabled.");
                  this.msgCooldown.put(player.getName(), System.currentTimeMillis() + 3000L);
               }

               return;
            }

            for (PotionEffect potionEffect : event.getPlayer().getActivePotionEffects()) {
               if (potionEffect.getDuration() < 180) {
                  event.getPlayer().removePotionEffect(potionEffect.getType());
               }
            }

            event.setTo(event.getTo().getWorld().getSpawnLocation().clone().add(0.5, 1.0, 0.5));
         }
      }
   }

   private static void retargetMapWorldPortal(PlayerPortalEvent event) {
      World mapWorld = Foxtrot.getInstance().getServerHandler().getMapWorld();
      if (mapWorld != null) {
         Location from = event.getFrom();
         if (event.getTo() == null && from.getWorld().equals(mapWorld)) {
            if (event.getCause() == TeleportCause.NETHER_PORTAL) {
               World nether = Foxtrot.getInstance().getServer().getWorld("world_nether");
               if (nether != null) {
                  if (DTRBitmask.SAFE_ZONE.appliesAt(from)) {
                     event.setTo(getNetherSpawn(nether, from));
                     event.useTravelAgent(false);
                  } else {
                     event.setTo(new Location(nether, from.getX() / 8.0, from.getY(), from.getZ() / 8.0, from.getYaw(), from.getPitch()));
                     event.useTravelAgent(true);
                  }
               }
            } else if (event.getCause() == TeleportCause.END_PORTAL) {
               World end = Foxtrot.getInstance().getServer().getWorld("world_the_end");
               if (end != null) {
                  event.setTo(end.getSpawnLocation());
               }
            }
         } else if (event.getCause() == TeleportCause.NETHER_PORTAL
            && event.getTo() != null
            && from.getWorld().getEnvironment() == Environment.NETHER
            && event.getTo().getWorld().getEnvironment() == Environment.NORMAL
            && !event.getTo().getWorld().equals(mapWorld)) {
            Location to = event.getTo().clone();
            to.setWorld(mapWorld);
            event.setTo(to);
         }
      }
   }

   private static Location getNetherSpawn(World nether, Location from) {
      FileConfiguration config = Foxtrot.getInstance().getConfig();
      Location spawn = new Location(
         nether,
         config.getDouble("netherSpawn.x", 0.5),
         config.getDouble("netherSpawn.y", 36.0),
         config.getDouble("netherSpawn.z", 0.5),
         from.getYaw(),
         from.getPitch()
      );

      for (int i = 0; i < 4 && (spawn.getBlock().getType().isSolid() || spawn.clone().add(0.0, 1.0, 0.0).getBlock().getType().isSolid()); i++) {
         spawn.add(0.0, 1.0, 0.0);
      }

      return spawn;
   }

   @EventHandler
   public void onEntityExplode(EntityExplodeEvent event) {
      if (event.getEntity() instanceof EnderDragon) {
         event.blockList().clear();
         event.setCancelled(true);
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void onMove(PlayerMoveEvent event) {
      Location from = event.getFrom();
      Location to = event.getTo();
      if (from.getWorld().getEnvironment() == Environment.THE_END && to.getWorld().getEnvironment() == Environment.THE_END) {
         if (to.getBlockX() != from.getBlockX() || to.getBlockZ() != from.getBlockZ() || to.getBlockY() != from.getBlockY()) {
            if (event.getPlayer().getLocation().getBlock().getType() == Material.WATER
               || event.getPlayer().getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
               double pitch = 21.294214;
               double yaw = 28.865494;
               World mapWorld = Foxtrot.getInstance().getServerHandler().getMapWorld();
               event.getPlayer().teleport(new Location(mapWorld, 0.5, mapWorld.getHighestBlockYAt(0, 200) + 1, 200.5, (float)yaw, (float)pitch));
            }

            if (CustomTimerCreateCommand.isSOTWTimer() && !CustomTimerCreateCommand.hasSOTWEnabled(event.getPlayer().getUniqueId())) {
               if (to.getBlockY() < -100) {
                  event.getPlayer().teleport(Foxtrot.getInstance().getServerHandler().getSpawnLocation());
               }
            }
         }
      }
   }

   @EventHandler
   public void onEntityPortal(EntityPortalEvent event) {
      if (event.getEntity() instanceof EnderDragon) {
         event.setCancelled(true);
      }
   }

   public static void saveEndReturn() {
      Bukkit.getScheduler().scheduleAsyncDelayedTask(Foxtrot.getInstance(), () -> Proton.getInstance().runRedisCommand(redis -> {
         redis.set("endReturn", Proton.PLAIN_GSON.toJson(endReturn));
         return null;
      }));
   }

   public static void loadEndReturn() {
      if (endReturn == null) {
         Proton.getInstance().runRedisCommand(redis -> {
            if (redis.exists("endReturn")) {
               endReturn = Proton.PLAIN_GSON.fromJson(redis.get("endReturn"), Location.class);
            } else {
               endReturn = new Location(Foxtrot.getInstance().getServerHandler().getMapWorld(), 0.6, 64.0, 346.5);
            }

            return null;
         });
      }
   }

   public static Location getEndReturn() {
      return endReturn;
   }

   public static void setEndReturn(Location endReturn) {
      EndListener.endReturn = endReturn;
   }
}
