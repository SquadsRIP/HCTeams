package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.persist.PersistMap;

public class KillsMap extends PersistMap<Integer> {
   public KillsMap() {
      super("Kills", "Kills");
   }

   public String getRedisValue(Integer kills) {
      return String.valueOf(kills);
   }

   public Integer getJavaObject(String str) {
      return Integer.parseInt(str);
   }

   public Object getMongoValue(Integer kills) {
      return kills;
   }

   public int getKills(UUID check) {
      return this.contains(check) ? this.getValue(check) : 0;
   }

   public void setKills(UUID update, int kills) {
      this.updateValueAsync(update, kills);
      Foxtrot.getInstance().getKdrMap().updateKDR(update);
   }
}
