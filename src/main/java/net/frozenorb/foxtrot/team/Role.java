package net.frozenorb.foxtrot.team;

import java.beans.ConstructorProperties;

public enum Role {
   MEMBER("Member", 1),
   CAPTAIN("Captain", 2),
   COLEADER("Co-Leader", 3);

   String roleName;
   int number;

   @ConstructorProperties({"roleName", "number"})
   Role(String roleName, int number) {
      this.roleName = roleName;
      this.number = number;
   }

   public String getRoleName() {
      return this.roleName;
   }

   public int getNumber() {
      return this.number;
   }
}
