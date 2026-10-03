package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;
import net.frozenorb.foxtrot.team.FilterType;

public class FactionFilterMap extends PersistMap<FilterType> {
   public FactionFilterMap() {
      super("FactionFilter", "FactionFilter");
   }

   public String getRedisValue(FilterType filterType) {
      return filterType.name();
   }

   public FilterType getJavaObject(String str) {
      return FilterType.valueOf(str);
   }

   public Object getMongoValue(FilterType filterType) {
      return filterType.name();
   }

   public FilterType getFilterType(UUID check) {
      return this.contains(check) ? this.getValue(check) : FilterType.LOWEST_DTR;
   }

   public void setFilterType(UUID update, FilterType filterType) {
      this.updateValueAsync(update, filterType);
   }
}
