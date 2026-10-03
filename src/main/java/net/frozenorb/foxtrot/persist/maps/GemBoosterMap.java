package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;
import org.bukkit.entity.Player;

public class GemBoosterMap extends PersistMap<Long> {
   public GemBoosterMap() {
      super("GemBooster", "GemBooster");
   }

   public String getRedisValue(Long value) {
      return String.valueOf(value);
   }

   public Object getMongoValue(Long value) {
      return Long.toString(value);
   }

   public Long getJavaObject(String value) {
      return Long.valueOf(value);
   }

   public boolean hasGemBooster(UUID uuid) {
      Long time = this.getValue(uuid);
      return time == null ? false : time >= System.currentTimeMillis();
   }

   public long getGemBoosterMillisLeft(Player player) {
      Long time = this.getValue(player.getUniqueId());
      if (time == null) {
         return 0L;
      }

      long left = time - System.currentTimeMillis();
      return left < 0L ? 0L : left;
   }

   public void giveGemBooster(Player player, long minutes) {
      long millis = minutes * 1000L * 60L;
      Long time = this.getValue(player.getUniqueId());
      time = time != null && time >= System.currentTimeMillis() ? time + millis : System.currentTimeMillis() + millis;
      this.updateValueAsync(player.getUniqueId(), time);
   }

   public void giveGemBooster(UUID player, long minutes) {
      long millis = minutes * 1000L * 60L;
      Long time = this.getValue(player);
      time = time != null && time >= System.currentTimeMillis() ? time + millis : System.currentTimeMillis() + millis;
      this.updateValueAsync(player, time);
   }
}
