package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;
import net.frozenorb.foxtrot.tab.TabListMode;

public class TabListModeMap extends PersistMap<TabListMode> {
   public TabListModeMap() {
      super("TabListInfo", "TabListInfo", false);
   }

   public String getRedisValue(TabListMode toggled) {
      return toggled.name();
   }

   public TabListMode getJavaObject(String str) {
      return str.equals("VANILLA") ? TabListMode.DETAILED : TabListMode.valueOf(str);
   }

   public Object getMongoValue(TabListMode toggled) {
      return toggled;
   }

   public void setTabListMode(UUID update, TabListMode mode) {
      this.updateValueAsync(update, mode);
   }

   public TabListMode getTabListMode(UUID check) {
      return this.contains(check) ? this.getValue(check) : TabListMode.DETAILED;
   }
}
