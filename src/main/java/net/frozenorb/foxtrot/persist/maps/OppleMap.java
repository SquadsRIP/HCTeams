package net.frozenorb.foxtrot.persist.maps;

import java.util.Date;
import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;

public class OppleMap extends PersistMap<Long> {
   public OppleMap() {
      super("OppleCooldowns", "OppleCooldown");
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

   public boolean isOnCooldown(UUID check) {
      return this.getValue(check) != null && this.getValue(check) > System.currentTimeMillis();
   }

   public void useGoldenApple(UUID update, long seconds) {
      this.updateValueAsync(update, System.currentTimeMillis() + seconds * 1000L);
   }

   public void resetCooldown(UUID update) {
      this.updateValueAsync(update, 0L);
   }

   public long getCooldown(UUID check) {
      return this.contains(check) ? this.getValue(check) : -1L;
   }
}
