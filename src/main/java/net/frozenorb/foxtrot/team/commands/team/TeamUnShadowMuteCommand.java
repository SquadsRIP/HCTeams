package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import java.util.Iterator;
import java.util.UUID;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import net.minecraft.util.com.google.common.collect.ImmutableMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamUnShadowMuteCommand {
   @Command(names = {"team unshadowmute", "t unshadowmute", "f unshadowmute", "faction unshadowmute", "fac unshadowmute"}, permission = "foxtrot.mutefaction")
   public static void teamUnShadowMute(Player sender, @Parameter(name = "team") Team team) {
      TeamActionTracker.logActionAsync(team, TeamActionType.TEAM_MUTE_EXPIRED, ImmutableMap.of("shadowMute", "true"));
      Iterator<Entry<UUID, String>> mutesIterator = TeamShadowMuteCommand.getTeamShadowMutes().entrySet().iterator();

      while (mutesIterator.hasNext()) {
         Entry<UUID, String> mute = mutesIterator.next();
         if (mute.getValue().equalsIgnoreCase(team.getName())) {
            mutesIterator.remove();
         }
      }

      sender.sendMessage(ChatColor.GRAY + "Un-shadowmuted the team " + team.getName() + ChatColor.GRAY + ".");
   }
}
