package net.frozenorb.foxtrot.gameplay.events.koth.events;

import java.beans.ConstructorProperties;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class KOTHControlLostEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   private KOTH KOTH;

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   @ConstructorProperties("KOTH")
   public KOTHControlLostEvent(KOTH KOTH2) {
      this.KOTH = KOTH2;
   }

   public KOTH getKOTH() {
      return this.KOTH;
   }
}
