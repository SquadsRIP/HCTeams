package net.frozenorb.foxtrot.listener.event;

import java.beans.ConstructorProperties;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EnderpearlCooldownAppliedEvent extends Event {
   private static HandlerList handlerList = new HandlerList();
   private final Player player;
   private long timeToApply;

   @Override
   public HandlerList getHandlers() {
      return handlerList;
   }

   @ConstructorProperties({"player", "timeToApply"})
   public EnderpearlCooldownAppliedEvent(Player player, long timeToApply) {
      this.player = player;
      this.timeToApply = timeToApply;
   }

   public static HandlerList getHandlerList() {
      return handlerList;
   }

   public Player getPlayer() {
      return this.player;
   }

   public long getTimeToApply() {
      return this.timeToApply;
   }

   public void setTimeToApply(long timeToApply) {
      this.timeToApply = timeToApply;
   }
}
