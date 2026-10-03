package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.TimeUtils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import net.minecraft.util.com.google.common.collect.ImmutableMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TeamShadowMuteCommand {
   public static Map<UUID, String> teamShadowMutes = new HashMap<>();

   @Command(names = {"team shadowmute", "t shadowmute", "f shadowmute", "faction shadowmute", "fac shadowmute"}, permission = "foxtrot.mutefaction")
   public static void teamShadowMute(Player sender, @Parameter(name = "team") final Team team, @Parameter(name = "time") int time) {
      int timeSeconds = time * 60;

      for (UUID player : team.getMembers()) {
         teamShadowMutes.put(player, team.getName());
      }

      TeamActionTracker.logActionAsync(
         team,
         TeamActionType.TEAM_MUTE_CREATED,
         ImmutableMap.of("shadowMute", "true", "mutedById", sender.getUniqueId(), "mutedByName", sender.getName(), "duration", time)
      );
      (new BukkitRunnable() {
         @Override
         public void run() {
            TeamActionTracker.logActionAsync(team, TeamActionType.TEAM_MUTE_EXPIRED, ImmutableMap.of("shadowMute", "true"));
            Iterator<Entry<UUID, String>> mutesIterator = TeamShadowMuteCommand.teamShadowMutes.entrySet().iterator();

            while (mutesIterator.hasNext()) {
               Entry<UUID, String> mute = mutesIterator.next();
               if (mute.getValue().equalsIgnoreCase(team.getName())) {
                  mutesIterator.remove();
               }
            }
         }
      }).runTaskLater(Foxtrot.getInstance(), timeSeconds * 20L);
      sender.sendMessage(ChatColor.YELLOW + "Shadow muted the team " + team.getName() + ChatColor.GRAY + " for " + TimeUtils.formatIntoMMSS(timeSeconds) + ".");
   }

   public static Map<UUID, String> getTeamShadowMutes() {
      return teamShadowMutes;
   }
}
