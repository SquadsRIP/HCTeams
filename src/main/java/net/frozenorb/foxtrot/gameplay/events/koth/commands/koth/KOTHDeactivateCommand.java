package net.frozenorb.foxtrot.gameplay.events.koth.commands.koth;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.Event;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class KOTHDeactivateCommand {
   @Command(names = {"KOTH Deactivate", "KOTH Inactive", "event deactivate"}, permission = "foxtrot.koth.admin")
   public static void kothDectivate(CommandSender sender, @Parameter(name = "koth") Event koth) {
      if (!koth.isActive()) {
         sender.sendMessage(ChatColor.YELLOW + "That event isn't active!");
      } else {
         koth.deactivate();
         Foxtrot.getInstance()
            .getServer()
            .broadcastMessage(sender.getName() + ChatColor.GOLD + " has cancelled " + ChatColor.WHITE + koth.getName() + ChatColor.GOLD + ".");
      }
   }
}
