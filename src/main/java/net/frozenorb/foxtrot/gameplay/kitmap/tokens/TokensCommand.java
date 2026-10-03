package net.frozenorb.foxtrot.gameplay.kitmap.tokens;

import cc.fyre.proton.util.TimeUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TokensCommand {
   public static void tokens(Player sender) {
      int nextTokenSeconds = (int)((Foxtrot.getInstance().getTokensHandler().getPendingTokens().get(sender.getUniqueId()) - System.currentTimeMillis()) / 1000L);
      sender.sendMessage(
         ChatColor.YELLOW
            + "You have "
            + ChatColor.GOLD
            + Foxtrot.getInstance().getKitmapTokensMap().getTokens(sender.getUniqueId())
            + ChatColor.YELLOW
            + " tokens."
      );
      sender.sendMessage(
         ChatColor.YELLOW
            + "You will receive another token in "
            + ChatColor.LIGHT_PURPLE
            + TimeUtils.formatIntoDetailedString(nextTokenSeconds)
            + ChatColor.YELLOW
            + "."
      );
   }

   public static void tokensAll(Player sender) {
      sender.sendMessage(ChatColor.GREEN + "Giving everyone a token...");

      for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
         onlinePlayer.sendMessage(ChatColor.GREEN + "You have been given 1 token!");
         Foxtrot.getInstance()
            .getKitmapTokensMap()
            .setTokens(onlinePlayer.getUniqueId(), Foxtrot.getInstance().getKitmapTokensMap().getTokens(onlinePlayer.getUniqueId()) + 1);
      }
   }

   public static void tokenMerchant(Player sender) {
      int tokens = Foxtrot.getInstance().getKitmapTokensMap().getTokens(sender.getUniqueId());
      if (tokens <= 0) {
         sender.sendMessage(org.bukkit.ChatColor.RED + "You don't have any tokens to claim.");
      } else if (tokens < 3) {
         sender.sendMessage(org.bukkit.ChatColor.RED + "You need at least 3 tokens to exchange them for a key.");
      } else {
         int keys;
         for (keys = 0; tokens >= 3; keys++) {
            tokens -= 3;
         }

         Foxtrot.getInstance()
            .getKitmapTokensMap()
            .setTokens(sender.getUniqueId(), Foxtrot.getInstance().getKitmapTokensMap().getTokens(sender.getUniqueId()) % 3);
         sender.sendMessage(
            org.bukkit.ChatColor.GREEN
               + "You've exchanged your tokens for "
               + org.bukkit.ChatColor.DARK_GREEN
               + keys
               + org.bukkit.ChatColor.GREEN
               + " crate keys."
         );
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + sender.getName() + " token " + keys);
      }
   }
}
