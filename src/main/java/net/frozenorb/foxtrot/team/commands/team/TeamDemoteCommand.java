package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamDemoteCommand {
   @Command(names = {"team demote", "t demote", "f demote", "faction demote", "fac demote"}, permission = "")
   public static void teamDemote(Player sender, @Parameter(name = "player") UUID player) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
      if (team == null) {
         sender.sendMessage(ChatColor.RED + "You are not in a faction!");
      } else if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
         sender.sendMessage(ChatColor.DARK_AQUA + "Only faction co-leaders (and above) can do this.");
      } else if (!team.isMember(player)) {
         sender.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player) + " is not on your faction.");
      } else {
         if (team.isOwner(player)) {
            sender.sendMessage(ChatColor.YELLOW + UUIDUtils.name(player) + " is the leader. To change leaders, the faction leader must use /f leader <name>");
         } else if (team.isCoLeader(player)) {
            if (team.isOwner(sender.getUniqueId())) {
               team.removeCoLeader(player);
               team.addCaptain(player);
               team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player) + " has been demoted to Captain!");
            } else {
               sender.sendMessage(ChatColor.YELLOW + "Only the faction leader can demote Co-Leaders.");
            }
         } else if (team.isCaptain(player)) {
            team.removeCaptain(player);
            team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player) + " has been demoted to a member!");
         } else {
            sender.sendMessage(ChatColor.YELLOW + UUIDUtils.name(player) + " is currently a member. To kick them, use /f kick");
         }
      }
   }
}
