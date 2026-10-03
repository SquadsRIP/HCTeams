package net.frozenorb.foxtrot.listener;

import cc.fyre.proton.util.TimeUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.EOTWCommand;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.cavepvp.suge.Suge;

public class GoldenAppleListener implements Listener {
   private static Map<UUID, Long> crappleCooldown = new HashMap<>();

   @EventHandler(priority = EventPriority.LOW)
   public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
      Player player = event.getPlayer();
      if (!event.isCancelled()) {
         if (event.getItem() != null && event.getItem().getType() == Material.GOLDEN_APPLE) {
            if (Foxtrot.getInstance().getMapHandler().isKitMap() && Foxtrot.getInstance().getServerHandler().isTeams()) {
               event.setCancelled(true);
               event.getPlayer().sendMessage(ChatColor.YELLOW + "Golden Apples are disabled on Squads!");
            } else if (event.getItem().getDurability() == 0 && EOTWCommand.realFFAStarted()) {
               event.setCancelled(true);
               event.getPlayer().sendMessage(ChatColor.YELLOW + "Crapples are disabled during FFA.");
            } else {
               long cooldown = Foxtrot.getInstance().getConfig().getInt("crappleCooldownSeconds", 30) * 1000L;
               if (Suge.getInstance()
                  .getEnchantHandler()
                  .findAllCustomEnchants(player)
                  .keySet()
                  .stream()
                  .anyMatch(it -> it.getName().equalsIgnoreCase("Greed"))) {
                  cooldown = this.isIncreasedAtLocation(player.getLocation()) ? 20000L : 30000L;
               }

               if (player.getWorld().getEnvironment() == Environment.NETHER) {
                  cooldown = 45000L;
               }

               if (!Foxtrot.getInstance().getServerHandler().isUhcHealing()) {
                  if (event.getItem().getDurability() == 0 && !crappleCooldown.containsKey(player.getUniqueId())) {
                     crappleCooldown.put(player.getUniqueId(), System.currentTimeMillis() + cooldown);
                     return;
                  }

                  if (event.getItem().getDurability() == 0 && crappleCooldown.containsKey(player.getUniqueId())) {
                     long millisRemaining = crappleCooldown.get(player.getUniqueId()) - System.currentTimeMillis();
                     double value = millisRemaining / 1000.0;
                     double sec = value > 0.1 ? Math.round(10.0 * value) / 10.0 : 0.1;
                     if (crappleCooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                        player.sendMessage(ChatColor.YELLOW + "You cannot use this for another " + ChatColor.BOLD + sec + ChatColor.YELLOW + " seconds!");
                        event.setCancelled(true);
                        return;
                     }

                     crappleCooldown.put(player.getUniqueId(), System.currentTimeMillis() + cooldown);
                     return;
                  }
               }

               if (event.getItem().getType() != Material.GOLDEN_APPLE || event.getItem().getDurability() != 0) {
                  if (Foxtrot.getInstance().getMapHandler().getGoppleCooldown() == -1) {
                     event.setCancelled(true);
                     event.getPlayer().sendMessage(ChatColor.YELLOW + "Super golden apples are currently disabled.");
                  } else {
                     long cooldownUntil = Foxtrot.getInstance().getOppleMap().getCooldown(event.getPlayer().getUniqueId());
                     if (cooldownUntil > System.currentTimeMillis()) {
                        long millisLeft = cooldownUntil - System.currentTimeMillis();
                        String msg = TimeUtils.formatIntoDetailedString((int)millisLeft / 1000);
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot use this for another §c§l" + msg + "§c.");
                     } else {
                        int seconds = Foxtrot.getInstance().getMapHandler().isKitMap()
                           ? (int)TimeUnit.MINUTES.toSeconds(5L)
                           : Foxtrot.getInstance().getMapHandler().getGoppleCooldown() * 60;
                        Foxtrot.getInstance().getOppleMap().useGoldenApple(event.getPlayer().getUniqueId(), seconds);
                        long millisLeft = Foxtrot.getInstance().getOppleMap().getCooldown(event.getPlayer().getUniqueId()) - System.currentTimeMillis();
                        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "███" + ChatColor.YELLOW + "██" + ChatColor.DARK_GREEN + "███");
                        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "███" + ChatColor.YELLOW + "█" + ChatColor.DARK_GREEN + "████");
                        event.getPlayer()
                           .sendMessage(ChatColor.DARK_GREEN + "██" + ChatColor.GOLD + "████" + ChatColor.DARK_GREEN + "██" + ChatColor.GOLD + " Golden Apple:");
                        event.getPlayer()
                           .sendMessage(
                              ChatColor.DARK_GREEN
                                 + "█"
                                 + ChatColor.GOLD
                                 + "██"
                                 + ChatColor.WHITE
                                 + "█"
                                 + ChatColor.GOLD
                                 + "███"
                                 + ChatColor.DARK_GREEN
                                 + "█"
                                 + ChatColor.DARK_GREEN
                                 + "   Consumed"
                           );
                        event.getPlayer()
                           .sendMessage(
                              ChatColor.DARK_GREEN
                                 + "█"
                                 + ChatColor.GOLD
                                 + "█"
                                 + ChatColor.WHITE
                                 + "█"
                                 + ChatColor.GOLD
                                 + "████"
                                 + ChatColor.DARK_GREEN
                                 + "█"
                                 + ChatColor.YELLOW
                                 + " Cooldown Remaining:"
                           );
                        event.getPlayer()
                           .sendMessage(
                              ChatColor.DARK_GREEN
                                 + "█"
                                 + ChatColor.GOLD
                                 + "██████"
                                 + ChatColor.DARK_GREEN
                                 + "█"
                                 + ChatColor.BLUE
                                 + "   "
                                 + TimeUtils.formatIntoDetailedString((int)millisLeft / 1000)
                           );
                        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "█" + ChatColor.GOLD + "██████" + ChatColor.DARK_GREEN + "█");
                        event.getPlayer().sendMessage(ChatColor.DARK_GREEN + "██" + ChatColor.GOLD + "████" + ChatColor.DARK_GREEN + "██");
                     }
                  }
               }
            }
         }
      }
   }

   public boolean isIncreasedAtLocation(Location location) {
      return Foxtrot.getInstance().getMapHandler().isKitMap()
         ? false
         : !Foxtrot.getInstance().getServerHandler().isWarzone(location)
            && location.getWorld().getEnvironment() == Environment.NORMAL
            && !DTRBitmask.KOTH.appliesAt(location)
            && !DTRBitmask.CONQUEST.appliesAt(location)
            && !DTRBitmask.CITADEL.appliesAt(location)
            && !DTRBitmask.DTC.appliesAt(location)
            && !DTRBitmask.SAFE_ZONE.appliesAt(location);
   }

   public static Map<UUID, Long> getCrappleCooldown() {
      return crappleCooldown;
   }
}
