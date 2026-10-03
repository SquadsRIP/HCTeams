package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;

public class EmeraldMinedMap extends PersistMap<Integer> {
   public EmeraldMinedMap() {
      super("EmeraldMined", "MiningStats.Emerald");
   }

   public String getRedisValue(Integer kills) {
      return String.valueOf(kills);
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
