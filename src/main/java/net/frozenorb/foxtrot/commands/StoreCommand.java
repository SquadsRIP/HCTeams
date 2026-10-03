package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.config.ConfigMessages;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class StoreCommand {
   @Command(names = {"store", "donate"}, permission = "")
   public static void store(Player player) {
      player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Store: &c" + ConfigMessages.link("store", "squadsrip.tebex.io")));
      player.sendMessage(ChatColor.GRAY + "Use " + ChatColor.WHITE + "/buy" + ChatColor.GRAY + " to browse packages in-game.");
   }
}
