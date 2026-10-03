package net.frozenorb.foxtrot.gameplay.events.mini.type;

import java.util.Arrays;
import java.util.List;
import net.frozenorb.foxtrot.gameplay.events.mini.MiniEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

public class Traveller extends MiniEvent {
   @Override
   public String getObjective() {
      return "Traveller";
   }

   @Override
   public List<String> getDescription() {
      return Arrays.asList("Travel the furthest", "distance within 20 minutes!");
   }

   @Override
   public String getEventID() {
      return "Traveller";
   }

   @Override
   public int getSeconds() {
      return 1200;
   }

   @EventHandler(priority = EventPriority.MONITOR)
   private void onMove(PlayerMoveEvent event) {
      if (!event.isCancelled() && (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockZ() != event.getTo().getBlockZ())) {
         if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("Deathban")) {
            this.addProgress(event.getPlayer(), false);
         }
      }
   }
}
