package net.frozenorb.foxtrot.map.listener;

import java.util.List;
import net.minecraft.util.com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;

public class BlockDecayListeners implements Listener {
   private List<Material> PREVENTED_STATES = Lists.newArrayList(
      new Material[]{Material.DIRT, Material.GRASS, Material.WATER, Material.STATIONARY_WATER, Material.ICE, Material.SNOW_BLOCK, Material.SNOW}
   );

   @EventHandler
   public void onBlockFadeEvent(BlockFadeEvent event) {
      if (this.PREVENTED_STATES.contains(event.getNewState().getType())) {
         event.setCancelled(true);
      }

      BlockState newState = event.getBlock().getState();
      if (newState.getType().name().contains("SNOW")) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onBlockPhysicsEvent(BlockPhysicsEvent event) {
      if (event.getChangedType() == Material.GRASS) {
         event.setCancelled(true);
      }

      BlockState newState = event.getBlock().getState();
      if (newState.getType().name().contains("SNOW") || event.getChangedType().name().contains("SNOW")) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onSpread(BlockSpreadEvent event) {
      BlockState newState = event.getNewState();
      if (newState.getType().name().contains("SNOW")) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onLeavesDecayEvent(LeavesDecayEvent event) {
      event.setCancelled(true);
   }
}
