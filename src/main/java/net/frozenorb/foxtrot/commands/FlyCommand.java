package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.config.ConfigMessages;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FlyCommand {
   public static void execute(Player player) {
      if (player.hasPermission("command.fly")) {
         player.setAllowFlight(!player.getAllowFlight());
         player.sendMessage(ChatColor.GOLD + "Fly: " + (player.getAllowFlight() ? ChatColor.GREEN + "Enabled" : ChatColor.YELLOW + "Disabled"));
      } else if (!player.hasPermission("command.customrank.fly")) {
         player.sendMessage(ConfigMessages.message("messages.store.custom-rank-required", "&eYou must purchase the Custom Rank to use this at %store%!"));
      } else if (!CustomTimerCreateCommand.isSOTWTimer()) {
         player.sendMessage(ChatColor.YELLOW + "You can only use this during SOTW Timer!");
      } else if (CustomTimerCreateCommand.hasSOTWEnabled(player.getUniqueId())) {
         player.sendMessage(ChatColor.YELLOW + "You can only use this during SOTW Timer!");
      } else {
         player.setAllowFlight(!player.getAllowFlight());
         player.sendMessage(ChatColor.GOLD + "Fly: " + (player.getAllowFlight() ? ChatColor.GREEN + "Enabled" : ChatColor.YELLOW + "Disabled"));
      }
   }

   public static void execute(Player player, @Parameter(name = "speed") int speed) {
      if (!player.getAllowFlight()) {
         player.sendMessage(ChatColor.YELLOW + "You must already have flight enabled to do this!");
      } else if (!player.hasPermission("command.customrank.fly") && !player.hasPermission("command.fly")) {
         player.sendMessage(ConfigMessages.message("messages.store.custom-rank-required", "&eYou must purchase the Custom Rank to use this at %store%!"));
      } else if (!CustomTimerCreateCommand.isSOTWTimer()) {
         player.sendMessage(ChatColor.YELLOW + "You can only use this during SOTW Timer!");
      } else if (CustomTimerCreateCommand.hasSOTWEnabled(player.getUniqueId())) {
         player.sendMessage(ChatColor.YELLOW + "You can only use this during SOTW Timer!");
      } else if (speed <= 4 && speed > 0) {
         player.setFlySpeed((float)(speed * 0.1));
         player.sendMessage(ChatColor.GOLD + "Fly Speed: " + (player.getAllowFlight() ? ChatColor.GREEN + "Enabled" : ChatColor.YELLOW + "Disabled"));
      } else {
         player.sendMessage(ChatColor.YELLOW + "The speed must be a number between 1-4.");
      }
   }

   @Command(names = "setfly", permission = "op")
   public static void execute(Player player, @Parameter(name = "target") Player target) {
      player.setAllowFlight(!player.getAllowFlight());
      player.sendMessage(
         ChatColor.GOLD + "Fly for " + target.getName() + ": " + (player.getAllowFlight() ? ChatColor.GREEN + "Enabled" : ChatColor.YELLOW + "Disabled")
      );
   }
}
