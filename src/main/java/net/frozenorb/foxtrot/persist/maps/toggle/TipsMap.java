package net.frozenorb.foxtrot.persist.maps.toggle;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;

public class TipsMap extends PersistMap<Boolean> {
   public TipsMap() {
      super("Tips", "Tips");
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

   public void setTips(UUID update, boolean toggled) {
      this.updateValueAsync(update, toggled);
   }

   public boolean isTips(UUID check) {
      return this.contains(check) ? this.getValue(check) : true;
   }
}
