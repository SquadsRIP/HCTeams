package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;

public class ToggleClaimMessageMap extends PersistMap<Boolean> {
   public ToggleClaimMessageMap() {
      super("ClaimMessageToggles", "ClaimMessageEnabled");
   }

   public String getRedisValue(Boolean toggled) {
      return String.valueOf(toggled);
   }

   public Object getMongoValue(Boolean toggled) {
      return String.valueOf(toggled);
   }

   public Boolean getJavaObject(String str) {
      return Boolean.valueOf(str);
   }

   public void setClaimMessagesEnabled(UUID update, boolean toggled) {
      this.updateValueAsync(update, toggled);
   }

   public boolean areClaimMessagesEnabled(UUID check) {
      return this.contains(check) ? this.getValue(check) : true;
   }
}
