package net.frozenorb.foxtrot.gameplay.cavesays.type;

import com.vexsoftware.votifier.model.VotifierEvent;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.cavesays.Task;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class Vote extends Task {
   @Override
   public String getTaskDisplayName() {
      return "Vote for the Server (/vote)";
   }

   @Override
   public String getTaskID() {
      return "Vote";
   }

   @Override
   public int getPointsToWin() {
      return 1;
   }

   @EventHandler
   private void onWorldChange(VotifierEvent event) {
      Player player = Foxtrot.getInstance().getServer().getPlayer(event.getVote().getUsername());
      if (player != null) {
         this.addProgress(player);
      }
   }
}
