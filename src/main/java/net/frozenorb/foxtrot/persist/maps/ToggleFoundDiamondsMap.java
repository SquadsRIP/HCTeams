package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;

public class ToggleFoundDiamondsMap extends PersistMap<Boolean> {
   public ToggleFoundDiamondsMap() {
      super("FoundDiamondToggles", "FoundDiamondEnabled");
   }

   public String getRedisValue(Boolean toggled) {
      return String.valueOf(toggled);
   }

   public Boolean getJavaObject(String str) {
      return Boolean.valueOf(str);
   }

   public Object getMongoValue(Boolean toggled) {
      return toggled;
   }

   public void setFoundDiamondToggled(UUID update, boolean toggled) {
      this.updateValueAsync(update, toggled);
   }

   public boolean isFoundDiamondToggled(UUID check) {
      return this.contains(check) ? this.getValue(check) : true;
   }
}
