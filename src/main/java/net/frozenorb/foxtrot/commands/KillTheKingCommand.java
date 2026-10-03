package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KillTheKingCommand {
   public static UUID king = null;

   @Command(names = {"ktk", "killtheking"}, permission = "op")
   public static void execute(Player player) {
      if (king != null) {
         king = null;
         player.sendMessage(ChatColor.YELLOW + "Ended Kill The King!");
      } else {
         king = player.getUniqueId();
         player.sendMessage(ChatColor.YELLOW + "Set yourself as the king!");
      }
   }
}
