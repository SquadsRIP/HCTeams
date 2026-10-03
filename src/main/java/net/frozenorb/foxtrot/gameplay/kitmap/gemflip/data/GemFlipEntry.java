package net.frozenorb.foxtrot.gameplay.kitmap.gemflip.data;

import java.beans.ConstructorProperties;
import org.bukkit.entity.Player;

public class GemFlipEntry {
   private final Player creator;
   private final long amount;
   private GemFlipSide chosenSide;
   private boolean started;

   public Player getCreator() {
      return this.creator;
   }

   public long getAmount() {
      return this.amount;
   }

   public GemFlipSide getChosenSide() {
      return this.chosenSide;
   }

   public boolean isStarted() {
      return this.started;
   }

   public void setChosenSide(GemFlipSide chosenSide) {
      this.chosenSide = chosenSide;
   }

   public void setStarted(boolean started) {
      this.started = started;
   }

   @ConstructorProperties({"creator", "amount"})
   public GemFlipEntry(Player creator, long amount) {
      this.creator = creator;
      this.amount = amount;
   }
}
