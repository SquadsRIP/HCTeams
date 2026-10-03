package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;

public class ToggleGlobalChatMap extends PersistMap<Boolean> {
   public ToggleGlobalChatMap() {
      super("GlobalChatToggles", "GlobalChat");
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

   public void setGlobalChatToggled(UUID update, boolean toggled) {
      this.updateValueAsync(update, toggled);
   }

   public boolean isGlobalChatToggled(UUID check) {
      return this.contains(check) ? this.getValue(check) : true;
   }
}
