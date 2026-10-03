package net.frozenorb.foxtrot.listener.event;

import java.beans.ConstructorProperties;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerIncreaseKillEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   private Player killer;

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   @ConstructorProperties("killer")
   public PlayerIncreaseKillEvent(Player killer) {
      this.killer = killer;
   }

   public Player getKiller() {
      return this.killer;
   }
}
