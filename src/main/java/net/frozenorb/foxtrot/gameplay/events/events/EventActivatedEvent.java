package net.frozenorb.foxtrot.gameplay.events.events;

import java.beans.ConstructorProperties;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EventActivatedEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   private net.frozenorb.foxtrot.gameplay.events.Event event;

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   @ConstructorProperties("event")
   public EventActivatedEvent(net.frozenorb.foxtrot.gameplay.events.Event event) {
      this.event = event;
   }

   public net.frozenorb.foxtrot.gameplay.events.Event getEvent() {
      return this.event;
   }
}
