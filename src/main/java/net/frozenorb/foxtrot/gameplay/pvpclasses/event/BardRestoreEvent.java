package net.frozenorb.foxtrot.gameplay.pvpclasses.event;

import java.beans.ConstructorProperties;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClass;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BardRestoreEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   private Player player;
   private PvPClass.SavedPotion potions;

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   @ConstructorProperties({"player", "potions"})
   public BardRestoreEvent(Player player, PvPClass.SavedPotion potions) {
      this.player = player;
      this.potions = potions;
   }

   public Player getPlayer() {
      return this.player;
   }

   public PvPClass.SavedPotion getPotions() {
      return this.potions;
   }
}
