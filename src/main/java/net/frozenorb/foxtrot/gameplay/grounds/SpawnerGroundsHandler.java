package net.frozenorb.foxtrot.gameplay.grounds;

import cc.fyre.proton.event.HourEvent;
import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class SpawnerGroundsHandler implements Listener {
   private Foxtrot instance;
   private List<GroundsArea> areas = new ArrayList<>();

   public SpawnerGroundsHandler(Foxtrot instance) {
      this.instance = instance;
   }

   public GroundsArea findArea(String id) {
      return this.areas.stream().filter(it -> it.getGroundsID().equalsIgnoreCase(id)).findFirst().orElse(null);
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onHour(HourEvent event) {
   }

   public void resetAll() {
   }

   public List<GroundsArea> getAreas() {
      return this.areas;
   }
}
