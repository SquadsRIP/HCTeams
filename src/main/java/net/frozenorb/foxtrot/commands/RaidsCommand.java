package net.frozenorb.foxtrot.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.extra.runningin.RunningInMenu;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.entity.Player;

public class RaidsCommand {
   public static void raids(Player player) {
      ArrayList<Team> teams = new ArrayList<>(Foxtrot.getInstance().getTeamHandler().getTeams());
      List<Team> sortedTeams = teams.stream().filter(it -> it.isRaidable() && it.getHQ() != null).collect(Collectors.toList());
      new RunningInMenu(sortedTeams).openMenu(player);
   }
}
