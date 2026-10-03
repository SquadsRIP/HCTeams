package net.frozenorb.foxtrot.persist.maps.toggle;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;

public class LCTeamViewMap extends PersistMap<Boolean> {
   public LCTeamViewMap() {
      super("LCTeamView", "LCTeamView");
   }

   public String getRedisValue(Boolean toggled) {
      return String.valueOf(toggled);
   }

   public Object getMongoValue(Boolean toggled) {
      return String.valueOf(toggled);
   }

   public Boolean getJavaObject(String str) {
      return Boolean.valueOf(str);
   }

   public void setLCTeamView(UUID update, boolean toggled) {
      this.updateValueAsync(update, toggled);
   }

   public boolean isLCTeamView(UUID check) {
      return this.contains(check) ? this.getValue(check) : true;
   }
}
