package net.frozenorb.foxtrot.gameplay.events.cavenite;

import java.beans.ConstructorProperties;
import java.util.Arrays;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTask extends BukkitRunnable {
   private Foxtrot instance;
   private CaveNiteHandler caveNiteHandler;

   @Override
   public void run() {
      if (this.caveNiteHandler.getGameState() == CaveNiteState.RUNNING) {
         for (Location chestLocation : this.caveNiteHandler.getChestLocations()) {
            if (chestLocation.getBlock() != null
               && chestLocation.getBlock().getType() == Material.CHEST
               && !Arrays.stream(((Chest)chestLocation.getBlock().getState()).getInventory().getContents())
                  .noneMatch(it -> it != null && it.getType() != Material.AIR)) {
               chestLocation.getWorld().playEffect(chestLocation, Effect.ENDER_SIGNAL, 1);
               chestLocation.getWorld().playSound(chestLocation, Sound.PORTAL_TRAVEL, 0.03F, 1.0F);
            }
         }
      }
   }

   @ConstructorProperties({"instance", "caveNiteHandler"})
   public GameTask(Foxtrot instance, CaveNiteHandler caveNiteHandler) {
      this.instance = instance;
      this.caveNiteHandler = caveNiteHandler;
   }
}
