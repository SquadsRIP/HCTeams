package net.frozenorb.foxtrot.team.claims;

import cc.fyre.neutron.util.PlayerUtil;
import cc.fyre.proton.util.ItemUtils;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import lombok.NonNull;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.commands.team.TeamBaseCommand;
import net.frozenorb.foxtrot.team.commands.team.TeamClaimCommand;
import net.frozenorb.foxtrot.team.commands.team.TeamFallTrapCommand;
import net.frozenorb.foxtrot.team.commands.team.TeamResizeCommand;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.team.menu.BaseMenu;
import net.frozenorb.foxtrot.team.menu.FallTrapMenu;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import net.frozenorb.foxtrot.util.CuboidRegion;
import net.minecraft.util.com.google.common.collect.ImmutableMap;
import net.minecraft.util.com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class VisualClaim implements Listener {
   public static final int MAP_RADIUS = 50;
   public static final Material[] MAP_MATERIALS = new Material[]{
      Material.DIAMOND_BLOCK,
      Material.GOLD_BLOCK,
      Material.LOG,
      Material.BRICK,
      Material.WOOD,
      Material.REDSTONE_BLOCK,
      Material.LAPIS_BLOCK,
      Material.CHEST,
      Material.MELON_BLOCK,
      Material.STONE,
      Material.COBBLESTONE,
      Material.COAL_BLOCK,
      Material.DIAMOND_ORE,
      Material.COAL_ORE,
      Material.GOLD_ORE,
      Material.REDSTONE_ORE,
      Material.FURNACE
   };
   public static final BlockFace[] NESW_BLOCKS = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
   private static Map<String, VisualClaim> currentMaps = new HashMap<>();
   private static Map<String, VisualClaim> currentSubclaimMaps = new HashMap<>();
   private static Map<String, VisualClaim> visualClaims = new HashMap<>();
   @NonNull
   private Player player;
   @NonNull
   private VisualClaimType type;
   @NonNull
   private boolean bypass;
   private List<Location> blockChanges = new ArrayList<>();
   private Claim resizing;
   private Location corner1;
   private Location corner2;
   private static boolean taskSetup = false;
   private static Map<UUID, Queue<VisualClaim.QueuedBlockChange>> queuedBlockChanges = Maps.newHashMap();

   private static void checkTaskSetup() {
      if (!taskSetup) {
         taskSetup = true;
         Bukkit.getScheduler().runTaskTimer(Foxtrot.getInstance(), () -> {
            Iterator<Entry<UUID, Queue<VisualClaim.QueuedBlockChange>>> entryIterator = queuedBlockChanges.entrySet().iterator();

            while (entryIterator.hasNext()) {
               Entry<UUID, Queue<VisualClaim.QueuedBlockChange>> entry = entryIterator.next();
               Player bukkitPlayer = Bukkit.getPlayer(entry.getKey());
               if (bukkitPlayer == null) {
                  entryIterator.remove();
               } else {
                  Queue<VisualClaim.QueuedBlockChange> queue = entry.getValue();
                  VisualClaim.QueuedBlockChange queuedBlockChange = queue.poll();
                  if (queuedBlockChange == null) {
                     entryIterator.remove();
                  } else {
                     bukkitPlayer.sendBlockChange(queuedBlockChange.getLocation(), queuedBlockChange.getType(), queuedBlockChange.getData());
                  }
               }
            }
         }, 1L, 1L);
      }
   }

   private static void sendBlockChange(Player player, Location location, Material type, byte data) {
      player.sendBlockChange(location, type, data);
   }

   public void draw(boolean silent) {
      checkTaskSetup();
      if (!currentMaps.containsKey(this.player.getName()) || this.type != VisualClaimType.MAP && this.type != VisualClaimType.SURFACE_MAP) {
         if (currentSubclaimMaps.containsKey(this.player.getName()) && this.type == VisualClaimType.SUBCLAIM_MAP) {
            currentSubclaimMaps.get(this.player.getName()).cancel();
            if (!silent) {
               this.player.sendMessage(ChatColor.YELLOW + "Subclaims have been hidden!");
            }
         } else {
            if (visualClaims.containsKey(this.player.getName())
               && this.type != VisualClaimType.MAP
               && this.type != VisualClaimType.SUBCLAIM_MAP
               && this.type != VisualClaimType.SURFACE_MAP) {
               visualClaims.get(this.player.getName()).cancel();
            }

            switch (this.type) {
               case MAP:
               case SURFACE_MAP:
                  currentMaps.put(this.player.getName(), this);
                  break;
               case SUBCLAIM_MAP:
                  currentSubclaimMaps.put(this.player.getName(), this);
                  break;
               default:
                  visualClaims.put(this.player.getName(), this);
            }

            Foxtrot.getInstance().getServer().getPluginManager().registerEvents(this, Foxtrot.getInstance());
            switch (this.type) {
               case MAP:
                  int claimIteration = 0;
                  Map<Entry<Claim, Team>, Material> sendMaps = new HashMap<>();

                  for (Entry<Claim, Team> regionData : LandBoard.getInstance().getRegionData(this.player.getLocation(), 50, 256, 50)) {
                     Material mat = this.getMaterial(claimIteration);
                     claimIteration++;
                     this.drawClaim(regionData.getKey(), mat);
                     sendMaps.put(regionData, mat);
                  }

                  if (sendMaps.isEmpty()) {
                     if (!silent) {
                        this.player.sendMessage(ChatColor.RED + "There are no claims within " + 50 + " blocks of you!");
                     }

                     this.cancel();
                  }

                  if (!silent) {
                     for (Entry<Entry<Claim, Team>, Material> mapEntry : sendMaps.entrySet()) {
                        Team team = mapEntry.getKey().getValue();
                        Claim claim = mapEntry.getKey().getKey();
                        if (team.getOwner() == null) {
                           this.player
                              .sendMessage(
                                 ChatColor.YELLOW
                                    + "Land "
                                    + ChatColor.BLUE
                                    + team.getName(this.player)
                                    + ChatColor.GREEN
                                    + "("
                                    + ChatColor.AQUA
                                    + ItemUtils.getName(new ItemStack(mapEntry.getValue()))
                                    + ChatColor.GREEN
                                    + ") "
                                    + ChatColor.YELLOW
                                    + "is claimed by "
                                    + ChatColor.BLUE
                                    + team.getName(this.player)
                              );
                        } else {
                           this.player
                              .sendMessage(
                                 ChatColor.YELLOW
                                    + "Land "
                                    + ChatColor.BLUE
                                    + claim.getName()
                                    + ChatColor.GREEN
                                    + "("
                                    + ChatColor.AQUA
                                    + ItemUtils.getName(new ItemStack(mapEntry.getValue()))
                                    + ChatColor.GREEN
                                    + ") "
                                    + ChatColor.YELLOW
                                    + "is claimed by "
                                    + ChatColor.BLUE
                                    + team.getName()
                              );
                        }
                     }
                  }
                  break;
               case SURFACE_MAP:
                  for (Entry<Claim, Team> regionData : LandBoard.getInstance().getRegionData(this.player.getLocation(), 50, 256, 50)) {
                     Claim claim = regionData.getKey();
                     Team claimOwner = regionData.getValue();
                     World claimWorld = Foxtrot.getInstance().getServer().getWorld(claim.getWorld());

                     for (Coordinate coordinate : claim) {
                        Block block = claimWorld.getBlockAt(coordinate.getX(), 100, coordinate.getZ());
                        boolean displayCarpet = false;

                        for (BlockFace blockFace : NESW_BLOCKS) {
                           Location relative = block.getRelative(blockFace).getLocation();
                           if (!claimOwner.ownsLocation(relative)) {
                              displayCarpet = true;
                              break;
                           }
                        }

                        if (displayCarpet) {
                           while (
                              !block.getType().isSolid()
                                 || block.getType() == Material.LEAVES
                                 || block.getType() == Material.LOG
                                 || block.getType() == Material.LOG_2
                           ) {
                              block = block.getRelative(BlockFace.DOWN);
                           }

                           DyeColor carpetColor;
                           if (claimOwner.getOwner() != null) {
                              if (claimOwner.isMember(this.player.getUniqueId())) {
                                 carpetColor = DyeColor.GREEN;
                              } else if (claimOwner.isAlly(this.player.getUniqueId())) {
                                 carpetColor = DyeColor.BLUE;
                              } else {
                                 carpetColor = DyeColor.RED;
                              }
                           } else if (claimOwner.hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
                              carpetColor = DyeColor.LIME;
                           } else {
                              carpetColor = DyeColor.BLUE;
                           }

                           sendBlockChange(this.player, block.getLocation(), Material.WOOL, carpetColor.getWoolData());
                           sendBlockChange(this.player, block.getRelative(BlockFace.UP).getLocation(), Material.CARPET, carpetColor.getWoolData());
                           this.blockChanges.add(block.getLocation());
                           this.blockChanges.add(block.getRelative(BlockFace.UP).getLocation());
                        }
                     }
                  }

                  if (this.blockChanges.size() == 0) {
                     if (!silent) {
                        this.player.sendMessage(ChatColor.YELLOW + "There are no claims near you!");
                     }

                     this.cancel();
                     return;
                  }

                  if (!silent) {
                     this.player.sendMessage(ChatColor.YELLOW + "Claims have been shown.");
                  }
                  break;
               case SUBCLAIM_MAP:
                  Team senderTeam = Foxtrot.getInstance().getTeamHandler().getTeam(this.player);
                  if (this.bypass) {
                     senderTeam = LandBoard.getInstance().getTeam(this.player.getLocation());
                  }

                  if (senderTeam == null) {
                     if (!silent) {
                        this.player.sendMessage(ChatColor.RED + "You must be on a team to view subclaims.");
                     }

                     this.cancel();
                     return;
                  }

                  int subclaimIteration = 0;
                  Map<Subclaim, Material> subclaimMaterialMap = new HashMap<>();

                  for (Subclaim subclaim : senderTeam.getSubclaims()) {
                     if (!(subclaim.getLoc1().distanceSquared(this.player.getLocation()) > 2500.0)
                        || !(subclaim.getLoc2().distanceSquared(this.player.getLocation()) > 2500.0)) {
                        Material mat = this.getMaterial(subclaimIteration);
                        subclaimIteration++;
                        subclaimMaterialMap.put(subclaim, mat);
                        this.drawSubclaim(subclaim, mat);
                     }
                  }

                  if (subclaimIteration == 0) {
                     if (!silent) {
                        this.player.sendMessage(ChatColor.YELLOW + "There are no subclaims within " + 50 + " blocks of you!");
                     }

                     this.cancel();
                     return;
                  }

                  if (!silent) {
                     for (Entry<Subclaim, Material> entry : subclaimMaterialMap.entrySet()) {
                        this.player
                           .sendMessage(
                              ChatColor.YELLOW
                                 + "Subclaim "
                                 + ChatColor.BLUE
                                 + entry.getKey().getName()
                                 + ChatColor.GREEN
                                 + "("
                                 + ChatColor.AQUA
                                 + ItemUtils.getName(new ItemStack(entry.getValue()))
                                 + ChatColor.GREEN
                                 + ") "
                                 + ChatColor.YELLOW
                                 + "is claimed by "
                                 + ChatColor.BLUE
                                 + senderTeam.getName()
                           );
                     }
                  }
                  break;
               case CREATE:
                  this.player.sendMessage(ChatColor.GRAY + "Faction claiming process has begun.");
                  this.player.sendMessage(ChatColor.WHITE + "Left click at a corner of the land you'd like to claim.");
                  this.player.sendMessage(ChatColor.WHITE + "Right click on the second corner of the land you'd like to claim.");
                  this.player.sendMessage(ChatColor.WHITE + "Crouch left click the air to purchase your claim.");
                  break;
               case RESIZE:
                  this.player.sendMessage(ChatColor.GRAY + "Faction land resize started.");
                  this.player.sendMessage(ChatColor.WHITE + "Left click in the claim you'd like to resize.");
                  this.player.sendMessage(ChatColor.WHITE + "Right click on the corner you'd like to resize to.");
                  this.player.sendMessage(ChatColor.WHITE + "Crouch left click the air to confirm your resize.");
            }
         }
      } else {
         currentMaps.get(this.player.getName()).cancel();
         if (!silent) {
            if (this.type == VisualClaimType.MAP) {
               this.player.sendMessage(ChatColor.YELLOW + "Claim pillars have been hidden!");
            } else {
               this.player.sendMessage(ChatColor.YELLOW + "The surface map has been hidden!");
            }
         }
      }
   }

   public boolean containsOtherClaim(Claim claim) {
      Location maxPoint = claim.getMaximumPoint();
      Location minPoint = claim.getMinimumPoint();
      Team maxTeam = LandBoard.getInstance().getTeam(maxPoint);
      if (maxTeam == null || this.type == VisualClaimType.RESIZE && maxTeam.isMember(this.player.getUniqueId())) {
         Team minTeam = LandBoard.getInstance().getTeam(minPoint);
         if (minTeam == null || this.type == VisualClaimType.RESIZE && minTeam.isMember(this.player.getUniqueId())) {
            if (Math.abs(claim.getX1() - claim.getX2()) != 0 && Math.abs(claim.getZ1() - claim.getZ2()) != 0) {
               for (int x = minPoint.getBlockX(); x <= maxPoint.getBlockX(); x++) {
                  for (int z = minPoint.getBlockZ(); z <= maxPoint.getBlockZ(); z++) {
                     Location at = new Location(Foxtrot.getInstance().getServer().getWorld(claim.getWorld()), x, 80.0, z);
                     Team teamAt = LandBoard.getInstance().getTeam(at);
                     if (teamAt != null && (this.type != VisualClaimType.RESIZE || !teamAt.isMember(this.player.getUniqueId()))) {
                        return true;
                     }
                  }
               }

               return false;
            } else {
               return false;
            }
         } else {
            return true;
         }
      } else {
         return true;
      }
   }

   public Set<Claim> getTouchingClaims(Claim claim) {
      Set<Claim> touchingClaims = new HashSet<>();

      for (Coordinate coordinate : claim.outset(Claim.CuboidDirection.Horizontal, 1)) {
         Location loc = new Location(Foxtrot.getInstance().getServer().getWorld(claim.getWorld()), coordinate.getX(), 80.0, coordinate.getZ());
         Entry<Claim, Team> claimAtLocation = LandBoard.getInstance().getRegionData(loc);
         if (claimAtLocation != null) {
            touchingClaims.add(claimAtLocation.getKey());
         }
      }

      return touchingClaims;
   }

   public void setLoc(int locationId, final Location clicked) {
      Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(this.player);
      if (playerTeam == null) {
         this.player.sendMessage(ChatColor.RED + "You have to be on a team to " + this.type.name().toLowerCase() + " land!");
         this.cancel();
      } else {
         if (this.type == VisualClaimType.CREATE) {
            if (!this.bypass && !Foxtrot.getInstance().getServerHandler().isUnclaimed(clicked)) {
               this.player.sendMessage(ChatColor.RED + "You can only claim land in the Wilderness!");
               return;
            }

            if (locationId == 1) {
               if (this.corner2 != null && this.isIllegalClaim(new Claim(clicked, this.corner2), null)) {
                  return;
               }

               this.clearPillarAt(this.corner1);
               this.corner1 = clicked;
            } else if (locationId == 2) {
               if (this.corner1 != null && this.isIllegalClaim(new Claim(this.corner1, clicked), null)) {
                  return;
               }

               this.clearPillarAt(this.corner2);
               this.corner2 = clicked;
            }

            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), new BukkitRunnable() {
               @Override
               public void run() {
                  VisualClaim.this.erectPillar(clicked, Material.EMERALD_BLOCK);
               }
            }, 1L);
            this.player
               .sendMessage(
                  ChatColor.YELLOW
                     + "Set claim's location "
                     + ChatColor.LIGHT_PURPLE
                     + locationId
                     + ChatColor.YELLOW
                     + " to "
                     + ChatColor.GREEN
                     + "("
                     + ChatColor.WHITE
                     + clicked.getBlockX()
                     + ", "
                     + clicked.getBlockY()
                     + ", "
                     + clicked.getBlockZ()
                     + ChatColor.GREEN
                     + ")"
                     + ChatColor.YELLOW
                     + "."
               );
            if (this.corner1 != null && this.corner2 != null) {
               int price = Claim.getPrice(new Claim(this.corner1, this.corner2), playerTeam, true);
               int x = Math.abs(this.corner1.getBlockX() - this.corner2.getBlockX());
               int z = Math.abs(this.corner1.getBlockZ() - this.corner2.getBlockZ());
               if (price > playerTeam.getBalance() && !this.bypass) {
                  this.player
                     .sendMessage(
                        ChatColor.YELLOW
                           + "Claim cost: "
                           + ChatColor.RED
                           + "$"
                           + price
                           + ChatColor.YELLOW
                           + ", Current size: ("
                           + ChatColor.WHITE
                           + x
                           + ", "
                           + z
                           + ChatColor.YELLOW
                           + "), "
                           + ChatColor.WHITE
                           + x * z
                           + ChatColor.YELLOW
                           + " blocks"
                     );
               } else {
                  this.player
                     .sendMessage(
                        ChatColor.YELLOW
                           + "Claim cost: "
                           + ChatColor.GREEN
                           + "$"
                           + price
                           + ChatColor.YELLOW
                           + ", Current size: ("
                           + ChatColor.WHITE
                           + x
                           + ", "
                           + z
                           + ChatColor.YELLOW
                           + "), "
                           + ChatColor.WHITE
                           + x * z
                           + ChatColor.YELLOW
                           + " blocks"
                     );
               }
            }
         } else if (this.type != VisualClaimType.BASE && this.type != VisualClaimType.FALL_TRAP) {
            if (this.type == VisualClaimType.RESIZE) {
               Entry<Claim, Team> teamAtLocation = LandBoard.getInstance().getRegionData(clicked);
               if (locationId == 1) {
                  if (teamAtLocation == null || !teamAtLocation.getValue().isMember(this.player.getUniqueId())) {
                     this.player.sendMessage(ChatColor.YELLOW + "To resize your claim, please left click in the claim you'd like to resize.");
                     return;
                  }

                  this.resizing = teamAtLocation.getKey();
                  this.drawClaim(this.resizing, Material.LAPIS_BLOCK);
               } else if (locationId == 2) {
                  if (this.resizing == null) {
                     this.player
                        .sendMessage(
                           ChatColor.YELLOW + "Before you set the location you'd like to resize to, first left click in the claim you'd like to resize."
                        );
                     return;
                  }

                  final Claim claimClone = new Claim(this.resizing);
                  this.applyResize(claimClone, clicked);
                  if (this.isIllegalClaim(claimClone, Arrays.asList(this.resizing, claimClone))) {
                     return;
                  }

                  this.corner2 = clicked;
                  (new BukkitRunnable() {
                     @Override
                     public void run() {
                        VisualClaim.this.clearAllBlocks();
                        VisualClaim.this.drawClaim(VisualClaim.this.resizing, Material.LAPIS_BLOCK);
                        VisualClaim.this.drawClaim(claimClone, Material.EMERALD_BLOCK);
                     }
                  }).runTaskLater(Foxtrot.getInstance(), 1L);
               }

               if (locationId == 1) {
                  this.player
                     .sendMessage(
                        ChatColor.YELLOW + "Selected claim " + ChatColor.LIGHT_PURPLE + teamAtLocation.getKey().getName() + ChatColor.YELLOW + " to resize."
                     );
               } else {
                  this.player
                     .sendMessage(
                        ChatColor.YELLOW
                           + "Set resize location to "
                           + ChatColor.GREEN
                           + "("
                           + ChatColor.WHITE
                           + clicked.getBlockX()
                           + ", "
                           + clicked.getBlockY()
                           + ", "
                           + clicked.getBlockZ()
                           + ChatColor.GREEN
                           + ")"
                           + ChatColor.YELLOW
                           + "."
                     );
               }

               if (this.resizing != null && this.corner2 != null) {
                  int oldPrice = Claim.getPrice(this.resizing, null, false);
                  Claim preview = new Claim(this.resizing);
                  this.applyResize(preview, this.corner2);
                  int newPrice = Claim.getPrice(preview, null, false);
                  int cost = newPrice - oldPrice;
                  if (cost > playerTeam.getBalance() && !this.bypass) {
                     this.player.sendMessage(ChatColor.YELLOW + "Resize cost: " + ChatColor.RED + "$" + cost);
                  } else {
                     this.player.sendMessage(ChatColor.YELLOW + "Resize cost: " + ChatColor.GREEN + "$" + cost);
                  }
               }
            }
         } else {
            Team base = LandBoard.getInstance().getTeam(clicked);
            if (base == null || !base.isMember(this.player.getUniqueId())) {
               this.player
                  .sendMessage(
                     ChatColor.RED + "You can only generate your " + (this.type == VisualClaimType.FALL_TRAP ? "falltrap" : "base") + " in your own claim!"
                  );
               return;
            }

            if (this.type.equals(VisualClaimType.FALL_TRAP) && this.getCorner1() != null && this.getCorner2() != null) {
               Location cloned = this.getCorner1().clone();
               cloned.setY(0.0);
               Location cloned2 = this.getCorner2().clone();
               cloned2.setY(0.0);
               int distance = (int)Math.ceil(cloned.distance(cloned2));
               if (distance > 25) {
                  this.player.sendMessage(ChatColor.RED + "Fallers can only have a distance of 25 blocks! Right now the distance is " + distance);
                  return;
               }
            }

            if (locationId == 1) {
               this.clearPillarAt(this.corner1);
               this.corner1 = clicked;
            } else if (locationId == 2) {
               this.clearPillarAt(this.corner2);
               this.corner2 = clicked;
            }

            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), new BukkitRunnable() {
               @Override
               public void run() {
                  VisualClaim.this.erectPillar(clicked, VisualClaim.this.type == VisualClaimType.FALL_TRAP ? Material.BEDROCK : Material.GOLD_BLOCK);
               }
            }, 1L);
            this.player
               .sendMessage(
                  ChatColor.YELLOW
                     + "Set "
                     + (this.type == VisualClaimType.FALL_TRAP ? "falltrap" : "base")
                     + "'s location "
                     + ChatColor.LIGHT_PURPLE
                     + locationId
                     + ChatColor.YELLOW
                     + " to "
                     + ChatColor.GREEN
                     + "("
                     + ChatColor.WHITE
                     + clicked.getBlockX()
                     + ", "
                     + clicked.getBlockY()
                     + ", "
                     + clicked.getBlockZ()
                     + ChatColor.GREEN
                     + ")"
                     + ChatColor.YELLOW
                     + "."
               );
         }
      }
   }

   public void cancel() {
      if (this.type == VisualClaimType.CREATE
         || this.type == VisualClaimType.RESIZE
         || this.type == VisualClaimType.BASE
         || this.type == VisualClaimType.FALL_TRAP) {
         this.player.getInventory().remove(TeamClaimCommand.SELECTION_WAND);
         this.player.getInventory().remove(TeamResizeCommand.SELECTION_WAND);
         this.player.getInventory().remove(TeamBaseCommand.SELECTION_WAND);
         this.player.getInventory().remove(TeamFallTrapCommand.SELECTION_WAND);
      }

      HandlerList.unregisterAll(this);
      switch (this.type) {
         case MAP:
         case SURFACE_MAP:
            currentMaps.remove(this.player.getName());
            break;
         case SUBCLAIM_MAP:
            currentSubclaimMaps.remove(this.player.getName());
            break;
         default:
            visualClaims.remove(this.player.getName());
      }

      this.clearAllBlocks();
   }

   public void clearAllBlocks() {
      for (Location location : this.blockChanges) {
         sendBlockChange(this.player, location, location.getBlock().getType(), location.getBlock().getData());
      }
   }

   public void generateBase() {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(this.player);
      if (team == null) {
         this.player.sendMessage(ChatColor.RED + "You have to be on a team to generate base!");
         this.cancel();
      } else {
         if (this.corner1 != null && this.corner2 != null) {
            if (!this.bypass
               && !team.isCaptain(this.player.getUniqueId())
               && !team.isCoLeader(this.player.getUniqueId())
               && !team.isOwner(this.player.getUniqueId())) {
               this.player.sendMessage(ChatColor.RED + "Only team captains can claim land.");
               return;
            }

            Claim claim = new Claim(this.corner1, this.corner2);
            new BaseMenu(team, claim).openMenu(this.player);
            this.cancel();
            (new BukkitRunnable() {
               @Override
               public void run() {
                  if (VisualClaim.getCurrentMaps().containsKey(VisualClaim.this.player.getName())) {
                     VisualClaim.getCurrentMaps().get(VisualClaim.this.player.getName()).cancel();
                  }

                  new VisualClaim(VisualClaim.this.player, VisualClaimType.MAP, false).draw(true);
               }
            }).runTaskLater(Foxtrot.getInstance(), 1L);
         } else {
            this.player.sendMessage(ChatColor.RED + "You have not selected both corners of your base yet!");
         }
      }
   }

   public void generateFallTrap() {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(this.player);
      if (team == null) {
         this.player.sendMessage(ChatColor.RED + "You have to be on a team to generate falltrap!");
         this.cancel();
      } else {
         if (this.corner1 != null && this.corner2 != null) {
            if (!this.bypass
               && !team.isCaptain(this.player.getUniqueId())
               && !team.isCoLeader(this.player.getUniqueId())
               && !team.isOwner(this.player.getUniqueId())) {
               this.player.sendMessage(ChatColor.RED + "Only team captains can claim land.");
               return;
            }

            new FallTrapMenu(team, new Claim(this.corner1, this.corner2)).openMenu(this.player);
            this.cancel();
            (new BukkitRunnable() {
               @Override
               public void run() {
                  if (VisualClaim.getCurrentMaps().containsKey(VisualClaim.this.player.getName())) {
                     VisualClaim.getCurrentMaps().get(VisualClaim.this.player.getName()).cancel();
                  }

                  new VisualClaim(VisualClaim.this.player, VisualClaimType.MAP, false).draw(true);
               }
            }).runTaskLater(Foxtrot.getInstance(), 1L);
         } else {
            this.player.sendMessage(ChatColor.RED + "You have not selected both corners of your falltrap yet!");
         }
      }
   }

   public void purchaseClaim() {
      this.player.chat("/f d all");
      Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(this.player);
      if (playerTeam == null) {
         this.player.sendMessage(ChatColor.RED + "You have to be on a team to claim land!");
         this.cancel();
      } else {
         if (this.corner1 != null && this.corner2 != null) {
            int price = Claim.getPrice(new Claim(this.corner1, this.corner2), playerTeam, true);
            if (!this.bypass) {
               if (playerTeam.getClaims().size() >= 2) {
                  this.player.sendMessage(ChatColor.RED + "Your team has the maximum amount of claims, which is " + 2 + ".");
                  return;
               }

               if (!playerTeam.isCaptain(this.player.getUniqueId())
                  && !playerTeam.isCoLeader(this.player.getUniqueId())
                  && !playerTeam.isOwner(this.player.getUniqueId())) {
                  this.player.sendMessage(ChatColor.RED + "Only team captains can claim land.");
                  return;
               }

               if (playerTeam.getBalance() < price) {
                  this.player.sendMessage(ChatColor.RED + "Your team does not have enough money to do this!");
                  return;
               }

               if (playerTeam.isRaidable()) {
                  this.player.sendMessage(ChatColor.RED + "You cannot claim land while raidable.");
                  return;
               }
            }

            Claim claim = new Claim(this.corner1, this.corner2);
            if (this.isIllegalClaim(claim, null) || Double.isNaN(playerTeam.getBalance())) {
               return;
            }

            claim.setName(playerTeam.getName() + "_" + (100 + ThreadLocalRandom.current().nextInt(800)));
            claim.setY1(0);
            claim.setY2(256);
            LandBoard.getInstance().setTeamAt(claim, playerTeam);
            playerTeam.getClaims().add(claim);
            playerTeam.flagForSave();
            playerTeam.recalculateSpawnersInClaims();
            Foxtrot.getInstance().getQuestHandler().completeQuest(this.player, "Claim");
            this.player.sendMessage(ChatColor.YELLOW + "You have claimed this land for your team!");
            this.player.playSound(this.player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
            PlayerUtil.sendTitle(this.player, "&a&lClaim Success", "Set your team's home by typing /f sethome!");
            if (!this.bypass) {
               playerTeam.setBalance(playerTeam.getBalance() - price);
               Foxtrot.getInstance().getLogger().info("Economy Logger: Withdrawing " + price + " from " + playerTeam.getBalance() + "'s account: Claimed land");
               this.player
                  .sendMessage(
                     ChatColor.YELLOW
                        + "Your team's new balance is "
                        + ChatColor.WHITE
                        + "$"
                        + (int)playerTeam.getBalance()
                        + ChatColor.LIGHT_PURPLE
                        + " (Price: $"
                        + price
                        + ")"
                  );
            }

            Location minLoc = claim.getMinimumPoint();
            Location maxLoc = claim.getMaximumPoint();
            TeamActionTracker.logActionAsync(
               playerTeam,
               TeamActionType.PLAYER_CLAIM_LAND,
               ImmutableMap.of(
                  "playerId",
                  this.player.getUniqueId(),
                  "playerName",
                  playerTeam.getName(),
                  "cost",
                  price,
                  "point1",
                  minLoc.getBlockX() + ", " + minLoc.getBlockY() + ", " + minLoc.getBlockZ(),
                  "point2",
                  maxLoc.getBlockX() + ", " + maxLoc.getBlockY() + ", " + maxLoc.getBlockZ()
               )
            );
            this.cancel();
            (new BukkitRunnable() {
               @Override
               public void run() {
                  if (VisualClaim.getCurrentMaps().containsKey(VisualClaim.this.player.getName())) {
                     VisualClaim.getCurrentMaps().get(VisualClaim.this.player.getName()).cancel();
                  }

                  new VisualClaim(VisualClaim.this.player, VisualClaimType.MAP, false).draw(true);
               }
            }).runTaskLater(Foxtrot.getInstance(), 1L);
         } else {
            this.player.sendMessage(ChatColor.RED + "You have not selected both corners of your claim yet!");
         }
      }
   }

   public void resizeClaim() {
      Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(this.player);
      if (playerTeam == null) {
         this.player.sendMessage(ChatColor.RED + "You have to be on a team to resize land!");
         this.cancel();
      } else {
         if (this.resizing != null && this.corner2 != null) {
            Claim newClaim = new Claim(this.resizing);
            this.applyResize(newClaim, this.corner2);
            int oldPrice = Claim.getPrice(this.resizing, null, false);
            int newPrice = Claim.getPrice(newClaim, null, false);
            int cost = newPrice - oldPrice;
            if (!this.bypass) {
               if (!playerTeam.isCaptain(this.player.getUniqueId())
                  && !playerTeam.isCoLeader(this.player.getUniqueId())
                  && !playerTeam.isOwner(this.player.getUniqueId())) {
                  this.player.sendMessage(ChatColor.RED + "Only team captains can resize land.");
                  return;
               }

               if (playerTeam.getBalance() < cost) {
                  this.player.sendMessage(ChatColor.RED + "Your team does not have enough money to do this!");
                  return;
               }

               if (playerTeam.isRaidable()) {
                  this.player.sendMessage(ChatColor.RED + "You cannot resize land while raidable.");
                  return;
               }
            }

            if (this.isIllegalClaim(newClaim, null)) {
               return;
            }

            LandBoard.getInstance().setTeamAt(this.resizing, null);
            LandBoard.getInstance().setTeamAt(newClaim, playerTeam);
            playerTeam.getClaims().remove(this.resizing);
            playerTeam.getClaims().add(newClaim);
            playerTeam.flagForSave();
            playerTeam.recalculateSpawnersInClaims();
            this.player.sendMessage(ChatColor.YELLOW + "You have resized this land!");
            if (!this.bypass) {
               playerTeam.setBalance(playerTeam.getBalance() - cost);
               this.player
                  .sendMessage(
                     ChatColor.YELLOW
                        + "Your team's new balance is "
                        + ChatColor.WHITE
                        + "$"
                        + (int)playerTeam.getBalance()
                        + ChatColor.LIGHT_PURPLE
                        + " (Price: $"
                        + cost
                        + ")"
                  );
            }

            Location minLoc = this.resizing.getMinimumPoint();
            Location maxLoc = this.resizing.getMaximumPoint();
            TeamActionTracker.logActionAsync(
               playerTeam,
               TeamActionType.PLAYER_RESIZE_LAND,
               ImmutableMap.of(
                  "playerId",
                  this.player.getUniqueId(),
                  "playerName",
                  playerTeam.getName(),
                  "cost",
                  cost,
                  "newPoint1",
                  minLoc.getBlockX() + ", " + minLoc.getBlockY() + ", " + minLoc.getBlockZ(),
                  "newPoint2",
                  maxLoc.getBlockX() + ", " + maxLoc.getBlockY() + ", " + maxLoc.getBlockZ()
               )
            );
            this.cancel();
            (new BukkitRunnable() {
               @Override
               public void run() {
                  if (VisualClaim.getCurrentMaps().containsKey(VisualClaim.this.player.getName())) {
                     VisualClaim.getCurrentMaps().get(VisualClaim.this.player.getName()).cancel();
                  }

                  new VisualClaim(VisualClaim.this.player, VisualClaimType.MAP, false).draw(true);
               }
            }).runTaskLater(Foxtrot.getInstance(), 1L);
         } else {
            this.player.sendMessage(ChatColor.RED + "You have not selected both corners of your claim yet!");
         }
      }
   }

   private void drawClaim(Claim claim, Material material) {
      for (Location loc : claim.getCornerLocations()) {
         this.erectPillar(loc, material);
      }
   }

   private void drawSubclaim(Subclaim subclaim, Material material) {
      CuboidRegion cuboidRegion = new CuboidRegion(subclaim.getName(), subclaim.getLoc1(), subclaim.getLoc2());
      int glassIteration = 0;

      for (Location location : cuboidRegion) {
         int matches = 0;
         if (location.getBlockX() == cuboidRegion.getMinimumPoint().getBlockX()) {
            matches++;
         }

         if (location.getBlockX() == cuboidRegion.getMaximumPoint().getBlockX()) {
            matches++;
         }

         if (location.getBlockY() == cuboidRegion.getMinimumPoint().getBlockY()) {
            matches++;
         }

         if (location.getBlockY() == cuboidRegion.getMaximumPoint().getBlockY()) {
            matches++;
         }

         if (location.getBlockZ() == cuboidRegion.getMinimumPoint().getBlockZ()) {
            matches++;
         }

         if (location.getBlockZ() == cuboidRegion.getMaximumPoint().getBlockZ()) {
            matches++;
         }

         if (matches >= 2) {
            if (glassIteration++ % 3 == 0) {
               sendBlockChange(this.player, location, material, (byte)0);
            } else {
               sendBlockChange(this.player, location, Material.GLASS, (byte)0);
            }

            this.blockChanges.add(location.clone());
         }
      }
   }

   private void erectPillar(Location loc, Material mat) {
      Location set = loc.clone();

      for (int y = 0; y < 256; y++) {
         set.setY(y);
         if (set.getBlock().getType() == Material.AIR
            || set.getBlock().getType().isTransparent()
            || set.getBlock().getType() == Material.WATER
            || set.getBlock().getType() == Material.STATIONARY_WATER) {
            if (y % 5 == 0) {
               sendBlockChange(this.player, set, mat, (byte)0);
            } else {
               sendBlockChange(this.player, set, Material.GLASS, (byte)0);
            }

            this.blockChanges.add(set.clone());
         }
      }
   }

   private void clearPillarAt(Location location) {
      if (location != null) {
         Iterator<Location> blockChangeIterator = this.blockChanges.iterator();

         while (blockChangeIterator.hasNext()) {
            Location blockChange = blockChangeIterator.next();
            if (blockChange.getBlockX() == location.getBlockX() && blockChange.getBlockZ() == location.getBlockZ()) {
               sendBlockChange(this.player, blockChange, blockChange.getBlock().getType(), blockChange.getBlock().getData());
               blockChangeIterator.remove();
            }
         }
      }
   }

   public boolean isIllegalClaim(Claim claim, List<Claim> ignoreNearby) {
      if (this.bypass) {
         return false;
      }

      Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(this.player);
      if (this.containsOtherClaim(claim)) {
         this.player.sendMessage(ChatColor.RED + "This claim contains unclaimable land!");
         return true;
      }

      if (this.player.getWorld().getEnvironment() != Environment.NORMAL) {
         this.player.sendMessage(ChatColor.RED + "Land can only be claimed in the overworld.");
         return true;
      }

      Set<Claim> touchingClaims = this.getTouchingClaims(claim);
      Iterator<Claim> teamClaims = touchingClaims.iterator();
      boolean removedSelfClaims = false;

      while (teamClaims.hasNext()) {
         Claim possibleClaim = teamClaims.next();
         if (ignoreNearby != null && ignoreNearby.contains(possibleClaim)) {
            removedSelfClaims = true;
            teamClaims.remove();
         } else if (playerTeam.ownsClaim(possibleClaim)) {
            removedSelfClaims = true;
            teamClaims.remove();
         }
      }

      if (playerTeam.getClaims().size() != (this.type == VisualClaimType.RESIZE ? 1 : 0) && !removedSelfClaims) {
         this.player.sendMessage(ChatColor.RED + "All of your claims must be touching each other!");
         return true;
      }

      if (touchingClaims.size() <= 1 && (touchingClaims.size() != 1 || removedSelfClaims)) {
         int x = Math.abs(claim.getX1() - claim.getX2());
         int z = Math.abs(claim.getZ1() - claim.getZ2());
         int limit = this.player.hasPermission("challenge.claim") ? 2 : 5;
         if (x < limit || z < limit) {
            this.player.sendMessage(ChatColor.RED + "Your claim is too small! The claim has to be at least " + (limit + 1) + " x " + (limit + 1) + "!");
            return true;
         }

         if (x <= 3 * z && z <= 3 * x) {
            return false;
         }

         this.player.sendMessage(ChatColor.RED + "One side of your claim cannot be more than 3 times larger than the other!");
         return true;
      } else {
         this.player.sendMessage(ChatColor.RED + "Your claim must be at least 1 block away from enemy claims!");
         return true;
      }
   }

   public void applyResize(Claim claim, Location location) {
      double furthestDistance = 0.0;
      Location furthestCorner = null;

      for (Location corner : claim.getCornerLocations()) {
         double distance = location.distanceSquared(corner);
         if (furthestCorner == null || distance > furthestDistance) {
            furthestDistance = distance;
            furthestCorner = corner;
         }
      }

      claim.setLocations(location, furthestCorner);
      claim.setY1(0);
      claim.setY2(256);
   }

   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent event) {
      if (event.getPlayer() == this.player && this.player.getItemInHand() != null) {
         if (this.player.getItemInHand().getType() == TeamClaimCommand.SELECTION_WAND.getType() && this.type == VisualClaimType.CREATE) {
            switch (event.getAction()) {
               case RIGHT_CLICK_BLOCK:
                  this.setLoc(2, event.getClickedBlock().getLocation());
                  break;
               case RIGHT_CLICK_AIR:
                  this.cancel();
                  this.player.sendMessage(ChatColor.RED + "You have cancelled the claiming process.");
                  break;
               case LEFT_CLICK_BLOCK:
                  if (this.player.isSneaking()) {
                     this.purchaseClaim();
                  } else {
                     this.setLoc(1, event.getClickedBlock().getLocation());
                  }
                  break;
               case LEFT_CLICK_AIR:
                  if (this.player.isSneaking()) {
                     this.purchaseClaim();
                  }
            }

            event.setCancelled(true);
         } else if (this.player.getItemInHand().isSimilar(TeamBaseCommand.SELECTION_WAND) && this.type.equals(VisualClaimType.BASE)) {
            event.setCancelled(true);
            switch (event.getAction()) {
               case RIGHT_CLICK_BLOCK:
                  this.setLoc(2, event.getClickedBlock().getLocation());
                  break;
               case RIGHT_CLICK_AIR:
                  this.cancel();
                  this.player.sendMessage(ChatColor.RED + "You have cancelled the base process.");
                  break;
               case LEFT_CLICK_BLOCK:
                  if (this.player.isSneaking()) {
                     this.generateBase();
                  } else {
                     this.setLoc(1, event.getClickedBlock().getLocation());
                  }
                  break;
               case LEFT_CLICK_AIR:
                  if (this.player.isSneaking()) {
                     this.generateBase();
                  }
            }
         } else if (this.player.getItemInHand().isSimilar(TeamFallTrapCommand.SELECTION_WAND) && this.type == VisualClaimType.FALL_TRAP) {
            event.setCancelled(true);
            switch (event.getAction()) {
               case RIGHT_CLICK_BLOCK:
                  this.setLoc(2, event.getClickedBlock().getLocation());
                  break;
               case RIGHT_CLICK_AIR:
                  this.cancel();
                  this.player.sendMessage(ChatColor.RED + "You have cancelled the falltrap process.");
                  break;
               case LEFT_CLICK_BLOCK:
                  if (this.player.isSneaking()) {
                     this.generateFallTrap();
                  } else {
                     this.setLoc(1, event.getClickedBlock().getLocation());
                  }
                  break;
               case LEFT_CLICK_AIR:
                  if (this.player.isSneaking()) {
                     this.generateFallTrap();
                  }
            }
         } else if (this.player.getItemInHand().getType() == TeamResizeCommand.SELECTION_WAND.getType() && this.type == VisualClaimType.RESIZE) {
            switch (event.getAction()) {
               case RIGHT_CLICK_BLOCK:
                  this.setLoc(2, event.getClickedBlock().getLocation());
                  break;
               case RIGHT_CLICK_AIR:
                  this.cancel();
                  this.player.sendMessage(ChatColor.RED + "You have cancelled the resizing process.");
                  break;
               case LEFT_CLICK_BLOCK:
                  if (this.player.isSneaking()) {
                     this.resizeClaim();
                  } else {
                     this.setLoc(1, event.getClickedBlock().getLocation());
                  }
                  break;
               case LEFT_CLICK_AIR:
                  if (this.player.isSneaking()) {
                     this.resizeClaim();
                  }
            }

            event.setCancelled(true);
         }
      }
   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent event) {
      if (this.player == event.getPlayer()) {
         this.cancel();
      }
   }

   public Material getMaterial(int iteration) {
      if (iteration == -1) {
         return Material.IRON_BLOCK;
      }

      while (iteration >= MAP_MATERIALS.length) {
         iteration -= MAP_MATERIALS.length;
      }

      return MAP_MATERIALS[iteration];
   }

   public static VisualClaim getVisualClaim(String name) {
      return visualClaims.get(name);
   }

   @ConstructorProperties({"player", "type", "bypass"})
   public VisualClaim(@NonNull Player player, @NonNull VisualClaimType type, @NonNull boolean bypass) {
      if (player == null) {
         throw new NullPointerException("player");
      }

      if (type == null) {
         throw new NullPointerException("type");
      }

      this.player = player;
      this.type = type;
      this.bypass = bypass;
   }

   public static Map<String, VisualClaim> getCurrentMaps() {
      return currentMaps;
   }

   public static Map<String, VisualClaim> getCurrentSubclaimMaps() {
      return currentSubclaimMaps;
   }

   public static Map<String, VisualClaim> getVisualClaims() {
      return visualClaims;
   }

   @NonNull
   public Player getPlayer() {
      return this.player;
   }

   @NonNull
   public VisualClaimType getType() {
      return this.type;
   }

   @NonNull
   public boolean isBypass() {
      return this.bypass;
   }

   public List<Location> getBlockChanges() {
      return this.blockChanges;
   }

   public Claim getResizing() {
      return this.resizing;
   }

   public void setResizing(Claim resizing) {
      this.resizing = resizing;
   }

   public Location getCorner1() {
      return this.corner1;
   }

   public void setCorner1(Location corner1) {
      this.corner1 = corner1;
   }

   public Location getCorner2() {
      return this.corner2;
   }

   public void setCorner2(Location corner2) {
      this.corner2 = corner2;
   }

   private static class QueuedBlockChange {
      private Location location;
      private Material type;
      private byte data;

      @ConstructorProperties({"location", "type", "data"})
      public QueuedBlockChange(Location location, Material type, byte data) {
         this.location = location;
         this.type = type;
         this.data = data;
      }

      public Location getLocation() {
         return this.location;
      }

      public Material getType() {
         return this.type;
      }

      public byte getData() {
         return this.data;
      }
   }
}
