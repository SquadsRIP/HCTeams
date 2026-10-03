package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;

public class StartingPvPTimerMap extends PersistMap<Boolean> {
   public StartingPvPTimerMap() {
      super("StartingTimer", "StartingTimer");
   }

   public String getRedisValue(Boolean value) {
      return value.toString();
   }

   public Object getMongoValue(Boolean value) {
      return value.toString();
   }

   public Boolean getJavaObject(String string) {
      return Boolean.valueOf(string);
   }

   public void set(UUID uuid, boolean value) {
      this.updateValueAsync(uuid, value);
   }

   public boolean get(UUID uuid) {
      return this.contains(uuid) && this.getValue(uuid);
   }
}
