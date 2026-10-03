package net.frozenorb.foxtrot.deathmessage.trackers;

import java.util.List;
import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.deathmessage.DeathMessageHandler;
import net.frozenorb.foxtrot.deathmessage.event.CustomPlayerDamageEvent;
import net.frozenorb.foxtrot.deathmessage.objects.Damage;
import net.frozenorb.foxtrot.deathmessage.objects.PlayerDamage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class FallTracker implements Listener {
   @EventHandler(priority = EventPriority.LOW)
   public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
      if (event.getCause().getCause() == DamageCause.FALL) {
         List<Damage> record = DeathMessageHandler.getDamage(event.getPlayer());
         Damage knocker = null;
         long knockerTime = 0L;
         if (record != null) {
            for (Damage damage : record) {
               if (!(damage instanceof FallTracker.FallDamage)
                  && !(damage instanceof FallTracker.FallDamageByPlayer)
                  && damage instanceof PlayerDamage
                  && (knocker == null || damage.getTime() > knockerTime)) {
                  knocker = damage;
                  knockerTime = damage.getTime();
               }
            }
         }

         if (knocker != null && knockerTime + TimeUnit.MINUTES.toMillis(1L) > System.currentTimeMillis()) {
            event.setTrackerDamage(new FallTracker.FallDamageByPlayer(event.getPlayer().getName(), event.getDamage(), ((PlayerDamage)knocker).getDamager()));
         } else {
            event.setTrackerDamage(new FallTracker.FallDamage(event.getPlayer().getName(), event.getDamage()));
         }
      }
   }

   public static class FallDamage extends Damage {
      public FallDamage(String damaged, double damage) {
         super(damaged, damage);
      }

      @Override
      public String getDeathMessage() {
         return this.wrapName(this.getDamaged()) + " hit the ground too hard.";
      }
   }

   public static class FallDamageByPlayer extends PlayerDamage {
      public FallDamageByPlayer(String damaged, double damage, String damager) {
         super(damaged, damage, damager);
      }

      @Override
      public String getDeathMessage() {
         return this.wrapName(this.getDamaged()) + " hit the ground too hard thanks to " + this.wrapName(this.getDamager()) + ".";
      }
   }
}
