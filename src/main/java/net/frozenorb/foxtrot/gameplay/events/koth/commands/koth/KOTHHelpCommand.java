package net.frozenorb.foxtrot.gameplay.events.koth.commands.koth;

import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHHelpCommand {
   @Command(names = "KOTH Help", permission = "foxtrot.koth")
   public static void kothHelp(Player sender) {
      sender.sendMessage(ChatColor.YELLOW + "/koth list - Lists KOTHs");
      sender.sendMessage(ChatColor.YELLOW + "/koth activate <name> - Activates a KOTH");
      sender.sendMessage(ChatColor.YELLOW + "/koth deactivate <name> - Deactivates a KOTH");
      sender.sendMessage(ChatColor.YELLOW + "/koth loc <name> - Set a KOTH's cap location");
      sender.sendMessage(ChatColor.YELLOW + "/koth time <name> <time> - Sets a KOTH's cap time");
      sender.sendMessage(ChatColor.YELLOW + "/koth dist <name> <distance> - Sets a KOTH's cap distance");
      sender.sendMessage(ChatColor.YELLOW + "/koth tp <name> - TPs to a KOTH's");
      sender.sendMessage(ChatColor.YELLOW + "/koth create <name> - Creates a KOTH");
      sender.sendMessage(ChatColor.YELLOW + "/koth delete <name> - Deletes a KOTH");
   }
}
