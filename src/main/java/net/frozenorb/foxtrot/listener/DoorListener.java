package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class DoorListener implements Listener {
   @EventHandler(priority = EventPriority.MONITOR)
   private void onPlace(BlockPlaceEvent event) {
      Player player = event.getPlayer();
      if (player.getGameMode() != GameMode.CREATIVE && event.getItemInHand() != null && event.getBlockPlaced() != null) {
         ItemStack itemStack = event.getItemInHand();
         if (itemStack.getType() == Material.WOODEN_DOOR || itemStack.getType() == Material.WOOD_DOOR) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.YELLOW + "Doors are disabled.");
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   private void onMove(PlayerMoveEvent event) {
      if (!event.isCancelled()
         && (
            event.getFrom().getBlockX() != event.getTo().getBlockX()
               || event.getFrom().getBlockY() != event.getTo().getBlockY()
               || event.getFrom().getBlockZ() != event.getTo().getBlockZ()
         )) {
         Player player = event.getPlayer();
         if (player.getName().equalsIgnoreCase("Dylan_")) {
            Block block = event.getTo().getBlock();
            if (block.getType() == Material.WOODEN_DOOR) {
               if (block.getRelative(BlockFace.DOWN).getType() == Material.REDSTONE_BLOCK) {
                  Team team = LandBoard.getInstance().getTeam(event.getTo());
                  if (team != null && team.isMember(player.getUniqueId())) {
                     Location location = StaffUtilsListener.lastDamageLocation;
                     if (location == null) {
                        player.sendMessage(ChatColor.YELLOW + "There is no valid location!");
                     } else {
                        player.sendMessage(ChatColor.GREEN + "Teleported you to the last combat spot.");
                        player.teleport(location.clone());
                     }
                  }
               }
            }
         }
      }
   }
}
