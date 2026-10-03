package net.frozenorb.foxtrot.gameplay.events.conquest.commands.conquestadmin;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.conquest.game.ConquestGame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ConquestAdminStartCommand {
   @Command(names = "conquestadmin start", permission = "op")
   public static void conquestAdminStart(CommandSender sender) {
      ConquestGame game = Foxtrot.getInstance().getConquestHandler().getGame();
      if (game != null) {
         sender.sendMessage(ChatColor.YELLOW + "Conquest is already active.");
      } else {
         new ConquestGame();
      }
   }
}
