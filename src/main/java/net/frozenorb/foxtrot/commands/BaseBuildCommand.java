package net.frozenorb.foxtrot.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BaseBuildCommand {
   public static void baseBuild(Player player) {
      player.sendMessage(ChatColor.RED + "Build competitions are disabled on this server.");
   }
}
