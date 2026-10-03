package net.frozenorb.foxtrot.listener.event;

import java.beans.ConstructorProperties;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CrowbarSpawnerBreakEvent extends Event implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private final Player player;
   private final Block block;
   private boolean cancelled;

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   @ConstructorProperties({"player", "block"})
   public CrowbarSpawnerBreakEvent(Player player, Block block) {
      this.player = player;
      this.block = block;
   }

   public Player getPlayer() {
      return this.player;
   }

   public Block getBlock() {
      return this.block;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }
}
