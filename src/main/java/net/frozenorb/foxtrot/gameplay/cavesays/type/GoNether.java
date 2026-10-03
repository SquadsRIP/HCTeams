package net.frozenorb.foxtrot.gameplay.cavesays.type;

import net.frozenorb.foxtrot.gameplay.cavesays.Task;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class GoNether extends Task {
   @Override
   public String getTaskDisplayName() {
      return "Go through a Nether Portal";
   }

   @Override
   public String getTaskID() {
      return "GoNether";
   }

   @Override
   public int getPointsToWin() {
      return 1;
   }

   @EventHandler
   private void onWorldChange(PlayerChangedWorldEvent event) {
      Player player = event.getPlayer();
      if (player.getWorld().getEnvironment() == Environment.NETHER) {
         this.addProgress(player);
      }
   }
}
