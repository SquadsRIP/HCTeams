package net.frozenorb.foxtrot.gameplay.events.conquest.commands.conquestadmin;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.conquest.game.ConquestGame;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ConquestAdminStopCommand {
   @Command(names = "conquestadmin stop", permission = "op")
   public static void conquestAdminStop(CommandSender sender) {
      ConquestGame game = Foxtrot.getInstance().getConquestHandler().getGame();
      if (game == null) {
         sender.sendMessage(ChatColor.YELLOW + "Conquest is not active.");
      } else {
         game.endGame(null);
      }
   }
}
