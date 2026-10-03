package net.frozenorb.foxtrot.gameplay.events.dtc;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class DTCListener implements Listener {
   @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
   public void onBreak(BlockBreakEvent event) {
      if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
         Location location = event.getBlock().getLocation();
         if (DTRBitmask.DTC.appliesAt(location)) {
            DTC activeDTC = (DTC)Foxtrot.getInstance()
               .getEventHandler()
               .getEvents()
               .stream()
               .filter(Event::isActive)
               .filter(ev -> ev.getType() == EventType.DTC)
               .findFirst()
               .orElse(null);
            if (activeDTC != null) {
               if (event.getBlock().getType() == Material.OBSIDIAN) {
                  event.setCancelled(true);
                  activeDTC.blockBroken(event.getPlayer());
               }
            }
         }
      }
   }
}
