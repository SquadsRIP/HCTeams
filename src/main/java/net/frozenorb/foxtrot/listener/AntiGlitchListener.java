package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.MaterialUtils;
import net.minecraft.util.com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class AntiGlitchListener implements Listener {
   private static final ImmutableSet<BlockFace> SURROUNDING = ImmutableSet.of(
      BlockFace.SELF,
      BlockFace.NORTH,
      BlockFace.NORTH_EAST,
      BlockFace.NORTH_WEST,
      BlockFace.SOUTH,
      BlockFace.SOUTH_EAST,
      new BlockFace[]{BlockFace.SOUTH_WEST, BlockFace.EAST, BlockFace.WEST, BlockFace.UP}
   );

   @EventHandler(priority = EventPriority.LOW)
   private void onDrop(PlayerDropItemEvent event) {
      ItemStack itemStack = event.getItemDrop().getItemStack();
      Player player = event.getPlayer();
      if (itemStack.getType().equals(Material.WRITTEN_BOOK) || itemStack.getType().equals(Material.BOOK_AND_QUILL)) {
         event.getItemDrop().remove();
         player.sendMessage(ChatColor.RED + "These have been disabled!");
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onSpawn(BlockDispenseEvent event) {
      if (event.getItem() != null && event.getItem().getType().equals(Material.MINECART)) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onInteract(PlayerInteractEvent event) {
      Player player = event.getPlayer();
      if (event.getAction().name().contains("RIGHT")) {
         ItemStack itemStack = event.getItem();
         if (itemStack != null) {
            if (itemStack.getType().equals(Material.FIREWORK)) {
               event.setCancelled(true);
               player.sendMessage(ChatColor.RED + "Fireworks have been disabled.");
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   private void onBookPublish(PlayerEditBookEvent event) {
      if (!event.getPlayer().isOp()) {
         event.getPlayer().sendMessage(ChatColor.RED + "What are you doing with a book?");
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   private void onBookPublish(PlayerInteractEvent event) {
      if (event.getAction().name().contains("RIGHT")) {
         ItemStack itemStack = event.getItem();
         if (itemStack != null && itemStack.getType() == Material.BOOK_AND_QUILL) {
            if (!event.getPlayer().isOp()) {
               event.getPlayer().sendMessage(ChatColor.RED + "What are you doing with a book?");
               event.setCancelled(true);
            }
         }
      }
   }

   @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
   public void onBoatMove(VehicleMoveEvent event) {
      Location from = event.getFrom();
      Location to = event.getTo();
      if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
         Block block = to.getBlock();
         if (block.getType() == Material.FENCE_GATE) {
            event.getVehicle().teleport(from);
            Entity passenger = event.getVehicle().getPassenger();
            if (passenger instanceof Player) {
               ((Player)passenger).sendMessage(ChatColor.RED + "You can't move your boat into a fence gate.");
            }
         }
      }
   }

   @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
   public void onPlayerBoatMove(PlayerMoveEvent event) {
      Location from = event.getFrom();
      Location to = event.getTo();
      if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
         Player player = event.getPlayer();
         if (player.getVehicle() != null && player.getVehicle() instanceof Boat && to.getBlock().getType() == Material.FENCE_GATE) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You can't move your boat into a fence gate.");
         }
      }
   }

   @EventHandler
   public void onBlockPistonRetract(BlockPistonRetractEvent event) {
      if (event.getBlock().getType().name().contains("RAIL")) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onBlockPistonExtend(BlockPistonExtendEvent event) {
      for (Block block : event.getBlocks()) {
         if (block.getType().name().contains("RAIL")) {
            event.setCancelled(true);
            return;
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onVehicleExit(VehicleExitEvent event) {
      if (event.getExited() instanceof Player) {
         final Player player = (Player)event.getExited();
         Location location = player.getLocation();

         while (location.getBlock().getType().isSolid()) {
            location.add(0.0, 1.0, 0.0);
            if (location.getBlockY() == 255) {
               break;
            }
         }

         while (location.getBlock().getType().isSolid()) {
            location.subtract(0.0, 1.0, 0.0);
            if (location.getBlockY() == 1) {
               break;
            }
         }

         final Location locationFinal = location;
         (new BukkitRunnable() {
            @Override
            public void run() {
               player.teleport(locationFinal);
            }
         }).runTaskLater(Foxtrot.getInstance(), 1L);
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void onVehicleEnter(VehicleEnterEvent event) {
      if (!(event.getVehicle() instanceof Horse) && !(event.getVehicle() instanceof Minecart)) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST)
   public void onEntityDeath(EntityDeathEvent event) {
      if (event.getEntity().getWorld().getEnvironment() == Environment.NETHER) {
         if (event.getEntity() instanceof Skeleton) {
            event.getDrops().removeIf(item -> item.getType() == Material.SKULL_ITEM);
         }
      }
   }

   @EventHandler
   public void onBlockPlace(BlockPlaceEvent event) {
      Player player = event.getPlayer();
      if (player.getGameMode() != GameMode.CREATIVE && player.getWorld().getEnvironment() == Environment.NETHER) {
         if (event.getBlock().getType() == Material.MOB_SPAWNER) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You aren't allowed to place mob spawners in the nether.");
         }
      }
   }

   @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
   public void denyDismountClipping(VehicleExitEvent event) {
      if (event.getExited() instanceof Player) {
         Player player = (Player)event.getExited();
         Location pLoc = player.getLocation();
         Location vLoc = event.getVehicle().getLocation();
         if (player.getLocation().getY() > 250.0) {
            pLoc.add(0.0, 10.0, 0.0);
         } else if (!MaterialUtils.isFullBlock(vLoc.add(0.0, 1.0, 0.0).getBlock().getType())) {
            if (!MaterialUtils.isFullBlock(vLoc.getBlock().getType())) {
               pLoc = new Location(vLoc.getWorld(), vLoc.getBlockX() + 0.5, vLoc.getBlockY(), vLoc.getBlockZ() + 0.5, pLoc.getYaw(), pLoc.getPitch());
            } else {
               pLoc.subtract(0.0, 1.0, 0.0);
            }
         }

         Location finalLocation = pLoc;
         Bukkit.getScheduler().runTask(Foxtrot.getInstance(), () -> player.teleport(finalLocation));
      }
   }

   @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
   public void denyDismountClipping(CreatureSpawnEvent event) {
      if (event.getEntityType() == EntityType.HORSE) {
         if (!Foxtrot.getInstance().getServerHandler().isHardcore()) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
   public void denyDismountGlitching(PlayerDeathEvent event) {
      if (event.getEntity().isInsideVehicle()) {
         event.getEntity().getVehicle().remove();
      }
   }

   @EventHandler
   public void denyMinecartSpawns(VehicleCreateEvent event) {
      EntityType type = event.getVehicle().getType();
      if (type == EntityType.MINECART_HOPPER || type == EntityType.MINECART_CHEST) {
         event.getVehicle().remove();
      }
   }

   @EventHandler
   public void onChunkLoad(ChunkLoadEvent event) {
      Chunk chunk = event.getChunk();

      for (Entity entity : chunk.getEntities()) {
         if (entity.getType() == EntityType.MINECART_CHEST) {
            entity.remove();
         }
      }
   }
}
