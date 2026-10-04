package net.frozenorb.foxtrot.deathmessage.objects;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.util.UUIDUtils;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;

public abstract class Damage {
   private String damaged;
   private double damage;
   private long time;

   public Damage(String damaged, double damage) {
      this.damaged = damaged;
      this.damage = damage;
      this.time = System.currentTimeMillis();
   }

   public abstract String getDeathMessage();

   public String wrapName(String player) {
      UUID uuid = UUIDUtils.uuid(player);
      int kills = Foxtrot.getInstance().getMapHandler().isKitMap()
         ? Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(player).getKills()
         : Foxtrot.getInstance().getKillsMap().getKills(uuid);
      return getRankColor(uuid) + player + ChatColor.DARK_RED + "[" + kills + "]" + ChatColor.RED;
   }

   private static ChatColor getRankColor(UUID uuid) {
      Profile profile = uuid == null ? null : Neutron.getInstance().getProfileHandler().fromUuid(uuid);
      if (profile == null || profile.getActiveGrant() == null || profile.getActiveRank() == null || profile.getActiveRank().getColor() == null) {
         return ChatColor.DARK_RED;
      }

      return profile.getActiveRank().getColor();
   }

   public long getTimeDifference() {
      return System.currentTimeMillis() - this.time;
   }

   public String getDamaged() {
      return this.damaged;
   }

   public double getDamage() {
      return this.damage;
   }

   public long getTime() {
      return this.time;
   }
}
