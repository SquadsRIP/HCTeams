package net.frozenorb.foxtrot.persist.maps;

import java.util.Date;
import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;

public class LastJoinMap extends PersistMap<Long> {
   public LastJoinMap() {
      super("LastJoin", "LastJoined");
   }

   public String getRedisValue(Long time) {
      return String.valueOf(time);
   }

   public Long getJavaObject(String str) {
      return Long.parseLong(str);
   }

   public Object getMongoValue(Long time) {
      return new Date(time);
   }

   public void setLastJoin(UUID update) {
      this.updateValueAsync(update, System.currentTimeMillis());
   }

   public long getLastJoin(UUID check) {
      return this.contains(check) ? this.getValue(check) : 0L;
   }
}
