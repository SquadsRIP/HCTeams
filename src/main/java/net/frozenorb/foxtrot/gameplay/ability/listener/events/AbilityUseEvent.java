package net.frozenorb.foxtrot.gameplay.ability.listener.events;

import net.frozenorb.foxtrot.gameplay.ability.Ability;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AbilityUseEvent extends Event implements Cancellable {
   private static final HandlerList handlerList = new HandlerList();
   private final Player player;
   private final Player target;
   private final Location chosenLocation;
   private final Ability ability;
   private boolean cancelled;
   private boolean oneHit;

   public AbilityUseEvent(Player player, Player target, Location chosenLocation, Ability ability, boolean oneHit) {
      this.player = player;
      this.target = target;
      this.chosenLocation = chosenLocation;
      this.ability = ability;
      this.oneHit = oneHit;
   }

   @Override
   public HandlerList getHandlers() {
      return handlerList;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void setCancelled(boolean b) {
      this.cancelled = b;
   }

   public Player getPlayer() {
      return this.player;
   }

   public Player getTarget() {
      return this.target;
   }

   public Location getChosenLocation() {
      return this.chosenLocation;
   }

   public Ability getAbility() {
      return this.ability;
   }

   public boolean isOneHit() {
      return this.oneHit;
   }

   public void setOneHit(boolean oneHit) {
      this.oneHit = oneHit;
   }

   public static HandlerList getHandlerList() {
      return handlerList;
   }
}
