package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;

public class AnnoyingBroadcastMap extends PersistMap<Boolean> {
   public AnnoyingBroadcastMap() {
      super("AnnoyingBroadcast", "AnnoyingBroadcast");
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

   public void setAnnoyingBroadcast(UUID update, boolean toggled) {
      this.updateValueAsync(update, toggled);
   }

   public boolean isAnnoyingBroadcast(UUID check) {
      return this.contains(check) ? this.getValue(check) : true;
   }
}
