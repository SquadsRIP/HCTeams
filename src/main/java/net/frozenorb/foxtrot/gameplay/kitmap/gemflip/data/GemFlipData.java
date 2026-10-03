package net.frozenorb.foxtrot.gameplay.kitmap.gemflip.data;

import java.util.concurrent.atomic.AtomicInteger;

public class GemFlipData {
   private AtomicInteger won;
   private AtomicInteger lost;

   public GemFlipData(int won, int lost) {
      this.won = new AtomicInteger(won);
      this.lost = new AtomicInteger(lost);
   }

   public double getRatio() {
      return this.lost.get() == 0 ? this.won.get() : this.won.get() / this.lost.doubleValue();
   }

   public boolean isEmpty() {
      return this.won.get() <= 0 && this.lost.get() <= 0;
   }

   public AtomicInteger getWon() {
      return this.won;
   }

   public AtomicInteger getLost() {
      return this.lost;
   }

   public void setWon(AtomicInteger won) {
      this.won = won;
   }

   public void setLost(AtomicInteger lost) {
      this.lost = lost;
   }
}
