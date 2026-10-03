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

public class VoidTracker implements Listener {
   @EventHandler(priority = EventPriority.LOW)
   public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
      if (event.getCause().getCause() == DamageCause.VOID) {
         List<Damage> record = DeathMessageHandler.getDamage(event.getPlayer());
         Damage knocker = null;
         long knockerTime = 0L;
         if (record != null) {
            for (Damage damage : record) {
               if (!(damage instanceof VoidTracker.VoidDamage)
                  && !(damage instanceof VoidTracker.VoidDamageByPlayer)
                  && damage instanceof PlayerDamage
                  && (knocker == null || damage.getTime() > knockerTime)) {
                  knocker = damage;
                  knockerTime = damage.getTime();
               }
            }
         }

         if (knocker != null && knockerTime + TimeUnit.MINUTES.toMillis(1L) > System.currentTimeMillis()) {
            event.setTrackerDamage(new VoidTracker.VoidDamageByPlayer(event.getPlayer().getName(), event.getDamage(), ((PlayerDamage)knocker).getDamager()));
         } else {
            event.setTrackerDamage(new VoidTracker.VoidDamage(event.getPlayer().getName(), event.getDamage()));
         }
      }
   }

   public static class VoidDamage extends Damage {
      public VoidDamage(String damaged, double damage) {
         super(damaged, damage);
      }

      @Override
      public String getDeathMessage() {
         return this.wrapName(this.getDamaged()) + " fell into the void.";
      }
   }

   public static class VoidDamageByPlayer extends PlayerDamage {
      public VoidDamageByPlayer(String damaged, double damage, String damager) {
         super(damaged, damage, damager);
      }

      @Override
      public String getDeathMessage() {
         return this.wrapName(this.getDamaged()) + " fell into the void thanks to " + this.wrapName(this.getDamager()) + ".";
      }
   }
}
