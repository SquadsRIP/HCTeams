package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;

public class SaleTimersScoreboardMap extends PersistMap<Boolean> {
   public SaleTimersScoreboardMap() {
      super("SaleTimers", "SaleTimers");
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

   public void setSaleTimers(UUID update, boolean toggled) {
      this.updateValueAsync(update, toggled);
   }

   public boolean isSaleTimers(UUID check) {
      return this.contains(check) ? this.getValue(check) : true;
   }
}
