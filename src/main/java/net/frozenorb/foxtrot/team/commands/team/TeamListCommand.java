package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.modsuite.mod.ModHandler;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TeamListCommand {
   @Command(names = {"team list", "t list", "f list", "faction list", "fac list"}, permission = "")
   public static void teamList(final Player sender, @Parameter(name = "page", defaultValue = "1") final int page) {
      (new BukkitRunnable() {
            @Override
            public void run() {
               if (page < 1) {
                  sender.sendMessage(ChatColor.YELLOW + "You cannot view a page less than 1");
               } else {
                  HashMap<Team, Integer> teamPlayerCount = new HashMap<>();

                  for (Player player : TeamListCommand.getOnlinePlayersList()) {
                     Team playerTeam;
                     if (!ModHandler.INSTANCE.isInVanish(player.getUniqueId()) && (playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player)) != null
                        )
                      {
                        if (teamPlayerCount.containsKey(playerTeam)) {
                           teamPlayerCount.put(playerTeam, teamPlayerCount.get(playerTeam) + 1);
                        } else {
                           teamPlayerCount.put(playerTeam, 1);
                        }
                     }
                  }

                  int maxPages = teamPlayerCount.size() / 10 + 1;
                  int currentPage = Math.min(page, maxPages);
                  LinkedHashMap<Team, Integer> sortedTeamPlayerCount = TeamListCommand.sortByValues(teamPlayerCount);
                  int start = (currentPage - 1) * 10;
                  int index = 0;
                  sender.sendMessage(Team.GRAY_LINE);
                  sender.sendMessage(
                     ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Faction List " + ChatColor.GRAY + "(Page " + currentPage + "/" + maxPages + ")"
                  );

                  for (Entry<Team, Integer> teamEntry : sortedTeamPlayerCount.entrySet()) {
                     if (++index >= start) {
                        if (index > start + 10) {
                           break;
                        }

                        FancyMessage teamMessage = new FancyMessage();
                        teamMessage.text(index + ". ").color(ChatColor.DARK_RED).then();
                        teamMessage.text(teamEntry.getKey().getName())
                           .color(ChatColor.WHITE)
                           .tooltip(ChatColor.YELLOW + "DTR: " + teamEntry.getKey().formatDTR() + "\n" + ChatColor.GREEN + "Click to view faction info")
                           .command("/f who " + teamEntry.getKey().getName())
                           .then();
                        teamMessage.text(" (" + teamEntry.getValue() + "/" + teamEntry.getKey().getSize() + ")").color(ChatColor.DARK_RED);
                        teamMessage.send(sender);
                     }
                  }

                  sender.sendMessage(ChatColor.GRAY + "You are currently on " + ChatColor.WHITE + "Page " + currentPage + "/" + maxPages + ChatColor.GRAY + ".");
                  sender.sendMessage(ChatColor.GRAY + "To view other pages, use " + ChatColor.DARK_RED + "/f list <page#>" + ChatColor.GRAY + ".");
                  sender.sendMessage(Team.GRAY_LINE);
               }
            }
         })
         .runTaskAsynchronously(Foxtrot.getInstance());
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

   private static List<Player> getOnlinePlayersList() {
      try {
         Method m = Bukkit.class.getMethod("getOnlinePlayers");
         Object result = m.invoke(null);
         if (result instanceof Collection) {
            return new ArrayList<>((Collection<? extends Player>)result);
         }

         if (result instanceof Player[]) {
            return Arrays.asList((Player[])result);
         }
      } catch (Exception var2) {
      }

      return new ArrayList<>();
   }
}
