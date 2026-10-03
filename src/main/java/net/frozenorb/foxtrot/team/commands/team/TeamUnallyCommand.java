package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamUnallyCommand {
   @Command(names = {"team unally", "t unally", "f unally", "faction unally", "fac unally"}, permission = "")
   public static void teamUnally(Player sender, @Parameter(name = "team") Team team) {
      Team senderTeam = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
      if (senderTeam == null) {
         sender.sendMessage(ChatColor.GRAY + "You are not in a faction!");
      } else if (!senderTeam.isOwner(sender.getUniqueId()) && !senderTeam.isCoLeader(sender.getUniqueId()) && !senderTeam.isCaptain(sender.getUniqueId())) {
         sender.sendMessage(ChatColor.DARK_AQUA + "Only faction captains can do this.");
      } else if (!senderTeam.isAlly(team)) {
         sender.sendMessage(ChatColor.RED + "You are not allied to " + team.getName() + "!");
      } else {
         senderTeam.getAllies().remove(team.getUniqueId());
         team.getAllies().remove(senderTeam.getUniqueId());
         senderTeam.flagForSave();
         team.flagForSave();

         for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (team.isMember(player.getUniqueId())) {
               player.sendMessage(senderTeam.getName(player) + ChatColor.YELLOW + " has dropped their alliance with your faction.");
            } else if (senderTeam.isMember(player.getUniqueId())) {
               player.sendMessage(ChatColor.YELLOW + "Your faction has dropped its alliance with " + team.getName(sender) + ChatColor.YELLOW + ".");
            }

            if (team.isMember(player.getUniqueId()) || senderTeam.isMember(player.getUniqueId())) {
               LunarClientListener.updateNametag(sender);
            }
         }
      }
   }
}
