package net.frozenorb.foxtrot.listener;

import cc.fyre.universe.util.BungeeUtil;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PracticeListener implements Listener {
   @EventHandler(priority = EventPriority.MONITOR)
   private void onMove(PlayerMoveEvent event) {
      if (!event.isCancelled()) {
         Player player = event.getPlayer();
         Location from = event.getFrom();
         Location to = event.getTo();
         if (to.getBlockX() != from.getBlockX() || to.getBlockZ() != from.getBlockZ() || to.getBlockY() != from.getBlockY()) {
            if (DTRBitmask.SAFE_ZONE.appliesAt(to)) {
               if (to.getBlock().getType().name().contains("WATER")) {
                  Block downBlock = to.getBlock().getRelative(BlockFace.DOWN);
                  if (downBlock.getType().equals(Material.QUARTZ_BLOCK) || downBlock.getRelative(BlockFace.DOWN).getType().equals(Material.QUARTZ_BLOCK)) {
                     player.playSound(player.getLocation(), Sound.PORTAL_TRAVEL, 1.0F, 1.0F);
                     player.sendMessage(ChatColor.GREEN + "Warping...");
                     player.teleport(player.getWorld().getSpawnLocation().clone());
                     BungeeUtil.sendToServer(player, "NA-Practice");
                  }
               }
            }
         }
      }
   }
}
