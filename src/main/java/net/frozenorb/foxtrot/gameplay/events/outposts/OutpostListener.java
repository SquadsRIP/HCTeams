package net.frozenorb.foxtrot.gameplay.events.outposts;

import cc.fyre.proton.event.HourEvent;
import java.beans.ConstructorProperties;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.outposts.data.Outpost;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class OutpostListener implements Listener {
   private final OutpostHandler outpostHandler;

   @EventHandler
   private void onHour(HourEvent event) {
   }

   @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
   public void onMove(PlayerMoveEvent event) {
      Location to = event.getTo();
      Location from = event.getFrom();
      if (to.getBlockX() != from.getBlockX() || to.getBlockY() != from.getBlockY() || to.getBlockZ() != from.getBlockZ()) {
         Player player = event.getPlayer();
         Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
         if (team != null) {
            Outpost outpostTo = this.outpostHandler.findOutpost(to);
            Outpost outpostFrom = this.outpostHandler.findOutpost(from);
            if (outpostFrom != null && outpostFrom != outpostTo && outpostFrom.removePlayer(player)) {
               outpostFrom.updateAttackers();
            } else if (!this.outpostHandler.findControllingOutposts(team).contains(outpostTo)) {
               if (outpostTo != null && outpostTo.addPlayer(player)) {
                  outpostTo.updateAttackers();
               }
            }
         }
      }
   }

   @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
   public void onTeleport(PlayerTeleportEvent event) {
      Outpost outpostTo = this.outpostHandler.findOutpost(event.getTo());
      Outpost outpostFrom = this.outpostHandler.findOutpost(event.getFrom());
      if (outpostFrom != null && outpostFrom != outpostTo) {
         Player player = event.getPlayer();
         if (outpostFrom.removePlayer(player)) {
            outpostFrom.updateAttackers();
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   public void onQuit(PlayerQuitEvent event) {
      Player player = event.getPlayer();
      Outpost outpost = this.outpostHandler.findOutpost(player.getLocation());
      if (outpost != null) {
         if (outpost.removePlayer(player)) {
            outpost.updateAttackers();
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onDeath(PlayerDeathEvent event) {
      Player player = event.getEntity();
      Outpost outpostZone = this.outpostHandler.findOutpost(player.getLocation());
      if (outpostZone != null && outpostZone.removePlayer(player)) {
         outpostZone.updateAttackers();
      }
   }

   @ConstructorProperties("outpostHandler")
   public OutpostListener(OutpostHandler outpostHandler) {
      this.outpostHandler = outpostHandler;
   }
}
