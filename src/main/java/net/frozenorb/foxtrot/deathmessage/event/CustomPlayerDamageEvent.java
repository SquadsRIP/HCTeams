package net.frozenorb.foxtrot.deathmessage.event;

import java.beans.ConstructorProperties;
import net.frozenorb.foxtrot.deathmessage.objects.Damage;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

public class CustomPlayerDamageEvent extends Event {
   private static HandlerList handlerList = new HandlerList();
   private EntityDamageEvent cause;
   private Damage trackerDamage;

   public Player getPlayer() {
      return (Player)this.cause.getEntity();
   }

   public double getDamage() {
      return this.cause.getDamage();
   }

   @Override
   public HandlerList getHandlers() {
      return handlerList;
   }

   public static HandlerList getHandlerList() {
      return handlerList;
   }

   @ConstructorProperties({"cause", "trackerDamage"})
   public CustomPlayerDamageEvent(EntityDamageEvent cause, Damage trackerDamage) {
      this.cause = cause;
      this.trackerDamage = trackerDamage;
   }

   public EntityDamageEvent getCause() {
      return this.cause;
   }

   public Damage getTrackerDamage() {
      return this.trackerDamage;
   }

   public void setTrackerDamage(Damage trackerDamage) {
      this.trackerDamage = trackerDamage;
   }
}
