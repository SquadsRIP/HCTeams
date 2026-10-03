package net.frozenorb.foxtrot.team.claims;

import java.beans.ConstructorProperties;

public class Coordinate {
   int x;
   int z;

   @Override
   public String toString() {
      return this.x + ", " + this.z;
   }

   @ConstructorProperties({"x", "z"})
   public Coordinate(int x, int z) {
      this.x = x;
      this.z = z;
   }

   public int getX() {
      return this.x;
   }

   public void setX(int x) {
      this.x = x;
   }

   public int getZ() {
      return this.z;
   }

   public void setZ(int z) {
      this.z = z;
   }
}
