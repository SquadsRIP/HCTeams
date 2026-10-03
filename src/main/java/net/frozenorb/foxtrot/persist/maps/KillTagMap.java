package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.gameplay.killtags.KillTags;
import net.frozenorb.foxtrot.persist.PersistMap;

public class KillTagMap extends PersistMap<KillTags> {
   public KillTagMap() {
      super("KillTags", "KillTags");
   }

   public String getRedisValue(KillTags killTags) {
      return killTags.name();
   }

   public KillTags getJavaObject(String str) {
      return KillTags.valueOf(str);
   }

   public Object getMongoValue(KillTags killTags) {
      return killTags.name();
   }

   public KillTags getKillTag(UUID check) {
      return this.contains(check) ? this.getValue(check) : KillTags.NONE;
   }

   public void setKillTag(UUID update, KillTags killTags) {
      this.updateValueAsync(update, killTags);
   }
}
