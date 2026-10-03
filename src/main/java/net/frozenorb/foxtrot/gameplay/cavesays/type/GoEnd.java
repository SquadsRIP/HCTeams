package net.frozenorb.foxtrot.gameplay.cavesays.type;

import net.frozenorb.foxtrot.gameplay.cavesays.Task;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class GoEnd extends Task {
   @Override
   public String getTaskDisplayName() {
      return "Go through an End Portal";
   }

   @Override
   public String getTaskID() {
      return "GoEnd";
   }

   @Override
   public int getPointsToWin() {
      return 1;
   }

   @EventHandler
   private void onWorldChange(PlayerChangedWorldEvent event) {
      Player player = event.getPlayer();
      if (player.getWorld().getEnvironment() == Environment.THE_END) {
         this.addProgress(player);
      }
   }
}
