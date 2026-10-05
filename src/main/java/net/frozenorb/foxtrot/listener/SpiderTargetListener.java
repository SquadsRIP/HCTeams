package net.frozenorb.foxtrot.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

public class SpiderTargetListener implements Listener {
   // 1.7 spiders pick the closest player within 16 blocks with no line-of-sight check, so they lock onto players through walls and climb out of grinders.
   @EventHandler(ignoreCancelled = true)
   public void onSpiderTarget(EntityTargetEvent event) {
      if (event.getReason() != EntityTargetEvent.TargetReason.CLOSEST_PLAYER || !(event.getTarget() instanceof Player)) {
         return;
      }

      EntityType type = event.getEntityType();
      if (type != EntityType.SPIDER && type != EntityType.CAVE_SPIDER) {
         return;
      }

      if (!((LivingEntity) event.getEntity()).hasLineOfSight(event.getTarget())) {
         event.setCancelled(true);
      }
   }
}
