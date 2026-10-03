package net.frozenorb.foxtrot.deathmessage.objects;

import cc.fyre.proton.util.UUIDUtils;
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
      int kills = Foxtrot.getInstance().getMapHandler().isKitMap()
         ? Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(player).getKills()
         : Foxtrot.getInstance().getKillsMap().getKills(UUIDUtils.uuid(player));
      return ChatColor.DARK_RED + player + ChatColor.DARK_RED + "[" + kills + "]" + ChatColor.DARK_RED;
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
