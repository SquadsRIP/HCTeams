package net.frozenorb.foxtrot.listener.event;

import java.beans.ConstructorProperties;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ReclaimEvent extends Event {
   private static final HandlerList handlerList = new HandlerList();
   private final Player player;
   private final String rankName;

   @Override
   public HandlerList getHandlers() {
      return handlerList;
   }

   @ConstructorProperties({"player", "rankName"})
   public ReclaimEvent(Player player, String rankName) {
      this.player = player;
      this.rankName = rankName;
   }

   public Player getPlayer() {
      return this.player;
   }

   public String getRankName() {
      return this.rankName;
   }

   public static HandlerList getHandlerList() {
      return handlerList;
   }
}
