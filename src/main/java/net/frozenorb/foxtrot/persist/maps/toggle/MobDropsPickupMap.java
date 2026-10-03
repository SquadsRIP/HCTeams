package net.frozenorb.foxtrot.persist.maps.toggle;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;

public class MobDropsPickupMap extends PersistMap<Boolean> {
   public MobDropsPickupMap() {
      super("MobPickup", "MobPickup");
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

   public void setMobPickup(UUID update, boolean toggled) {
      this.updateValueAsync(update, toggled);
   }

   public boolean isMobPickup(UUID check) {
      return this.contains(check) ? this.getValue(check) : true;
   }
}
