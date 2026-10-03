package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeamRallyCommand {
   @Command(
      names = {"team rally", "t r", "t rally", "f rally", "f r", "f tl", "t tl", "faction rally", "faction tl", "faction r", "team tl", "team r"},
      permission = ""
   )
   public static void teamRally(Player sender) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
      if (team == null) {
         sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
      } else {
         Location location = sender.getLocation();
         if (team.getRallyWaypoint() != null) {
            team.getOnlineMembers().forEach(it -> LunarClientAPI.getInstance().removeWaypoint(it, team.getRallyWaypoint()));
         }

         team.setRallyPoint(location);
         team.setRallyWaypoint(new LCWaypoint("Rally", team.getRallyPoint(), Color.ORANGE.asRGB(), true, true));

         for (Player onlineMember : team.getOnlineMembers()) {
            LunarClientAPI.getInstance().sendWaypoint(onlineMember, team.getRallyWaypoint());
         }

         team.sendMessage(
            ChatColor.translateAlternateColorCodes('&', "&f" + sender.getName() + " &6has updated the team's rally point! This will last for 10 minutes")
         );
      }
   }

   @Command(names = "team unrally", permission = "")
   public static void teamUnRally(Player sender) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
      if (team == null) {
         sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
      } else {
         Location location = sender.getLocation();
         if (team.getRallyWaypoint() != null) {
            team.getOnlineMembers().forEach(it -> LunarClientAPI.getInstance().removeWaypoint(it, team.getRallyWaypoint()));
         }

         team.setRallyPoint(location);
         team.sendMessage(
            ChatColor.translateAlternateColorCodes('&', "&f" + sender.getName() + " &6has updated the team's rally point! This will last for 10 minutes")
         );
      }
   }
}
