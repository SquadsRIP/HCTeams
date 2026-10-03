package net.frozenorb.foxtrot.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DailyLoginBonusCommand {
   public static void execute(Player player) {
      player.sendMessage(ChatColor.YELLOW + "Daily Login Bonuses will be enabled once the Advent Calendar season is over! (December 26th)");
   }
}
