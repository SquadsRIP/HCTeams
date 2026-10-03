package net.frozenorb.foxtrot.controlpanel;

import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ControlPanelCommand {
   @Command(names = {"controlpanel", "cp"}, permission = "op")
   public static void controlPanel(Player sender) {
      if (!ControlPanelService.isEnabled()) {
         sender.sendMessage(ChatColor.RED + "Control Panel is not available (website not reachable).");
      } else {
         String code = ControlPanelService.issueAccessCode();
         if (code == null) {
            sender.sendMessage(ChatColor.RED + "Failed to generate access code. Check the console for errors.");
         } else {
            sender.sendMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Squads Control Panel");
            sender.sendMessage(ChatColor.GRAY + "Your access code: " + ChatColor.WHITE + ChatColor.BOLD + code);
            sender.sendMessage(ChatColor.GRAY + "This code expires in 10 minutes.");
            sender.sendMessage(ChatColor.GRAY + "Go to the control panel website and enter the code to log in.");
         }
      }
   }
}
