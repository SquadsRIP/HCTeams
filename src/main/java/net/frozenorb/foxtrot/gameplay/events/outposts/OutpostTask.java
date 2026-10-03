package net.frozenorb.foxtrot.gameplay.events.outposts;

import java.beans.ConstructorProperties;
import net.frozenorb.foxtrot.gameplay.events.outposts.data.Outpost;
import org.bukkit.scheduler.BukkitRunnable;

public class OutpostTask extends BukkitRunnable {
   private final OutpostHandler outpostHandler;

   @Override
   public void run() {
      for (Outpost outpost : this.outpostHandler.getOutposts()) {
         outpost.updatePercentage();
         outpost.updateHologram();
         outpost.checkOld();
         if (outpost.findController() == null && outpost.getPercentage().get() >= 100.0) {
            outpost.updateController();
         } else if (outpost.findController() != null && outpost.getPercentage().get() <= 0.0) {
            outpost.setController(null);
         }
      }
   }

   @ConstructorProperties("outpostHandler")
   public OutpostTask(OutpostHandler outpostHandler) {
      this.outpostHandler = outpostHandler;
   }
}
