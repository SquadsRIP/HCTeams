package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.param.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class RatioCommand implements Listener {
   public static Map<UUID, Long> CACHE = new HashMap<>();
   public static String CURRENT_RATIO = "";
   public static String STARTED_BY = "";
   public static List<UUID> POSITIVES = new ArrayList<>();
   public static List<UUID> NEGATIVES = new ArrayList<>();

   public static void execute(Player player, @Parameter(name = "target") Player target) {
      if (!player.hasPermission("command.ratio")) {
         player.sendMessage(ChatColor.RED + "You need VIP status in order to ratio someone!");
         player.sendMessage(ChatColor.RED + "Purchase VIP status at Coming Soon!");
      } else if (!CURRENT_RATIO.equalsIgnoreCase("")) {
         player.sendMessage(ChatColor.RED + "Someone is already being ratio'd");
      } else if (CACHE.containsKey(player.getUniqueId()) && CACHE.get(player.getUniqueId()) > System.currentTimeMillis()) {
         player.sendMessage(ChatColor.RED + "You are still on cooldown for the ratio system!");
      } else {
         if (target.getName().equalsIgnoreCase("SimplyTrash")) {
            target = player;
         }

         POSITIVES.clear();
         NEGATIVES.clear();
         CURRENT_RATIO = target.getName();
         STARTED_BY = player.getName();
         CACHE.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1L));
         String displayName = player.getDisplayName();
         String targetName = target.getDisplayName();

         for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            onlinePlayer.sendMessage("");
            onlinePlayer.sendMessage(
               ChatColor.translate("&4&lRatio &8┃ &f" + player.getDisplayName() + " &chas started a ratio on &f" + target.getDisplayName() + "&c!")
            );
            onlinePlayer.sendMessage(ChatColor.translate("&7Type &f+1 &7in chat to upvote this ratio!"));
            onlinePlayer.sendMessage(ChatColor.translate("&7Type &f-1 &7in chat to downvote this ratio!"));
            onlinePlayer.sendMessage("");
         }

         Foxtrot.getInstance()
            .getServer()
            .getScheduler()
            .runTaskLater(
               Foxtrot.getInstance(),
               () -> {
                  for (Player onlinePlayerx : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                     int negatives = NEGATIVES.size();
                     int positives = POSITIVES.size();
                     onlinePlayerx.sendMessage("");
                     if (negatives == positives) {
                        onlinePlayerx.sendMessage(
                           ChatColor.translate(
                              "&4&lRatio &8┃ &f"
                                 + displayName
                                 + " &chas failed the ratio on &f"
                                 + targetName
                                 + "&c as it was equal! ["
                                 + positives
                                 + "-"
                                 + negatives
                                 + "]"
                           )
                        );
                     }

                     if (negatives > positives) {
                        onlinePlayerx.sendMessage(
                           ChatColor.translate(
                              "&4&lRatio &8┃ &f"
                                 + displayName
                                 + " &chas lost the ratio on &f"
                                 + targetName
                                 + "&c as there were more downvotes! ["
                                 + positives
                                 + "-"
                                 + negatives
                                 + "]"
                           )
                        );
                     }

                     if (positives > negatives) {
                        onlinePlayerx.sendMessage(
                           ChatColor.translate(
                              "&4&lRatio &8┃ &f" + displayName + " &ahas successfully ratio'd &f" + targetName + "&a! &c[" + positives + "-" + negatives + "]"
                           )
                        );
                     }

                     onlinePlayerx.sendMessage("");
                  }

                  CURRENT_RATIO = "";
                  STARTED_BY = "";
                  POSITIVES.clear();
                  NEGATIVES.clear();
               },
               300L
            );
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   private void onChat(AsyncPlayerChatEvent event) {
      Player player = event.getPlayer();
      if (!CURRENT_RATIO.equalsIgnoreCase("")) {
         if (event.getMessage().replace("-", "").equalsIgnoreCase("1") || event.getMessage().replace("+", "").equalsIgnoreCase("1")) {
            if (NEGATIVES.contains(player.getUniqueId())) {
               player.sendMessage(ChatColor.RED + "You have already -1'd!");
            } else if (POSITIVES.contains(player.getUniqueId())) {
               player.sendMessage(ChatColor.RED + "You have already +1'd!");
            } else {
               if (event.getMessage().equalsIgnoreCase("+1")) {
                  POSITIVES.add(player.getUniqueId());
                  Foxtrot.getInstance()
                     .getServer()
                     .broadcastMessage(
                        ChatColor.translate(
                           "&4&lRatio &8┃ &f"
                              + player.getName()
                              + " &ahas +1 the ratio against &f"
                              + CURRENT_RATIO
                              + "&a! &c["
                              + POSITIVES.size()
                              + "-"
                              + NEGATIVES.size()
                              + "]"
                        )
                     );
               } else if (event.getMessage().equalsIgnoreCase("-1")) {
                  NEGATIVES.add(player.getUniqueId());
                  Foxtrot.getInstance()
                     .getServer()
                     .broadcastMessage(
                        ChatColor.translate(
                           "&4&lRatio &8┃ &f"
                              + player.getName()
                              + " &chas -1 the ratio against &f"
                              + CURRENT_RATIO
                              + "&c! &c["
                              + POSITIVES.size()
                              + "-"
                              + NEGATIVES.size()
                              + "]"
                        )
                     );
               }
            }
         }
      }
   }
}
