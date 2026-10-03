package net.frozenorb.foxtrot.gameplay.events.mini.type.kitmap;

import java.util.Arrays;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.mini.MiniEvent;
import org.bukkit.entity.Player;

public class Shooter extends MiniEvent {
   @Override
   public String getObjective() {
      return "Shooter";
   }

   @Override
   public List<String> getDescription() {
      return Arrays.asList("Archer Tag the most people", "within the next hour!");
   }

   @Override
   public String getEventID() {
      return "Shooter";
   }

   @Override
   public int getSeconds() {
      return 3600;
   }

   public static void archerTag(Player player) {
      if (Foxtrot.getInstance().getMiniEventsHandler().getActiveEvent() != null) {
         if (Foxtrot.getInstance().getMiniEventsHandler().getActiveEvent().getEventID().equalsIgnoreCase("Shooter")) {
            Foxtrot.getInstance().getMiniEventsHandler().getActiveEvent().addProgress(player, false);
         }
      }
   }
}
