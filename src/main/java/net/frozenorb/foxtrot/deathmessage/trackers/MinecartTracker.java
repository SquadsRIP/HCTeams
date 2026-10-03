package net.frozenorb.foxtrot.deathmessage.trackers;

import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.deathmessage.event.CustomPlayerDamageEvent;
import net.frozenorb.foxtrot.deathmessage.objects.PlayerDamage;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class MinecartTracker implements Listener {
   @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
   public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
      EntityDamageByEntityEvent entityDamageByEntityEvent;
      if (event.getCause() instanceof EntityDamageByEntityEvent
         && (entityDamageByEntityEvent = (EntityDamageByEntityEvent)event.getCause()).getDamager() instanceof ExplosiveMinecart) {
         ExplosiveMinecart explosiveMinecart = (ExplosiveMinecart)entityDamageByEntityEvent.getDamager();
         if (!explosiveMinecart.hasMetadata("TNT_MINECART")) {
            return;
         }

         UUID uuid = UUID.fromString(explosiveMinecart.getMetadata("TNT_MINECART").get(0).asString());
         Player damager = Foxtrot.getInstance().getServer().getPlayer(uuid);
         if (damager == null) {
            return;
         }

         Player damaged = event.getPlayer();
         if (damager != damaged) {
            event.setTrackerDamage(new MinecartTracker.ExplodeByPlayer(damaged.getName(), event.getDamage(), damager.getName()));
         }
      }
   }

   public static class ExplodeByPlayer extends PlayerDamage {
      public ExplodeByPlayer(String damaged, double damage, String damager) {
         super(damaged, damage, damager);
      }

      @Override
      public String getDeathMessage() {
         return this.wrapName(this.getDamaged()) + " exploded thanks to " + this.wrapName(this.getDamager()) + ".";
      }
   }
}
