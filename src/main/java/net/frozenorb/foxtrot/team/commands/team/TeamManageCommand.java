package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.menu.manage.ManageMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamManageCommand {
   @Command(names = {"team manage", "t manage", "f manage", "fac manage", "faction manage"}, permission = "")
   public static void execute(Player player) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
      if (team == null) {
         player.sendMessage(ChatColor.RED + "You are not in a faction!");
      } else {
         new ManageMenu(team).openMenu(player);
      }
   }
}
