package net.frozenorb.foxtrot.gameplay.bosses;

import cc.fyre.proton.event.HalfHourEvent;
import cc.fyre.proton.util.ClassUtils;
import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class BossHandler implements Listener {
   private Foxtrot instance;
   private List<Boss> bosses = new ArrayList<>();
   private Boss activeBoss;

   public BossHandler(Foxtrot foxtrot) {
      this.instance = foxtrot;

      for (Class clazz : ClassUtils.getClassesInPackage(Foxtrot.getInstance(), "net.frozenorb.foxtrot.gameplay.bosses.type")) {
         if (Boss.class.isAssignableFrom(clazz)) {
            try {
               this.bosses.add((Boss)clazz.newInstance());
            } catch (IllegalAccessException | InstantiationException reflectiveOperationException) {
               reflectiveOperationException.printStackTrace();
            }
         }
      }

      this.instance.getServer().getPluginManager().registerEvents(this, this.instance);
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onHour(HalfHourEvent halfHourEvent) {
   }

   public Boss findBoss(String string) {
      return this.bosses.stream().filter(boss -> boss.getBossID().equalsIgnoreCase(string)).findFirst().orElse(null);
   }

   public void activateRandom() {
   }

   public List<Boss> getBosses() {
      return this.bosses;
   }

   public Boss getActiveBoss() {
      return this.activeBoss;
   }

   public void setActiveBoss(Boss boss) {
      this.activeBoss = boss;
   }
}
