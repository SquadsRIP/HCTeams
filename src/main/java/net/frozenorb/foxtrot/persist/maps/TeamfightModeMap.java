package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;

public class TeamfightModeMap extends PersistMap<Boolean> {
   public TeamfightModeMap() {
      super("TeamFight", "TeamFight");
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

   public void setTeamfight(UUID update, boolean toggled) {
      this.updateValueAsync(update, toggled);
   }

   public boolean isTeamfight(UUID check) {
      return this.contains(check) ? this.getValue(check) : false;
   }
}
