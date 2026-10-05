package net.frozenorb.foxtrot.server;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.piston.PistonConstants;
import cc.fyre.proton.util.ItemUtils;
import cc.fyre.universe.util.BungeeUtil;
import java.beans.ConstructorProperties;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.config.ConfigMessages;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import net.frozenorb.foxtrot.listener.MegaFixListener;
import net.frozenorb.foxtrot.listener.SignShopListener;
import net.frozenorb.foxtrot.server.deathban.Deathban;
import net.frozenorb.foxtrot.server.idle.IdleCheckRunnable;
import net.frozenorb.foxtrot.server.uhc.UHCListener;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.foxtrot.util.Logout;
import net.frozenorb.foxtrot.util.Pair;
import net.frozenorb.foxtrot.util.SignShopFeedback;
import net.minecraft.util.com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ServerHandler {
   public static int WARZONE_RADIUS = 1000;
   public static int WARZONE_BORDER = 3000;
   private final Map<PotionType, ServerHandler.PotionStatus> potionStatus = new HashMap<>();
   private Map<PotionType, Integer> potionLimits = new HashMap<>();
   private static Map<String, Logout> tasks = new HashMap<>();
   private String serverName;
   private String networkWebsite;
   private String statsWebsiteRoot;
   private String tabServerName;
   private String tabSectionColor;
   private String tabInfoColor;
   private boolean teams;
   private boolean au;
   private boolean idleCheckEnabled;
   private boolean startingTimerEnabled;
   private boolean forceInvitesEnabled;
   private boolean uhcHealing;
   private boolean passiveTagEnabled;
   private boolean allowBoosting;
   private boolean waterPlacementInClaimsAllowed;
   private boolean blockRemovalEnabled;
   private boolean rodPrevention;
   private boolean skybridgePrevention;
   private boolean teamHQInEnemyClaims;
   private static Map<String, Long> homeTimer = new ConcurrentHashMap<>();
   private boolean EOTW = false;
   private boolean PreEOTW = false;
   private boolean reduceArmorDamage;
   private boolean blockEntitiesThroughPortals;
   private ChatColor archerTagColor;
   private ChatColor stunTagColor;
   private ChatColor defaultRelationColor;
   private boolean hardcore;
   private boolean placeBlocksInCombat;
   private Map<UUID, String> nickName = new HashMap<>();
   private Map<UUID, String> pastNicknames = new HashMap<>();
   private Map<UUID, Long> playerDamageRestrictMap = Maps.newHashMap();
   private HashMap<Sign, BukkitRunnable> showSignTasks = new HashMap<>();

   public ServerHandler() {
      this.reloadSettings(true);
      if (this.idleCheckEnabled) {
         FileConfiguration gameplay = Foxtrot.getInstance().getConfiguration("gameplay.yml");
         new IdleCheckRunnable()
            .runTaskTimer(
               Foxtrot.getInstance(),
               Math.max(0L, gameplay.getLong("gameplay.idle-check.initial-delay-ticks", 1200L)),
               Math.max(1L, gameplay.getLong("gameplay.idle-check.interval-ticks", 1200L))
            );
      }

      if (this.uhcHealing) {
         Bukkit.getPluginManager().registerEvents(new UHCListener(), Foxtrot.getInstance());
      }

      FileConfiguration messages;
      if ((messages = Foxtrot.getInstance().getConfiguration("messages.yml")).getBoolean("broadcasts.custom-rank.enabled", true)) {
         long initialDelay = Math.max(1L, messages.getLong("broadcasts.custom-rank.initial-delay-seconds", 350L)) * 20L;
         long interval = Math.max(1L, messages.getLong("broadcasts.custom-rank.interval-seconds", 350L)) * 20L;
         Foxtrot.getInstance()
            .getServer()
            .getScheduler()
            .runTaskTimerAsynchronously(
               Foxtrot.getInstance(),
               () -> {
                  StringBuilder stringBuilder = new StringBuilder();

                  for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                     Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(onlinePlayer.getUniqueId());
                     if (profile != null && profile.getActiveRank() != null && profile.getActiveRank().getName().equalsIgnoreCase("Custom")) {
                        stringBuilder.append(stringBuilder.length() == 0 ? "" : ChatColor.GRAY + ", " + ChatColor.WHITE).append(onlinePlayer.getDisplayName());
                     }
                  }

                  for (String line : ConfigMessages.lines(
                     "broadcasts.custom-rank.lines",
                     Arrays.asList(
                        "", "&6&l  Online Custom Ranks", "", "  %players%", "", "&7  Purchase the Custom Rank at", "&f  %store%/category/custom-rank", ""
                     ),
                     "%players%",
                     stringBuilder.length() == 0 ? "None" : stringBuilder.toString()
                  )) {
                     Foxtrot.getInstance().getServer().broadcastMessage(line);
                  }
               },
               initialDelay,
               interval
            );
      }

      this.registerPlayerDamageRestrictionListener();
      Bukkit.getServer().getPluginManager().registerEvents(new ServerListener(), Foxtrot.getInstance());
   }

   public void reloadSettings() {
      this.reloadSettings(false);
   }

   private void reloadSettings(boolean startup) {
      FileConfiguration branding = Foxtrot.getInstance().getConfiguration("branding.yml");
      this.serverName = branding.getString("serverName", "Teams");
      this.networkWebsite = branding.getString("networkWebsite", "Coming Soon!");
      this.statsWebsiteRoot = branding.getString("statsRoot", "Coming Soon!");
      this.tabServerName = branding.getString("tab.serverName", "&e&lTeams");
      this.tabSectionColor = branding.getString("tab.sectionColor", "&7");
      this.tabInfoColor = branding.getString("tab.infoColor", "&e");
      if (startup) {
         this.teams = Foxtrot.getInstance().getConfig().getBoolean("teams", false);
         this.idleCheckEnabled = Foxtrot.getInstance().getConfig().getBoolean("idleCheck", true);
         this.uhcHealing = Foxtrot.getInstance().getConfig().getBoolean("uhcHealing", false);
      } else {
         this.warnIfStartupOnlyChanged("teams", this.teams);
         this.warnIfStartupOnlyChanged("idleCheck", this.idleCheckEnabled);
         this.warnIfStartupOnlyChanged("uhcHealing", this.uhcHealing);
      }

      this.startingTimerEnabled = Foxtrot.getInstance().getConfig().getBoolean("startingTimer", false);
      this.forceInvitesEnabled = Foxtrot.getInstance().getConfig().getBoolean("forceInvites", true);
      this.au = Foxtrot.getInstance().getConfig().getBoolean("au", false);
      this.passiveTagEnabled = Foxtrot.getInstance().getConfig().getBoolean("passiveTag", true);
      this.allowBoosting = Foxtrot.getInstance().getConfig().getBoolean("allowBoosting", false);
      this.waterPlacementInClaimsAllowed = Foxtrot.getInstance().getConfig().getBoolean("waterPlacementInClaims", false);
      this.blockRemovalEnabled = Foxtrot.getInstance().getConfig().getBoolean("blockRemoval", false);
      this.rodPrevention = Foxtrot.getInstance().getConfig().getBoolean("rodPrevention", true);
      this.skybridgePrevention = Foxtrot.getInstance().getConfig().getBoolean("skybridgePrevention", true);
      this.teamHQInEnemyClaims = Foxtrot.getInstance().getConfig().getBoolean("teamHQInEnemyClaims", true);
      this.reduceArmorDamage = Foxtrot.getInstance().getConfig().getBoolean("reduceArmorDamage", true);
      this.blockEntitiesThroughPortals = Foxtrot.getInstance().getConfig().getBoolean("blockEntitiesThroughPortals", true);
      this.archerTagColor = this.readColor("archerTagColor", "YELLOW");
      this.stunTagColor = this.readColor("stunTagColor", "BLUE");
      this.defaultRelationColor = this.readColor("defaultRelationColor", "YELLOW");
      this.hardcore = Foxtrot.getInstance().getConfig().getBoolean("hardcore", false);
      this.placeBlocksInCombat = Foxtrot.getInstance().getConfig().getBoolean("placeBlocksInCombat", true);
      FileConfiguration potions = Foxtrot.getInstance().getConfiguration("potions.yml");
      this.potionStatus.clear();

      for (PotionType type : PotionType.values()) {
         if (type != PotionType.WATER) {
            String path = "potions." + type.name();
            this.potionStatus
               .put(
                  type,
                  new ServerHandler.PotionStatus(
                     potions.getBoolean(path + ".drinkables"), potions.getBoolean(path + ".splash"), potions.getInt(path + ".maxLevel", -1)
                  )
               );
         }
      }

      this.potionLimits.clear();
      this.potionLimits.put(PotionType.REGEN, potions.getInt("potions.REGEN.limit", 0));
      this.potionLimits.put(PotionType.POISON, potions.getInt("potions.POISON.limit", 1));
      this.potionLimits.put(PotionType.SLOWNESS, potions.getInt("potions.SLOWNESS.limit", 1));
      this.potionLimits.put(PotionType.STRENGTH, potions.getInt("potions.STRENGTH.limit", 0));
      this.potionLimits.put(PotionType.WEAKNESS, potions.getInt("potions.WEAKNESS.limit", 0));
      this.potionLimits.put(PotionType.INVISIBILITY, potions.getInt("potions.INVISIBILITY.limit", 1));
      this.potionLimits.put(PotionType.INSTANT_DAMAGE, potions.getInt("potions.INSTANT_DAMAGE.limit", 0));
   }

   private ChatColor readColor(String path, String fallback) {
      String configured = Foxtrot.getInstance().getConfig().getString(path, fallback);

      try {
         return ChatColor.valueOf(configured.toUpperCase(Locale.ENGLISH));
      } catch (IllegalArgumentException exception) {
         Foxtrot.getInstance().getLogger().warning("Invalid ChatColor '" + configured + "' at " + path + "; using " + fallback + ".");
         return ChatColor.valueOf(fallback);
      }
   }

   private void warnIfStartupOnlyChanged(String path, boolean activeValue) {
      boolean configuredValue = Foxtrot.getInstance().getConfig().getBoolean(path, activeValue);
      if (configuredValue != activeValue) {
         Foxtrot.getInstance().getLogger().warning(path + " is startup-only; keeping the current value until restart.");
      }
   }

   public int getPotionLimit(PotionType type) {
      return this.potionLimits.getOrDefault(type, type.getMaxLevel());
   }

   public void save() {
   }

   public String getEnchants() {
      return Enchantment.DAMAGE_ALL.getMaxLevel() == 0 && Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel() == 0
         ? "No Enchants"
         : "Prot " + Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel() + ", Sharp " + Enchantment.DAMAGE_ALL.getMaxLevel();
   }

   public boolean isWarzone(Location loc) {
      if (loc.getWorld().getEnvironment() == Environment.NETHER) {
         return MegaFixListener.isNetherWarzone(loc);
      }

      return loc.getWorld().getEnvironment() != Environment.NORMAL
         ? false
         : Math.abs(loc.getBlockX()) <= WARZONE_RADIUS && Math.abs(loc.getBlockZ()) <= WARZONE_RADIUS
            || Math.abs(loc.getBlockX()) > WARZONE_BORDER
            || Math.abs(loc.getBlockZ()) > WARZONE_BORDER;
   }

   public boolean isSplashPotionAllowed(PotionType type) {
      return !this.potionStatus.containsKey(type) || this.potionStatus.get(type).splash;
   }

   public boolean isDrinkablePotionAllowed(PotionType type) {
      return this.potionStatus.containsKey(type) && !this.potionStatus.get(type).drinkables;
   }

   public boolean isPotionLevelAllowed(PotionType type, int amplifier) {
      return this.potionStatus.containsKey(type) && this.potionStatus.get(type).maxLevel != -1 && this.potionStatus.get(type).maxLevel < amplifier;
   }

   public void startLogoutSequence(final Player player) {
      final FileConfiguration gameplay = Foxtrot.getInstance().getConfiguration("gameplay.yml");
      final int logoutSeconds = Math.max(1, gameplay.getInt("gameplay.logout.delay-seconds", 30));
      player.sendMessage(
         ConfigMessages.message(
            "messages.logout.started", "&e&lLogging out... &ePlease wait &e%seconds% &eseconds.", "%seconds%", Integer.toString(logoutSeconds)
         )
      );
      BukkitTask taskid = (new BukkitRunnable() {
         int seconds = logoutSeconds;

         @Override
         public void run() {
            if (player.hasMetadata(PistonConstants.FREEZE_METADATA)) {
               player.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "LOGOUT " + ChatColor.YELLOW + ChatColor.BOLD + "CANCELLED!");
               this.cancel();
            } else {
               this.seconds--;
               if (this.seconds == 0 && ServerHandler.tasks.containsKey(player.getName())) {
                  ServerHandler.tasks.remove(player.getName());
                  player.setMetadata("loggedout", new FixedMetadataValue(Foxtrot.getInstance(), true));
                  int minimumHub = gameplay.getInt("gameplay.logout.hub-minimum", 1);
                  int maximumHubExclusive = gameplay.getInt("gameplay.logout.hub-maximum-exclusive", 3);
                  int hub = maximumHubExclusive <= minimumHub ? minimumHub : ThreadLocalRandom.current().nextInt(minimumHub, maximumHubExclusive);
                  BungeeUtil.sendToServer(player, gameplay.getString("gameplay.logout.hub-prefix", "Hub-0") + hub);
                  Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                     if (player.isOnline()) {
                        player.kickPlayer(ConfigMessages.message("messages.logout.complete", "&eYou have been safely logged out of the server!"));
                     }
                  }, Math.max(0L, gameplay.getLong("gameplay.logout.fallback-kick-delay-ticks", 40L)));
                  this.cancel();
               }
            }
         }
      }).runTaskTimer(Foxtrot.getInstance(), 20L, 20L);
      tasks.put(player.getName(), new Logout(taskid.getTaskId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(logoutSeconds)));
   }

   public RegionData getRegion(Team ownerTo, Location location) {
      if (ownerTo != null && ownerTo.getOwner() == null) {
         if (ownerTo.hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
            return new RegionData(RegionType.SPAWN, ownerTo);
         }

         if (ownerTo.hasDTRBitmask(DTRBitmask.KOTH)) {
            return new RegionData(RegionType.KOTH, ownerTo);
         }

         if (ownerTo.hasDTRBitmask(DTRBitmask.CITADEL)) {
            return new RegionData(RegionType.CITADEL, ownerTo);
         }

         if (ownerTo.hasDTRBitmask(DTRBitmask.ROAD)) {
            return new RegionData(RegionType.ROAD, ownerTo);
         }

         if (ownerTo.hasDTRBitmask(DTRBitmask.CONQUEST)) {
            return new RegionData(RegionType.CONQUEST, ownerTo);
         }
      }

      if (ownerTo != null) {
         return new RegionData(RegionType.CLAIMED_LAND, ownerTo);
      } else {
         return this.isWarzone(location) ? new RegionData(RegionType.WARZONE, null) : new RegionData(RegionType.WILDNERNESS, null);
      }
   }

   public boolean isUnclaimed(Location loc) {
      return LandBoard.getInstance().getClaim(loc) == null && !this.isWarzone(loc);
   }

   public boolean isAdminOverride(Player player) {
      return player.getGameMode() == GameMode.CREATIVE && player.hasPermission("hcf.admin.override");
   }

   public boolean isNetherOpOverride(Player player, Location loc) {
      return player != null && player.isOp() && loc.getWorld().getEnvironment() == Environment.NETHER;
   }

   public World getMapWorld() {
      return Foxtrot.getInstance().getServer().getWorld(Foxtrot.getInstance().getMapHandler().isKitMap() ? "world" : "Spawn");
   }

   public Location getSpawnLocation() {
      return this.getMapWorld().getSpawnLocation().add(0.0, 1.0, 0.0);
   }

   public boolean isUnclaimedOrRaidable(Player player, Location loc) {
      Team owner = LandBoard.getInstance().getTeam(loc);
      if (owner == null) {
         return true;
      } else if (!owner.isRaidable()) {
         return false;
      } else if (player != null && owner.getRaidStealTeam() != null) {
         return Foxtrot.getInstance().getTeamHandler().getTeam(player) == null
            ? false
            : owner.getRaidStealTeam().toString().equalsIgnoreCase(Foxtrot.getInstance().getTeamHandler().getTeam(player).getUniqueId().toString());
      } else {
         return true;
      }
   }

   public double getDTRLoss(Player player) {
      return this.getDTRLoss(player.getLocation());
   }

   public double getDTRLoss(Location location) {
      double dtrLoss = 1.0;
      if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
         dtrLoss = 0.01;
      }

      Team ownerTo = LandBoard.getInstance().getTeam(location);
      if (Foxtrot.getInstance().getConquestHandler().getGame() != null
         && location.getWorld().getEnvironment() == Environment.THE_END
         && ownerTo != null
         && ownerTo.hasDTRBitmask(DTRBitmask.CONQUEST)) {
         dtrLoss = Math.min(dtrLoss, 0.5);
      }

      if (ownerTo != null) {
         if (ownerTo.hasDTRBitmask(DTRBitmask.QUARTER_DTR_LOSS)) {
            dtrLoss = Math.min(dtrLoss, 0.25);
         } else if (ownerTo.hasDTRBitmask(DTRBitmask.REDUCED_DTR_LOSS)) {
            dtrLoss = Math.min(dtrLoss, 0.75);
         }
      }

      return dtrLoss;
   }

   public long getDeathban(Player player) {
      return this.getDeathban(player.getUniqueId(), player.getLocation());
   }

   public long getDeathban(UUID playerUUID, Location location) {
      if (this.isPreEOTW()) {
         return TimeUnit.DAYS.toSeconds(1000L);
      }

      if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
         return TimeUnit.SECONDS.toSeconds(5L);
      }

      Team ownerTo = LandBoard.getInstance().getTeam(location);
      Player player = Foxtrot.getInstance().getServer().getPlayer(playerUUID);
      Event linkedKOTH;
      if (ownerTo != null
         && ownerTo.getOwner() == null
         && ((linkedKOTH = Foxtrot.getInstance().getEventHandler().getEvent(ownerTo.getName())) == null || linkedKOTH.isActive())) {
         if (ownerTo.hasDTRBitmask(DTRBitmask.FIVE_MINUTE_DEATHBAN)) {
            return TimeUnit.MINUTES.toSeconds(5L);
         }

         if (ownerTo.hasDTRBitmask(DTRBitmask.FIFTEEN_MINUTE_DEATHBAN)) {
            return TimeUnit.MINUTES.toSeconds(15L);
         }
      }

      return Deathban.getDeathbanSeconds(player);
   }

   public void beginHQWarp(final Player player, final Team team, int warmup, boolean charge) {
      Team inClaim = LandBoard.getInstance().getTeam(player.getLocation());
      if (team.getBalance() < 0.0) {
         team.setBalance(0.0);
      }

      if (inClaim != null) {
         if (Foxtrot.getInstance().getServerHandler().isTeams()
            && !this.teamHQInEnemyClaims
            && inClaim.getOwner() != null
            && !inClaim.isMember(player.getUniqueId())) {
            player.sendMessage(ChatColor.YELLOW + "You may not go to your team headquarters from an enemy's claim! Use '/f stuck' first.");
            return;
         }

         if (inClaim.getOwner() == null
            && (inClaim.hasDTRBitmask(DTRBitmask.KOTH) || inClaim.hasDTRBitmask(DTRBitmask.CITADEL) || inClaim.hasDTRBitmask(DTRBitmask.CONQUEST))) {
            player.sendMessage(ChatColor.YELLOW + "You may not go to your team headquarters from inside of events!");
            return;
         }

         if (inClaim.hasDTRBitmask(DTRBitmask.SAFE_ZONE)
            || CustomTimerCreateCommand.isSOTWTimer() && !CustomTimerCreateCommand.hasSOTWEnabled(player.getUniqueId())) {
            if (player.getWorld().getEnvironment() != Environment.THE_END) {
               player.sendMessage(ChatColor.YELLOW + "Warping to " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + "'s HQ.");
               player.teleport(team.getHQ());
            } else {
               player.sendMessage(ChatColor.YELLOW + "You cannot teleport to your end headquarters while you're in end spawn!");
            }

            return;
         }
      }

      if (SpawnTagHandler.isTagged(player)) {
         player.sendMessage(ChatColor.YELLOW + "You may not go to your team headquarters while spawn tagged!");
      } else {
         player.sendMessage(
            ChatColor.YELLOW
               + "Teleporting to your team's HQ in "
               + ChatColor.LIGHT_PURPLE
               + warmup
               + " seconds"
               + ChatColor.YELLOW
               + "... Stay still and do not take damage."
         );
         if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
            player.sendMessage(ChatColor.YELLOW + "Your PvP Timer will be removed if the teleport is not cancelled.");
         }

         homeTimer.put(player.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(warmup));
         final int finalWarmup = warmup;
         (new BukkitRunnable() {
               int time = finalWarmup;
               Location startLocation = player.getLocation();
               double startHealth = player.getHealth();

               @Override
               public void run() {
                  this.time--;
                  if (player.getLocation().getWorld().equals(this.startLocation.getWorld())
                     && !(player.getLocation().distanceSquared(this.startLocation) >= 0.1)
                     && !(player.getHealth() < this.startHealth)) {
                     this.startHealth = player.getHealth();
                     if (ServerHandler.homeTimer.containsKey(player.getName()) && ServerHandler.homeTimer.get(player.getName()) <= System.currentTimeMillis()) {
                        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
                           Foxtrot.getInstance().getPvPTimerMap().removeTimer(player.getUniqueId());
                           LunarClientListener.updateNametag(player);
                        }

                        for (EnderPearl enderPearl : player.getWorld().getEntitiesByClass(EnderPearl.class)) {
                           if (enderPearl.getShooter() != null && enderPearl.getShooter().equals(player)) {
                              enderPearl.remove();
                           }
                        }

                        player.sendMessage(ChatColor.YELLOW + "Warping to " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + "'s HQ.");
                        player.teleport(team.getHQ());
                        ServerHandler.homeTimer.remove(player.getName());
                        this.cancel();
                     } else {
                        if (this.time == 0) {
                           if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
                              player.removeMetadata("PVP_TIMER_BYPASS", Foxtrot.getInstance());
                              Foxtrot.getInstance().getPvPTimerMap().removeTimer(player.getUniqueId());
                              LunarClientListener.updateNametag(player);
                           }

                           for (EnderPearl enderPearl : player.getWorld().getEntitiesByClass(EnderPearl.class)) {
                              if (enderPearl.getShooter() != null && enderPearl.getShooter().equals(player)) {
                                 enderPearl.remove();
                              }
                           }

                           player.sendMessage(ChatColor.YELLOW + "Warping to " + ChatColor.YELLOW + team.getName() + ChatColor.YELLOW + "'s HQ.");
                           player.teleport(team.getHQ());
                           ServerHandler.homeTimer.remove(player.getName());
                           this.cancel();
                        }
                     }
                  } else {
                     player.sendMessage(ChatColor.YELLOW + "Teleport cancelled.");
                     ServerHandler.homeTimer.remove(player.getName());
                     this.cancel();
                  }
               }
            })
            .runTaskTimer(Foxtrot.getInstance(), 20L, 20L);
      }
   }

   public void disablePlayerAttacking(Player player, int seconds) {
      if (seconds == 10) {
         player.sendMessage(ChatColor.GRAY + "You cannot attack for " + seconds + " seconds.");
      }

      this.playerDamageRestrictMap.put(player.getUniqueId(), System.currentTimeMillis() + seconds * 1000L);
   }

   private void registerPlayerDamageRestrictionListener() {
      Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new Listener() {
         @EventHandler(ignoreCancelled = true)
         public void onDamage(EntityDamageByEntityEvent event) {
            Long expiry = ServerHandler.this.playerDamageRestrictMap.get(event.getDamager().getUniqueId());
            if (expiry != null && System.currentTimeMillis() < expiry) {
               event.setCancelled(true);
            }
         }

         @EventHandler
         public void onQuit(PlayerQuitEvent event) {
            ServerHandler.this.playerDamageRestrictMap.remove(event.getPlayer().getUniqueId());
         }
      }, Foxtrot.getInstance());
   }

   public boolean isCloseToSpawn(Location loc) {
      if (loc.getWorld().getEnvironment() != Environment.NORMAL) {
         return false;
      }

      if (loc.getWorld().getName().equalsIgnoreCase("kits_events")) {
         return false;
      }

      int radius = 100;
      int x = loc.getBlockX();
      int z = loc.getBlockZ();
      return x < radius && x > -radius && z < radius && z > -radius;
   }

   public boolean isSpawnBufferZone(Location loc) {
      if (loc.getWorld().getEnvironment() != Environment.NORMAL) {
         return false;
      }

      if (loc.getWorld().getName().equalsIgnoreCase("kits_events")) {
         return false;
      }

      int radius = Foxtrot.getInstance().getMapHandler().getWorldBuffer();
      int x = loc.getBlockX();
      int z = loc.getBlockZ();
      return x < radius && x > -radius && z < radius && z > -radius;
   }

   public boolean isNetherBufferZone(Location loc) {
      if (loc.getWorld().getEnvironment() != Environment.NETHER) {
         return false;
      }

      int radius = Foxtrot.getInstance().getMapHandler().getNetherBuffer();
      int x = loc.getBlockX();
      int z = loc.getBlockZ();
      return x < radius && x > -radius && z < radius && z > -radius;
   }

   public Pair<ItemStack, Pair<Integer, Long>> extractSignData(Sign sign) {
      ItemStack itemStack = sign.getLine(2).contains("Crowbar") ? InventoryUtils.CROWBAR : ItemUtils.get(sign.getLine(2).toLowerCase().replace(" ", ""));
      if (itemStack == null && sign.getLine(2).contains("Antidote")) {
         itemStack = InventoryUtils.ANTIDOTE;
      }

      if (itemStack == null) {
         System.err.println(sign.getLine(2).toLowerCase().replace(" ", ""));
         return null;
      } else {
         return null;
      }
   }

   public void handleShopSign(Sign sign, Player player) {
      if (!SignShopListener.isFormattedShopSign(sign)) {
         ItemStack itemStack = ItemUtils.get(sign.getLine(2).toLowerCase().replace(" ", ""));
         if (itemStack == null) {
            if (sign.getLine(2).contains("Crowbar")) {
               itemStack = InventoryUtils.CROWBAR;
            } else if (sign.getLine(2).contains("Antidote")) {
               itemStack = InventoryUtils.ANTIDOTE;
            } else if (sign.getLine(2).contains("Zombie Spawner")) {
               itemStack = InventoryUtils.ZOMBIE_SPAWNER.clone();
            } else if (sign.getLine(2).contains("Skele Spawner")) {
               itemStack = InventoryUtils.SKELETON_SPAWNER.clone();
            } else if (sign.getLine(2).contains("Spider Spawner")) {
               itemStack = InventoryUtils.SPIDER_SPAWNER.clone();
            } else if (sign.getLine(2).contains("Cave Spawner")) {
               itemStack = InventoryUtils.CAVE_SPIDER_SPAWNER.clone();
            }
         }

         if (itemStack == null && !sign.getLine(2).equalsIgnoreCase("valuables")) {
            System.err.println("Failed to handle shop sign, null item: " + sign.getLine(2).toLowerCase().replace(" ", ""));
         } else {
            if (sign.getLine(0).toLowerCase().contains("buy")) {
               int amount;
               int price;
               try {
                  price = Integer.parseInt(sign.getLine(3).replace("$", "").replace(",", ""));
                  amount = Integer.parseInt(sign.getLine(1));
               } catch (NumberFormatException e) {
                  return;
               }

               if (Foxtrot.getInstance().getEconomyHandler().getBalance(player.getUniqueId()) >= price) {
                  if (Double.isNaN(Foxtrot.getInstance().getEconomyHandler().getBalance(player.getUniqueId()))) {
                     Foxtrot.getInstance().getEconomyHandler().setBalance(player.getUniqueId(), 0.0);
                     player.sendMessage("§cYour balance was fucked, but we unfucked it.");
                     return;
                  }

                  if (player.getInventory().firstEmpty() != -1) {
                     Foxtrot.getInstance().getEconomyHandler().withdraw(player.getUniqueId(), price);
                     itemStack.setAmount(amount);
                     player.getInventory().addItem(itemStack);
                     player.updateInventory();
                     SignShopFeedback.show(
                        player, sign, 1, ConfigMessages.message("messages.sign-shop.bought", "&aBought %amount%", "%amount%", String.valueOf(amount))
                     );
                  } else {
                     this.showSignPacket(player, sign, "§c§lError!", "", "§cNo space", "§cin inventory!");
                  }
               } else {
                  SignShopFeedback.show(player, sign, 3, ConfigMessages.message("messages.sign-shop.cannot-afford", "&cCan't afford"));
               }
            } else if (sign.getLine(0).toLowerCase().contains("sell")) {
               int amount;
               double pricePerItem;
               try {
                  int price = Integer.parseInt(sign.getLine(3).replace("$", "").replace(",", ""));
                  amount = Integer.parseInt(sign.getLine(1));
                  pricePerItem = (float)price / amount;
               } catch (NumberFormatException e) {
                  return;
               }

               int amountInInventory = Math.min(amount, this.countItems(player, itemStack.getType(), itemStack.getDurability()));
               if (amountInInventory == 0) {
                  SignShopFeedback.show(player, sign, 1, ConfigMessages.message("messages.sign-shop.not-carrying", "&cNot carrying"));
               } else {
                  int totalPrice = (int)(amountInInventory * pricePerItem);
                  this.removeItem(player, itemStack, amountInInventory);
                  player.updateInventory();
                  Foxtrot.getInstance().getEconomyHandler().deposit(player.getUniqueId(), totalPrice);
                  SignShopFeedback.show(
                     player, sign, 1, ConfigMessages.message("messages.sign-shop.sold", "&cSold %amount%", "%amount%", String.valueOf(amountInInventory))
                  );
               }
            }
         }
      }
   }

   public void handleKitSign(Sign sign, Player player) {
      String kit = ChatColor.stripColor(sign.getLine(1));
      if (kit.equalsIgnoreCase("Fishing")) {
         int uses = Foxtrot.getInstance().getFishingKitMap().getUses(player.getUniqueId());
         if (uses == 3) {
            this.showSignPacket(player, sign, "§aFishing Kit:", "", "§cAlready used", "§c3/3 times!");
         } else {
            ItemStack rod = new ItemStack(Material.FISHING_ROD);
            rod.addEnchantment(Enchantment.LURE, 2);
            player.getInventory().addItem(rod);
            player.updateInventory();
            player.sendMessage(ChatColor.GOLD + "Equipped the " + ChatColor.WHITE + "Fishing" + ChatColor.GOLD + " kit!");
            Foxtrot.getInstance().getFishingKitMap().setUses(player.getUniqueId(), uses + 1);
            this.showSignPacket(player, sign, "§aFishing Kit:", "§bEquipped!", "", "§dUses: §e" + (uses + 1) + "/3");
         }
      }
   }

   public void removeItem(Player p, ItemStack it, int amount) {
      boolean specialDamage = it.getType().getMaxDurability() == 0;

      for (int a = 0; a < amount; a++) {
         for (ItemStack i : p.getInventory()) {
            if (i != null && i.getType() == it.getType() && (!specialDamage || it.getDurability() == i.getDurability())) {
               if (i.getAmount() == 1) {
                  p.getInventory().clear(p.getInventory().first(i));
               } else {
                  i.setAmount(i.getAmount() - 1);
               }
               break;
            }
         }
      }
   }

   public ItemStack generateDeathSign(String killed, String killer) {
      ItemStack deathsign = new ItemStack(Material.SIGN);
      ItemMeta meta = deathsign.getItemMeta();
      ArrayList<String> lore = new ArrayList<>();
      lore.add("§4" + killed);
      lore.add("§eSlain By:");
      lore.add("§a" + killer);
      SimpleDateFormat sdf = new SimpleDateFormat("M/d HH:mm:ss");
      lore.add(sdf.format(new Date()).replace(" AM", "").replace(" PM", ""));
      meta.setLore(lore);
      meta.setDisplayName("§dDeath Sign");
      deathsign.setItemMeta(meta);
      return deathsign;
   }

   public ItemStack generateKOTHSign(String koth, String capper, EventType eventType) {
      ItemStack kothsign = new ItemStack(Material.SIGN);
      ItemMeta meta = kothsign.getItemMeta();
      ArrayList<String> lore = new ArrayList<>();
      lore.add("§9" + koth);
      lore.add("§eCaptured By:");
      lore.add("§a" + capper);
      SimpleDateFormat sdf = new SimpleDateFormat("M/d HH:mm:ss");
      lore.add(sdf.format(new Date()).replace(" AM", "").replace(" PM", ""));
      meta.setLore(lore);
      meta.setDisplayName("§d" + eventType.name() + "Capture Sign");
      kothsign.setItemMeta(meta);
      return kothsign;
   }

   public void showSignPacket(Player player, final Sign sign, String... lines) {
      player.sendSignChange(sign.getLocation(), lines);
      if (this.showSignTasks.containsKey(sign)) {
         this.showSignTasks.remove(sign).cancel();
      }

      BukkitRunnable br = new BukkitRunnable() {
         @Override
         public void run() {
            sign.update();
            ServerHandler.this.showSignTasks.remove(sign);
         }
      };
      this.showSignTasks.put(sign, br);
      br.runTaskLater(Foxtrot.getInstance(), 90L);
   }

   public int countItems(Player player, Material material, int damageValue) {
      PlayerInventory inventory = player.getInventory();
      ItemStack[] items = inventory.getContents();
      int amount = 0;

      for (ItemStack item : items) {
         if (item != null) {
            boolean specialDamage = material.getMaxDurability() == 0;
            if (item.getType() != null && item.getType() == material && (!specialDamage || item.getDurability() == (short)damageValue)) {
               amount += item.getAmount();
            }
         }
      }

      return amount;
   }

   public String getNick(Player player) {
      return !this.nickName.containsKey(player.getUniqueId()) ? player.getName() : this.nickName.get(player.getUniqueId());
   }

   public Map<PotionType, Integer> getPotionLimits() {
      return this.potionLimits;
   }

   public static Map<String, Logout> getTasks() {
      return tasks;
   }

   public String getServerName() {
      return this.serverName;
   }

   public String getNetworkWebsite() {
      return "Coming Soon!";
   }

   public String getStatsWebsiteRoot() {
      return "Coming Soon!";
   }

   public String getTabServerName() {
      return this.tabServerName;
   }

   public String getTabSectionColor() {
      return this.tabSectionColor;
   }

   public String getTabInfoColor() {
      return this.tabInfoColor;
   }

   public boolean isTeams() {
      return this.teams;
   }

   public boolean isAu() {
      return this.au;
   }

   public boolean isIdleCheckEnabled() {
      return this.idleCheckEnabled;
   }

   public boolean isStartingTimerEnabled() {
      return this.startingTimerEnabled;
   }

   public boolean isForceInvitesEnabled() {
      return this.forceInvitesEnabled;
   }

   public boolean isUhcHealing() {
      return this.uhcHealing;
   }

   public boolean isPassiveTagEnabled() {
      return this.passiveTagEnabled;
   }

   public boolean isAllowBoosting() {
      return this.allowBoosting;
   }

   public boolean isWaterPlacementInClaimsAllowed() {
      return this.waterPlacementInClaimsAllowed;
   }

   public boolean isBlockRemovalEnabled() {
      return this.blockRemovalEnabled;
   }

   public boolean isRodPrevention() {
      return this.rodPrevention;
   }

   public boolean isSkybridgePrevention() {
      return this.skybridgePrevention;
   }

   public boolean isTeamHQInEnemyClaims() {
      return this.teamHQInEnemyClaims;
   }

   public static Map<String, Long> getHomeTimer() {
      return homeTimer;
   }

   public boolean isEOTW() {
      return this.EOTW;
   }

   public void setEOTW(boolean EOTW) {
      this.EOTW = EOTW;
   }

   public boolean isPreEOTW() {
      return this.PreEOTW;
   }

   public void setPreEOTW(boolean PreEOTW) {
      this.PreEOTW = PreEOTW;
   }

   public boolean isReduceArmorDamage() {
      return this.reduceArmorDamage;
   }

   public boolean isBlockEntitiesThroughPortals() {
      return this.blockEntitiesThroughPortals;
   }

   public ChatColor getArcherTagColor() {
      return this.archerTagColor;
   }

   public ChatColor getStunTagColor() {
      return this.stunTagColor;
   }

   public ChatColor getDefaultRelationColor() {
      return this.defaultRelationColor;
   }

   public boolean isHardcore() {
      return this.hardcore;
   }

   public boolean isPlaceBlocksInCombat() {
      return this.placeBlocksInCombat;
   }

   public Map<UUID, String> getNickName() {
      return this.nickName;
   }

   public Map<UUID, String> getPastNicknames() {
      return this.pastNicknames;
   }

   private static class PotionStatus {
      private boolean drinkables;
      private boolean splash;
      private int maxLevel;

      @ConstructorProperties({"drinkables", "splash", "maxLevel"})
      public PotionStatus(boolean drinkables, boolean splash, int maxLevel) {
         this.drinkables = drinkables;
         this.splash = splash;
         this.maxLevel = maxLevel;
      }
   }
}
