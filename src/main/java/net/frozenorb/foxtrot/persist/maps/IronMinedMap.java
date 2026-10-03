package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;

public class IronMinedMap extends PersistMap<Integer> {
   public IronMinedMap() {
      super("IronMined", "MiningStats.Iron");
   }

   public String getRedisValue(Integer mined) {
      return String.valueOf(mined);
   }

   public Integer getJavaObject(String str) {
      return Integer.parseInt(str);
   }

   public Object getMongoValue(Integer mined) {
      return mined;
   }

   public int getMined(UUID check) {
      return this.contains(check) ? this.getValue(check) : 0;
   }

   public void setMined(UUID update, int mined) {
      this.updateValueAsync(update, mined);
   }
}
