package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.menu.BlueprintMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BlueprintsCommand {
   @Command(names = {"blueprint", "blueprints"}, permission = "")
   public static void execute(Player player) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
      if (team == null) {
         player.sendMessage(ChatColor.GRAY + "You are not on a team!");
      } else if (!team.getOwner().equals(player.getUniqueId())) {
         player.sendMessage(ChatColor.YELLOW + "You must be the leader of the team!");
      } else {
         new BlueprintMenu().openMenu(player);
      }
   }
}
