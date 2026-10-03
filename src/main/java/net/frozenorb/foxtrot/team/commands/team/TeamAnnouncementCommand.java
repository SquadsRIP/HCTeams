package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamAnnouncementCommand {
   @Command(
      names = {
            "team announcement",
            "t announcement",
            "f announcement",
            "faction announcement",
            "fac announcement",
            "team anouncement",
            "t anouncement",
            "f anouncement",
            "faction anouncement",
            "fac anouncement"
      },
      permission = ""
   )
   public static void teamAnnouncement(Player sender, @Parameter(name = "new announcement", wildcard = true) String newAnnouncement) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
      if (team == null) {
         sender.sendMessage(ChatColor.RED + "You are not in a faction!");
      } else if (!team.isOwner(sender.getUniqueId()) && !team.isCaptain(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
         sender.sendMessage(ChatColor.DARK_AQUA + "Only faction captains can do this.");
      } else if (newAnnouncement.equalsIgnoreCase("clear")) {
         team.setAnnouncement(null);
         sender.sendMessage(ChatColor.YELLOW + "Faction announcement cleared.");
      } else {
         team.setAnnouncement(newAnnouncement);
         team.sendMessage(
            ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.YELLOW + " changed the faction announcement to " + ChatColor.LIGHT_PURPLE + newAnnouncement
         );
      }
   }
}
