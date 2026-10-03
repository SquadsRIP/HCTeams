package net.frozenorb.foxtrot.listener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class HardcoreCommandListener implements Listener {
   private static final Set<String> BLOCKED_COMMANDS = new HashSet<>(
      Arrays.asList(
         "gkits",
         "kits",
         "freekits",
         "kitupgrades",
         "upgradekits",
         "kiteditor",
         "kit",
         "selectkit",
         "partneritems",
         "abilityitems",
         "ability",
         "randomownerkeys",
         "randompartnerkeys",
         "randomclickablekit",
         "clickablekit",
         "clickablekits",
         "booster",
         "boosters",
         "networkbooster",
         "xpshop",
         "rtp",
         "wild",
         "wildtp",
         "randomtp",
         "randomteleport",
         "tpa",
         "tpayes",
         "tpyes",
         "tpaccept",
         "tpdeny",
         "tpadeny",
         "tpahere",
         "shop",
         "buyshop",
         "sellshop",
         "simplytrashmode",
         "coinshop",
         "gemshop",
         "gemsshop",
         "airdrop",
         "airdrops",
         "gemflip",
         "gemflips",
         "coinflip",
         "coinflips",
         "cf",
         "gf",
         "perkkeybroadcast",
         "cavechat",
         "battlepass",
         "bp",
         "missions",
         "challenges",
         "basebuildcompetition",
         "poll",
         "polls",
         "duel",
         "duelarena",
         "armorclass",
         "accept",
         "perk"
      )
   );

   @EventHandler(priority = EventPriority.LOWEST)
   public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
      if (isGkitCommand(event.getMessage())) {
         event.setCancelled(true);
         event.getPlayer().sendMessage("§cThese commands are disabled.");
      } else if (Foxtrot.getInstance().getServerHandler().isHardcore()) {
         Player player = event.getPlayer();
         String message = event.getMessage();
         if (message.startsWith("/")) {
            message = message.substring(1);
         }

         String commandName = message.split(" ")[0].toLowerCase();
         String fullCommand = message.toLowerCase().trim();
         if (BLOCKED_COMMANDS.contains(commandName)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "These commands are disabled.");
         } else if (fullCommand.startsWith("booster ") || fullCommand.startsWith("boosters ")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "These commands are disabled.");
         } else if (fullCommand.startsWith("airdrop ") || fullCommand.startsWith("airdrops ")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "These commands are disabled.");
         } else if (fullCommand.startsWith("kit ")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "These commands are disabled.");
         } else if (fullCommand.startsWith("gem shop") || fullCommand.startsWith("gems shop")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "These commands are disabled.");
         }
      }
   }

   private static boolean isGkitCommand(String var0) {
      if (var0 == null) {
         return false;
      }

      String var1 = var0.trim();
      if (var1.startsWith("/")) {
         var1 = var1.substring(1);
      }

      var1 = var1.split("\\s+", 2)[0];
      var1 = var1.substring(var1.lastIndexOf(58) + 1);
      return var1.equalsIgnoreCase("gkit")
         || var1.equalsIgnoreCase("gkits")
         || var1.equalsIgnoreCase("classes")
         || var1.equalsIgnoreCase("class")
         || var1.equalsIgnoreCase("armorclasses")
         || var1.equalsIgnoreCase("armorclass");
   }
}
