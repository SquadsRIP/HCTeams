package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class NetherPortalListener implements Listener {
   @EventHandler
   public void onPlayerPortal(PlayerPortalEvent event) {
      Player player = event.getPlayer();
      if (event.getCause() == TeleportCause.NETHER_PORTAL) {
         Location to = event.getTo();
         if (DTRBitmask.ROAD.appliesAt(to)) {
            Team team = LandBoard.getInstance().getTeam(to);
            if (team.getName().contains("North")) {
               to.add(20.0, 0.0, 0.0);
            } else if (team.getName().contains("South")) {
               to.subtract(20.0, 0.0, 0.0);
            } else if (team.getName().contains("East")) {
               to.add(0.0, 0.0, 20.0);
            } else if (team.getName().contains("West")) {
               to.subtract(0.0, 0.0, 20.0);
            }
         }

         event.setTo(to);
      }
   }
}
