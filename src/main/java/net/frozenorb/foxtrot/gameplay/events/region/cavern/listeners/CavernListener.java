package net.frozenorb.foxtrot.gameplay.events.region.cavern.listeners;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.region.cavern.Cavern;
import net.frozenorb.foxtrot.gameplay.events.region.cavern.CavernHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class CavernListener implements Listener {
   @EventHandler(priority = EventPriority.HIGHEST)
   public void onBlockBreak(BlockBreakEvent event) {
      Location location = event.getBlock().getLocation();
      CavernHandler cavernHandler = Foxtrot.getInstance().getCavernHandler();
      Cavern cavern = cavernHandler.getCavern();
      Team teamAt = LandBoard.getInstance().getTeam(location);
      if (!Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getPlayer(), location) && cavernHandler.hasCavern()) {
         if (teamAt != null && teamAt.getName().equals(CavernHandler.getCavernTeamName())) {
            if (cavern.getOres().containsKey(Cavern.toString(location))) {
               event.setCancelled(false);
            }
         }
      }
   }
}
