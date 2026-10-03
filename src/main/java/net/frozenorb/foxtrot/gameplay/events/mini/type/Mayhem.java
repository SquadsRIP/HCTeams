package net.frozenorb.foxtrot.gameplay.events.mini.type;

import java.util.Arrays;
import java.util.List;
import net.frozenorb.foxtrot.gameplay.events.mini.MiniEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Mayhem extends MiniEvent {
   @Override
   public String getObjective() {
      return "Mayhem";
   }

   @Override
   public List<String> getDescription() {
      return Arrays.asList("Cause the most damage to", "enemies within the next hour!");
   }

   @Override
   public String getEventID() {
      return "Mayhem";
   }

   @Override
   public int getSeconds() {
      return 3600;
   }

   @EventHandler(priority = EventPriority.MONITOR)
   private void onDamage(EntityDamageByEntityEvent event) {
      if (!event.isCancelled() && event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
         Player damager = (Player)event.getDamager();
         if (!damager.getWorld().getName().equalsIgnoreCase("Deathban")) {
            this.addProgress(damager, false, (int)Math.round(event.getDamage()));
         }
      }
   }
}
