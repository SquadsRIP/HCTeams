package net.frozenorb.foxtrot.listener;

import cc.fyre.proton.util.PlayerUtils;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Wither;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class BasicPreventionListener implements Listener {
   private static final List<EntityType> PREVENT_MOBS = Arrays.asList(EntityType.CREEPER, EntityType.SKELETON, EntityType.ZOMBIE, EntityType.SPIDER);
   private static final List<String> NAMES = Arrays.asList("SimplyTrash", "Rowin");

   @EventHandler
   public void onProjectileHit(ProjectileHitEvent event) {
      if (event.getEntity() instanceof Arrow) {
         event.getEntity().remove();
      }
   }

   @EventHandler
   public void onPlayerQuit(PlayerKickEvent event) {
      event.setLeaveMessage(null);
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void onEntityChangeBlock(EntityChangeBlockEvent event) {
      if (event.getEntity() instanceof Wither) {
         event.setCancelled(true);
      }

      if (DTRBitmask.SAFE_ZONE.appliesAt(event.getBlock().getLocation())) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent event) {
      Player player = event.getPlayer();
      if (event.getAction() == Action.RIGHT_CLICK_BLOCK
         && player.getGameMode() != GameMode.CREATIVE
         && event.getClickedBlock() != null
         && event.getClickedBlock().getType() == Material.ENDER_CHEST) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void onInventoryOpen(InventoryOpenEvent event) {
      if (event.getInventory().getType() == InventoryType.ENDER_CHEST) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
      String message = event.getMessage();
      Player player = event.getPlayer();
      if ((message.startsWith("/co l") || message.startsWith("/core l") || message.startsWith("/coreprotect l"))
         && !NAMES.contains(player.getName())
         && this.messageContainsName(message)) {
         event.getPlayer().sendMessage(ChatColor.YELLOW + "No permission.");
         event.setCancelled(true);
      }
   }

   private boolean messageContainsName(String message) {
      for (String name : NAMES) {
         if (StringUtils.containsIgnoreCase(message, name)) {
            return true;
         }
      }

      return false;
   }

   @EventHandler
   public void onVehicleEnter(VehicleEnterEvent event) {
      if (event.getVehicle() instanceof Horse && event.getEntered() instanceof Player) {
         Horse horse = (Horse)event.getVehicle();
         Player player = (Player)event.getEntered();
         if (horse.getOwner() != null && !horse.getOwner().getName().equals(player.getName())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.YELLOW + "This is not your horse!");
         }
      }
   }

   @EventHandler
   public void onFoodLevelChange(FoodLevelChangeEvent event) {
      if (CustomTimerCreateCommand.isSOTWTimer()) {
         event.setCancelled(true);
      } else if (event.getFoodLevel() < ((Player)event.getEntity()).getFoodLevel()) {
         if (DTRBitmask.SAFE_ZONE.appliesAt(event.getEntity().getLocation())
            || Foxtrot.getInstance().getPvPTimerMap().hasTimer(event.getEntity().getUniqueId())
            || ThreadLocalRandom.current().nextInt(100) >= Foxtrot.getInstance().getConfig().getInt("hungerLossChance", 15)) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler
   public void onPlayerRespawn(PlayerRespawnEvent event) {
      if (!Foxtrot.getInstance().getDeathbanArenaHandler().isDeathbanArena(event.getPlayer())) {
         Bukkit.getScheduler().scheduleSyncDelayedTask(Foxtrot.getInstance(), () -> {
            event.getPlayer().setMetadata("PVP_TIMER_BYPASS", new FixedMetadataValue(Foxtrot.getInstance(), true));
            Foxtrot.getInstance().getPvPTimerMap().createTimer(event.getPlayer().getUniqueId(), 1800);
         }, 20L);
         event.setRespawnLocation(Foxtrot.getInstance().getServerHandler().getSpawnLocation());
      }
   }

   @EventHandler
   public void onBlockBurn(BlockBurnEvent event) {
      if (Foxtrot.getInstance().getServerHandler().isWarzone(event.getBlock().getLocation())) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onPlace(BlockPlaceEvent event) {
      if (Foxtrot.getInstance().getServerHandler().isSkybridgePrevention()
         && 110.0 < event.getBlock().getLocation().getY()
         && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
         event.getPlayer().sendMessage(ChatColor.YELLOW + "You can't build higher than 110 blocks.");
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onFish(PlayerInteractEvent event) {
      if ((Foxtrot.getInstance().getServerHandler().isEOTW() || Foxtrot.getInstance().getServerHandler().isRodPrevention())
         && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
         && event.getPlayer().getItemInHand() != null
         && event.getPlayer().getItemInHand().getType() == Material.FISHING_ROD) {
         event.getPlayer().sendMessage(ChatColor.YELLOW + "Rods are currently disabled!");
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onEntityExplodeCreeper(EntityExplodeEvent event) {
      if (event.getEntity() instanceof Creeper) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onCreatureSpawnSpawn(CreatureSpawnEvent event) {
      Team team;
      if (event.getSpawnReason() == SpawnReason.NATURAL
         && PREVENT_MOBS.contains(event.getEntityType())
         && (team = LandBoard.getInstance().getTeam(event.getLocation())) != null
         && team.hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onCreatureSpawnAnywhere(CreatureSpawnEvent event) {
      if (event.getSpawnReason() == SpawnReason.NATURAL
         && event.getEntityType() == EntityType.BLAZE
         && event.getLocation().getWorld().getEnvironment() == Environment.NETHER
         && Foxtrot.getInstance().getConfig().getBoolean("spawners.naturalBlazes", true)) {
         return;
      }

      if (event.getSpawnReason() == SpawnReason.NATURAL
         || event.getSpawnReason() == SpawnReason.CHUNK_GEN
         || event.getSpawnReason() == SpawnReason.MOUNT
         || event.getSpawnReason() == SpawnReason.JOCKEY) {
         event.setCancelled(true);
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void onCreatureSpawnWither(CreatureSpawnEvent event) {
      if (event.getSpawnReason() == SpawnReason.NATURAL
         && event.getEntity().getType() == EntityType.SKELETON
         && ((Skeleton)event.getEntity()).getSkeletonType() == SkeletonType.WITHER) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST)
   public void onBlockPlace(BlockPlaceEvent event) {
      if (event.getPlayer().getWorld().getEnvironment() == Environment.NETHER
         && (event.getBlock().getType() == Material.BED || event.getBlock().getType() == Material.BED_BLOCK)) {
         if (DTRBitmask.SAFE_ZONE.appliesAt(event.getBlockPlaced().getLocation())) {
            return;
         }

         if (event.isCancelled()) {
            event.getPlayer().setItemInHand(null);
         } else {
            event.getBlock().setType(Material.AIR);
         }

         event.getPlayer().getWorld().createExplosion(event.getBlock().getLocation(), 5.0F);
      }
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void onBlockIgnite(BlockIgniteEvent event) {
      if (event.getCause() == IgniteCause.SPREAD) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onFireBurn(BlockBurnEvent event) {
      if (Foxtrot.getInstance().getServerHandler().isWarzone(event.getBlock().getLocation())) {
         event.setCancelled(true);
      } else if (!Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(null, event.getBlock().getLocation())) {
         if (LandBoard.getInstance().getTeam(event.getBlock().getLocation()) != null) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler
   public void onEntityExplode(EntityExplodeEvent event) {
      event.blockList().clear();
   }

   @EventHandler
   public void onEntitySpawn(EntitySpawnEvent event) {
      EntityType type = event.getEntityType();
      if (type == EntityType.MINECART_TNT) {
         event.setCancelled(true);
      }
   }

   @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
   public void onDamageByEntity(EntityDamageByEntityEvent event) {
      if (CustomTimerCreateCommand.isSOTWTimer()) {
         Player damager = PlayerUtils.getDamageSource(event.getDamager());
         Entity damaged = event.getEntity();
         if (damaged instanceof Player) {
            if (damager != null && !CustomTimerCreateCommand.hasSOTWEnabled(damager.getUniqueId())) {
               event.setCancelled(true);
            }
         }
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void onDamage(EntityDamageEvent event) {
      if (event.getEntity() instanceof Player
         && CustomTimerCreateCommand.isSOTWTimer()
         && !CustomTimerCreateCommand.hasSOTWEnabled(event.getEntity().getUniqueId())) {
         event.setCancelled(true);
      }
   }
}
