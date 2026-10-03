package net.frozenorb.foxtrot.team.claims;

public class CoordinateSet {
   public static final int BITS = 6;
   private final int x;
   private final int z;

   public CoordinateSet(int x, int z) {
      this.x = x >> 6;
      this.z = z >> 6;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj != null && this.getClass() == obj.getClass()) {
         CoordinateSet other = (CoordinateSet)obj;
         return other.x == this.x && other.z == this.z;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int hash = 5;
      hash = 37 * hash + this.x;
      return 37 * hash + this.z;
   }
}
