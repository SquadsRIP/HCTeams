package net.frozenorb.foxtrot.gameplay.events.mini.type.kitmap;

import java.util.Arrays;
import java.util.List;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import net.frozenorb.foxtrot.gameplay.events.events.EventCapturedEvent;
import net.frozenorb.foxtrot.gameplay.events.mini.MiniEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class King extends MiniEvent {
   @Override
   public String getObjective() {
      return "King";
   }

   @Override
   public List<String> getDescription() {
      return Arrays.asList("Capture the most KOTHs", "within the next hour!");
   }

   @Override
   public String getEventID() {
      return "King";
   }

   @Override
   public int getSeconds() {
      return 7200;
   }

   @EventHandler(priority = EventPriority.MONITOR)
   private void onDeath(EventCapturedEvent event) {
      if (!event.getEvent().isHidden()) {
         if (event.getEvent().getType().equals(EventType.KOTH)) {
            this.addProgress(event.getPlayer(), true);
         }
      }
   }
}
