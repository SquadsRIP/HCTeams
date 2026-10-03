package net.frozenorb.foxtrot.persist.maps.toggle;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;

public class DTRDisplayMap extends PersistMap<Boolean> {
   public DTRDisplayMap() {
      super("DTRDisplay", "DTRDisplay");
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

   public void setHearts(UUID update, boolean toggled) {
      this.updateValueAsync(update, toggled);
   }

   public boolean isHearts(UUID check) {
      return this.contains(check) ? this.getValue(check) : false;
   }
}
