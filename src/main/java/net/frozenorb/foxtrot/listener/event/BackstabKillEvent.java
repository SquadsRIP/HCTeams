package net.frozenorb.foxtrot.listener.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class BackstabKillEvent extends PlayerEvent {
   private static HandlerList handlerList = new HandlerList();
   private final Player killed;
   private boolean allowed = false;

   public BackstabKillEvent(Player who, Player killed) {
      super(who);
      this.killed = killed;
   }

   @Override
   public HandlerList getHandlers() {
      return handlerList;
   }

   public static HandlerList getHandlerList() {
      return handlerList;
   }

   public Player getKilled() {
      return this.killed;
   }

   public boolean isAllowed() {
      return this.allowed;
   }

   public void setAllowed(boolean allowed) {
      this.allowed = allowed;
   }
}
