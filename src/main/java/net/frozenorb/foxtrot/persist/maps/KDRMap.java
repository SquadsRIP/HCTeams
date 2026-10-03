package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.persist.PersistMap;

public class KDRMap extends PersistMap<Double> {
   public KDRMap() {
      super("KDR", "KDR");
   }

   public String getRedisValue(Double kdr) {
      return String.valueOf(kdr);
   }

   public Double getJavaObject(String str) {
      return Double.parseDouble(str);
   }

   public Object getMongoValue(Double kdr) {
      return kdr;
   }

   public void setKDR(UUID update, double kdr) {
      this.updateValueAsync(update, kdr);
   }

   public void updateKDR(UUID update) {
      this.setKDR(
         update,
         Math.max((double)Foxtrot.getInstance().getKillsMap().getKills(update) / Math.max(Foxtrot.getInstance().getDeathsMap().getDeaths(update), 1), 0.0)
      );
   }
}
