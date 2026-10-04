package net.frozenorb.foxtrot.listener;

import cc.fyre.neutron.Neutron;
import cc.fyre.proton.event.HourEvent;
import cc.fyre.proton.util.ItemUtils;
import cc.fyre.proton.uuid.UUIDCache;
import com.vexsoftware.votifier.model.VotifierEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.commands.DisableCommand;
import net.frozenorb.foxtrot.commands.KillTheKingCommand;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.citadel.CitadelHandler;
import net.frozenorb.foxtrot.server.RegionData;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.team.commands.team.TeamStuckCommand;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.team.event.TeamEnterClaimEvent;
import net.frozenorb.foxtrot.team.event.TeamLeaveClaimEvent;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.foxtrot.util.PotionUtil;
import net.minecraft.util.com.google.common.collect.ImmutableMap;
import net.minecraft.util.com.google.common.collect.ImmutableSet;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemsEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.cavepvp.profiles.playerProfiles.PlayerProfileAPI;
import org.cavepvp.profiles.playerProfiles.impl.stats.StatisticType;
import org.cavepvp.suge.Suge;

public class FoxListener implements Listener {
   private Map<UUID, Integer> sessionVotes = new HashMap<>();
   public static final ItemStack FIRST_SPAWN_FISHING_ROD = new ItemStack(Material.FISHING_ROD);
   public static final Set<PotionEffectType> DEBUFFS = ImmutableSet.of(
      PotionEffectType.POISON, PotionEffectType.SLOW, PotionEffectType.WEAKNESS, PotionEffectType.HARM, PotionEffectType.WITHER
   );
   public static final Set<Material> NO_INTERACT_WITH = ImmutableSet.of(Material.LAVA_BUCKET, Material.WATER_BUCKET, Material.BUCKET);
   public static final Set<Material> ATTACK_DISABLING_BLOCKS = ImmutableSet.of(Material.GLASS, Material.WOOD_DOOR, Material.IRON_DOOR, Material.FENCE_GATE);
   public static final Set<Material> NO_INTERACT = ImmutableSet.of(
      Material.FENCE_GATE,
      Material.FURNACE,
      Material.BURNING_FURNACE,
      Material.BREWING_STAND,
      Material.CHEST,
      Material.HOPPER,
      new Material[]{
         Material.DISPENSER,
         Material.WOODEN_DOOR,
         Material.STONE_BUTTON,
         Material.WOOD_BUTTON,
         Material.TRAPPED_CHEST,
         Material.TRAP_DOOR,
         Material.LEVER,
         Material.DROPPER,
         Material.ENCHANTMENT_TABLE,
         Material.BED_BLOCK,
         Material.ANVIL,
         Material.BEACON
      }
   );
   private static final List<UUID> processingTeleportPlayers = new CopyOnWriteArrayList<>();
   private static final List<String> SPAWN_COMMANDS = Arrays.asList(
      "pv",
      "vault",
      "chest",
      "vc",
      "playervaults",
      "playervaults:pv",
      "playervaults:vault",
      "playervaults:chest",
      "playervaults:vc",
      "playervaults:playervaults"
   );
   private static final List<String> DISALLOWED_COMMANDS = Arrays.asList(
      "pv",
      "vault",
      "chest",
      "vc",
      "playervaults",
      "playervaults:pv",
      "playervaults:vault",
      "playervaults:chest",
      "playervaults:vc",
      "playervaults:playervaults",
      "tinker",
      "tinkerer",
      "rename"
   );

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onPlayerTeleport(PlayerTeleportEvent event) {
      this.processTerritoryInfo(event);
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onMelt(BlockFadeEvent event) {
      if (event.getBlock().getWorld().getEnvironment() == Environment.NETHER) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onVote(VotifierEvent event) {
      Server server = Foxtrot.getInstance().getServer();
      Player player = Foxtrot.getInstance().getServer().getPlayer(event.getVote().getUsername());
      if (player != null && player.isOnline()) {
         int votes = this.sessionVotes.getOrDefault(player.getUniqueId(), 0) + 1;
         this.sessionVotes.put(player.getUniqueId(), votes);
         if (votes >= 3) {
            this.sessionVotes.remove(player.getUniqueId());
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onPortal(PlayerPortalEvent event) {
      this.processTerritoryInfo(event);
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onCombatCommand(PlayerCommandPreprocessEvent event) {
      Player player = event.getPlayer();
      String command = event.getMessage().toLowerCase();
      String cmd = command.split(" ")[0].replace("/", "");
      if (player.isOp()
         || !event.getMessage().startsWith("/mv") && !event.getMessage().startsWith("/multiverse-core:mv") && !event.getMessage().startsWith("/multiverse")) {
         if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            if (SPAWN_COMMANDS.contains(cmd) && !DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
               event.setCancelled(true);
               player.sendMessage(ChatColor.RED + "You can only do this at spawn.");
            } else if (SpawnTagHandler.isTagged(event.getPlayer()) && DISALLOWED_COMMANDS.contains(cmd)) {
               event.setCancelled(true);
               player.sendMessage(ChatColor.RED + "You cannot use this command in combat.");
            } else {
               if (Foxtrot.getInstance().getMapHandler().getGameHandler() != null
                  && Foxtrot.getInstance().getMapHandler().getGameHandler().isOngoingGame()
                  && Foxtrot.getInstance().getMapHandler().getGameHandler().getOngoingGame().isPlayingOrSpectating(player.getUniqueId())
                  && DISALLOWED_COMMANDS.contains(cmd)) {
                  event.setCancelled(true);
                  player.sendMessage(ChatColor.RED + "You cannot use this command in a game.");
               }
            }
         }
      } else {
         event.setCancelled(true);
         player.sendMessage(ChatColor.RED + "No permission");
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onBlockDropItems(BlockDropItemsEvent event) {
      if (event.getToDrop().stream().anyMatch(it -> it.getItemStack().getType().equals(Material.SNOW_BALL))) {
         event.getToDrop().clear();
      }
   }

   @EventHandler
   public void onBreak(BlockBreakEvent event) {
      Player player = event.getPlayer();
      if (player.isOp()) {
         if (player.getItemInHand() != null
            && player.getItemInHand().getType() == Material.EYE_OF_ENDER
            && player.getItemInHand().hasItemMeta()
            && player.getItemInHand().getItemMeta().hasDisplayName()
            && player.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + ChatColor.BOLD.toString() + "Teleporter")) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Wooooooooooooooooooooooooooosh to the border!");
            Location location = this.findSuitableLocation(event.getBlock().getLocation().clone(), this.getDirection(player));
            location.setYaw(player.getLocation().getYaw());
            location.setPitch(player.getLocation().getPitch());
            player.teleport(location);
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onPlayerMove(PlayerMoveEvent event) {
      if (event.getFrom().getBlockX() != event.getTo().getBlockX()
         || event.getFrom().getBlockY() != event.getTo().getBlockY()
         || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
         if (ServerHandler.getTasks().containsKey(event.getPlayer().getName())) {
            Foxtrot.getInstance().getServer().getScheduler().cancelTask(ServerHandler.getTasks().get(event.getPlayer().getName()).getTaskId());
            ServerHandler.getTasks().remove(event.getPlayer().getName());
            event.getPlayer().sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "LOGOUT " + ChatColor.RED.toString() + ChatColor.BOLD + "CANCELLED!");
         }

         this.processTerritoryInfo(event);
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   private void onConsume(PlayerItemConsumeEvent event) {
      if (!event.isCancelled()) {
         Player player = event.getPlayer();
         ItemStack itemStack = event.getItem();
         if (itemStack.getType() == Material.GOLDEN_APPLE && itemStack.getDurability() != 0) {
            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
               player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 320, 4), true);
               player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 1220, 0), true);
               player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1, 0), true);
            }, 2L);
         } else if (!Foxtrot.getInstance().getMapHandler().isKitMap() && itemStack.getType() == Material.GOLDEN_APPLE && itemStack.getDurability() == 0) {
            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
               player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0), true);
               player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 80, 1), true);
            }, 2L);
         }
      }
   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent event) {
      event.setQuitMessage(null);
      Foxtrot.getInstance().getPlaytimeMap().playerQuit(event.getPlayer().getUniqueId(), true);
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onCommand(PlayerCommandPreprocessEvent event) {
      if (event.getMessage().startsWith("/to ")
         || event.getMessage().startsWith("/targetoffset ")
         || event.getMessage().startsWith("//targetoffset ")
         || event.getMessage().startsWith("//to ")) {
         event.setCancelled(true);
         event.getPlayer().sendMessage(ChatColor.RED + "No permission.");
      }

      if (event.getMessage().startsWith("/lives give")
         || event.getMessage().startsWith("/lives send")
         || event.getMessage().startsWith("/pvp sendlives")
         || event.getMessage().startsWith("/lives pay")) {
         event.setCancelled(true);
         event.getPlayer().sendMessage(ChatColor.RED + "No permission.");
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onXPChange(PlayerExpChangeEvent event) {
      Player player = event.getPlayer();
      if (!player.getWorld().getEnvironment().equals(Environment.NORMAL)) {
         event.setAmount(event.getAmount() * 2);
      }
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent event) {
      event.setJoinMessage(null);
      Player player = event.getPlayer();
      Foxtrot.getInstance().getPlaytimeMap().playerJoined(player.getUniqueId());
      Foxtrot.getInstance().getLastJoinMap().setLastJoin(player.getUniqueId());
      if (!player.hasPlayedBefore()) {
         Foxtrot.getInstance()
            .getServer()
            .dispatchCommand(Foxtrot.getInstance().getServer().getConsoleSender(), "clickitem give random-spawner 1 " + player.getName());
         Foxtrot.getInstance().getFriendLivesMap().setLives(player.getUniqueId(), 3);
         if (!Foxtrot.getInstance().getServerHandler().isAu()) {
            PlayerProfileAPI.addStatistic(event.getPlayer().getUniqueId(), Foxtrot.getInstance().getStatisticServer(), StatisticType.MAPS_PLAYED, 1);
         }

         Foxtrot.getInstance().getReclaimMap().setReclaimed(player.getUniqueId(), false);
         Foxtrot.getInstance().getFirstJoinMap().setFirstJoin(player.getUniqueId());
         if (Foxtrot.getInstance().getServerHandler().isTeams()) {
            Foxtrot.getInstance().getEconomyHandler().setBalance(player.getUniqueId(), 100.0);
         } else {
            Foxtrot.getInstance().getEconomyHandler().setBalance(player.getUniqueId(), 1500.0);
         }

         if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            player.getInventory().addItem(FIRST_SPAWN_FISHING_ROD);
            player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 16));
         }

         if (!CustomTimerCreateCommand.isSOTWTimer()) {
            if (Foxtrot.getInstance().getServerHandler().isStartingTimerEnabled()) {
               player.setMetadata("PVP_TIMER_BYPASS", new FixedMetadataValue(Foxtrot.getInstance(), true));
               Foxtrot.getInstance().getPvPTimerMap().createStartingTimer(player.getUniqueId(), (int)TimeUnit.HOURS.toSeconds(1L));
            } else {
               player.setMetadata("PVP_TIMER_BYPASS", new FixedMetadataValue(Foxtrot.getInstance(), true));
               Foxtrot.getInstance().getPvPTimerMap().createTimer(player.getUniqueId(), (int)TimeUnit.MINUTES.toSeconds(30L));
            }
         }

         Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            World spawn = Foxtrot.getInstance().getServer().getWorld("Spawn");
            if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
               spawn = Foxtrot.getInstance().getServer().getWorld("world");
            }

            player.teleport(spawn.getSpawnLocation());
         }, 5L);
         if (Foxtrot.getInstance().getDeathsMap().getDeaths(player.getUniqueId()) == 0) {
            Foxtrot.getInstance().getDeathsMap().setDeaths(player.getUniqueId(), 0);
         }

         if (Foxtrot.getInstance().getKillsMap().getKills(player.getUniqueId()) == 0) {
            Foxtrot.getInstance().getKillsMap().setKills(player.getUniqueId(), 0);
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
   public void onStealthPickaxe(BlockBreakEvent event) {
      Block block = event.getBlock();
      ItemStack inHand = event.getPlayer().getItemInHand();
      if (inHand.getType() == Material.GOLD_PICKAXE && inHand.hasItemMeta() && inHand.getItemMeta().getDisplayName().startsWith(ChatColor.AQUA.toString())) {
         event.setCancelled(true);
         block.breakNaturally(inHand);
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
   public void onStealthItemPickup(PlayerPickupItemEvent event) {
      ItemStack inHand = event.getPlayer().getItemInHand();
      if (inHand.getType() == Material.GOLD_PICKAXE && inHand.hasItemMeta() && inHand.getItemMeta().getDisplayName().startsWith(ChatColor.AQUA.toString())) {
         event.setCancelled(true);
         event.getPlayer().getInventory().addItem(event.getItem().getItemStack());
         event.getItem().remove();
      }
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onEntityDamage(EntityDamageEvent event) {
      if (event.getEntity() instanceof Player) {
         Player player = (Player)event.getEntity();
         if (ServerHandler.getTasks().containsKey(player.getName())) {
            Foxtrot.getInstance().getServer().getScheduler().cancelTask(ServerHandler.getTasks().get(player.getName()).getTaskId());
            ServerHandler.getTasks().remove(player.getName());
            player.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "LOGOUT " + ChatColor.RED.toString() + ChatColor.BOLD + "CANCELLED!");
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onProjectileInteract(PlayerInteractEvent event) {
      Player player = event.getPlayer();
      if (event.getItem() != null
         && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
         && event.getItem().getType() == Material.POTION) {
         ItemStack i = event.getItem();
         if (i.getDurability() != 0) {
            Potion pot = Potion.fromItemStack(i);
            if (pot != null && pot.isSplash() && pot.getType() != null && DEBUFFS.contains(pot.getType().getEffectType())) {
               if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
                  player.sendMessage(ChatColor.RED + "You cannot do this while your PVP Timer is active!");
                  player.sendMessage(ChatColor.RED + "Type '" + ChatColor.GRAY + "/pvp enable" + ChatColor.RED + "' to remove your timer.");
                  event.setCancelled(true);
                  return;
               }

               if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
                  event.setCancelled(true);
                  event.getPlayer().sendMessage(ChatColor.RED + "You cannot launch debuffs from inside spawn!");
                  event.getPlayer().updateInventory();
               }
            }
         }
      }

      if (event.getClickedBlock() != null) {
         if (event.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE && event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (event.getItem() != null && event.getItem().getType() == Material.ENCHANTED_BOOK) {
               event.getItem().setType(Material.BOOK);
               event.getPlayer().sendMessage(ChatColor.GREEN + "You reverted this book to its original form!");
               event.setCancelled(true);
            }
         } else if (!Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getPlayer(), event.getClickedBlock().getLocation())
            && !Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            if (!Foxtrot.getInstance().getServerHandler().isEOTW()) {
               Team team = LandBoard.getInstance().getTeam(event.getClickedBlock().getLocation());
               Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());
               if (team != null && !team.isMember(event.getPlayer().getUniqueId())) {
                  if (NO_INTERACT.contains(event.getClickedBlock().getType()) || NO_INTERACT_WITH.contains(event.getMaterial())) {
                     if (event.getClickedBlock().getType().name().contains("BUTTON")
                        || event.getClickedBlock().getType().name().contains("CHEST")
                        || event.getClickedBlock().getType().name().contains("DOOR")) {
                        CitadelHandler citadelHandler = Foxtrot.getInstance().getCitadelHandler();
                        if (team.getName().equalsIgnoreCase("TreasureCove") && team.getOwner() == null
                           || DTRBitmask.CITADEL.appliesAt(event.getClickedBlock().getLocation()) && citadelHandler.canLootCitadel(event.getPlayer())
                           || DTRBitmask.OUTPOST.appliesAt(event.getClickedBlock().getLocation())
                              && player.getWorld().getEnvironment().equals(Environment.NORMAL)
                              && playerTeam.hasRoadOutpost()) {
                           return;
                        }
                     }

                     if (event.getItem() != null
                        && (event.getClickedBlock().getType() != Material.HOPPER || player.isSneaking())
                        && event.getItem().getType() == Material.POTION
                        && event.getItem().getDurability() != 0) {
                        Potion potion = Potion.fromItemStack(event.getItem());
                        if (potion.isSplash()) {
                           PotionUtil.splashPotion(player, event.getItem());
                           if (player.getItemInHand() != null && player.getItemInHand().isSimilar(event.getItem())) {
                              player.setItemInHand(null);
                              player.updateInventory();
                           } else {
                              InventoryUtils.removeAmountFromInventory(player.getInventory(), event.getItem(), 1);
                           }
                        }
                     }

                     event.setCancelled(true);
                     event.getPlayer()
                        .sendMessage(ChatColor.YELLOW + "You cannot do this in " + team.getName(event.getPlayer()) + ChatColor.YELLOW + "'s territory.");
                     if (event.getMaterial() == Material.TRAP_DOOR || event.getMaterial() == Material.FENCE_GATE || event.getMaterial().name().contains("DOOR")
                        )
                      {
                        Foxtrot.getInstance().getServerHandler().disablePlayerAttacking(event.getPlayer(), 1);
                     }

                     return;
                  }

                  if (event.getAction() == Action.PHYSICAL) {
                     if (Foxtrot.getInstance().getMapHandler().isKitMap()
                        && Foxtrot.getInstance().getServerHandler().isWarzone(event.getClickedBlock().getLocation())) {
                        return;
                     }

                     event.setCancelled(true);
                  }
               } else if (event.getMaterial() == Material.LAVA_BUCKET) {
                  if (team == null || !team.isMember(event.getPlayer().getUniqueId())) {
                     event.setCancelled(true);
                     event.getPlayer().sendMessage(ChatColor.RED + "You can only do this in your own claims!");
                  }
               } else {
                  UUID uuid = player.getUniqueId();
                  if (team != null && !team.isCaptain(uuid) && !team.isCoLeader(uuid) && !team.isOwner(uuid)) {
                     Subclaim subclaim = team.getSubclaim(event.getClickedBlock().getLocation());
                     if (subclaim != null
                        && !subclaim.isMember(event.getPlayer().getUniqueId())
                        && (NO_INTERACT.contains(event.getClickedBlock().getType()) || NO_INTERACT_WITH.contains(event.getMaterial()))) {
                        event.setCancelled(true);
                        event.getPlayer()
                           .sendMessage(
                              ChatColor.YELLOW + "You do not have access to the subclaim " + ChatColor.GREEN + subclaim.getName() + ChatColor.YELLOW + "!"
                           );
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onSignInteract(final PlayerInteractEvent event) {
      if (event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getState() instanceof Sign) {
         Sign s = (Sign)event.getClickedBlock().getState();
         if (DTRBitmask.SAFE_ZONE.appliesAt(event.getClickedBlock().getLocation())) {
            if (s.getLine(0).contains("Kit")) {
               Foxtrot.getInstance().getServerHandler().handleKitSign(s, event.getPlayer());
            } else if (s.getLine(0).contains("Buy") || s.getLine(0).contains("Sell")) {
               Foxtrot.getInstance().getServerHandler().handleShopSign(s, event.getPlayer());
            }

            event.setCancelled(true);
         }
      }

      if (event.getItem() != null && event.getMaterial() == Material.SIGN && event.getItem().hasItemMeta() && event.getItem().getItemMeta().getLore() != null) {
         ArrayList<String> lore = (ArrayList<String>)event.getItem().getItemMeta().getLore();
         if (lore.size() > 1 && lore.get(1).contains("§e") && event.getClickedBlock() != null) {
            event.getClickedBlock()
               .getRelative(event.getBlockFace())
               .getState()
               .setMetadata("noSignPacket", new FixedMetadataValue(Foxtrot.getInstance(), true));
            (new BukkitRunnable() {
               @Override
               public void run() {
                  event.getClickedBlock().getRelative(event.getBlockFace()).getState().removeMetadata("noSignPacket", Foxtrot.getInstance());
               }
            }).runTaskLater(Foxtrot.getInstance(), 20L);
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onBeacon(BlockPlaceEvent event) {
      if ((event.getBlockPlaced().getType() == Material.BEACON || event.getBlockPlaced().getType() == Material.BEDROCK) && !event.getPlayer().isOp()) {
         event.setCancelled(true);
         event.getPlayer().sendMessage(ChatColor.RED + "You can't place " + ItemUtils.getName(event.getPlayer().getItemInHand()));
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void onSignPlace(BlockPlaceEvent event) {
      ItemStack hand = event.getItemInHand();
      if (hand.getType() == Material.SIGN && hand.hasItemMeta() && hand.getItemMeta().getLore() != null) {
         ArrayList<String> lore = (ArrayList<String>)hand.getItemMeta().getLore();
         if (event.getBlock().getType() == Material.WALL_SIGN || event.getBlock().getType() == Material.SIGN_POST) {
            Sign s = (Sign)event.getBlock().getState();

            for (int i = 0; i < 4; i++) {
               s.setLine(i, lore.get(i));
            }

            s.setMetadata("deathSign", new FixedMetadataValue(Foxtrot.getInstance(), true));
            s.update();
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onDamage(EntityDamageEvent event) {
      if (event.getEntity() instanceof Player) {
         if (event.getCause() == DamageCause.SUFFOCATION || event.getCause() == DamageCause.LIGHTNING) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onSpawner(SpawnerSpawnEvent event) {
      if (event.getEntity() instanceof EnderDragon || event.getEntity() instanceof Wither || event.getEntity() instanceof Giant) {
         Location location = event.getLocation();
         event.setCancelled(true);

         for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("neutron.staff")) {
               onlinePlayer.sendMessage("");
               onlinePlayer.sendMessage(ChatColor.translate("&4&l!!!! ALERT !!!!"));
               onlinePlayer.sendMessage(ChatColor.translate("&4&lAn " + event.getEntityType().name() + " has spawned! ALERT SIMPLY IMMEDIATELY!"));
               onlinePlayer.sendMessage(ChatColor.translate("&4&lLocation: " + location.getBlockX() + location.getBlockY() + location.getBlockZ()));
               onlinePlayer.sendMessage(ChatColor.translate("&4&l!!!! ALERT !!!!"));
               onlinePlayer.sendMessage("");
            }
         }
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void onSpawnerPlace(BlockPlaceEvent event) {
      Player player = event.getPlayer();
      Block block = event.getBlock();
      ItemStack itemStack = event.getItemInHand();
      if (!event.isCancelled() && itemStack != null && itemStack.getType() == Material.MOB_SPAWNER) {
         if (itemStack.getItemMeta() != null && itemStack.getItemMeta().getDisplayName() != null) {
            Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
            Team teamAt = LandBoard.getInstance().getTeam(block.getLocation());
            if (team != null && teamAt != null && teamAt.isMember(player.getUniqueId())) {
               boolean canPlace = false;
               String displayName = ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).toLowerCase();
               if (player.hasPermission("opasofjsdigasduighasuitguasrtg")) {
                  canPlace = true;
               } else if (displayName.contains("spider") || displayName.contains("skeleton") || displayName.contains("zombie")) {
                  canPlace = true;
               }

               if (!canPlace) {
                  player.sendMessage(ChatColor.RED + "You may not place this type of spawners!");
               } else {
                  String name = ChatColor.stripColor(itemStack.getItemMeta().getDisplayName());
                  String entName = name.replace(" Spawner", "");
                  EntityType type = EntityType.valueOf(entName.toUpperCase().replaceAll(" ", "_"));
                  CreatureSpawner spawner = (CreatureSpawner)block.getState();
                  spawner.setSpawnedType(type);
                  spawner.update();
                  event.getPlayer().sendMessage(ChatColor.GREEN + "You placed a " + entName + " spawner!");
               }
            } else {
               player.sendMessage(ChatColor.RED + "You can only place a Spawner in your claim!");
               event.setCancelled(true);
            }
         }
      }
   }

   @EventHandler
   public void onSignChange(SignChangeEvent e) {
      if (e.getBlock().getState().hasMetadata("deathSign") || ((Sign)e.getBlock().getState()).getLine(1).contains("§e")) {
         e.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onSignBreak(BlockBreakEvent e) {
      if ((e.getBlock().getType() == Material.WALL_SIGN || e.getBlock().getType() == Material.SIGN_POST)
         && (
            e.getBlock().getState().hasMetadata("deathSign")
               || e.getBlock().getState() instanceof Sign && ((Sign)e.getBlock().getState()).getLine(1).contains("§e")
         )) {
         e.setCancelled(true);
         Sign sign = (Sign)e.getBlock().getState();
         ItemStack deathsign = new ItemStack(Material.SIGN);
         ItemMeta meta = deathsign.getItemMeta();
         if (sign.getLine(1).contains("Captured")) {
            meta.setDisplayName("§dKOTH Capture Sign");
         } else {
            meta.setDisplayName("§dDeath Sign");
         }

         meta.setLore(Arrays.asList(sign.getLines()));
         deathsign.setItemMeta(meta);
         e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), deathsign);
         e.getBlock().setType(Material.AIR);
         e.getBlock().getState().removeMetadata("deathSign", Foxtrot.getInstance());
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   private void onHour(HourEvent event) {
      Foxtrot instance = Foxtrot.getInstance();
      if (!instance.getMapHandler().isKitMap() && !instance.getServerHandler().isTeams()) {
         Calendar calendar = new GregorianCalendar();
         calendar.setTime(new Date(System.currentTimeMillis()));
         int day = calendar.get(7);
         if (instance.getMapHandler().isKitMap()) {
            if (day == 4 && event.getHour() == 0) {
               instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), "customtimer create 86400 &d&l2x Points");
            }

            if (day == 6 && event.getHour() == 15) {
               instance.getEventHandler()
                  .getEvents()
                  .stream()
                  .filter(Event::isActive)
                  .forEach(
                     it -> {
                        instance.getServer()
                           .broadcastMessage("CONSOLE " + ChatColor.GOLD + " has cancelled " + ChatColor.WHITE + it.getName() + ChatColor.GOLD + ".");
                        it.setActive(false);
                     }
                  );
               instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), "activatekoth Citadel");
            }

            if (day == 3 && event.getHour() == 16) {
               instance.getNetworkBoosterHandler()
                  .getBoostersQueued()
                  .put(UUIDCache.CONSOLE_UUID, Collections.singletonList(instance.getNetworkBoosterHandler().findBooster("Frenzy")));
            }

            if (day == 5 && event.getHour() == 13) {
               instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), "customtimer create 18000 &2&lDouble Gem");
            }
         } else if (instance.getServerHandler().isAu()) {
            if (day == 5 && event.getHour() == 13) {
               instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), "customtimer create 86400 &d&l2x Points");
            }

            if (event.getHour() == 6) {
               if (day == 1 || day == 3) {
                  instance.getEventHandler()
                     .getEvents()
                     .stream()
                     .filter(Event::isActive)
                     .forEach(
                        it -> {
                           instance.getServer()
                              .broadcastMessage("CONSOLE " + ChatColor.GOLD + " has cancelled " + ChatColor.WHITE + it.getName() + ChatColor.GOLD + ".");
                           it.setActive(false);
                        }
                     );
                  instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), "activatekoth Citadel");
               }

               if (day == 2 || day == 4) {
                  instance.getEventHandler()
                     .getEvents()
                     .stream()
                     .filter(Event::isActive)
                     .forEach(
                        it -> {
                           instance.getServer()
                              .broadcastMessage("CONSOLE " + ChatColor.GOLD + " has cancelled " + ChatColor.WHITE + it.getName() + ChatColor.GOLD + ".");
                           it.setActive(false);
                        }
                     );
                  instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), "activatekoth NetherCitadel");
               }
            }
         } else {
            if (day == 1 && event.getHour() == 15 || day == 5 && event.getHour() == 13) {
               instance.getEventHandler()
                  .getEvents()
                  .stream()
                  .filter(Event::isActive)
                  .forEach(
                     it -> {
                        instance.getServer()
                           .broadcastMessage("CONSOLE " + ChatColor.GOLD + " has cancelled " + ChatColor.WHITE + it.getName() + ChatColor.GOLD + ".");
                        it.setActive(false);
                     }
                  );
               instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), "activatekoth Citadel");
            }

            if (day == 2 && event.getHour() == 12 || day == 3 && event.getHour() == 17) {
               instance.getEventHandler()
                  .getEvents()
                  .stream()
                  .filter(Event::isActive)
                  .forEach(
                     it -> {
                        instance.getServer()
                           .broadcastMessage("CONSOLE " + ChatColor.GOLD + " has cancelled " + ChatColor.WHITE + it.getName() + ChatColor.GOLD + ".");
                        it.setActive(false);
                     }
                  );
               instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), "activatekoth NetherCitadel");
            }

            if (day == 5 && event.getHour() == 0) {
               instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), "customtimer create 86400 &d&l2x Points");
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   private void onArrow(EntityDamageEvent event) {
      if (event.getCause().equals(DamageCause.PROJECTILE)) {
         event.setDamage(event.getDamage() * Foxtrot.getInstance().getConfig().getDouble("arrowDamageMultiplier", 0.3));
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
   private void onPvPDamage(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Player) {
         Player attacker = null;
         if (event.getDamager() instanceof Player) {
            attacker = (Player)event.getDamager();
         } else if (event.getDamager() instanceof Projectile && ((Projectile)event.getDamager()).getShooter() instanceof Player) {
            attacker = (Player)((Projectile)event.getDamager()).getShooter();
         }

         if (attacker != null && attacker != event.getEntity()) {
            event.setDamage(event.getDamage() * Foxtrot.getInstance().getConfig().getDouble("pvpDamageMultiplier", 0.85));
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   private void onHighDamage(EntityDamageEvent event) {
      if (event.getEntity() instanceof Player) {
         if (DisableCommand.damage) {
            if (event.getCause().equals(DamageCause.ENTITY_ATTACK)) {
               event.setDamage(event.getDamage() * 1.125);
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   private void onVehicle(VehicleEnterEvent event) {
      event.setCancelled(true);
   }

   @EventHandler(priority = EventPriority.LOWEST)
   private void onVehiclePlace(PlayerInteractEvent event) {
      if (event.getAction() != null && event.getItem() != null) {
         if (event.getAction().name().contains("RIGHT")
            && (
               event.getItem().getType() == Material.MINECART
                  || event.getItem().getType() == Material.BOAT && event.getPlayer().getWorld().getEnvironment() == Environment.THE_END
            )) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You can't place that vehicle here!");
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onPlayerDeath(PlayerDeathEvent event) {
      event.getEntity().getWorld().strikeLightningEffect(event.getEntity().getLocation());
      if (!Foxtrot.getInstance().getDeathbanArenaHandler().isDeathbanArena(event.getEntity())) {
         SpawnTagHandler.removeTag(event.getEntity());
         if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            if (KillTheKingCommand.king != null && event.getEntity().getUniqueId().toString().equalsIgnoreCase(KillTheKingCommand.king.toString())) {
               Player killer = event.getEntity().getKiller();
               if (killer != null) {
                  Foxtrot.getInstance().getServer().broadcastMessage("");
                  Foxtrot.getInstance()
                     .getServer()
                     .broadcastMessage(
                        ChatColor.translate(Neutron.getInstance().getProfileHandler().findDisplayName(killer.getUniqueId()) + " &6killed the king!")
                     );
                  Foxtrot.getInstance().getServer().broadcastMessage("");
               }
            }

            if (Foxtrot.getInstance().getInDuelPredicate().test(event.getEntity()) || Foxtrot.getInstance().getInEventPredicate().test(event.getEntity())) {
               return;
            }
         }

         Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(event.getEntity());
         Player killer = event.getEntity().getKiller();
         if (killer != null) {
            Team killerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(killer);
            Location deathLoc = event.getEntity().getLocation();
            int deathX = deathLoc.getBlockX();
            int deathY = deathLoc.getBlockY();
            int deathZ = deathLoc.getBlockZ();
            if (killerTeam != null) {
               TeamActionTracker.logActionAsync(
                  killerTeam,
                  TeamActionType.MEMBER_KILLED_ENEMY_IN_PVP,
                  ImmutableMap.of(
                     "playerId",
                     killer.getUniqueId(),
                     "playerName",
                     killer.getName(),
                     "killedId",
                     event.getEntity().getUniqueId(),
                     "killedName",
                     event.getEntity().getName(),
                     "coordinates",
                     deathX + ", " + deathY + ", " + deathZ
                  )
               );
            }

            if (playerTeam != null) {
               TeamActionTracker.logActionAsync(
                  playerTeam,
                  TeamActionType.MEMBER_KILLED_BY_ENEMY_IN_PVP,
                  ImmutableMap.of(
                     "playerId",
                     event.getEntity().getUniqueId(),
                     "playerName",
                     event.getEntity().getName(),
                     "killerId",
                     killer.getUniqueId(),
                     "killerName",
                     killer.getName(),
                     "coordinates",
                     deathX + ", " + deathY + ", " + deathZ
                  )
               );
            }
         }

         if (playerTeam != null) {
            playerTeam.playerDeath(
               event.getEntity().getName(),
               Foxtrot.getInstance().getServerHandler().getDTRLoss(event.getEntity()),
               killer,
               Suge.getInstance()
                  .getEnchantHandler()
                  .findAllCustomEnchants(event.getEntity())
                  .keySet()
                  .stream()
                  .anyMatch(it -> it.getName().equalsIgnoreCase("Evader"))
            );
         }

         double bal = Foxtrot.getInstance().getEconomyHandler().getBalance(event.getEntity().getUniqueId());
         Foxtrot.getInstance().getEconomyHandler().withdraw(event.getEntity().getUniqueId(), bal);
         if ((killer = event.getEntity().getKiller()) != null && !Double.isNaN(bal) && bal > 0.0) {
            Foxtrot.getInstance().getEconomyHandler().deposit(killer.getUniqueId(), bal);
            killer.sendMessage(
               ChatColor.GOLD
                  + "You earned "
                  + ChatColor.BOLD
                  + "$"
                  + bal
                  + ChatColor.GOLD
                  + " for killing "
                  + event.getEntity().getDisplayName()
                  + ChatColor.GOLD
                  + "!"
            );
         }
      }
   }

   private void processTerritoryInfo(PlayerMoveEvent event) {
      Team ownerTo = LandBoard.getInstance().getTeam(event.getTo());
      if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId()) && !DTRBitmask.SAFE_ZONE.appliesAt(event.getTo())) {
         if (!DTRBitmask.KOTH.appliesAt(event.getTo()) && !DTRBitmask.CITADEL.appliesAt(event.getTo())) {
            if (ownerTo != null && ownerTo.getOwner() != null && !ownerTo.getMembers().contains(event.getPlayer().getUniqueId())) {
               event.setCancelled(true);

               for (Claim claim : ownerTo.getClaims()) {
                  if (claim.contains(event.getFrom()) && !ownerTo.isMember(event.getPlayer().getUniqueId())) {
                     Location nearest = TeamStuckCommand.nearestSafeLocation(event.getPlayer().getLocation());
                     boolean spawn = false;
                     if (nearest == null) {
                        nearest = Foxtrot.getInstance().getServerHandler().getSpawnLocation();
                        spawn = true;
                     }

                     event.getPlayer().teleport(nearest);
                     event.getPlayer()
                        .sendMessage(
                           ChatColor.RED + "Moved you to " + (spawn ? "spawn" : "nearest unclaimed territory") + " because you were in land that was claimed."
                        );
                     return;
                  }
               }

               event.getPlayer().sendMessage(ChatColor.RED + "You cannot enter another team's territory with PvP Protection.");
               event.getPlayer().sendMessage(ChatColor.RED + "Use " + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + " to remove your protection.");
               return;
            }
         } else {
            Foxtrot.getInstance().getPvPTimerMap().removeTimer(event.getPlayer().getUniqueId());
            LunarClientListener.updateNametag(event.getPlayer());
            event.getPlayer().sendMessage(ChatColor.RED + "Your PvP Protection has been removed for entering claimed land.");
         }
      }

      Team ownerFrom = LandBoard.getInstance().getTeam(event.getFrom());
      if (ownerFrom != ownerTo) {
         ServerHandler sm = Foxtrot.getInstance().getServerHandler();
         RegionData from = sm.getRegion(ownerFrom, event.getFrom());
         RegionData to = sm.getRegion(ownerTo, event.getTo());
         if (from.equals(to)) {
            return;
         }

         if (!to.getRegionType().getMoveHandler().handleMove(event)) {
            return;
         }

         boolean fromReduceDeathban = from.getData() != null
            && (
               from.getData().hasDTRBitmask(DTRBitmask.FIVE_MINUTE_DEATHBAN)
                  || from.getData().hasDTRBitmask(DTRBitmask.FIFTEEN_MINUTE_DEATHBAN)
                  || from.getData().hasDTRBitmask(DTRBitmask.SAFE_ZONE)
            );
         boolean toReduceDeathban = to.getData() != null
            && (
               to.getData().hasDTRBitmask(DTRBitmask.FIVE_MINUTE_DEATHBAN)
                  || to.getData().hasDTRBitmask(DTRBitmask.FIFTEEN_MINUTE_DEATHBAN)
                  || to.getData().hasDTRBitmask(DTRBitmask.SAFE_ZONE)
            );
         if (fromReduceDeathban && from.getData() != null) {
            Event fromLinkedKOTH = Foxtrot.getInstance().getEventHandler().getEvent(from.getData().getName());
            if (fromLinkedKOTH != null && !fromLinkedKOTH.isActive()) {
               fromReduceDeathban = false;
            }
         }

         if (toReduceDeathban && to.getData() != null) {
            Event toLinkedKOTH = Foxtrot.getInstance().getEventHandler().getEvent(to.getData().getName());
            if (toLinkedKOTH != null && !toLinkedKOTH.isActive()) {
               toReduceDeathban = false;
            }
         }

         if (Foxtrot.getInstance().getToggleClaimMessageMap().areClaimMessagesEnabled(event.getPlayer().getUniqueId())) {
            FancyMessage nowLeaving = new FancyMessage("Now leaving: ").color(ChatColor.YELLOW).then(from.getName(event.getPlayer())).color(ChatColor.YELLOW);
            if (ownerFrom != null) {
               nowLeaving.command("/t i " + ownerFrom.getName()).tooltip(ChatColor.GREEN + "View team info");
            }

            nowLeaving.then(" (")
               .color(ChatColor.YELLOW)
               .then(fromReduceDeathban ? "Non-Deathban" : "Deathban")
               .color(fromReduceDeathban ? ChatColor.GREEN : ChatColor.RED)
               .then(")")
               .color(ChatColor.YELLOW);
            FancyMessage nowEntering = new FancyMessage("Now entering: ").color(ChatColor.YELLOW).then(to.getName(event.getPlayer())).color(ChatColor.WHITE);
            if (ownerTo != null) {
               nowEntering.command("/t i " + ownerTo.getName()).tooltip(ChatColor.GREEN + "View team info");
            }

            nowEntering.then(" (")
               .color(ChatColor.YELLOW)
               .then(toReduceDeathban ? "Non-Deathban" : "Deathban")
               .color(toReduceDeathban ? ChatColor.GREEN : ChatColor.RED)
               .then(")")
               .color(ChatColor.YELLOW);
            nowLeaving.send(event.getPlayer());
            nowEntering.send(event.getPlayer());
         }

         TeamEnterClaimEvent teamEnterClaimEvent = new TeamEnterClaimEvent(
            event.getPlayer(), event.getFrom(), event.getTo(), from.getData(), to.getData(), false
         );
         TeamLeaveClaimEvent teamLeaveClaimEvent = new TeamLeaveClaimEvent(
            event.getPlayer(), event.getFrom(), event.getTo(), from.getData(), to.getData(), false
         );
         Foxtrot.getInstance().getServer().getPluginManager().callEvent(teamEnterClaimEvent);
         Foxtrot.getInstance().getServer().getPluginManager().callEvent(teamLeaveClaimEvent);
         if (teamEnterClaimEvent.isCancelled() || teamLeaveClaimEvent.isCancelled()) {
            event.setCancelled(true);
         }
      }
   }

   private BlockFace getDirection(Player player) {
      float yaw = player.getLocation().getYaw();
      if (yaw < 0.0F) {
         yaw += 360.0F;
      }

      if (yaw >= 315.0F || yaw < 45.0F) {
         return BlockFace.SOUTH;
      } else if (yaw < 135.0F) {
         return BlockFace.WEST;
      } else if (yaw < 225.0F) {
         return BlockFace.NORTH;
      } else {
         return yaw < 315.0F ? BlockFace.EAST : BlockFace.NORTH;
      }
   }

   private Location findSuitableLocation(Location to, BlockFace blockFace) {
      Location newLocation = to.clone();
      int border = to.getWorld().getEnvironment() == Environment.NORMAL ? (Foxtrot.getInstance().getMapHandler().isKitMap() ? 2000 : 3000) : 1500;
      if (blockFace == BlockFace.NORTH) {
         newLocation.setZ(-border);
      }

      if (blockFace == BlockFace.SOUTH) {
         newLocation.setZ(border);
      }

      if (blockFace == BlockFace.WEST) {
         newLocation.setX(-border);
      }

      if (blockFace == BlockFace.EAST) {
         newLocation.setX(border);
      }

      newLocation.setY(to.getBlockY() + 1);
      return newLocation;
   }

   static {
      FIRST_SPAWN_FISHING_ROD.addEnchantment(Enchantment.LURE, 2);
   }
}
