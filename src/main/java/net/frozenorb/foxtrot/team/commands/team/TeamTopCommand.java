package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TeamTopCommand {
   @Command(names = {"team top", "t top", "f top", "faction top", "fac top"}, permission = "")
   public static void teamList(final CommandSender sender) {
      (new BukkitRunnable() {
            @Override
            public void run() {
               LinkedHashMap<Team, Integer> sortedTeamPlayerCount = TeamTopCommand.getSortedPointTeams();
               int index = 0;
               sender.sendMessage(Team.DARK_GRAY_LINE);
               sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Top Factions Points");
               sender.sendMessage(" ");

               for (Entry<Team, Integer> teamEntry : sortedTeamPlayerCount.entrySet()) {
                  if (teamEntry.getKey().getOwner() != null) {
                     if (11 <= ++index) {
                        break;
                     }

                     FancyMessage teamMessage = new FancyMessage();
                     Team team = teamEntry.getKey();
                     teamMessage.text(index + ". ").color(ChatColor.DARK_RED).then();
                     teamMessage.text(teamEntry.getKey().getName())
                        .color(sender instanceof Player && teamEntry.getKey().isMember(((Player)sender).getUniqueId()) ? ChatColor.GREEN : ChatColor.WHITE)
                        .tooltip(
                           (sender instanceof Player && teamEntry.getKey().isMember(((Player)sender).getUniqueId()) ? ChatColor.GREEN : ChatColor.WHITE)
                                 .toString()
                              + teamEntry.getKey().getName()
                              + "\n"
                              + ChatColor.DARK_RED
                              + "Leader: "
                              + ChatColor.GRAY
                              + Proton.getInstance().getUuidCache().name(teamEntry.getKey().getOwner())
                              + "\n\n"
                              + ChatColor.DARK_RED
                              + "Balance: "
                              + ChatColor.DARK_GREEN
                              + "$"
                              + ChatColor.GREEN
                              + (int)Math.round(team.getBalance())
                              + "\n"
                              + ChatColor.DARK_RED
                              + "Kills: "
                              + ChatColor.GRAY
                              + team.getKills()
                              + "\n"
                              + ChatColor.DARK_RED
                              + "Deaths: "
                              + ChatColor.GRAY
                              + team.getDeaths()
                              + "\n\n"
                              + ChatColor.DARK_RED
                              + "KOTH Captures: "
                              + ChatColor.GRAY
                              + team.getKothCaptures()
                              + "\n"
                              + ChatColor.DARK_RED
                              + "Citadel Captures: "
                              + ChatColor.GRAY
                              + team.getCitadelsCapped()
                              + "\n"
                              + ChatColor.DARK_RED
                              + "Conquest Captures: "
                              + ChatColor.GRAY
                              + team.getConquestsCapped()
                              + "\n\n"
                              + ChatColor.GREEN
                              + "Click to view faction info"
                        )
                        .command("/t who " + teamEntry.getKey().getName())
                        .then();
                     teamMessage.text(" - ").color(ChatColor.WHITE).then();
                     teamMessage.text(teamEntry.getValue().toString()).color(ChatColor.DARK_RED);
                     teamMessage.send(sender);
                  }
               }

               sender.sendMessage(Team.DARK_GRAY_LINE);
            }
         })
         .runTaskAsynchronously(Foxtrot.getInstance());
   }

   @Command(names = {"team top trapping", "t top trapping", "f top trapping", "faction top trapping", "fac top trapping"}, permission = "")
   public static void teamTrapping(CommandSender sender) {
      sender.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   @Command(names = {"team top raidable", "t top raidable", "f top raidable", "faction top raidable", "fac top raidable"}, permission = "")
   public static void teamRaidable(CommandSender sender) {
      sender.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   public static LinkedHashMap<Team, Integer> getSortedPointTeams() {
      HashMap<Team, Integer> teamPointsCount = new HashMap<>();

      for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
         teamPointsCount.put(team, team.recalculatePoints());
      }

      return sortByValues(teamPointsCount);
   }

   public static LinkedHashMap<Team, Integer> getSortedRaidableTeams() {
      HashMap<Team, Integer> teamRaidableCount = new HashMap<>();

      for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
         teamRaidableCount.put(team, team.getFactionsMadeRaidable());
      }

      return sortByValues(teamRaidableCount);
   }

   public static LinkedHashMap<Team, Integer> getSortedTrappingPoints() {
      HashMap<Team, Integer> teamRaidableCount = new HashMap<>();

      for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
         teamRaidableCount.put(team, team.getTrappingPoints());
      }

      return sortByValues(teamRaidableCount);
   }

   public static LinkedHashMap<Team, Integer> sortByValues(Map<Team, Integer> map) {
      LinkedList<Entry<Team, Integer>> list = new LinkedList<>(map.entrySet());
      Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
      LinkedHashMap<Team, Integer> sortedHashMap = new LinkedHashMap<>();

      for (Entry entry : list) {
         sortedHashMap.put((Team)entry.getKey(), (Integer)entry.getValue());
      }

      return sortedHashMap;
   }
}
