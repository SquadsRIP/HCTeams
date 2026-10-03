package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;

public class FishingKitMap extends PersistMap<Integer> {
   public FishingKitMap() {
      super("FishingKitUses", "FishingKitUses");
   }

   public String getRedisValue(Integer uses) {
      return String.valueOf(uses);
   }

   public Integer getJavaObject(String str) {
      return Integer.parseInt(str);
   }

   public Object getMongoValue(Integer uses) {
      return uses;
   }

   public int getUses(UUID check) {
      return this.contains(check) ? this.getValue(check) : 0;
   }

   public void setUses(UUID update, int uses) {
      this.updateValueAsync(update, uses);
   }
}
