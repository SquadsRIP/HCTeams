package net.frozenorb.foxtrot.listener;

import cc.fyre.modsuite.mod.ModHandler;
import cc.fyre.neutron.util.PlayerUtil;
import cc.fyre.proton.Proton;
import cc.fyre.proton.util.PlayerUtils;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.gameplay.events.region.cavern.CavernHandler;
import net.frozenorb.foxtrot.gameplay.events.region.glowmtn.GlowHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.team.event.PlayerBuildInOthersClaimEvent;
import net.frozenorb.foxtrot.team.event.TeamEnterClaimEvent;
import net.frozenorb.foxtrot.team.event.TeamRaidableEvent;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.foxtrot.util.RegenUtils;
import net.minecraft.util.com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.scheduler.BukkitRunnable;

public class TeamListener implements Listener {
   @EventHandler
   public void onPlayerLogin(PlayerLoginEvent event) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());
      if (team != null && team.getMaxOnline() > 0 && team.getOnlineMemberAmount() >= team.getMaxOnline()) {
         event.disallow(Result.KICK_OTHER, ChatColor.YELLOW + "Your team currently has too many players logged in. Try again later!");
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onEnterClaim(TeamEnterClaimEvent event) {
      Player player = event.getPlayer();
      if (player.getGameMode() != GameMode.CREATIVE) {
         Team teamTo = event.getToTeam();
         if (teamTo != null && teamTo.getOwner() == null) {
            if (teamTo.hasDTRBitmask(DTRBitmask.SAFE_ZONE) && !player.getWorld().getName().equalsIgnoreCase("Spawn")) {
               PlayerUtil.sendTitle(player, "&4&lSpawn", "Type &c/spawn &fto teleport to the main spawn!");
            } else if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
               if (teamTo.hasDTRBitmask(DTRBitmask.CITADEL) || teamTo.hasDTRBitmask(DTRBitmask.FURY)) {
                  if (!ModHandler.INSTANCE.isInModMode(player.getUniqueId())) {
                     Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
                     int members = team == null ? 1 : team.getOnlineMembers().size();
                     if (members < 4) {
                        event.setCancelled(true);
                        player.sendMessage(ChatColor.RED + "You may not enter " + teamTo.getName(player) + ChatColor.RED + " as a " + members + " man faction.");
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onPhysics(BlockPhysicsEvent event) {
      if (event.getBlock() != null && event.getBlock().getType() == Material.DIRT) {
         if (event.getChangedType() == Material.GRASS && DTRBitmask.ROAD.appliesAt(event.getBlock().getLocation())) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler
   private void onRaidable(TeamRaidableEvent event) {
      Team team = event.getTeam();
      Player killer = event.getKiller();
      team.recalculatePoints();
      team.setRemovedPoints(team.getRemovedPoints() + team.getPoints() / 2);
      team.recalculatePoints();
      if (killer != null) {
         Team killerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(killer);
         if (killerTeam != null) {
            TeamActionTracker.logActionAsync(
               killerTeam,
               TeamActionType.MADE_FACTION_RAIDABLE,
               ImmutableMap.of(
                  "playerId",
                  event.getPlayerUUID().toString(),
                  "killerId",
                  killer.getUniqueId().toString(),
                  "factionRaidable",
                  killerTeam.getUniqueId().toString()
               )
            );
            killerTeam.setAddedPoints(killerTeam.getAddedPoints() + team.recalculatePoints() / 4);
            killerTeam.setFactionsMadeRaidable(killerTeam.getFactionsMadeRaidable() + 1);
            killerTeam.sendMessage("");
            killerTeam.sendMessage(ChatColor.GREEN + "Your faction has gained a raidable point.");
            killerTeam.sendMessage("");
            killerTeam.sendTitle("&4&lRaidable", "&fYour faction has made &c" + team.getName() + " &fraidable!");
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onAntiRaidSteal(TeamRaidableEvent event) {
      Player killer = event.getKiller();
      if (killer != null) {
         Team team = event.getTeam();
         Team killerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(killer);
         if (team != null && killerTeam != null) {
            team.setRaidStealTeam(killerTeam.getUniqueId());
            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> team.setRaidStealTeam(null), 400L);
         }
      }
   }

   @EventHandler
   public void onPlayerJoin(final PlayerJoinEvent event) {
      final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());
      if (team != null) {
         for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (team.isMember(player.getUniqueId())) {
               player.sendMessage(ChatColor.GREEN + "Member Online: " + ChatColor.WHITE + event.getPlayer().getName());
            } else if (team.getAllies().size() != 0 && team.isAlly(player.getUniqueId())) {
               player.sendMessage(Team.ALLY_COLOR + "Ally Online: " + ChatColor.WHITE + event.getPlayer().getName());
            }
         }

         TeamActionTracker.logActionAsync(
            team, TeamActionType.MEMBER_CONNECTED, ImmutableMap.of("playerId", event.getPlayer().getUniqueId(), "playerName", event.getPlayer().getName())
         );
         (new BukkitRunnable() {
            @Override
            public void run() {
               team.sendTeamInfo(event.getPlayer());
            }
         }).runTaskAsynchronously(Foxtrot.getInstance());
      } else {
         event.getPlayer().sendMessage(ChatColor.GRAY + "You are not in a faction!");
      }
   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent event) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());
      if (team != null) {
         for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (!player.equals(event.getPlayer())) {
               if (team.isMember(player.getUniqueId())) {
                  player.sendMessage(ChatColor.RED + "Member Offline: " + ChatColor.WHITE + event.getPlayer().getName());
               } else if (team.isAlly(player.getUniqueId())) {
                  player.sendMessage(Team.ALLY_COLOR + "Ally Offline: " + ChatColor.WHITE + event.getPlayer().getName());
               }
            }
         }

         TeamActionTracker.logActionAsync(
            team, TeamActionType.MEMBER_DISCONNECTED, ImmutableMap.of("playerId", event.getPlayer().getUniqueId(), "playerName", event.getPlayer().getName())
         );
      }
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void onBlockIgnite(BlockIgniteEvent event) {
      if (event.getPlayer() == null || !Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
         if (!Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getPlayer(), event.getBlock().getLocation())) {
            if (LandBoard.getInstance().getTeam(event.getBlock().getLocation()) != null) {
               Team owner = LandBoard.getInstance().getTeam(event.getBlock().getLocation());
               if (event.getCause() == IgniteCause.FLINT_AND_STEEL && owner.isMember(event.getPlayer().getUniqueId())) {
                  return;
               }

               event.setCancelled(true);
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onBlockPlace(final BlockPlaceEvent event) {
      if (!Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())
         && !Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getPlayer(), event.getBlock().getLocation())) {
         if (!Foxtrot.getInstance().getServerHandler().isEOTW()) {
            Team team = LandBoard.getInstance().getTeam(event.getBlock().getLocation());
            if (team.isMember(event.getPlayer().getUniqueId())) {
               if (!team.isCoLeader(event.getPlayer().getUniqueId())
                  && !team.isCaptain(event.getPlayer().getUniqueId())
                  && !team.isOwner(event.getPlayer().getUniqueId())) {
                  Subclaim subclaim = team.getSubclaim(event.getBlock().getLocation());
                  if (subclaim != null && !subclaim.isMember(event.getPlayer().getUniqueId())) {
                     event.setCancelled(true);
                     event.getPlayer()
                        .sendMessage(
                           ChatColor.YELLOW + "You do not have access to the subclaim " + ChatColor.GREEN + subclaim.getName() + ChatColor.YELLOW + "!"
                        );
                  }
               }
            } else {
               if (!DTRBitmask.SAFE_ZONE.appliesAt(event.getBlock().getLocation())
                  && event.getItemInHand() != null
                  && event.getItemInHand().getType() == Material.WEB) {
                  for (Event playableEvent : Foxtrot.getInstance().getEventHandler().getEvents()) {
                     if (playableEvent.isActive() && playableEvent instanceof KOTH) {
                        KOTH koth = (KOTH)playableEvent;
                        if (koth.onCap(event.getBlockPlaced().getLocation())) {
                           event.setCancelled(true);
                           event.getPlayer().sendMessage(ChatColor.YELLOW + "You can't place web on cap!");
                           event.getPlayer().setItemInHand(null);
                           return;
                        }
                     }
                  }

                  if (DTRBitmask.CITADEL.appliesAt(event.getBlock().getLocation())) {
                     event.setCancelled(true);
                     event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build on Citadel!");
                     event.getPlayer().updateInventory();
                     return;
                  }

                  Block block = event.getBlockPlaced();
                  if (Foxtrot.getInstance().getServerHandler().isCloseToSpawn(block.getLocation())) {
                     event.setCancelled(true);
                     event.getPlayer().sendMessage(ChatColor.RED + "This is too close to Spawn!");
                     event.getPlayer().updateInventory();
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
                  event.getPlayer()
                     .sendMessage(ChatColor.YELLOW + "You cannot build in " + team.getName(event.getPlayer()) + ChatColor.YELLOW + "'s territory!");
                  event.setCancelled(true);
               }
            }
         }
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void onBlockBreak(BlockBreakEvent event) {
      if (!Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())
         && !Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getPlayer(), event.getBlock().getLocation())) {
         Team team = LandBoard.getInstance().getTeam(event.getBlock().getLocation());
         if (event.getBlock().getType() != Material.GLOWSTONE
            || !Foxtrot.getInstance().getGlowHandler().hasGlowMountain()
            || !team.getName().equals(GlowHandler.getGlowTeamName())) {
            if (team.isMember(event.getPlayer().getUniqueId())) {
               if (!team.isCoLeader(event.getPlayer().getUniqueId())
                  && !team.isCaptain(event.getPlayer().getUniqueId())
                  && !team.isOwner(event.getPlayer().getUniqueId())) {
                  Subclaim subclaim = team.getSubclaim(event.getBlock().getLocation());
                  if (subclaim != null && !subclaim.isMember(event.getPlayer().getUniqueId())) {
                     event.setCancelled(true);
                     event.getPlayer()
                        .sendMessage(
                           ChatColor.YELLOW + "You do not have access to the subclaim " + ChatColor.GREEN + subclaim.getName() + ChatColor.YELLOW + "!"
                        );
                  }
               }
            } else {
               PlayerBuildInOthersClaimEvent buildEvent = new PlayerBuildInOthersClaimEvent(event.getPlayer(), event.getBlock(), team);
               Bukkit.getPluginManager().callEvent(buildEvent);
               if (!buildEvent.isWillIgnore()) {
                  if (!team.getName().equals(CavernHandler.getCavernTeamName())) {
                     event.getPlayer()
                        .sendMessage(ChatColor.YELLOW + "You cannot build in " + team.getName(event.getPlayer()) + ChatColor.YELLOW + "'s territory!");
                  }

                  event.setCancelled(true);
                  if (FoxListener.ATTACK_DISABLING_BLOCKS.contains(event.getBlock().getType())
                     || !event.getBlock().isEmpty() && !event.getBlock().getType().isTransparent() && event.getBlock().getType().isSolid()) {
                     Foxtrot.getInstance().getServerHandler().disablePlayerAttacking(event.getPlayer(), 1);
                  }
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onBlockPistonRetract(BlockPistonRetractEvent event) {
      if (event.isSticky()) {
         Block retractBlock = event.getRetractLocation().getBlock();
         if (!retractBlock.isEmpty() && !retractBlock.isLiquid()) {
            Team pistonTeam = LandBoard.getInstance().getTeam(event.getBlock().getLocation());
            Team targetTeam = LandBoard.getInstance().getTeam(retractBlock.getLocation());
            if (pistonTeam != targetTeam) {
               event.setCancelled(true);
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onBlockPistonExtend(BlockPistonExtendEvent event) {
      Team pistonTeam = LandBoard.getInstance().getTeam(event.getBlock().getLocation());
      int i = 0;

      for (Block ignored : event.getBlocks()) {
         Block targetBlock = event.getBlock().getRelative(event.getDirection(), ++i + 1);
         Team targetTeam = LandBoard.getInstance().getTeam(targetBlock.getLocation());
         if (targetTeam != pistonTeam && targetTeam != null && !targetTeam.isRaidable() && (targetBlock.isEmpty() || targetBlock.isLiquid())) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void onHangingPlace(HangingPlaceEvent event) {
      if (!Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())
         && !Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getPlayer(), event.getEntity().getLocation())) {
         Team team = LandBoard.getInstance().getTeam(event.getEntity().getLocation());
         if (!team.isMember(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
      if (event.getRemover() instanceof Player && !Foxtrot.getInstance().getServerHandler().isAdminOverride((Player)event.getRemover())) {
         if (!Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable((Player)event.getRemover(), event.getEntity().getLocation())) {
            Team team = LandBoard.getInstance().getTeam(event.getEntity().getLocation());
            if (!team.isMember(event.getRemover().getUniqueId())) {
               event.setCancelled(true);
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
      if (event.getRightClicked().getType() == EntityType.ITEM_FRAME && !Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
         if (!Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getPlayer(), event.getRightClicked().getLocation())) {
            Team team = LandBoard.getInstance().getTeam(event.getRightClicked().getLocation());
            if (!team.isMember(event.getPlayer().getUniqueId())) {
               event.setCancelled(true);
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
      if (event.getEntity().getType() == EntityType.ITEM_FRAME) {
         Player damager = null;
         if (event.getDamager() instanceof Player) {
            damager = (Player)event.getDamager();
         } else if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile)event.getDamager();
            if (projectile.getShooter() instanceof Player) {
               damager = (Player)projectile.getShooter();
            }
         }

         if (damager != null
            && !Foxtrot.getInstance().getServerHandler().isAdminOverride(damager)
            && !Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(damager, event.getEntity().getLocation())) {
            Team team = LandBoard.getInstance().getTeam(event.getEntity().getLocation());
            if (!team.isMember(event.getDamager().getUniqueId())) {
               event.setCancelled(true);
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onEntityDamageByEntity2(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Player) {
         if (Foxtrot.getInstance().getMapHandler().getGameHandler() == null
            || !Foxtrot.getInstance().getMapHandler().getGameHandler().isOngoingGame()
            || !Foxtrot.getInstance().getMapHandler().getGameHandler().getOngoingGame().isPlayingOrSpectating(event.getEntity().getUniqueId())) {
            Player damager = PlayerUtils.getDamageSource(event.getDamager());
            if (damager != null) {
               Team team = Foxtrot.getInstance().getTeamHandler().getTeam(damager);
               Player victim = (Player)event.getEntity();
               if (team != null && event.getCause() != DamageCause.FALL) {
                  if (team.isMember(victim.getUniqueId())) {
                     damager.sendMessage(ChatColor.YELLOW + "You cannot hurt " + ChatColor.DARK_GREEN + victim.getName() + ChatColor.YELLOW + ".");
                     event.setCancelled(true);
                  } else if (team.isAlly(victim.getUniqueId())) {
                     damager.sendMessage(ChatColor.YELLOW + "You cannot hurt " + Team.ALLY_COLOR + victim.getName() + ChatColor.YELLOW + ".");
                     event.setCancelled(true);
                  }
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onEntityHorseDamage(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Horse) {
         Player damager = PlayerUtils.getDamageSource(event.getDamager());
         Horse victim = (Horse)event.getEntity();
         if (damager != null && victim.isTamed()) {
            Team damagerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(damager);
            UUID horseOwner = victim.getOwner().getUniqueId();
            if (!damager.getUniqueId().equals(horseOwner) && damagerTeam != null && damagerTeam.isMember(horseOwner)) {
               event.setCancelled(true);
               damager.sendMessage(
                  ChatColor.YELLOW
                     + "This horse belongs to "
                     + ChatColor.DARK_GREEN
                     + Proton.getInstance().getUuidCache().name(horseOwner)
                     + ChatColor.YELLOW
                     + " who is in your faction."
               );
            }
         }
      }
   }

   @EventHandler
   public void onBucketEmpty(PlayerBucketEmptyEvent event) {
      Location checkLocation = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();
      if (Foxtrot.getInstance().getServerHandler().isWarzone(checkLocation) && !event.getPlayer().isOp()) {
         event.setCancelled(true);
         event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build in " + ChatColor.DARK_RED + "WarZone" + ChatColor.YELLOW + "'s territory!");
      } else if (!Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())
         && !Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getPlayer(), checkLocation)) {
         if (!Foxtrot.getInstance().getServerHandler().isEOTW()) {
            Team owner = LandBoard.getInstance().getTeam(checkLocation);
            boolean canPlace = owner.hasDTRBitmask(DTRBitmask.KOTH) && Foxtrot.getInstance().getServerHandler().isWaterPlacementInClaimsAllowed();
            if (!owner.isMember(event.getPlayer().getUniqueId())) {
               if (!canPlace) {
                  event.setCancelled(true);
                  event.getPlayer()
                     .sendMessage(ChatColor.YELLOW + "You cannot build in " + owner.getName(event.getPlayer()) + ChatColor.YELLOW + "'s territory!");
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
   }

   @EventHandler
   public void onBucketFill(PlayerBucketFillEvent event) {
      Location checkLocation = event.getBlockClicked().getRelative(event.getBlockFace()).getLocation();
      if (!Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())
         && !Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getPlayer(), checkLocation)) {
         if (!Foxtrot.getInstance().getServerHandler().isEOTW()) {
            Team owner = LandBoard.getInstance().getTeam(checkLocation);
            if (!owner.isMember(event.getPlayer().getUniqueId())) {
               event.setCancelled(true);
               event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot build in " + owner.getName(event.getPlayer()) + ChatColor.YELLOW + "'s territory!");
            }
         }
      }
   }
}
