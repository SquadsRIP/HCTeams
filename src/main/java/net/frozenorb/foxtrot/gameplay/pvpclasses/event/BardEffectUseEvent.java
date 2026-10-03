package net.frozenorb.foxtrot.gameplay.pvpclasses.event;

import java.beans.ConstructorProperties;
import java.util.List;
import net.frozenorb.foxtrot.gameplay.pvpclasses.energy.EnergyEffect;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BardEffectUseEvent extends Event implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private Player source;
   private EnergyEffect bardEffect;
   private List<Player> affectedPlayers;
   private boolean cancelled;

   public BardEffectUseEvent(Player source, EnergyEffect bardEffect, List<Player> affectedPlayers) {
      this.source = source;
      this.bardEffect = bardEffect;
      this.affectedPlayers = affectedPlayers;
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   @ConstructorProperties({"source", "bardEffect", "affectedPlayers", "cancelled"})
   public BardEffectUseEvent(Player source, EnergyEffect bardEffect, List<Player> affectedPlayers, boolean cancelled) {
      this.source = source;
      this.bardEffect = bardEffect;
      this.affectedPlayers = affectedPlayers;
      this.cancelled = cancelled;
   }

   public Player getSource() {
      return this.source;
   }

   public EnergyEffect getBardEffect() {
      return this.bardEffect;
   }

   public List<Player> getAffectedPlayers() {
      return this.affectedPlayers;
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
