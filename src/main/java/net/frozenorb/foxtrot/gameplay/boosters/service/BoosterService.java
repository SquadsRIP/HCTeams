package net.frozenorb.foxtrot.gameplay.boosters.service;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.boosters.Booster;
import net.frozenorb.foxtrot.gameplay.boosters.NetworkBoosterHandler;
import org.bukkit.scheduler.BukkitRunnable;

public class BoosterService extends BukkitRunnable {
   private Foxtrot instance;
   private NetworkBoosterHandler networkBoosterHandler;

   @Override
   public void run() {
      for (Entry<Booster, UUID> entry : this.networkBoosterHandler.getActiveBoosters().entrySet()) {
         if (entry.getKey().getActivatedAt() + TimeUnit.HOURS.toMillis(1L) <= System.currentTimeMillis()) {
            entry.getKey().deactivate();
         }
      }

      Map<UUID, List<Booster>> queuedBoosters = new HashMap<>(this.networkBoosterHandler.getBoostersQueued());

      for (Entry<UUID, List<Booster>> entry : queuedBoosters.entrySet()) {
         for (Booster booster : new ArrayList<>(entry.getValue())) {
            if (!booster.isActive() && booster.getLastDeactivateAt() + TimeUnit.HOURS.toMillis(1L) < System.currentTimeMillis()) {
               booster.activate(entry.getKey());
               List<Booster> secondBoosters = new ArrayList<>(entry.getValue());
               secondBoosters.remove(booster);
               if (secondBoosters.isEmpty()) {
                  this.networkBoosterHandler.getBoostersQueued().remove(entry.getKey());
               } else {
                  this.networkBoosterHandler.getBoostersQueued().put(entry.getKey(), secondBoosters);
               }
            }
         }
      }
   }

   @ConstructorProperties({"instance", "networkBoosterHandler"})
   public BoosterService(Foxtrot instance, NetworkBoosterHandler networkBoosterHandler) {
      this.instance = instance;
      this.networkBoosterHandler = networkBoosterHandler;
   }
}
