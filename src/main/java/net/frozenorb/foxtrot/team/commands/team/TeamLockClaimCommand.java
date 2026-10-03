package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeamLockClaimCommand {
   @Command(names = {"team lockclaim", "t lockclaim", "f lockclaim", "faction lockclaim", "fac lockclaim"}, permission = "")
   public static void teamLockClaim(Player sender) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
      if (team == null) {
         sender.sendMessage(ChatColor.RED + "You are not in a faction!");
      } else if (!CustomTimerCreateCommand.isSOTWTimer()) {
         sender.sendMessage(ChatColor.YELLOW + "This command can only be used during SOTW.");
      } else if (CustomTimerCreateCommand.remainingSOTWTime() <= TimeUnit.MINUTES.toMillis(10L)) {
         sender.sendMessage(ChatColor.YELLOW + "This command cannot be used this late into SOTW.");
      } else if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
         sender.sendMessage(ChatColor.DARK_AQUA + "Only faction co-leaders (and above) can do this.");
      } else if (team.isClaimLocked()) {
         sender.sendMessage(ChatColor.YELLOW + "Claim is already locked.");
      } else {
         for (Claim claim : team.getClaims()) {
            for (Player player : claim.getPlayers()) {
               if (!team.isMember(player.getUniqueId())) {
                  Location safeLocation = TeamStuckCommand.nearestSafeLocation(player.getLocation());
                  player.teleport(safeLocation);
                  player.sendMessage(ChatColor.YELLOW + "You have been teleported out because that claim was locked!");
               }
            }
         }

         team.setClaimLocked(true);
         team.sendMessage(sender.getDisplayName() + CC.translate(" &elocked &eyour faction's claim! &f(Claims will &aunlock &f10 minutes before SOTW ends)"));
      }
   }

   @Command(names = {"team unlockclaim", "t unlockclaim", "f unlockclaim", "faction unlockclaim", "fac unlockclaim"}, permission = "")
   public static void teamUnLockClaim(Player sender) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
      if (team == null) {
         sender.sendMessage(ChatColor.GRAY + "You are not on a faction!");
      } else if (!CustomTimerCreateCommand.isSOTWTimer()) {
         sender.sendMessage(ChatColor.YELLOW + "This command can only be used during SOTW.");
      } else if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
         sender.sendMessage(ChatColor.DARK_AQUA + "Only faction co-leaders (and above) can do this.");
      } else if (!team.isClaimLocked()) {
         sender.sendMessage(ChatColor.YELLOW + "Claim is already unlocked.");
      } else {
         team.setClaimLocked(false);
         team.sendMessage(sender.getName() + CC.translate(" &aunlocked &eyour faction’s claim!"));
      }
   }
}
