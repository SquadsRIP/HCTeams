package net.frozenorb.foxtrot.gameplay.kitmap.game.command;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ForceEndCommand {
   @Command(names = "game forceend", description = "Force end an event", permission = "game.command.end")
   public static void execute(Player player) {
      if (!Foxtrot.getInstance().getMapHandler().getGameHandler().isOngoingGame()) {
         player.sendMessage(ChatColor.YELLOW.toString() + "There is no ongoing event.");
      } else {
         Foxtrot.getInstance().getMapHandler().getGameHandler().getOngoingGame().endGame();
         player.sendMessage(ChatColor.GREEN + "Successfully ended the ongoing event!");
      }
   }
}
