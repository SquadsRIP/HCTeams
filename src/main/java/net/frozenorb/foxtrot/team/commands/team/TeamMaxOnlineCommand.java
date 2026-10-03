package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamMaxOnlineCommand {
   @Command(names = "team maxOnline", permission = "op")
   public static void teamMaxOnline(Player sender, @Parameter(name = "team") Team team, @Parameter(name = "max online", defaultValue = "-5") int maxOnline) {
      if (maxOnline == -5) {
         if (team.getMaxOnline() == -1) {
            sender.sendMessage(team.getName(sender) + ChatColor.YELLOW + "'s player limit is " + ChatColor.GREEN + "not set" + ChatColor.YELLOW + ".");
         } else {
            sender.sendMessage(
               team.getName(sender) + ChatColor.YELLOW + "'s player limit is " + ChatColor.YELLOW + team.getMaxOnline() + ChatColor.YELLOW + "."
            );
         }
      } else {
         team.setMaxOnline(maxOnline);
         sender.sendMessage(
            team.getName(sender) + ChatColor.YELLOW + "'s player limit has been set to " + ChatColor.YELLOW + maxOnline + ChatColor.YELLOW + "."
         );
      }
   }
}
