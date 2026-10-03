package net.frozenorb.foxtrot.gameplay.events.cavenite.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.cavenite.CaveNiteHandler;
import net.frozenorb.foxtrot.gameplay.events.cavenite.CaveNiteState;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CaveNiteStartCommand {
   public static void execute(CommandSender sender) {
      CaveNiteHandler caveniteHandler = Foxtrot.getInstance().getCaveNiteHandler();
      if (caveniteHandler.getGameState() != CaveNiteState.INACTIVE) {
         sender.sendMessage(ChatColor.YELLOW + "Its a no for me");
      } else {
         Team team = Foxtrot.getInstance().getTeamHandler().getTeam("CaveNite");
         if (team == null) {
            sender.sendMessage(ChatColor.YELLOW + "The 'SquadsNite' faction doesn't exist!");
         } else {
            caveniteHandler.start();
         }
      }
   }
}
