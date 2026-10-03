package net.frozenorb.foxtrot.persist.maps;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.persist.PersistMap;

public class DeathbanMap extends PersistMap<Long> {
   public DeathbanMap() {
      super("Deathbans", "Deathban");
   }

   public String getRedisValue(Long time) {
      return String.valueOf(time);
   }

   public Long getJavaObject(String str) {
      return Long.parseLong(str);
   }

   public Object getMongoValue(Long time) {
      return Long.toString(time);
   }

   public boolean isDeathbanned(UUID check) {
      return this.getValue(check) != null ? this.getValue(check) > System.currentTimeMillis() : false;
   }

   public void deathban(UUID update, long seconds) {
      this.updateValueAsync(update, System.currentTimeMillis() + seconds * 1000L);
   }

   public void revive(UUID update) {
      this.updateValueAsync(update, 0L);
   }

   public void reviveAsync(UUID update) {
      this.updateValueAsync(update, 0L);
   }

   public long getDeathban(UUID check) {
      return this.contains(check) ? this.getValue(check) : 0L;
   }

   public void wipeDeathbans() {
      this.wipeValues();
   }

   public Collection<UUID> getDeathbannedPlayers() {
      HashSet<UUID> deathbannedPlayers = new HashSet<>();

      for (Entry entry : this.wrappedMap.entrySet()) {
         if (this.isDeathbanned((UUID)entry.getKey())) {
            deathbannedPlayers.add((UUID)entry.getKey());
         }
      }

      return deathbannedPlayers;
   }
}
