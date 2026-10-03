package net.frozenorb.foxtrot.commands;

import cc.fyre.piston.PistonConstants;
import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.ServerHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LogoutCommand {
   @Command(names = "Logout", permission = "")
   public static void logout(Player sender) {
      if (sender.hasMetadata(PistonConstants.FREEZE_METADATA)) {
         sender.sendMessage(ChatColor.YELLOW + "You can't log out while you're frozen!");
      } else if (ServerHandler.getTasks().containsKey(sender.getName())) {
         sender.sendMessage(ChatColor.YELLOW + "You are already logging out.");
      } else {
         Foxtrot.getInstance().getServerHandler().startLogoutSequence(sender);
      }
   }
}
