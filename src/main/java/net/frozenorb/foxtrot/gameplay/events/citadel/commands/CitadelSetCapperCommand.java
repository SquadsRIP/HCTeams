package net.frozenorb.foxtrot.gameplay.events.citadel.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import java.util.ArrayList;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.citadel.CitadelHandler;
import net.frozenorb.foxtrot.team.Team;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CitadelSetCapperCommand {
   @Command(names = "citadel setcapper", permission = "op")
   public static void citadelSetCapper(Player sender, @Parameter(name = "cappers") String cappers) {
      if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
         sender.sendMessage(ChatColor.YELLOW + "This command may only be used on Kitmap!");
      } else {
         if (cappers.equals("null")) {
            Foxtrot.getInstance().getCitadelHandler().resetCappers();
            sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Reset Citadel cappers.");
         } else {
            String[] teamNames = cappers.split(",");
            ArrayList<ObjectId> teams = new ArrayList<>();

            for (String teamName : teamNames) {
               Team team = Foxtrot.getInstance().getTeamHandler().getTeam(teamName);
               if (team == null) {
                  sender.sendMessage(ChatColor.YELLOW + "Team '" + teamName + "' cannot be found.");
                  return;
               }

               teams.add(team.getUniqueId());
            }

            Foxtrot.getInstance().getCitadelHandler().getCappers().clear();
            Foxtrot.getInstance().getCitadelHandler().getCappers().addAll(teams);
            sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Updated Citadel cappers.");
         }
      }
   }
}
