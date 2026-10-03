package net.frozenorb.foxtrot.server;

import java.beans.ConstructorProperties;

public enum SpawnerType {
   ZOMBIE("Zombie"),
   SKELETON("Skeleton"),
   SPIDER("Spider"),
   CAVE_SPIDER("Cave Spider");

   String displayName;

   @ConstructorProperties("displayName")
   SpawnerType(String displayName) {
      this.displayName = displayName;
   }

   public String getDisplayName() {
      return this.displayName;
   }
}
