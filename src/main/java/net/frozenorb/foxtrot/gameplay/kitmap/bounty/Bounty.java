package net.frozenorb.foxtrot.gameplay.kitmap.bounty;

import java.beans.ConstructorProperties;
import java.util.UUID;

public class Bounty implements Comparable<Bounty> {
   private final UUID placedBy;
   private final int gems;

   public int compareTo(Bounty bounty) {
      return Integer.compare(this.gems, bounty.gems);
   }

   @ConstructorProperties({"placedBy", "gems"})
   public Bounty(UUID placedBy, int gems) {
      this.placedBy = placedBy;
      this.gems = gems;
   }

   public UUID getPlacedBy() {
      return this.placedBy;
   }

   public int getGems() {
      return this.gems;
   }
}
