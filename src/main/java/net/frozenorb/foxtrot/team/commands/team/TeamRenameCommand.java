package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamRenameCommand {
   @Command(names = {"team rename", "t rename", "f rename", "faction rename", "fac rename"}, permission = "")
   public static void teamRename(Player sender, @Parameter(name = "new name") String newName) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
      if (team == null) {
         sender.sendMessage(ChatColor.RED + "You are not in a faction!");
      } else if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
         sender.sendMessage(ChatColor.YELLOW + "Only faction owners and co-leaders can use this command!");
      } else if (newName.length() > 16) {
         sender.sendMessage(ChatColor.YELLOW + "Maximum faction name size is 16 characters!");
      } else if (newName.length() < 3) {
         sender.sendMessage(ChatColor.YELLOW + "Minimum faction name size is 3 characters!");
      } else if (newName.equalsIgnoreCase("iMakeMcVids")) {
         sender.sendMessage(ChatColor.YELLOW + "You can not create a faction with that name!");
      } else {
         if (!TeamCreateCommand.ALPHA_NUMERIC.matcher(newName).find()) {
            if (Foxtrot.getInstance().getTeamHandler().getTeam(newName) == null) {
               team.rename(newName);
               sender.sendMessage(ChatColor.GREEN + "Faction renamed to " + newName);
            } else {
               sender.sendMessage(ChatColor.YELLOW + "A faction with that name already exists!");
            }
         } else {
            sender.sendMessage(ChatColor.YELLOW + "Faction names must be alphanumeric!");
         }
      }
   }
}
