package net.frozenorb.foxtrot.persist.maps.event;

import java.beans.ConstructorProperties;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SyncPlaytimeEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   private Player player;

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   @ConstructorProperties("player")
   public SyncPlaytimeEvent(Player player) {
      this.player = player;
   }

   public Player getPlayer() {
      return this.player;
   }
}
