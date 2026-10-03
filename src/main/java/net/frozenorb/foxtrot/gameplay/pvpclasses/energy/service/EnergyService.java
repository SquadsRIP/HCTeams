package net.frozenorb.foxtrot.gameplay.pvpclasses.energy.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClass;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.gameplay.pvpclasses.pvpclasses.BardClass;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.upgrade.UpgradeType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class EnergyService extends BukkitRunnable {
   private Map<UUID, Float> cache = new ConcurrentHashMap<>();
   private Map<UUID, Long> lastEffectUsage = new ConcurrentHashMap<>();
   public static final float MAX_ENERGY = 100.0F;
   public static final float ENERGY_REGEN_PER_SECOND = 1.0F;

   @Override
   public void run() {
      for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
         PvPClass pvpClass = PvPClassHandler.getPvPClass(player);
         if (pvpClass != null
            && pvpClass.isEnergyBased()
            && (!(pvpClass instanceof BardClass) || !Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId()))) {
            if (this.cache.containsKey(player.getUniqueId())) {
               Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
               float max = team == null
                     || (!team.hasNetherOutpost() || !team.hasEndOutpost()) && !team.getPurchasedUpgrades().contains(UpgradeType.INCREASED_MAX_BARD)
                  ? 100.0F
                  : 120.0F;
               if (this.cache.get(player.getUniqueId()) == max) {
                  continue;
               }

               this.cache.put(player.getUniqueId(), Math.min(max, this.cache.get(player.getUniqueId()) + 1.0F));
            } else {
               this.cache.put(player.getUniqueId(), 0.0F);
            }

            int manaInt = this.cache.get(player.getUniqueId()).intValue();
            if (manaInt % 10 == 0) {
               player.sendMessage(ChatColor.AQUA + pvpClass.getName() + " Energy: " + ChatColor.GREEN + manaInt);
            }
         }
      }
   }

   public Map<UUID, Float> getCache() {
      return this.cache;
   }

   public Map<UUID, Long> getLastEffectUsage() {
      return this.lastEffectUsage;
   }
}
