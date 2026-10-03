package net.frozenorb.foxtrot.gameplay.armorclass.event;

import java.beans.ConstructorProperties;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import net.frozenorb.foxtrot.util.event.FoxtrotEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class ArmorSetEquipEvent extends FoxtrotEvent implements Cancellable {
   private final Player player;
   private final ArmorClass armorClass;
   private boolean cancelled;

   public Player getPlayer() {
      return this.player;
   }

   public ArmorClass getArmorClass() {
      return this.armorClass;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }

   @ConstructorProperties({"player", "armorClass"})
   public ArmorSetEquipEvent(Player player, ArmorClass armorClass) {
      this.player = player;
      this.armorClass = armorClass;
   }
}
