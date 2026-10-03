package net.frozenorb.foxtrot.deathmessage.event;

import java.beans.ConstructorProperties;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerKilledEvent extends Event {
   private static HandlerList handlerList = new HandlerList();
   private final Player killer;
   private final Player victim;

   @Override
   public HandlerList getHandlers() {
      return handlerList;
   }

   public static HandlerList getHandlerList() {
      return handlerList;
   }

   @ConstructorProperties({"killer", "victim"})
   public PlayerKilledEvent(Player killer, Player victim) {
      this.killer = killer;
      this.victim = victim;
   }

   public Player getKiller() {
      return this.killer;
   }

   public Player getVictim() {
      return this.victim;
   }
}
