package net.frozenorb.foxtrot.listener.event;

import java.beans.ConstructorProperties;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TimerEndEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   private long duration;
   private String displayName;

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   @ConstructorProperties({"duration", "displayName"})
   public TimerEndEvent(long duration, String displayName) {
      this.duration = duration;
      this.displayName = displayName;
   }

   public long getDuration() {
      return this.duration;
   }

   public String getDisplayName() {
      return this.displayName;
   }
}
