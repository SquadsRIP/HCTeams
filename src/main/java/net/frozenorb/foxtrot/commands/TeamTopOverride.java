package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import java.util.ArrayList;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class TeamTopOverride {
   @Command(names = {"team top", "t top", "f top", "faction top", "fac top"}, permission = "")
   public static void teamTop(CommandSender sender, @Parameter(name = "page", defaultValue = "1") int page) {
      ArrayList<Team> teams = new ArrayList<>();

      for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
         if (team.getOwner() != null) {
            teams.add(team);
         }
      }

      teams.sort((a, b) -> Integer.compare(b.recalculatePoints(), a.recalculatePoints()));
      int perPage = 10;
      int maxPage = (int)Math.ceil((double)teams.size() / perPage);
      if (page < 1) {
         page = 1;
      }

      if (page > maxPage) {
         page = maxPage;
      }

      int start = (page - 1) * perPage;
      int end = Math.min(start + perPage, teams.size());
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&m--------------------"));
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Top Factions &7(Page " + page + "/" + maxPage + ")"));
      sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&m--------------------"));

      for (int i = start; i < end; i++) {
         Team team = teams.get(i);
         int num = i + 1;
         int points = team.recalculatePoints();
         sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4" + num + ". &f" + team.getName() + " &4" + points));
      }
   }
}
