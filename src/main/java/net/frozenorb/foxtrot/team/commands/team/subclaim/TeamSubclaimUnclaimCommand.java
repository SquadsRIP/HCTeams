package net.frozenorb.foxtrot.team.commands.team.subclaim;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamSubclaimUnclaimCommand {
   @Command(
      names = {
            "team subclaim unclaim",
            "t subclaim unclaim",
            "f subclaim unclaim",
            "faction subclaim unclaim",
            "fac subclaim unclaim",
            "team subclaim unsubclaim",
            "t subclaim unsubclaim",
            "f subclaim unsubclaim",
            "faction subclaim unsubclaim",
            "fac subclaim unsubclaim",
            "team unsubclaim",
            "t unsubclaim",
            "f unsubclaim",
            "faction unsubclaim",
            "fac unsubclaim"
      },
      permission = ""
   )
   public static void teamSubclaimUnclaim(Player sender, @Parameter(name = "subclaim", defaultValue = "location") Subclaim subclaim) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
      if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId()) && !team.isCaptain(sender.getUniqueId())) {
         sender.sendMessage(ChatColor.YELLOW + "Only team captains can unclaim subclaims!");
      } else {
         team.getSubclaims().remove(subclaim);
         LandBoard.getInstance().updateSubclaim(subclaim);
         team.flagForSave();
         sender.sendMessage(ChatColor.YELLOW + "You have unclaimed the subclaim " + ChatColor.YELLOW + subclaim.getName() + ChatColor.YELLOW + ".");
      }
   }
}
