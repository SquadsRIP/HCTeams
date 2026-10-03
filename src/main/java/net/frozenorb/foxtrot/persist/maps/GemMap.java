package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.gem.GemHandler;
import net.frozenorb.foxtrot.persist.PersistMap;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.entity.Player;

public class GemMap extends PersistMap<Long> {
   public GemMap() {
      super("PlayerGems", "gems");
   }

   public long addGems(UUID uuid, long amount, boolean bypass) {
      if (!bypass) {
         amount = GemHandler.isDoubleGem() ? amount * 2L : amount;
         amount = Foxtrot.getInstance().getGemBoosterMap().hasGemBooster(uuid) ? amount * 2L : amount;
         Team team = Foxtrot.getInstance().getTeamHandler().getTeam(uuid);
         if (team != null && team.hasRoadOutpost()) {
            amount *= 2L;
         }
      }

      Long value;
      value = (value = this.getValue(uuid)) == null ? amount : value + amount;
      this.updateValueAsync(uuid, value);
      return amount;
   }

   public void addGemsSync(UUID uuid, long amount) {
      Long value = this.getValue(uuid);
      value = value == null ? amount : value + amount;
      this.updateValueSync(uuid, value);
   }

   public long addGems(UUID uuid, long amount) {
      return this.addGems(uuid, amount, false);
   }

   public boolean removeGems(UUID uuid, long amount) {
      Long value = this.getValue(uuid);
      if (value == null) {
         return false;
      }

      Long var7 = value - amount;
      boolean update = var7 >= 0L;
      if (update) {
         this.updateValueAsync(uuid, var7);
      }

      return update;
   }

   public long getGems(Player player) {
      return this.wrappedMap.getOrDefault(player.getUniqueId(), 0L);
   }

   public long getGems(UUID uuid) {
      return this.wrappedMap.getOrDefault(uuid, 0L);
   }

   public void setValue(Player player, long value) {
      this.updateValueSync(player.getUniqueId(), value);
   }

   public void setValue(UUID uuid, long value) {
      this.updateValueAsync(uuid, value);
   }

   public String getRedisValue(Long gems) {
      return String.valueOf(gems);
   }

   public Object getMongoValue(Long gems) {
      return gems.intValue();
   }

   public Long getJavaObject(String str) {
      return Long.parseLong(str);
   }
}
