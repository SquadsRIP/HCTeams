package net.frozenorb.foxtrot.gameplay.events.citadel.commands;

import cc.fyre.proton.command.Command;
import java.util.HashSet;
import java.util.Set;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.citadel.CitadelHandler;
import net.frozenorb.foxtrot.team.Team;
import net.minecraft.util.com.google.common.base.Joiner;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CitadelCommand {
   @Command(names = "citadel", permission = "")
   public static void citadel(Player sender) {
      if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
         sender.sendMessage(ChatColor.YELLOW + "This command may only be used on Kitmap!");
      } else {
         Set<ObjectId> cappers = Foxtrot.getInstance().getCitadelHandler().getCappers();
         HashSet<String> capperNames = new HashSet<>();

         for (ObjectId capper : cappers) {
            Team capperTeam = Foxtrot.getInstance().getTeamHandler().getTeam(capper);
            if (capperTeam != null) {
               capperNames.add(capperTeam.getName());
            }
         }

         if (!capperNames.isEmpty()) {
            sender.sendMessage(
               CitadelHandler.PREFIX
                  + " "
                  + ChatColor.YELLOW
                  + "Citadel was captured by "
                  + ChatColor.GREEN
                  + Joiner.on(", ").join(capperNames)
                  + ChatColor.YELLOW
                  + "."
            );
         } else {
            Event citadel = Foxtrot.getInstance().getEventHandler().getEvent("Citadel");
            if (citadel != null && citadel.isActive()) {
               sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Citadel can be captured now.");
            } else {
               sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Citadel was not captured last week.");
            }
         }
      }
   }
}
