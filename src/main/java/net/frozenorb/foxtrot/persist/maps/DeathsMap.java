package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.persist.PersistMap;

public class DeathsMap extends PersistMap<Integer> {
   public DeathsMap() {
      super("Deaths", "Deaths");
   }

   public String getRedisValue(Integer deaths) {
      return String.valueOf(deaths);
   }

   public Integer getJavaObject(String str) {
      return Integer.parseInt(str);
   }

   public Object getMongoValue(Integer deaths) {
      return deaths;
   }

   public int getDeaths(UUID check) {
      return this.contains(check) ? this.getValue(check) : 0;
   }

   public void setDeaths(UUID update, int kills) {
      this.updateValueAsync(update, kills);
      Foxtrot.getInstance().getKdrMap().updateKDR(update);
   }
}
