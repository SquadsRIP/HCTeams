package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;

public class FriendLivesMap extends PersistMap<Integer> {
   public FriendLivesMap() {
      super("FriendLives", "Lives.Friend");
   }

   public String getRedisValue(Integer lives) {
      return String.valueOf(lives);
   }

   public Integer getJavaObject(String str) {
      return Integer.parseInt(str);
   }

   public Object getMongoValue(Integer lives) {
      return lives;
   }

   public int getLives(UUID check) {
      return this.contains(check) ? this.getValue(check) : 0;
   }

   public void setLives(UUID update, int lives) {
      this.updateValueSync(update, lives);
   }
}
