package net.frozenorb.foxtrot.listener.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.potion.Potion;

public class DisallowedPotionDrinkEvent extends PlayerEvent {
   private static HandlerList handlerList = new HandlerList();
   private final Potion potion;
   private boolean allowed = false;

   public DisallowedPotionDrinkEvent(Player who, Potion potion) {
      super(who);
      this.potion = potion;
   }

   @Override
   public HandlerList getHandlers() {
      return handlerList;
   }

   public static HandlerList getHandlerList() {
      return handlerList;
   }

   public Potion getPotion() {
      return this.potion;
   }

   public boolean isAllowed() {
      return this.allowed;
   }

   public void setAllowed(boolean allowed) {
      this.allowed = allowed;
   }
}
