package net.frozenorb.foxtrot.persist.maps;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;

public class FirstJoinMap extends PersistMap<Long> {
   public FirstJoinMap() {
      super("FirstJoin", "FirstJoined");
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

   public void setFirstJoin(UUID update) {
      this.updateValueAsync(update, System.currentTimeMillis());
   }

   public long getFirstJoin(UUID check) {
      return this.contains(check) ? this.getValue(check) : 0L;
   }

   public int getAllPlayersSize() {
      return this.wrappedMap.size();
   }

   public Collection<UUID> getAllPlayers() {
      return this.wrappedMap.keySet();
   }
}
