package net.frozenorb.foxtrot.tab;

import java.beans.ConstructorProperties;

public enum TabListMode {
   DETAILED("Detailed"),
   DETAILED_WITH_FACTION_INFO("Detailed w/ Team List");

   private final String name;

   public String getName() {
      return this.name;
   }

   @ConstructorProperties("name")
   TabListMode(String name) {
      this.name = name;
   }
}
