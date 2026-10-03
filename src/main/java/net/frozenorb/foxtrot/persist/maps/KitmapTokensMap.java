package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;

public class KitmapTokensMap extends PersistMap<Integer> {
   public KitmapTokensMap() {
      super("Tokens", "KitMap.Tokens");
   }

   public String getRedisValue(Integer tokens) {
      return String.valueOf(tokens);
   }

   public Integer getJavaObject(String str) {
      return Integer.parseInt(str);
   }

   public Object getMongoValue(Integer tokens) {
      return tokens;
   }

   public int getTokens(UUID check) {
      return this.contains(check) ? this.getValue(check) : 0;
   }

   public void setTokens(UUID update, int tokens) {
      this.updateValueAsync(update, tokens);
   }
}
