package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class BorderListener implements Listener {
   public static int BORDER_SIZE = 3000;

   @EventHandler
   public void onBlockPlace(BlockPlaceEvent event) {
      if (Math.abs(event.getBlock().getX()) > this.getBorderSize(event.getBlock().getWorld())
         || Math.abs(event.getBlock().getZ()) > this.getBorderSize(event.getBlock().getWorld())) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onBlockBreak(BlockBreakEvent event) {
      if (Math.abs(event.getBlock().getX()) > this.getBorderSize(event.getBlock().getWorld())
         || Math.abs(event.getBlock().getZ()) > this.getBorderSize(event.getBlock().getWorld())) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onPlayerPortal(PlayerPortalEvent event) {
      if (Math.abs(event.getTo().getBlockX()) > this.getBorderSize(event.getTo().getWorld())
         || Math.abs(event.getTo().getBlockZ()) > this.getBorderSize(event.getTo().getWorld())) {
         Location newLocation = event.getTo().clone();

         while (Math.abs(newLocation.getX()) > this.getBorderSize(newLocation.getWorld())) {
            newLocation.setX(newLocation.getX() - (newLocation.getX() > 0.0 ? 1 : -1));
         }

         while (Math.abs(newLocation.getZ()) > this.getBorderSize(newLocation.getWorld())) {
            newLocation.setZ(newLocation.getZ() - (newLocation.getZ() > 0.0 ? 1 : -1));
         }

         event.setTo(newLocation);
         event.getPlayer().sendMessage(ChatColor.YELLOW + "That portal's location is past the border. It has been moved inwards.");
      }
   }

   @EventHandler
   public void onPlayerTeleport(PlayerTeleportEvent event) {
      if (event.getTo().getWorld().equals(event.getFrom().getWorld())) {
         if (!(event.getTo().distance(event.getFrom()) < 0.0) && event.getCause() != TeleportCause.PLUGIN) {
            if (Math.abs(event.getTo().getBlockX()) > this.getBorderSize(event.getTo().getWorld())
               || Math.abs(event.getTo().getBlockZ()) > this.getBorderSize(event.getTo().getWorld())) {
               Location newLocation = event.getTo().clone();

               while (Math.abs(newLocation.getX()) > this.getBorderSize(newLocation.getWorld())) {
                  newLocation.setX(newLocation.getX() - (newLocation.getX() > 0.0 ? 1 : -1));
               }

               while (Math.abs(newLocation.getZ()) > this.getBorderSize(newLocation.getWorld())) {
                  newLocation.setZ(newLocation.getZ() - (newLocation.getZ() > 0.0 ? 1 : -1));
               }

               while (newLocation.getBlock().getType() != Material.AIR) {
                  newLocation.setY(newLocation.getBlockY() + 1);
               }

               event.setTo(newLocation);
               event.getPlayer().sendMessage(ChatColor.YELLOW + "That location is past the border.");
            }
         }
      }
   }

   @EventHandler
   public void onPlayerMove(PlayerMoveEvent event) {
      Location from = event.getFrom();
      Location to = event.getTo();
      if ((from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ() || from.getBlockY() != to.getBlockY())
         && (
            Math.abs(event.getTo().getBlockX()) > this.getBorderSize(event.getTo().getWorld())
               || Math.abs(event.getTo().getBlockZ()) > this.getBorderSize(event.getTo().getWorld())
         )) {
         if (event.getPlayer().getVehicle() != null) {
            event.getPlayer().getVehicle().eject();
         }

         Location newLocation = event.getTo().clone();
         int tries = 0;

         while (Math.abs(newLocation.getX()) > this.getBorderSize(newLocation.getWorld()) && tries++ < 100) {
            newLocation.setX(newLocation.getX() - (newLocation.getX() > 0.0 ? 1 : -1));
         }

         if (tries >= 99) {
            Foxtrot.getInstance()
               .getLogger()
               .severe(" The server would have crashed while doing border checks! New X: " + newLocation.getX() + ", Old X: " + event.getTo().getBlockX());
            return;
         }

         tries = 0;

         while (Math.abs(newLocation.getZ()) > this.getBorderSize(newLocation.getWorld()) && tries++ < 100) {
            newLocation.setZ(newLocation.getZ() - (newLocation.getZ() > 0.0 ? 1 : -1));
         }

         if (tries >= 99) {
            Foxtrot.getInstance()
               .getLogger()
               .severe("The server would have crashed while doing border checks! New Z: " + newLocation.getZ() + ", Old Z: " + event.getTo().getBlockZ());
            return;
         }

         tries = 0;

         while (newLocation.getBlock().getType() != Material.AIR && tries++ < 100) {
            newLocation.setY(newLocation.getBlockY() + 1);
         }

         if (tries >= 99) {
            Foxtrot.getInstance()
               .getLogger()
               .severe(" The server would have crashed while doing border checks! New Y: " + newLocation.getY() + ", Old Y: " + event.getTo().getBlockY());
            return;
         }

         event.setTo(newLocation);
         event.getPlayer().sendMessage(ChatColor.YELLOW + "You have hit the border!");
      }
   }

   public int getBorderSize(World world) {
      if (world.getEnvironment() == Environment.NETHER) {
         return Foxtrot.getInstance().getConfig().getInt("netherBorderSize", 5000);
      } else {
         return Foxtrot.getInstance().getMapHandler().isKitMap() ? Foxtrot.getInstance().getConfig().getInt("kitMapBorderSize", 2000) : BORDER_SIZE;
      }
   }
}
