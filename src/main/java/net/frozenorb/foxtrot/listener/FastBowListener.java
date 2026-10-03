package net.frozenorb.foxtrot.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FastBowListener implements Listener {
   private Map<UUID, Long> lastFire = new HashMap<>();

   @EventHandler(priority = EventPriority.MONITOR)
   public void onProjectileLaunch(EntityShootBowEvent event) {
      if (event.getEntity() instanceof Player) {
         Player shooter = (Player)event.getEntity();
         Long lastFired = this.lastFire.get(shooter.getUniqueId());
         if (lastFired != null && System.currentTimeMillis() - lastFired < 500L) {
            event.setCancelled(true);
            this.lastFire.put(shooter.getUniqueId(), System.currentTimeMillis());
         } else {
            this.lastFire.put(shooter.getUniqueId(), System.currentTimeMillis());
         }
      }
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      this.lastFire.remove(event.getPlayer().getUniqueId());
   }
}
