package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import java.util.UUID;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.event.FullTeamBypassEvent;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import net.minecraft.util.com.google.common.collect.ImmutableMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamInviteCommand {
   @Command(
      names = {"team invite", "t invite", "f invite", "faction invite", "fac invite", "team inv", "t inv", "f inv", "faction inv", "fac inv"},
      permission = ""
   )
   public static void teamInvite(
      Player sender, @Parameter(name = "player") UUID player, @Parameter(name = "override?", defaultValue = "something-not-override") String override
   ) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
      if (team == null) {
         sender.sendMessage(ChatColor.GRAY + "You are not in a faction!");
      } else {
         if (team.getMembers().size() >= Foxtrot.getInstance().getMapHandler().getTeamSize()) {
            FullTeamBypassEvent bypassEvent = new FullTeamBypassEvent(sender, team, false);
            Foxtrot.getInstance().getServer().getPluginManager().callEvent(bypassEvent);
            if (!bypassEvent.isAllowBypass()) {
               sender.sendMessage(
                  ChatColor.RED
                     + "The max faction size is "
                     + Foxtrot.getInstance().getMapHandler().getTeamSize()
                     + (bypassEvent.getExtraSlots() == 0 ? "" : " (+" + bypassEvent.getExtraSlots() + ")")
                     + "!"
               );
               return;
            }
         }

         if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId()) && !team.isCaptain(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only faction captains can do this.");
         } else if (team.isMember(player)) {
            sender.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player) + " is already on your faction.");
         } else if (team.getInvitations().contains(player)) {
            sender.sendMessage(ChatColor.RED + "That player has already been invited.");
         } else {
            if (Foxtrot.getInstance().getServerHandler().isForceInvitesEnabled() && !Foxtrot.getInstance().getServerHandler().isPreEOTW()) {
               int possibleTeamSize = team.getSize() + team.getInvitations().size();
               if (!Foxtrot.getInstance().getMapHandler().isKitMap()
                  && team.getHistoricalMembers().contains(player)
                  && possibleTeamSize > Foxtrot.getInstance().getMapHandler().getMinForceInviteMembers()) {
                  sender.sendMessage(
                     ChatColor.RED
                        + "This player has previously joined your faction. You must use a force-invite to re-invite "
                        + UUIDUtils.name(player)
                        + ". Type "
                        + ChatColor.YELLOW
                        + "'/f forceinvite "
                        + UUIDUtils.name(player)
                        + "'"
                        + ChatColor.RED
                        + " to use a force-invite."
                  );
                  return;
               }
            }

            TeamActionTracker.logActionAsync(
               team,
               TeamActionType.PLAYER_INVITE_SENT,
               ImmutableMap.of("playerId", player, "invitedById", sender.getUniqueId(), "invitedByName", sender.getName(), "usedForceInvite", "false")
            );
            team.getInvitations().add(player);
            team.flagForSave();
            Player bukkitPlayer = Foxtrot.getInstance().getServer().getPlayer(player);
            if (bukkitPlayer != null) {
               bukkitPlayer.sendMessage(
                  ChatColor.DARK_AQUA + sender.getName() + " invited you to join '" + ChatColor.YELLOW + team.getName() + ChatColor.DARK_AQUA + "'."
               );
               FancyMessage clickToJoin = new FancyMessage("Type '").color(ChatColor.DARK_AQUA).then("/f join " + team.getName()).color(ChatColor.YELLOW);
               clickToJoin.then("' or ").color(ChatColor.DARK_AQUA);
               clickToJoin.then("click here").color(ChatColor.AQUA).command("/f join " + team.getName()).tooltip("§aJoin " + team.getName());
               clickToJoin.then(" to join.").color(ChatColor.DARK_AQUA);
               clickToJoin.send(bukkitPlayer);
            }

            team.sendMessage(ChatColor.YELLOW + UUIDUtils.name(player) + " has been invited to the faction!");
         }
      }
   }
}
