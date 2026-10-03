package net.frozenorb.foxtrot.gameplay.kitmap.game.impl.oitq;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameState;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class OITQListeners implements Listener {
   private final GameHandler gameHandler = Foxtrot.getInstance().getMapHandler().getGameHandler();

   @EventHandler(ignoreCancelled = true)
   private void onDamage(EntityDamageByEntityEvent event) {
      if (this.gameHandler.isOngoingGame() && this.gameHandler.getOngoingGame() instanceof OITQGame) {
         OITQGame ongoingGame = (OITQGame)this.gameHandler.getOngoingGame();
         if (ongoingGame.getState() == GameState.RUNNING) {
            if (event.getDamager() instanceof Arrow && event.getEntity() instanceof Player) {
               if (ongoingGame.isStarted()) {
                  Arrow arrow = (Arrow)event.getDamager();
                  if (arrow.getShooter() instanceof Player) {
                     Player target = (Player)event.getEntity();
                     Player damager = (Player)arrow.getShooter();
                     if (!damager.getName().equalsIgnoreCase(target.getName())) {
                        if (ongoingGame.isPlaying(target.getUniqueId()) && ongoingGame.isPlaying(damager.getUniqueId())) {
                           if (target.getLocation().getWorld().getName().equals("kits_events")) {
                              int killCount = ongoingGame.getKillsCount().getOrDefault(damager.getUniqueId(), 0) + 1;
                              event.setDamage(0.0);
                              if (killCount == 20) {
                                 ongoingGame.getPlayers()
                                    .stream()
                                    .filter(it -> !it.getName().equalsIgnoreCase(damager.getName()))
                                    .forEach(ongoingGame::removePlayer);
                                 ongoingGame.endGame();
                              } else {
                                 target.closeInventory();
                                 target.getInventory().clear();
                                 Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                                    target.getInventory().addItem(new ItemStack(Material.STONE_AXE, 1));
                                    target.getInventory().addItem(new ItemStack(Material.BOW, 1));
                                    target.getInventory().addItem(new ItemStack(Material.ARROW, 1));
                                 }, 5L);
                                 target.setVelocity(new Vector(0, 0, 0));
                                 damager.getInventory().addItem(new ItemStack(Material.ARROW, 1));
                                 ongoingGame.getKillsCount().put(damager.getUniqueId(), killCount);
                                 ongoingGame.sendMessages(
                                    ChatColor.GOLD.toString()
                                       + target.getName()
                                       + ChatColor.YELLOW
                                       + " was been shot by "
                                       + ChatColor.GOLD
                                       + damager.getName()
                                       + ChatColor.YELLOW
                                       + "!"
                                 );
                                 List<Location> locationList = ongoingGame.getVotedArena().getAlternateSpawns();
                                 if (locationList.isEmpty()) {
                                    target.teleport(ongoingGame.getVotedArena().getPointA());
                                 } else {
                                    target.teleport(locationList.get(ThreadLocalRandom.current().nextInt(locationList.size())));
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler
   private void onDeath(PlayerDeathEvent event) {
      if (this.gameHandler.isOngoingGame() && this.gameHandler.getOngoingGame() instanceof OITQGame) {
         OITQGame ongoingGame = (OITQGame)this.gameHandler.getOngoingGame();
         if (ongoingGame.getState() == GameState.RUNNING) {
            Player target = event.getEntity();
            Player killer = target.getKiller();
            if (killer != null && ongoingGame.isStarted()) {
               if (ongoingGame.isPlaying(target.getUniqueId()) && ongoingGame.isPlaying(killer.getUniqueId())) {
                  int killCount = ongoingGame.getKillsCount().getOrDefault(killer.getUniqueId(), 0) + 1;
                  if (killCount == 20) {
                     ongoingGame.getPlayers().stream().filter(it -> !it.getName().equalsIgnoreCase(killer.getName())).forEach(ongoingGame::removePlayer);
                     ongoingGame.endGame();
                  } else {
                     event.setKeepInventory(true);
                     killer.getInventory().addItem(new ItemStack(Material.ARROW, 1));
                     ongoingGame.getKillsCount().put(killer.getUniqueId(), killCount);
                     ongoingGame.sendMessages(
                        ChatColor.GOLD.toString()
                           + target.getName()
                           + ChatColor.YELLOW
                           + " was been slain by "
                           + ChatColor.GOLD
                           + killer.getName()
                           + ChatColor.YELLOW
                           + "!"
                     );
                  }
               }
            }
         }
      }
   }

   @EventHandler
   private void onRespawn(PlayerRespawnEvent event) {
      if (this.gameHandler.isOngoingGame() && this.gameHandler.getOngoingGame() instanceof OITQGame) {
         OITQGame ongoingGame = (OITQGame)this.gameHandler.getOngoingGame();
         if (ongoingGame.getState() == GameState.RUNNING) {
            Player target = event.getPlayer();
            if (ongoingGame.isStarted()) {
               if (ongoingGame.isPlaying(target.getUniqueId())) {
                  target.closeInventory();
                  target.getInventory().clear();
                  target.setHealth(20.0);
                  target.getInventory().addItem(new ItemStack(Material.STONE_AXE, 1));
                  target.getInventory().addItem(new ItemStack(Material.BOW, 1));
                  target.getInventory().addItem(new ItemStack(Material.ARROW, 1));
                  List<Location> locationList = ongoingGame.getVotedArena().getAlternateSpawns();
                  if (locationList.isEmpty()) {
                     event.setRespawnLocation(ongoingGame.getVotedArena().getPointA());
                  } else {
                     target.teleport(locationList.get(ThreadLocalRandom.current().nextInt(locationList.size())));
                  }
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onItemDamage(PlayerItemDamageEvent event) {
      if (this.gameHandler.isOngoingGame() && this.gameHandler.getOngoingGame() instanceof OITQGame) {
         Player player = event.getPlayer();
         if (event.getItem().getType() == Material.BOW) {
            OITQGame ongoingGame = (OITQGame)this.gameHandler.getOngoingGame();
            if (ongoingGame.getState() == GameState.RUNNING) {
               if (!ongoingGame.isPlaying(player.getUniqueId())) {
                  event.setCancelled(true);
               }
            }
         }
      }
   }
}
