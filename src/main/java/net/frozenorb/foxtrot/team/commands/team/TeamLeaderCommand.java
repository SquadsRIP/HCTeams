package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamLeaderCommand {
   @Command(
      names = {
            "team newleader",
            "t newleader",
            "f newleader",
            "faction newleader",
            "fac newleader",
            "team leader",
            "t leader",
            "f leader",
            "faction leader",
            "fac leader"
      },
      permission = ""
   )
   public static void teamLeader(Player sender, @Parameter(name = "player") UUID player) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
      if (team == null) {
         sender.sendMessage(ChatColor.RED + "You are not in a faction!");
      } else if (!team.isOwner(sender.getUniqueId())) {
         sender.sendMessage(ChatColor.DARK_AQUA + "Only the faction leader can do this.");
      } else if (!team.isMember(player)) {
         sender.sendMessage(ChatColor.YELLOW + UUIDUtils.name(player) + " is not on your faction.");
      } else {
         team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player) + " has been given ownership of " + team.getName() + ".");
         team.setOwner(player);
         team.addCaptain(sender.getUniqueId());
      }
   }
}
