package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;

public class ReclaimMap extends PersistMap<Boolean> {
   public ReclaimMap() {
      super("Reclaim", "Reclaim");
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

   public void setReclaimed(UUID update, boolean toggled) {
      this.updateValueAsync(update, toggled);
   }

   public boolean hasReclaimed(UUID check) {
      return this.contains(check) ? this.getValue(check) : false;
   }
}
