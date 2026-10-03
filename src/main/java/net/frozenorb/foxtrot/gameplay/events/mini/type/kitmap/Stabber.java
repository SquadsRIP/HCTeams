package net.frozenorb.foxtrot.gameplay.events.mini.type.kitmap;

import java.util.Arrays;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.mini.MiniEvent;
import org.bukkit.entity.Player;

public class Stabber extends MiniEvent {
   @Override
   public String getObjective() {
      return "Stabber";
   }

   @Override
   public List<String> getDescription() {
      return Arrays.asList("Backstab the most people", "within the next 30 mins!");
   }

   @Override
   public String getEventID() {
      return "Stabber";
   }

   @Override
   public int getSeconds() {
      return 1800;
   }

   public static void stab(Player player) {
      if (Foxtrot.getInstance().getMiniEventsHandler().getActiveEvent() != null) {
         if (Foxtrot.getInstance().getMiniEventsHandler().getActiveEvent().getEventID().equalsIgnoreCase("Stabber")) {
            Foxtrot.getInstance().getMiniEventsHandler().getActiveEvent().addProgress(player, false);
         }
      }
   }
}
