package net.frozenorb.foxtrot.gameplay.cavesays.type;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.foxtrot.gameplay.cavesays.Task;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.team.event.TeamEnterClaimEvent;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class GoKoth extends Task {
   private Map<UUID, List<Team>> enteredClaims = new HashMap<>();

   @Override
   public String getTaskDisplayName() {
      return "Visit all Overworld KOTHs";
   }

   @Override
   public String getTaskID() {
      return "GoKOTH";
   }

   @Override
   public int getPointsToWin() {
      return 3;
   }

   @EventHandler
   private void onClaimEnter(TeamEnterClaimEvent event) {
      Player player = event.getPlayer();
      Team toTeam = event.getToTeam();
      if (DTRBitmask.KOTH.appliesAt(event.getTo())) {
         if (player.getWorld().getEnvironment() != Environment.NORMAL) {
            player.sendMessage(ChatColor.YELLOW + "You can only visit a Overworld KOTH for this task!");
         } else {
            List teamsVisited = this.enteredClaims.getOrDefault(player.getUniqueId(), Collections.emptyList());
            if (teamsVisited.contains(toTeam)) {
               player.sendMessage(ChatColor.YELLOW + "You've already visited this KOTH!");
            } else {
               teamsVisited.add(toTeam);
               this.enteredClaims.put(player.getUniqueId(), teamsVisited);
               this.addProgress(player);
            }
         }
      }
   }

   public Map<UUID, List<Team>> getEnteredClaims() {
      return this.enteredClaims;
   }
}
