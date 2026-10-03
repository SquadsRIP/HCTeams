package net.frozenorb.foxtrot.team.commands.team.subclaim;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamSubclaimListCommand {
   @Command(
      names = {
            "team subclaim list",
            "t subclaim list",
            "f subclaim list",
            "faction subclaim list",
            "fac subclaim list",
            "team sub list",
            "t sub list",
            "f sub list",
            "faction sub list",
            "fac sub list"
      },
      permission = ""
   )
   public static void teamSubclaimList(Player sender) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
      if (team == null) {
         sender.sendMessage(ChatColor.YELLOW + "You must be on a team to execute this command!");
      } else {
         StringBuilder access = new StringBuilder();
         StringBuilder other = new StringBuilder();

         for (Subclaim subclaim : team.getSubclaims()) {
            if (!subclaim.isMember(sender.getUniqueId())
               && !team.isOwner(sender.getUniqueId())
               && !team.isCoLeader(sender.getUniqueId())
               && !team.isCaptain(sender.getUniqueId())) {
               other.append(subclaim).append(", ");
            } else {
               access.append(subclaim.getName()).append(", ");
            }
         }

         if (access.length() > 2) {
            access.setLength(access.length() - 2);
         }

         if (other.length() > 2) {
            other.setLength(other.length() - 2);
         }

         sender.sendMessage(ChatColor.BLUE + team.getName() + ChatColor.YELLOW + " Subclaim List");
         sender.sendMessage(ChatColor.YELLOW + "Subclaims you can access: " + ChatColor.WHITE + access.toString());
         sender.sendMessage(ChatColor.YELLOW + "Other Subclaims: " + ChatColor.WHITE + other.toString());
      }
   }
}
