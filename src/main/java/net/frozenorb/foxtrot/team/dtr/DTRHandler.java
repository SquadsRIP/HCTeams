package net.frozenorb.foxtrot.team.dtr;

import cc.fyre.modsuite.mod.ModHandler;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.event.TeamRegenerateEvent;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DTRHandler extends BukkitRunnable {
   private static double[] BASE_DTR_INCREMENT = new double[]{
      1.5,
      0.5,
      0.45,
      0.4,
      0.36,
      0.33,
      0.3,
      0.27,
      0.24,
      0.22,
      0.21,
      0.2,
      0.19,
      0.18,
      0.175,
      0.17,
      0.168,
      0.166,
      0.164,
      0.162,
      0.16,
      0.158,
      0.156,
      0.154,
      0.152,
      0.15,
      0.148,
      0.146,
      0.144,
      0.142,
      0.142,
      0.142,
      0.142,
      0.142,
      0.142,
      0.142,
      0.142,
      0.142,
      0.142,
      0.142
   };
   private static double[] MAX_DTR = new double[]{
      1.01,
      2.5,
      3.5,
      4.5,
      5.5,
      5.5,
      5.75,
      5.75,
      6.25,
      6.25,
      6.5,
      6.5,
      6.5,
      6.5,
      6.5,
      6.5,
      6.5,
      6.5,
      6.5,
      6.25,
      6.25,
      6.25,
      6.25,
      6.5,
      6.5,
      6.5,
      6.5,
      6.75,
      6.75,
      7.0,
      7.0,
      7.0,
      7.0,
      7.0,
      9.0,
      9.0,
      9.0,
      9.0,
      9.0
   };
   public static Set<ObjectId> wasOnCooldown = new HashSet<>();

   public static double getBaseDTRIncrement(int teamsize) {
      return teamsize == 0 ? 0.0 : BASE_DTR_INCREMENT[teamsize - 1] * Foxtrot.getInstance().getMapHandler().getDtrIncrementMultiplier();
   }

   public static double getMaxDTR(int teamsize) {
      return teamsize == 0 ? 100.0 : MAX_DTR[teamsize - 1];
   }

   public static boolean isOnCooldown(Team team) {
      return team.getDTRCooldown() > System.currentTimeMillis();
   }

   public static boolean isRegenerating(Team team) {
      return !isOnCooldown(team) && team.getDTR() != team.getMaxDTR();
   }

   @Override
   public void run() {
      Map<Team, Integer> playerOnlineMap = new HashMap<>();

      for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
         if (!ModHandler.INSTANCE.isInVanish(player.getUniqueId())) {
            Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player);
            if (playerTeam != null && playerTeam.getOwner() != null) {
               playerOnlineMap.put(playerTeam, playerOnlineMap.getOrDefault(playerTeam, 0) + 1);
            }
         }
      }

      playerOnlineMap.forEach((team, onlineCount) -> {
         try {
            if (isOnCooldown(team)) {
               wasOnCooldown.add(team.getUniqueId());
               return;
            }

            if (team.getDTR() == team.getMaxDTR()) {
               return;
            }

            if (wasOnCooldown.remove(team.getUniqueId())) {
               team.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "Your team is now regenerating DTR!");
            }

            if (Foxtrot.getInstance().getServerHandler().isTeams()) {
               team.setDTR(clampRegeneratedDtr(team.getDTR() + team.getDTRIncrement(onlineCount), team.getMaxDTR()));
            } else {
               boolean wasRaidable = team.isRaidable();
               double oldDTR = team.getDTR();
               team.setDTR(team.getMaxDTR());
               TeamRegenerateEvent teamRegenerateEvent = new TeamRegenerateEvent(team, oldDTR, team.getDTR(), wasRaidable);
               Foxtrot.getInstance().getServer().getPluginManager().callEvent(teamRegenerateEvent);
            }
         } catch (Exception ex) {
            Foxtrot.getInstance().getLogger().warning("Error regenerating DTR for team " + team.getName() + ".");
            ex.printStackTrace();
         }
      });
   }

   private static double clampRegeneratedDtr(double var0, double var2) {
      return var0 >= var2 - 1.0E-9 ? var2 : Math.min(var0, var2);
   }
}
