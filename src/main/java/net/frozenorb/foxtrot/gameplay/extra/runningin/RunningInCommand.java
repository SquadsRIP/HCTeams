package net.frozenorb.foxtrot.gameplay.extra.runningin;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;

public class RunningInCommand {
   public static void execute(Player player) {
      if (Foxtrot.getInstance().getServerHandler().isTeams()) {
         player.sendMessage(ChatColor.RED + "This feature is disabled on " + Foxtrot.getInstance().getServerHandler().getServerName() + ".");
      } else if (player.getWorld().getEnvironment() != Environment.NORMAL) {
         player.sendMessage(ChatColor.RED + "You must be in the overworld to run this command!");
      } else {
         List<Team> teams = Foxtrot.getInstance()
            .getTeamHandler()
            .getTeams()
            .stream()
            .filter(
               it -> it.getOnlineMemberAmount() != 0
                  && !it.isRaidable()
                  && it.getHQ() != null
                  && it.getHQ().getWorld().getName().equalsIgnoreCase(player.getWorld().getName())
            )
            .collect(Collectors.toList());
         if (teams.isEmpty()) {
            player.sendMessage(ChatColor.RED + "There are no teams available!");
         } else {
            Team team = teams.stream()
               .min(
                  Comparator.comparingDouble(Team::getDeathsTilRaidable)
                     .reversed()
                     .thenComparingInt(Team::getOnlineMemberAmount)
                     .reversed()
                     .thenComparingDouble(o -> player.getLocation().distance(o.getHQ()))
               )
               .orElse(null);
            Location hq = team.getHQ();
            FancyMessage topMessage = new FancyMessage();
            FancyMessage dtrMessage = new FancyMessage();
            FancyMessage playersOnlineMessage = new FancyMessage();
            FancyMessage distanceMessage = new FancyMessage();
            topMessage.text(ChatColor.translate("&cFaction: &f" + team.getName()))
               .tooltip(ChatColor.GREEN + "Click to focus this faction")
               .command("/f focus " + team.getName());
            distanceMessage.text(
                  ChatColor.translate(
                     "&cHome: &f" + hq.getBlockX() + ", " + hq.getBlockZ() + " &e[" + Math.ceil(hq.distance(player.getLocation())) + " blocks away]"
                  )
               )
               .tooltip(ChatColor.GREEN + "Click to focus this faction")
               .command("/f focus " + team.getName());
            playersOnlineMessage.text(ChatColor.translate("&cMembers Online: &f" + team.getOnlineMemberAmount()))
               .tooltip(ChatColor.GREEN + "Click to focus this faction")
               .command("/f focus " + team.getName());
            String dtrFormat = team.formatDTR();
            if (Foxtrot.getInstance().getDTRDisplayMap().isHearts(player.getUniqueId())) {
               int currentHearts = (int)Math.ceil(team.getDTR());
               dtrFormat = team.getDTRColor().toString() + currentHearts + "❤" + team.getDTRSuffix();
            }

            dtrMessage.text(ChatColor.translate("&cDeaths Until Raidable: &f" + dtrFormat))
               .tooltip(ChatColor.GREEN + "Click to focus this faction")
               .command("/f focus " + team.getName());
            player.sendMessage("");
            topMessage.send(player);
            distanceMessage.send(player);
            playersOnlineMessage.send(player);
            dtrMessage.send(player);
            player.sendMessage("");
         }
      }
   }

   public static void runningInMenu(Player player) {
      if (Foxtrot.getInstance().getServerHandler().isTeams()) {
         player.sendMessage(ChatColor.RED + "This feature is disabled on " + Foxtrot.getInstance().getServerHandler().getServerName() + ".");
      } else if (player.getWorld().getEnvironment() != Environment.NORMAL) {
         player.sendMessage(ChatColor.RED + "You must be in the overworld to run this command!");
      } else {
         List<Team> teams = Foxtrot.getInstance()
            .getTeamHandler()
            .getTeams()
            .stream()
            .filter(
               it -> it.getOnlineMemberAmount() != 0
                  && !it.isRaidable()
                  && it.getHQ() != null
                  && it.getHQ().getWorld().getName().equalsIgnoreCase(player.getWorld().getName())
            )
            .collect(Collectors.toList());
         if (teams.isEmpty()) {
            player.sendMessage(ChatColor.RED + "There are no teams available!");
         } else {
            List<Team> sortedTeams = teams.stream()
               .sorted(
                  Comparator.comparingInt(Team::getDeathsTilRaidable)
                     .reversed()
                     .thenComparingInt(Team::getOnlineMemberAmount)
                     .reversed()
                     .thenComparingDouble(o -> player.getLocation().distance(o.getHQ()))
               )
               .collect(Collectors.toList());
            new RunningInMenu(sortedTeams).openMenu(player);
         }
      }
   }
}
