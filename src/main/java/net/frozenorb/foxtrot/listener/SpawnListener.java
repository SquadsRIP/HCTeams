package net.frozenorb.foxtrot.listener;

import cc.fyre.proton.Proton;
import cc.fyre.proton.util.PlayerUtils;
import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.foxtrot.util.RegenUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class SpawnListener implements Listener {
   @EventHandler(priority = EventPriority.HIGH)
   public void onBlockIgnite(BlockIgniteEvent event) {
      if (event.getPlayer() == null || !Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
         if (DTRBitmask.SAFE_ZONE.appliesAt(event.getBlock().getLocation())) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onBlockPlace(final BlockPlaceEvent event) {
      if (!event.getPlayer().hasMetadata("ADMIN_BUILD")) {
         if (!Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            if (DTRBitmask.SAFE_ZONE.appliesAt(event.getBlock().getLocation())) {
               event.setCancelled(true);
               event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build in spawn!");
            } else if (DTRBitmask.ROAD.appliesAt(event.getBlock().getLocation())) {
               event.setCancelled(true);
               event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build on a road!");
            } else if (Foxtrot.getInstance().getServerHandler().isSpawnBufferZone(event.getBlock().getLocation())
               || Foxtrot.getInstance().getServerHandler().isNetherBufferZone(event.getBlock().getLocation())) {
               if (!DTRBitmask.SAFE_ZONE.appliesAt(event.getBlock().getLocation())
                  && event.getItemInHand() != null
                  && event.getItemInHand().getType() == Material.WEB
                  && Foxtrot.getInstance().getMapHandler().isKitMap()) {
                  if (event.getBlockAgainst() != null && event.getBlockAgainst().getType().name().contains("WATER")
                     || event.getBlock() != null && event.getBlock().getType().name().contains("WATER")) {
                     event.setCancelled(true);
                     event.getPlayer().sendMessage(ChatColor.YELLOW + "Can't place cobwebs on Water!");
                     return;
                  }

                  for (Event playableEvent : Foxtrot.getInstance().getEventHandler().getEvents()) {
                     KOTH koth;
                     if (playableEvent.isActive() && playableEvent instanceof KOTH && (koth = (KOTH)playableEvent).onCap(event.getBlockPlaced().getLocation())) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.YELLOW + "You can't place web on cap!");
                        event.getPlayer().setItemInHand(null);
                        event.getPlayer().setMetadata("ImmuneFromGlitchCheck", new FixedMetadataValue(Foxtrot.getInstance(), new Object()));
                        Bukkit.getScheduler()
                           .runTask(Foxtrot.getInstance(), () -> event.getPlayer().removeMetadata("ImmuneFromGlitchCheck", Foxtrot.getInstance()));
                        return;
                     }
                  }

                  if (DTRBitmask.CITADEL.appliesAt(event.getBlock().getLocation())) {
                     event.setCancelled(true);
                     event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build on Citadel!");
                     event.getPlayer().setMetadata("ImmuneFromGlitchCheck", new FixedMetadataValue(Foxtrot.getInstance(), new Object()));
                     Bukkit.getScheduler()
                        .runTask(Foxtrot.getInstance(), () -> event.getPlayer().removeMetadata("ImmuneFromGlitchCheck", Foxtrot.getInstance()));
                     return;
                  }

                  if (Proton.getInstance().getAutoRebootHandler().isRebooting()) {
                     event.setCancelled(true);
                     event.getPlayer().sendMessage(ChatColor.YELLOW + "May not place webs whilst the server is rebooting.");
                     return;
                  }

                  (new BukkitRunnable() {
                     @Override
                     public void run() {
                        if (event.getBlock().getType() == Material.WEB) {
                           event.getBlock().setType(Material.AIR);
                        }
                     }
                  }).runTaskLater(Foxtrot.getInstance(), 200L);
               } else {
                  event.setCancelled(true);
                  event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build this close to spawn!");
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onBlockBreak(BlockBreakEvent event) {
      if (!event.getPlayer().hasMetadata("ADMIN_BUILD")) {
         if (!Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            if (DTRBitmask.SAFE_ZONE.appliesAt(event.getBlock().getLocation())) {
               event.setCancelled(true);
               event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot break blocks in spawn!");
            } else if (DTRBitmask.ROAD.appliesAt(event.getBlock().getLocation())) {
               event.setCancelled(true);
               event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot break blocks on a road!");
            } else if (!DTRBitmask.DTC.appliesAt(event.getBlock().getLocation())
               && (
                  Foxtrot.getInstance().getServerHandler().isSpawnBufferZone(event.getBlock().getLocation())
                     || Foxtrot.getInstance().getServerHandler().isNetherBufferZone(event.getBlock().getLocation())
               )) {
               event.setCancelled(true);
               if (event.getBlock().getType() != Material.LONG_GRASS && event.getBlock().getType() != Material.GRASS) {
                  event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build this close to spawn!");
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void onHangingPlace(HangingPlaceEvent event) {
      if (!Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
         if (DTRBitmask.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
      if (event.getRemover() instanceof Player && !Foxtrot.getInstance().getServerHandler().isAdminOverride((Player)event.getRemover())) {
         if (DTRBitmask.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
      if (event.getRightClicked().getType() == EntityType.ITEM_FRAME && !Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
         if (DTRBitmask.SAFE_ZONE.appliesAt(event.getRightClicked().getLocation())) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Player
         && event.getEntity().getType() == EntityType.ITEM_FRAME
         && !Foxtrot.getInstance().getServerHandler().isAdminOverride((Player)event.getDamager())) {
         if (DTRBitmask.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler
   public void onBucketEmpty(PlayerBucketEmptyEvent event) {
      if (!Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
         if (Foxtrot.getInstance().getServerHandler().isSpawnBufferZone(event.getBlockClicked().getLocation())) {
            if (!Foxtrot.getInstance().getServerHandler().isWaterPlacementInClaimsAllowed()) {
               event.setCancelled(true);
            } else {
               Block waterBlock = event.getBlockClicked().getRelative(event.getBlockFace());
               if (waterBlock.getRelative(BlockFace.NORTH).isLiquid()
                  || waterBlock.getRelative(BlockFace.SOUTH).isLiquid()
                  || waterBlock.getRelative(BlockFace.EAST).isLiquid()
                  || waterBlock.getRelative(BlockFace.WEST).isLiquid()) {
                  event.setCancelled(true);
                  return;
               }

               RegenUtils.schedule(waterBlock, 30, TimeUnit.SECONDS, block -> InventoryUtils.fillBucket(event.getPlayer()), block -> true);
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onEntityDamage(EntityDamageEvent event) {
      if (!event.getEntity().getWorld().getName().equalsIgnoreCase("iMake") || event.getCause() != DamageCause.PROJECTILE) {
         if ((event.getEntity() instanceof Player || event.getEntity() instanceof Horse) && DTRBitmask.SAFE_ZONE.appliesAt(event.getEntity().getLocation())) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onEntityDamageByEntity2(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Player) {
         Player damager = PlayerUtils.getDamageSource(event.getDamager());
         if (damager != null
            && (DTRBitmask.SAFE_ZONE.appliesAt(((Player)event.getEntity()).getLocation()) || DTRBitmask.SAFE_ZONE.appliesAt(damager.getLocation()))) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onDrop(PlayerDropItemEvent event) {
      Team team = LandBoard.getInstance().getTeam(event.getPlayer().getLocation());
      if (team != null && team.hasDTRBitmask(DTRBitmask.SAFE_ZONE) && Foxtrot.getInstance().getMapHandler().isKitMap()) {
         if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            event.getItemDrop().remove();
         } else if (CustomTimerCreateCommand.isSOTWTimer()) {
            event.getItemDrop().remove();
         }
      }
   }
}
