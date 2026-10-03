package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.persist.PersistMap;

public class ToggleDeathMessageMap extends PersistMap<Boolean> {
   public ToggleDeathMessageMap() {
      super("DeathMessageToggles", "DeathMessageEnabled");
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

   public void setDeathMessagesEnabled(UUID update, boolean toggled) {
      this.updateValueAsync(update, toggled);
   }

   public boolean areDeathMessagesEnabled(UUID check) {
      return this.contains(check) ? this.getValue(check) : !Foxtrot.getInstance().getMapHandler().isKitMap();
   }
}
