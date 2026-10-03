package net.frozenorb.foxtrot.persist.maps.toggle;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;

public class CobblePickupMap extends PersistMap<Boolean> {
   public CobblePickupMap() {
      super("CobblePickup", "CobblePickup");
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

   public void setCobblePickup(UUID update, boolean toggled) {
      this.updateValueAsync(update, toggled);
   }

   public boolean isCobblePickup(UUID check) {
      return this.contains(check) ? this.getValue(check) : true;
   }
}
