package net.frozenorb.foxtrot.team;

import cc.fyre.modsuite.mod.ModHandler;
import cc.fyre.neutron.util.PlayerUtil;
import cc.fyre.proton.Proton;
import cc.fyre.proton.serialization.LocationSerializer;
import cc.fyre.proton.util.TimeUtils;
import cc.fyre.proton.util.UUIDUtils;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.chat.enums.ChatMode;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.fury.FuryCapZone;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.gameplay.events.outposts.data.Outpost;
import net.frozenorb.foxtrot.gameplay.events.outposts.type.EndOutpost;
import net.frozenorb.foxtrot.gameplay.events.outposts.type.NetherOutpost;
import net.frozenorb.foxtrot.gameplay.events.outposts.type.RoadOutpost;
import net.frozenorb.foxtrot.gameplay.events.outposts.type.kitmap.KitmapOutpost;
import net.frozenorb.foxtrot.gameplay.events.region.glowmtn.GlowHandler;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import net.frozenorb.foxtrot.persist.maps.DeathbanMap;
import net.frozenorb.foxtrot.persist.maps.KillsMap;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import net.frozenorb.foxtrot.team.event.TeamRaidableEvent;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import net.frozenorb.foxtrot.team.upgrade.UpgradeType;
import net.frozenorb.foxtrot.util.CuboidRegion;
import net.frozenorb.foxtrot.util.LocationUtil;
import net.minecraft.util.com.google.common.collect.ImmutableMap;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.material.TrapDoor;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;

public class Team {
   public static final DecimalFormat DTR_FORMAT = new DecimalFormat("0.00");
   public static final String GRAY_LINE = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 53);
   public static final String DARK_GRAY_LINE = ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 53);
   public static final ChatColor ALLY_COLOR = ChatColor.BLUE;
   public static final int MAX_CLAIMS = 2;
   public static final int MAX_FORCE_INVITES = 5;
   private LCWaypoint homeWaypoint;
   private LCWaypoint focusWaypoint;
   private LCWaypoint rallyWaypoint;
   private boolean needsSave = false;
   private boolean loading = false;
   private ObjectId uniqueId;
   private String name;
   private Location HQ;
   private ObjectId raidStealTeam;
   private double balance;
   private int baseTokens;
   private int fallTrapTokens;
   private double DTR;
   private int trapDoors;
   private long DTRCooldown;
   private List<Claim> claims = new ArrayList<>();
   private List<Subclaim> subclaims = new ArrayList<>();
   private UUID owner = null;
   private Set<UUID> members = new HashSet<>();
   private Set<UUID> captains = new HashSet<>();
   private Set<UUID> coleaders = new HashSet<>();
   private Set<UUID> invitations = new HashSet<>();
   private Map<UUID, Role> roster = new HashMap<>();
   private Set<ObjectId> allies = new HashSet<>();
   private Set<ObjectId> requestedAllies = new HashSet<>();
   private List<PotionEffectType> purchasedEffects = new ArrayList<>();
   private List<UpgradeType> purchasedUpgrades = new ArrayList<>();
   private String announcement;
   private int maxOnline = -1;
   private boolean powerFaction = false;
   private int lives = 0;
   private int kills = 0;
   private int addedGems = 0;
   private int removedGems = 0;
   private int kothCaptures = 0;
   private int miniKothCaptures = 0;
   private int furysCaptured = 0;
   private int doublePoints = 0;
   private int doubleKillPoints = 0;
   private int addedPoints = 0;
   private int trappingPoints = 0;
   private int playtimeGems = 0;
   private int points = 0;
   private int gems = 0;
   private int removedPoints = 0;
   private int factionsMadeRaidable = 0;
   private int citadelsCapped = 0;
   private int conquestsCapped = 0;
   private int spawnersInClaim = 0;
   private int diamondsMined = 0;
   private int deaths = 0;
   private boolean claimLocked = false;
   private boolean eotwCapped = false;
   private Location rallyPoint = null;
   private int forceInvites = 5;
   private Set<UUID> historicalMembers = new HashSet<>();
   private UUID focused;
   private Team focusedTeam;
   private long lastRequestReport;
   private Player rallyPlayer;
   private int bards;
   private int archers;
   private int rogues;

   public void setRally(Location location) {
   }

   public Team(String name) {
      this.name = name;
   }

   public int getDeathsTilRaidable() {
      return (int)Math.ceil(this.getDTR());
   }

   public void setDTR(double newDTR) {
      this.setDTR(newDTR, null);
   }

   public void setDTR(double newDTR, Player actor) {
      if (this.DTR != newDTR) {
         if (this.DTR <= 0.0 && newDTR > 0.0) {
            TeamActionTracker.logActionAsync(this, TeamActionType.TEAM_NO_LONGER_RAIDABLE, ImmutableMap.of());
         }

         if (0.0 < this.DTR && newDTR <= 0.0) {
            TeamActionTracker.logActionAsync(
               this, TeamActionType.TEAM_NOW_RAIDABLE, actor == null ? ImmutableMap.of() : ImmutableMap.of("actor", actor.getName())
            );
         }

         if (!this.isLoading()) {
            if (actor != null) {
               Foxtrot.getInstance()
                  .getLogger()
                  .info("[DTR Change] " + this.getName() + ": " + this.DTR + ChatColor.STRIKETHROUGH + "-->" + newDTR + ". Actor: " + actor.getName());
            } else {
               Foxtrot.getInstance().getLogger().info("[DTR Change] " + this.getName() + ": " + this.DTR + ChatColor.STRIKETHROUGH + "-->" + newDTR);
            }
         }

         this.DTR = newDTR;
         this.flagForSave();
         this.getOnlineMembers().forEach(LunarClientListener::updateNametag);
      }
   }

   public void setName(String name) {
      this.name = name;
      this.flagForSave();
   }

   public String getName(Player player) {
      if (this.name.equals(GlowHandler.getGlowTeamName()) && this.getMembers().size() == 0) {
         return ChatColor.GOLD + "Glowstone Mountain";
      }

      if (this.owner == null) {
         if (this.hasDTRBitmask(DTRBitmask.FIVE_MINUTE_DEATHBAN) && this.name.equalsIgnoreCase("EndPortal")) {
            return ChatColor.DARK_PURPLE + "End Portal";
         }

         if (this.getName().equalsIgnoreCase("TreasureCove") && this.hasDTRBitmask(DTRBitmask.MOUNTAIN)) {
            return ChatColor.GOLD + "Treasure Cove";
         }

         if (this.hasDTRBitmask(DTRBitmask.OUTPOST)) {
            if (this.getName().startsWith("Road")) {
               return ChatColor.GOLD + "Road Outpost";
            }

            if (this.getName().startsWith("Nether")) {
               return ChatColor.DARK_RED + "Nether Outpost";
            }

            if (this.getName().startsWith("End")) {
               return ChatColor.DARK_PURPLE + "End Outpost";
            }

            return ChatColor.GOLD + "Outpost";
         }

         if (this.name.startsWith("Fury")) {
            FuryCapZone furyCapZone = FuryCapZone.valueOf(this.name.replace("Fury", "").toUpperCase());
            if (furyCapZone == null) {
               return ChatColor.RED + this.name.replace("Fury", " ");
            }

            return furyCapZone.getChatColor() + "Fury " + furyCapZone.getDisplayName();
         }

         if (this.hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
            if (this.name.toLowerCase().contains("nether")) {
               return ChatColor.RED + this.name;
            }

            return ChatColor.GREEN + this.name;
         }

         if (this.name.equalsIgnoreCase("Hell") & this.hasDTRBitmask(DTRBitmask.KOTH)) {
            return ChatColor.RED + "Hell KOTH";
         }

         if (this.name.equalsIgnoreCase("EOTW") && this.hasDTRBitmask(DTRBitmask.KOTH)) {
            return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "EOTW";
         }

         if (this.name.equalsIgnoreCase("End") && this.hasDTRBitmask(DTRBitmask.KOTH)) {
            return ChatColor.DARK_PURPLE + "End KOTH";
         }

         if (this.hasDTRBitmask(DTRBitmask.KOTH)) {
            return ChatColor.BLUE + this.getName() + " KOTH";
         }

         if (this.hasDTRBitmask(DTRBitmask.CITADEL) && this.name.equalsIgnoreCase("NetherCitadel")) {
            return ChatColor.DARK_RED + "Nether Citadel";
         }

         if (this.hasDTRBitmask(DTRBitmask.CITADEL)) {
            return ChatColor.DARK_PURPLE + "Citadel";
         }

         if (this.hasDTRBitmask(DTRBitmask.ROAD)) {
            return ChatColor.GREEN + this.getName().replace("Road", " Road");
         }

         if (this.hasDTRBitmask(DTRBitmask.CONQUEST)) {
            return ChatColor.BLUE + "Conquest";
         }

         if (this.name.equalsIgnoreCase("warzone")) {
            return ChatColor.RED + "WarZone";
         }
      }

      if (this.isMember(player.getUniqueId())) {
         return ChatColor.GREEN + this.getName();
      }

      if (this.isAlly(player.getUniqueId())) {
         return ALLY_COLOR + this.getName();
      }

      Team targetTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player);
      return targetTeam != null && targetTeam.getFocusedTeam() == this ? ChatColor.LIGHT_PURPLE + this.getName() : ChatColor.DARK_RED + this.getName();
   }

   public void addMember(UUID member) {
      if (this.members.add(member)) {
         this.historicalMembers.add(member);
         if (this.loading) {
            return;
         }

         TeamActionTracker.logActionAsync(this, TeamActionType.PLAYER_JOINED, ImmutableMap.of("playerId", member));
         this.flagForSave();
      }
   }

   public void addCaptain(UUID captain) {
      if (this.captains.add(captain) && !this.isLoading()) {
         TeamActionTracker.logActionAsync(this, TeamActionType.PROMOTED_TO_CAPTAIN, ImmutableMap.of("playerId", captain));
         this.flagForSave();
      }
   }

   public void addCoLeader(UUID co) {
      if (this.coleaders.add(co) && !this.isLoading()) {
         TeamActionTracker.logActionAsync(this, TeamActionType.PROMOTED_TO_CO_LEADER, ImmutableMap.of("playerId", co));
         this.flagForSave();
      }
   }

   public void setBalance(double balance) {
      this.balance = balance;
      this.flagForSave();
   }

   public void setDTRCooldown(long dtrCooldown) {
      this.DTRCooldown = dtrCooldown;
      this.flagForSave();
   }

   public void removeCaptain(UUID captain) {
      if (this.captains.remove(captain)) {
         TeamActionTracker.logActionAsync(this, TeamActionType.DEMOTED_FROM_CAPTAIN, ImmutableMap.of("playerId", captain));
         this.flagForSave();
      }
   }

   public void removeCoLeader(UUID co) {
      if (this.coleaders.remove(co)) {
         TeamActionTracker.logActionAsync(this, TeamActionType.DEMOTED_FROM_CO_LEADER, ImmutableMap.of("playerId", co));
         this.flagForSave();
      }
   }

   public void setOwner(UUID owner) {
      this.owner = owner;
      if (owner != null) {
         this.members.add(owner);
         this.coleaders.remove(owner);
         this.captains.remove(owner);
      }

      if (!this.loading) {
         if (owner != null) {
            TeamActionTracker.logActionAsync(this, TeamActionType.LEADER_CHANGED, ImmutableMap.of("playerId", owner));
         }

         this.flagForSave();
      }
   }

   public void setMaxOnline(int maxOnline) {
      this.maxOnline = maxOnline;
      this.flagForSave();
   }

   public void setAnnouncement(String announcement) {
      this.announcement = announcement;
      if (!this.loading) {
         TeamActionTracker.logActionAsync(this, TeamActionType.ANNOUNCEMENT_CHANGED, ImmutableMap.of("newAnnouncement", announcement));
         this.flagForSave();
      }
   }

   public void setHQ(Location hq) {
      String oldHQ = this.HQ == null ? "None" : this.getHQ().getBlockX() + ", " + this.getHQ().getBlockY() + ", " + this.getHQ().getBlockZ();
      String newHQ = hq == null ? "None" : hq.getBlockX() + ", " + hq.getBlockY() + ", " + hq.getBlockZ();
      this.HQ = hq;
      if (!this.loading) {
         TeamActionTracker.logActionAsync(this, TeamActionType.HEADQUARTERS_CHANGED, ImmutableMap.of("oldHq", oldHQ, "newHq", newHQ));
         this.flagForSave();
      }
   }

   public void setPowerFaction(boolean bool) {
      this.powerFaction = bool;
      if (bool) {
         TeamHandler.addPowerFaction(this);
      } else {
         TeamHandler.removePowerFaction(this);
      }

      if (!this.loading) {
         TeamActionTracker.logActionAsync(this, TeamActionType.POWER_FAC_STATUS_CHANGED, ImmutableMap.of("powerFaction", bool));
         this.flagForSave();
      }
   }

   public void setLives(int lives) {
      this.lives = lives;
      this.flagForSave();
   }

   public void addLives(int lives) {
      if (lives >= 0) {
         this.lives += lives;
         this.flagForSave();
      }
   }

   public void removeLives(int lives) {
      if (this.lives >= lives && lives >= 0) {
         this.lives -= lives;
         this.flagForSave();
      }
   }

   public void disband() {
      try {
         if (this.owner != null) {
            double refund = this.balance;

            for (Claim claim : this.claims) {
               refund += Claim.getPrice(claim, this, false);
            }

            Foxtrot.getInstance().getEconomyHandler().deposit(this.owner, refund);
            Foxtrot.getInstance().getWrappedBalanceMap().setBalance(this.owner, Foxtrot.getInstance().getEconomyHandler().getBalance(this.owner));
            Foxtrot.getInstance()
               .getLogger()
               .info("Economy Logger: Depositing " + refund + " into " + UUIDUtils.name(this.owner) + "'s account: Disbanded team");
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      for (ObjectId allyId : this.getAllies()) {
         Team ally = Foxtrot.getInstance().getTeamHandler().getTeam(allyId);
         if (ally != null) {
            ally.getAllies().remove(this.getUniqueId());
         }
      }

      for (UUID uuid : this.members) {
         Foxtrot.getInstance().getChatModeMap().setChatMode(uuid, ChatMode.PUBLIC);
      }

      Foxtrot.getInstance().getTeamHandler().getTeams().stream().filter(it -> it.getFocusedTeam() == this).forEach(it -> it.setFocusedTeam(null));
      Foxtrot.getInstance().getTeamHandler().removeTeam(this);
      LandBoard.getInstance().clear(this);
      (new BukkitRunnable() {
         @Override
         public void run() {
            Proton.getInstance().runRedisCommand(redis -> {
               redis.del("fox_teams." + Team.this.name.toLowerCase());
               return null;
            });
            DBCollection teamsCollection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Teams");
            teamsCollection.remove(Team.this.getJSONIdentifier());
         }
      }).runTaskAsynchronously(Foxtrot.getInstance());
      this.needsSave = false;
   }

   public List<Outpost> findControlledOutposts() {
      return Foxtrot.getInstance().getOutpostHandler().findControllingOutposts(this);
   }

   public boolean hasEndOutpost() {
      return this.findControlledOutposts().stream().anyMatch(it -> it instanceof EndOutpost);
   }

   public boolean hasNetherOutpost() {
      return this.findControlledOutposts().stream().anyMatch(it -> it instanceof NetherOutpost);
   }

   public boolean hasRoadOutpost() {
      return this.findControlledOutposts().stream().anyMatch(it -> it instanceof RoadOutpost);
   }

   public boolean hasKitsOutpost() {
      return this.findControlledOutposts().stream().anyMatch(it -> it instanceof KitmapOutpost);
   }

   public void rename(String newName) {
      String oldName = this.name;
      Foxtrot.getInstance().getTeamHandler().removeTeam(this);
      this.name = newName;
      Foxtrot.getInstance().getTeamHandler().setupTeam(this);
      Proton.getInstance().runRedisCommand(redis -> {
         redis.del("fox_teams." + oldName.toLowerCase());
         return null;
      });

      for (Claim claim : this.getClaims()) {
         claim.setName(claim.getName().replaceAll(oldName, newName));
      }

      this.getOnlineMembers().forEach(LunarClientListener::updateNametag);
      this.flagForSave();
   }

   public void setForceInvites(int forceInvites) {
      this.forceInvites = forceInvites;
      this.flagForSave();
   }

   public void setKills(int kills) {
      this.kills = kills;
      this.recalculatePoints();
      this.recalculateGems();
      this.flagForSave();
   }

   public void setDeaths(int deaths) {
      this.deaths = deaths;
      this.recalculatePoints();
      this.recalculateGems();
      this.flagForSave();
   }

   public void setKothCaptures(int kothCaptures) {
      this.kothCaptures = kothCaptures;
      this.recalculateGems();
      this.recalculatePoints();
      this.flagForSave();
   }

   public void setMiniKothCaptures(int miniKothCaptures) {
      this.miniKothCaptures = miniKothCaptures;
      this.recalculateGems();
      this.recalculatePoints();
      this.flagForSave();
   }

   public void addPlaytimeGems(int playtimePoints) {
      this.playtimeGems = playtimePoints;
      this.recalculateGems();
      this.flagForSave();
   }

   public void setRemovedGems(int removedGems) {
      this.removedGems = removedGems;
      this.recalculateGems();
      this.flagForSave();
   }

   public void setDiamondsMined(int diamondsMined) {
      this.diamondsMined = diamondsMined;
      this.flagForSave();
   }

   public void setCitadelsCapped(int citadels) {
      this.citadelsCapped = citadels;
      this.recalculateGems();
      this.recalculatePoints();
      this.flagForSave();
   }

   public void setClaimLocked(boolean claimLocked) {
      this.claimLocked = claimLocked;
      this.flagForSave();
   }

   public void setEotwCapped(boolean eotwCapped) {
      this.eotwCapped = eotwCapped;
      this.flagForSave();
   }

   public void setConquestsCapped(int conquestsCapped) {
      this.conquestsCapped = conquestsCapped;
      this.recalculateGems();
      this.recalculatePoints();
      this.flagForSave();
   }

   public void setFurysCaptured(int furysCaptured) {
      this.furysCaptured = furysCaptured;
      this.recalculateGems();
      this.recalculatePoints();
      this.flagForSave();
   }

   public void setAddedPoints(int addedPoints) {
      this.addedPoints = addedPoints;
      this.recalculateGems();
      this.recalculatePoints();
      this.flagForSave();
   }

   public void setPoints(int points) {
      this.points = points;
      this.recalculateGems();
      this.recalculatePoints();
      this.flagForSave();
   }

   public void setRemovedPoints(int removedPoints) {
      this.removedPoints = removedPoints;
      this.recalculateGems();
      this.recalculatePoints();
      this.flagForSave();
   }

   public void setDoublePoints(int doublePoints) {
      this.doublePoints = doublePoints;
      this.recalculateGems();
      this.recalculatePoints();
      this.flagForSave();
   }

   public void addSpawnersInClaim(int amount) {
      this.spawnersInClaim += amount;
      if (this.spawnersInClaim < 0) {
         this.spawnersInClaim = 0;
      }

      this.recalculatePoints();
      this.flagForSave();
   }

   public void removeSpawnersInClaim(int amount) {
      this.spawnersInClaim -= amount;
      if (this.spawnersInClaim < 0) {
         this.spawnersInClaim = 0;
      }

      this.recalculatePoints();
      this.flagForSave();
   }

   public void setSpawnersInClaim(int amount) {
      if (amount < 0) {
         amount = 0;
      }

      this.spawnersInClaim = amount;
      this.recalculatePoints();
      this.flagForSave();
   }

   public void recalculateSpawnersInClaims() {
   }

   public List<CreatureSpawner> findSpawners() {
      return new ArrayList<>();
   }

   public void flagForSave() {
      this.needsSave = true;
   }

   public boolean isOwner(UUID check) {
      return check.equals(this.owner);
   }

   public boolean isMember(UUID check) {
      return this.members.contains(check);
   }

   public boolean isCaptain(UUID check) {
      return this.captains.contains(check);
   }

   public boolean isCoLeader(UUID check) {
      return this.coleaders.contains(check);
   }

   public boolean isAlly(UUID check) {
      Team checkTeam = Foxtrot.getInstance().getTeamHandler().getTeam(check);
      return checkTeam != null && this.isAlly(checkTeam);
   }

   public boolean isAlly(Team team) {
      return this.getAllies().contains(team.getUniqueId());
   }

   public boolean ownsLocation(Location location) {
      return LandBoard.getInstance().getTeam(location) == this;
   }

   public boolean ownsClaim(Claim claim) {
      return this.claims.contains(claim);
   }

   public boolean removeMember(UUID member) {
      this.members.remove(member);
      this.captains.remove(member);
      this.coleaders.remove(member);
      if (this.isOwner(member)) {
         Iterator<UUID> membersIterator = this.members.iterator();
         this.owner = membersIterator.hasNext() ? membersIterator.next() : null;
      }

      try {
         for (Subclaim subclaim : this.subclaims) {
            if (subclaim.isMember(member)) {
               subclaim.removeMember(member);
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      if (this.DTR > this.getMaxDTR()) {
         this.DTR = this.getMaxDTR();
      }

      if (this.loading) {
         return false;
      }

      TeamActionTracker.logActionAsync(this, TeamActionType.MEMBER_REMOVED, ImmutableMap.of("playerId", member));
      this.getOnlineMembers().forEach(LunarClientListener::updateNametag);
      this.flagForSave();
      return this.owner == null || this.members.size() == 0;
   }

   public boolean hasDTRBitmask(DTRBitmask bitmaskType) {
      if (this.getOwner() != null) {
         return false;
      }

      int dtrInt = (int)this.DTR;
      return (dtrInt & bitmaskType.getBitmask()) == bitmaskType.getBitmask();
   }

   public int getOnlineMemberAmount() {
      int amt = 0;

      for (UUID member : this.getMembers()) {
         Player exactPlayer = Foxtrot.getInstance().getServer().getPlayer(member);
         if (exactPlayer != null && !ModHandler.INSTANCE.isInVanish(member)) {
            amt++;
         }
      }

      return amt;
   }

   public Collection<Player> getOnlineMembers() {
      ArrayList<Player> players = new ArrayList<>();

      for (UUID member : this.getMembers()) {
         Player exactPlayer = Foxtrot.getInstance().getServer().getPlayer(member);
         if (exactPlayer != null && !ModHandler.INSTANCE.isInVanish(member)) {
            players.add(exactPlayer);
         }
      }

      return players;
   }

   public Collection<UUID> getOfflineMembers() {
      ArrayList<UUID> players = new ArrayList<>();

      for (UUID member : this.getMembers()) {
         Player exactPlayer = Foxtrot.getInstance().getServer().getPlayer(member);
         if (exactPlayer == null || ModHandler.INSTANCE.isInVanish(member)) {
            players.add(member);
         }
      }

      return players;
   }

   public Subclaim getSubclaim(String name) {
      for (Subclaim subclaim : this.subclaims) {
         if (subclaim.getName().equalsIgnoreCase(name)) {
            return subclaim;
         }
      }

      return null;
   }

   public Subclaim getSubclaim(Location location) {
      for (Subclaim subclaim : this.subclaims) {
         if (new CuboidRegion(subclaim.getName(), subclaim.getLoc1(), subclaim.getLoc2()).contains(location)) {
            return subclaim;
         }
      }

      return null;
   }

   public int getSize() {
      return this.getMembers().size();
   }

   public boolean isRaidable() {
      return this.DTR <= 0.0;
   }

   public void playerDeath(String playerName, double dtrLoss, Player killer, boolean evader) {
      if (killer == null || !Foxtrot.getInstance().getDeathbanArenaHandler().isDeathbanArena(killer)) {
         double newDTR = Math.max(this.DTR - dtrLoss, -0.99);
         TeamActionTracker.logActionAsync(
            this, TeamActionType.MEMBER_DEATH, ImmutableMap.of("playerName", playerName, "dtrLoss", dtrLoss, "oldDtr", this.DTR, "newDtr", newDTR)
         );
         if (!this.isRaidable() && newDTR <= 0.0) {
            TeamRaidableEvent teamRaidableEvent = new TeamRaidableEvent(
               Foxtrot.getInstance().getServer().getPlayer(playerName), killer, this, this.DTR, newDTR, UUIDUtils.uuid(playerName)
            );
            Foxtrot.getInstance().getServer().getPluginManager().callEvent(teamRaidableEvent);
         }

         Foxtrot.getInstance().getLogger().info("[TeamDeath] " + this.name + " > Player death: [" + playerName + "]");
         this.setDTR(newDTR);
         if (!this.getPurchasedUpgrades().contains(UpgradeType.REDUCED_DTR_REGEN) && !evader) {
            if (killer != null && killer.getWorld().getEnvironment() == Environment.NORMAL) {
               this.DTRCooldown = System.currentTimeMillis() + Foxtrot.getInstance().getMapHandler().getRegenTimeDeath();
            } else {
               TeamActionTracker.logActionAsync(this, TeamActionType.TEAM_NOW_RAIDABLE, ImmutableMap.of());
               this.DTRCooldown = System.currentTimeMillis() + Foxtrot.getInstance().getMapHandler().getRegenTimeRaidable();
            }
         } else {
            this.DTRCooldown = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(25L);
         }

         DTRHandler.wasOnCooldown.add(this.uniqueId);
      }
   }

   public double getDTRIncrement() {
      return this.getDTRIncrement(this.getOnlineMemberAmount());
   }

   public double getDTRIncrement(int var1) {
      return var1 <= 0 ? 0.0 : this.getMaxDTR() / 90.0;
   }

   public double getMaxDTR() {
      return DTRHandler.getMaxDTR(this.getSize());
   }

   public void load(BasicDBObject obj) {
      this.loading = true;
      this.setUniqueId(obj.getObjectId("_id"));
      this.setOwner(obj.getString("Owner") == null ? null : UUID.fromString(obj.getString("Owner")));
      if (obj.containsField("CoLeaders")) {
         for (Object coLeader : (BasicDBList)obj.get("CoLeaders")) {
            this.addCoLeader(UUID.fromString((String)coLeader));
         }
      }

      if (obj.containsField("Captains")) {
         for (Object captain : (BasicDBList)obj.get("Captains")) {
            this.addCaptain(UUID.fromString((String)captain));
         }
      }

      if (obj.containsField("Members")) {
         for (Object member : (BasicDBList)obj.get("Members")) {
            this.addMember(UUID.fromString((String)member));
         }
      }

      if (obj.containsField("Invitations")) {
         for (Object invite : (BasicDBList)obj.get("Invitations")) {
            this.getInvitations().add(UUID.fromString((String)invite));
         }
      }

      if (obj.containsField("DTR")) {
         this.setDTR(obj.getDouble("DTR"));
      }

      if (obj.containsField("DTRCooldown")) {
         this.setDTRCooldown(obj.getDate("DTRCooldown").getTime());
      }

      if (obj.containsField("Balance")) {
         this.setBalance(obj.getDouble("Balance"));
      }

      if (obj.containsField("MaxOnline")) {
         this.setMaxOnline(obj.getInt("MaxOnline"));
      }

      if (obj.containsField("HQ")) {
         this.setHQ(LocationSerializer.deserialize((BasicDBObject)obj.get("HQ")));
      }

      if (obj.containsField("Announcement")) {
         this.setAnnouncement(obj.getString("Announcement"));
      }

      if (obj.containsField("PowerFaction")) {
         this.setPowerFaction(obj.getBoolean("PowerFaction"));
      }

      if (obj.containsField("Lives")) {
         this.setLives(obj.getInt("Lives"));
      }

      if (obj.containsField("Claims")) {
         for (Object claim : (BasicDBList)obj.get("Claims")) {
            this.getClaims().add(Claim.fromJson((BasicDBObject)claim));
         }
      }

      if (obj.containsField("Subclaims")) {
         for (Object subclaim : (BasicDBList)obj.get("Subclaims")) {
            this.getSubclaims().add(Subclaim.fromJson((BasicDBObject)subclaim));
         }
      }

      if (obj.containsKey("SpawnersInClaim")) {
         this.setSpawnersInClaim(obj.getInt("SpawnersInClaim"));
      }

      this.loading = false;
   }

   public void load(String str) {
      this.load(str, false);
   }

   public void load(String str, boolean forceSave) {
      this.loading = true;

      for (String line : str.split("\n")) {
         if (line.indexOf(58) == -1) {
            System.out.println("Found an invalid line... `" + line + "`");
         } else {
            String identifier = line.substring(0, line.indexOf(58));
            String[] lineParts = line.substring(line.indexOf(58) + 1).split(",");
            if (identifier.equalsIgnoreCase("Owner")) {
               if (!lineParts[0].equals("null")) {
                  this.setOwner(UUID.fromString(lineParts[0].trim()));
               }
            } else if (identifier.equalsIgnoreCase("UUID")) {
               this.uniqueId = new ObjectId(lineParts[0].trim());
            } else if (identifier.equalsIgnoreCase("Members")) {
               for (String name2 : lineParts) {
                  if (name2.length() >= 2) {
                     this.addMember(UUID.fromString(name2.trim()));
                  }
               }
            } else if (identifier.equalsIgnoreCase("CoLeaders")) {
               for (String name2 : lineParts) {
                  if (name2.length() >= 2) {
                     this.addCoLeader(UUID.fromString(name2.trim()));
                  }
               }
            } else if (identifier.equalsIgnoreCase("Captains")) {
               for (String name2 : lineParts) {
                  if (name2.length() >= 2) {
                     this.addCaptain(UUID.fromString(name2.trim()));
                  }
               }
            } else if (identifier.equalsIgnoreCase("Invited")) {
               for (String name2 : lineParts) {
                  if (name2.length() >= 2) {
                     this.getInvitations().add(UUID.fromString(name2.trim()));
                  }
               }
            } else if (identifier.equalsIgnoreCase("HistoricalMembers")) {
               for (String name2 : lineParts) {
                  if (name2.length() >= 2) {
                     this.getHistoricalMembers().add(UUID.fromString(name2.trim()));
                  }
               }
            } else if (identifier.equalsIgnoreCase("PotionEffects")) {
               for (String name2 : lineParts) {
                  if (name2.length() >= 2) {
                     this.purchasedEffects.add(PotionEffectType.getByName(name2.trim()));
                  }
               }
            } else if (identifier.equalsIgnoreCase("Upgrades")) {
               for (String name2 : lineParts) {
                  if (name2.length() >= 2) {
                     this.purchasedUpgrades.add(UpgradeType.valueOf(name2.trim()));
                  }
               }
            } else if (identifier.equalsIgnoreCase("HQ")) {
               this.setHQ(this.parseLocation(lineParts));
            } else if (identifier.equalsIgnoreCase("DTR")) {
               this.setDTR(Double.parseDouble(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("Balance")) {
               this.setBalance(Double.parseDouble(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("MaxOnline")) {
               this.setMaxOnline(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("ForceInvites")) {
               this.setForceInvites(Integer.parseInt(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("DTRCooldown")) {
               this.setDTRCooldown(Long.parseLong(lineParts[0]));
            } else if (identifier.equalsIgnoreCase("FriendlyName")) {
               this.setName(lineParts[0]);
            } else if (identifier.equalsIgnoreCase("Claims")) {
               for (String claim : lineParts) {
                  if ((claim = claim.replace("[", "").replace("]", "")).contains(":")) {
                     String[] split = claim.split(":");
                     int x1 = Integer.parseInt(split[0].trim());
                     int y1 = Integer.parseInt(split[1].trim());
                     int z1 = Integer.parseInt(split[2].trim());
                     int x2 = Integer.parseInt(split[3].trim());
                     int y2 = Integer.parseInt(split[4].trim());
                     int z2 = Integer.parseInt(split[5].trim());
                     String name = split[6].trim();
                     String world = split[7].trim();
                     Claim claimObj = new Claim(world, x1, y1, z1, x2, y2, z2);
                     claimObj.setName(name);
                     this.getClaims().add(claimObj);
                  }
               }
            } else if (identifier.equalsIgnoreCase("Allies")) {
               if (Foxtrot.getInstance().getMapHandler().getAllyLimit() != 0) {
                  for (String ally : lineParts) {
                     if ((ally = ally.replace("[", "").replace("]", "")).length() != 0) {
                        this.allies.add(new ObjectId(ally.trim()));
                     }
                  }
               }
            } else if (identifier.equalsIgnoreCase("RequestedAllies")) {
               if (Foxtrot.getInstance().getMapHandler().getAllyLimit() != 0) {
                  for (String requestedAlly : lineParts) {
                     if ((requestedAlly = requestedAlly.replace("[", "").replace("]", "")).length() != 0) {
                        this.requestedAllies.add(new ObjectId(requestedAlly.trim()));
                     }
                  }
               }
            } else if (!identifier.equalsIgnoreCase("Subclaims")) {
               if (identifier.equalsIgnoreCase("Announcement")) {
                  this.setAnnouncement(lineParts[0]);
               } else if (identifier.equalsIgnoreCase("PowerFaction")) {
                  this.setPowerFaction(Boolean.parseBoolean(lineParts[0]));
               } else if (identifier.equalsIgnoreCase("Lives")) {
                  this.setLives(Integer.parseInt(lineParts[0]));
               } else if (identifier.equalsIgnoreCase("Kills")) {
                  this.setKills(Integer.parseInt(lineParts[0]));
               } else if (identifier.equalsIgnoreCase("Deaths")) {
                  this.setDeaths(Integer.parseInt(lineParts[0]));
               } else if (identifier.equalsIgnoreCase("KothCaptures")) {
                  this.setKothCaptures(Integer.parseInt(lineParts[0]));
               } else if (identifier.equalsIgnoreCase("MiniKothCaptures")) {
                  this.setMiniKothCaptures(Integer.parseInt(lineParts[0]));
               } else if (identifier.equalsIgnoreCase("TrapDoors")) {
                  this.setTrapDoors(Integer.parseInt(lineParts[0]));
               } else if (identifier.equalsIgnoreCase("DiamondsMined")) {
                  this.setDiamondsMined(Integer.parseInt(lineParts[0]));
               } else if (identifier.equalsIgnoreCase("CitadelsCapped")) {
                  this.setCitadelsCapped(Integer.parseInt(lineParts[0]));
               } else if (identifier.equalsIgnoreCase("SpawnersInClaim")) {
                  this.setSpawnersInClaim(Integer.parseInt(lineParts[0]));
               } else if (identifier.equalsIgnoreCase("ClaimLocked")) {
                  this.setClaimLocked(Boolean.parseBoolean(lineParts[0]));
               } else if (identifier.equalsIgnoreCase("ConquestsCapped")) {
                  this.setConquestsCapped(Integer.parseInt(lineParts[0]));
               } else if (identifier.equalsIgnoreCase("Points")) {
                  this.setPoints(Integer.parseInt(lineParts[0]));
               } else if (identifier.equalsIgnoreCase("AddedPoints")) {
                  this.setAddedPoints(Integer.parseInt(lineParts[0]));
               } else if (identifier.equalsIgnoreCase("TrappingPoints")) {
                  this.setTrappingPoints(Integer.parseInt(lineParts[0]));
               } else if (identifier.equalsIgnoreCase("FactionsMadeRaidable")) {
                  this.setFactionsMadeRaidable(Integer.parseInt(lineParts[0]));
               } else if (identifier.equalsIgnoreCase("RemovedPoints")) {
                  this.setRemovedPoints(Integer.parseInt(lineParts[0]));
               } else if (identifier.equalsIgnoreCase("DoublePoints")) {
                  this.setDoublePoints(Integer.parseInt(lineParts[0]));
               } else if (identifier.equalsIgnoreCase("Gems")) {
                  this.setGems(Integer.parseInt(lineParts[0]));
               } else if (identifier.equalsIgnoreCase("DoubleKillPoints")) {
                  this.doubleKillPoints = Integer.parseInt(lineParts[0]);
               } else if (identifier.equalsIgnoreCase("RemovedGems")) {
                  this.setRemovedGems(Integer.parseInt(lineParts[0]));
               } else if (identifier.equalsIgnoreCase("PlaytimeGems")) {
                  this.setPlaytimeGems(Integer.parseInt(lineParts[0]));
               } else if (identifier.equalsIgnoreCase("AddedGems")) {
                  this.setAddedGems(Integer.parseInt(lineParts[0]));
               } else if (identifier.equalsIgnoreCase("FurysCapped")) {
                  this.setFurysCaptured(Integer.parseInt(lineParts[0]));
               }
            } else {
               for (String subclaim : lineParts) {
                  if ((subclaim = subclaim.replace("[", "").replace("]", "")).contains(":")) {
                     String[] split = subclaim.split(":");
                     int x1 = Integer.parseInt(split[0].trim());
                     int y1 = Integer.parseInt(split[1].trim());
                     int z1 = Integer.parseInt(split[2].trim());
                     int x2 = Integer.parseInt(split[3].trim());
                     int y2 = Integer.parseInt(split[4].trim());
                     int z2 = Integer.parseInt(split[5].trim());
                     String name = split[6].trim();
                     String membersRaw = "";
                     if (split.length >= 8) {
                        membersRaw = split[7].trim();
                     }

                     Location location1 = new Location(Bukkit.getWorlds().get(0), x1, y1, z1);
                     Location location2 = new Location(Bukkit.getWorlds().get(0), x2, y2, z2);
                     ArrayList<UUID> members = new ArrayList<>();

                     for (String uuidString : membersRaw.split(", ")) {
                        if (!uuidString.isEmpty()) {
                           members.add(UUID.fromString(uuidString.trim()));
                        }
                     }

                     Subclaim subclaimObj = new Subclaim(location1, location2, name);
                     subclaimObj.setMembers(members);
                     this.getSubclaims().add(subclaimObj);
                  }
               }
            }
         }
      }

      if (this.uniqueId == null) {
         this.uniqueId = new ObjectId();
         Foxtrot.getInstance().getLogger().info("Generating UUID for team " + this.getName() + "...");
      }

      this.loading = false;
      this.needsSave = forceSave;
   }

   public String saveString(boolean toJedis) {
      if (toJedis) {
         this.needsSave = false;
      }

      if (this.loading) {
         return null;
      }

      StringBuilder teamString = new StringBuilder();
      StringBuilder members = new StringBuilder();
      StringBuilder captains = new StringBuilder();
      StringBuilder coleaders = new StringBuilder();
      StringBuilder invites = new StringBuilder();
      StringBuilder rosters = new StringBuilder();
      StringBuilder historicalMembers = new StringBuilder();
      StringBuilder potionEffects = new StringBuilder();
      StringBuilder upgrade = new StringBuilder();

      for (UUID member : this.getMembers()) {
         members.append(member.toString()).append(", ");
      }

      for (UUID captain : this.getCaptains()) {
         captains.append(captain.toString()).append(", ");
      }

      for (UUID co : this.getColeaders()) {
         coleaders.append(co.toString()).append(", ");
      }

      for (PotionEffectType potionEffectType : this.purchasedEffects) {
         potionEffects.append(potionEffectType.getName()).append(", ");
      }

      for (UpgradeType upgradeType : this.purchasedUpgrades) {
         upgrade.append(upgradeType.name()).append(", ");
      }

      for (UUID invite : this.getInvitations()) {
         invites.append(invite.toString()).append(", ");
      }

      for (UUID member : this.getHistoricalMembers()) {
         historicalMembers.append(member.toString()).append(", ");
      }

      if (members.length() > 2) {
         members.setLength(members.length() - 2);
      }

      if (captains.length() > 2) {
         captains.setLength(captains.length() - 2);
      }

      if (invites.length() > 2) {
         invites.setLength(invites.length() - 2);
      }

      if (historicalMembers.length() > 2) {
         historicalMembers.setLength(historicalMembers.length() - 2);
      }

      teamString.append("UUID:").append(this.getUniqueId().toString()).append("\n");
      teamString.append("Owner:").append(this.getOwner()).append('\n');
      teamString.append("CoLeaders:").append(coleaders).append('\n');
      teamString.append("Captains:").append(captains).append('\n');
      teamString.append("Upgrades:").append(upgrade).append('\n');
      teamString.append("Members:").append(members).append('\n');
      teamString.append("PotionEffects:").append(potionEffects).append('\n');
      teamString.append("Invited:").append(invites.toString().replace("\n", "")).append('\n');
      teamString.append("Rosters:").append(rosters.toString().replace("\n", "")).append('\n');
      teamString.append("Subclaims:").append(this.getSubclaims().toString().replace("\n", "")).append('\n');
      teamString.append("Claims:").append(this.getClaims().toString().replace("\n", "")).append('\n');
      teamString.append("Allies:").append(this.getAllies().toString()).append('\n');
      teamString.append("RequestedAllies:").append(this.getRequestedAllies().toString()).append('\n');
      teamString.append("HistoricalMembers:").append(historicalMembers).append('\n');
      teamString.append("DTR:").append(this.getDTR()).append('\n');
      teamString.append("Balance:").append(this.getBalance()).append('\n');
      teamString.append("TrapDoors:").append(this.getTrapDoors()).append('\n');
      teamString.append("MaxOnline:").append(this.getMaxOnline()).append('\n');
      teamString.append("ForceInvites:").append(this.getForceInvites()).append('\n');
      teamString.append("DTRCooldown:").append(this.getDTRCooldown()).append('\n');
      teamString.append("FriendlyName:").append(this.getName().replace("\n", "")).append('\n');
      teamString.append("Announcement:").append(String.valueOf(this.getAnnouncement()).replace("\n", "")).append("\n");
      teamString.append("PowerFaction:").append(this.isPowerFaction()).append("\n");
      teamString.append("Lives:").append(this.getLives()).append("\n");
      teamString.append("Kills:").append(this.getKills()).append("\n");
      teamString.append("Deaths:").append(this.getDeaths()).append("\n");
      teamString.append("DiamondsMined:").append(this.getDiamondsMined()).append("\n");
      teamString.append("KothCaptures:").append(this.getKothCaptures()).append("\n");
      teamString.append("MiniKothCaptures:").append(this.getMiniKothCaptures()).append("\n");
      teamString.append("CitadelsCapped:").append(this.getCitadelsCapped()).append("\n");
      teamString.append("ConquestsCapped:").append(this.getConquestsCapped()).append("\n");
      teamString.append("SpawnersInClaim:").append(this.getSpawnersInClaim()).append("\n");
      teamString.append("DoublePoints:").append(this.getDoublePoints()).append("\n");
      teamString.append("AddedPoints:").append(this.getAddedPoints()).append("\n");
      teamString.append("TrappingPoints:").append(this.getTrappingPoints()).append("\n");
      teamString.append("Gems:").append(this.getGems()).append("\n");
      teamString.append("AddedGems:").append(this.getAddedGems()).append("\n");
      teamString.append("RemovedGems:").append(this.getRemovedGems()).append("\n");
      teamString.append("PlaytimeGems:").append(this.getPlaytimeGems()).append("\n");
      teamString.append("RemovedPoints:").append(this.getRemovedPoints()).append("\n");
      teamString.append("FactionsMadeRaidable:").append(this.getFactionsMadeRaidable()).append("\n");
      teamString.append("ClaimLocked:").append(this.isClaimLocked()).append("\n");
      teamString.append("EotwCapped:").append(this.isEotwCapped()).append("\n");
      teamString.append("FurysCapped:").append(this.getFurysCaptured()).append("\n");
      teamString.append("DoubleKillPoints:").append(this.getDoubleKillPoints()).append("\n");
      if (this.getHQ() != null) {
         teamString.append("HQ:")
            .append(this.getHQ().getWorld().getName())
            .append(",")
            .append(this.getHQ().getX())
            .append(",")
            .append(this.getHQ().getY())
            .append(",")
            .append(this.getHQ().getZ())
            .append(",")
            .append(this.getHQ().getYaw())
            .append(",")
            .append(this.getHQ().getPitch())
            .append('\n');
      }

      return teamString.toString();
   }

   public BasicDBObject toJSON() {
      BasicDBObject dbObject = new BasicDBObject();
      dbObject.put("Owner", this.getOwner() == null ? null : this.getOwner().toString());
      dbObject.put("CoLeaders", UUIDUtils.uuidsToStrings(this.getColeaders()));
      dbObject.put("Captains", UUIDUtils.uuidsToStrings(this.getCaptains()));
      dbObject.put("Members", UUIDUtils.uuidsToStrings(this.getMembers()));
      dbObject.put("Invitations", UUIDUtils.uuidsToStrings(this.getInvitations()));
      dbObject.put("Allies", this.getAllies());
      dbObject.put("RequestedAllies", this.getRequestedAllies());
      dbObject.put("DTR", this.getDTR());
      dbObject.put("DTRCooldown", new Date(this.getDTRCooldown()));
      dbObject.put("Balance", this.getBalance());
      dbObject.put("MaxOnline", this.getMaxOnline());
      dbObject.put("Name", this.getName());
      dbObject.put("HQ", LocationSerializer.serialize(this.getHQ()));
      dbObject.put("Announcement", this.getAnnouncement());
      dbObject.put("PowerFaction", this.isPowerFaction());
      dbObject.put("Lives", this.getLives());
      dbObject.put("TrapDoors", this.getTrapDoors());
      BasicDBList claims = new BasicDBList();
      BasicDBList subclaims = new BasicDBList();

      for (Claim claim : this.getClaims()) {
         claims.add(claim.json());
      }

      for (Subclaim subclaim : this.getSubclaims()) {
         subclaims.add(subclaim.json());
      }

      dbObject.put("Claims", claims);
      dbObject.put("Subclaims", subclaims);
      dbObject.put("Kills", this.kills);
      dbObject.put("Deaths", this.deaths);
      dbObject.put("DiamondsMined", this.diamondsMined);
      dbObject.put("CitadelsCaptured", this.citadelsCapped);
      dbObject.put("ConquestsCapped", this.conquestsCapped);
      dbObject.put("KothCaptures", this.kothCaptures);
      dbObject.put("ClaimLocked", this.claimLocked);
      dbObject.put("EotwCapped", this.eotwCapped);
      dbObject.put("FactionsMadeRaidable", this.factionsMadeRaidable);
      dbObject.put("AddedPoints", this.addedPoints);
      dbObject.put("TrappingPoints", this.trappingPoints);
      dbObject.put("DoublePoints", this.doublePoints);
      dbObject.put("RemovedPoints", this.removedPoints);
      dbObject.put("Gems", this.gems);
      dbObject.put("SpawnersInClaim", this.spawnersInClaim);
      dbObject.put("FurysCapped", this.furysCaptured);
      dbObject.put("DoubleKillPoints", this.doubleKillPoints);
      return dbObject;
   }

   public BasicDBObject getJSONIdentifier() {
      return new BasicDBObject("_id", this.getUniqueId().toHexString());
   }

   private Location parseLocation(String[] args) {
      if (args.length != 6) {
         return null;
      }

      World world = Foxtrot.getInstance().getServer().getWorld(args[0]);
      double x = Double.parseDouble(args[1]);
      double y = Double.parseDouble(args[2]);
      double z = Double.parseDouble(args[3]);
      float yaw = Float.parseFloat(args[4]);
      float pitch = Float.parseFloat(args[5]);
      return new Location(world, x, y, z, yaw, pitch);
   }

   public void sendMessage(String message) {
      this.getOnlineMembers().forEach(it -> it.sendMessage(message));
   }

   public void playSound(Sound sound) {
      this.getOnlineMembers().forEach(it -> it.playSound(it.getLocation(), sound, 1.0F, 1.0F));
   }

   public void sendTitle(String title, String subtitle) {
      for (Player player : getOnlinePlayersList()) {
         if (this.isMember(player.getUniqueId())) {
            PlayerUtil.sendTitle(player, title, subtitle);
         }
      }
   }

   private static List<Player> getOnlinePlayersList() {
      try {
         Method m = Bukkit.class.getMethod("getOnlinePlayers");
         Object result = m.invoke(null);
         if (result instanceof Collection) {
            return new ArrayList<>((Collection<? extends Player>)result);
         }

         if (result instanceof Player[]) {
            return Arrays.asList((Player[])result);
         }
      } catch (Exception var2) {
      }

      return new ArrayList<>();
   }

   public int recalculatePoints() {
      int basePoints = 0;
      boolean teams = Foxtrot.getInstance().getMapHandler().isKitMap();
      basePoints += this.kills;
      basePoints -= this.deaths;
      basePoints += this.kothCaptures * (Foxtrot.getInstance().getMapHandler().isKitMap() ? 10 : 20);
      basePoints += this.miniKothCaptures * 10;
      basePoints += this.citadelsCapped * (Foxtrot.getInstance().getServerHandler().isAu() ? 125 : 200);
      basePoints += this.furysCaptured * 125;
      basePoints += this.addedPoints;
      basePoints += this.doublePoints;
      if ((basePoints = basePoints - this.removedPoints) < 0) {
         basePoints = 0;
      }

      this.points = basePoints;
      return basePoints;
   }

   public int recalculateGems() {
      int baseGems = 0;
      baseGems += this.kills;
      baseGems -= this.deaths;
      baseGems += this.addedGems;
      baseGems -= this.removedGems;
      baseGems += this.playtimeGems;
      baseGems += this.kothCaptures * 25;
      baseGems += this.miniKothCaptures * 10;
      if ((baseGems = baseGems + this.citadelsCapped * 100) < 0) {
         baseGems = 0;
      }

      this.gems = baseGems;
      return baseGems;
   }

   public String[] getPointBreakDown() {
      int basePoints = 0;
      basePoints += this.kills;
      basePoints -= this.deaths;
      basePoints += this.kothCaptures * 10;
      basePoints += this.citadelsCapped * 100;
      basePoints += this.conquestsCapped * 125;
      basePoints += this.addedPoints;
      basePoints += this.doublePoints;
      if ((basePoints = basePoints - this.removedPoints) < 0) {
         basePoints = 0;
      }

      return new String[]{
         "&a&lKill &6Points: &7(&f" + this.kills + " kills&7) &6x &a1pts/each &7= &f" + this.kills + " pts",
         "&4&lDeath &6Points: &7(&f" + this.deaths + " deaths&7) &6x &c-1pts/each &7= &f-" + this.deaths + " pts",
         "&9&lKOTH &6Captures Points: &7(&f" + this.kothCaptures + " caps&7) &6x &a10 pts/each &7= &f" + this.kothCaptures * 10 + " pts",
         "&5&lCitadel &6Captures Points: &7(&f" + this.citadelsCapped + " caps&7) &6x &a100pts/each &7= &f" + this.citadelsCapped * 100 + " pts",
         "&9&lConquest &6Captures Points: &7(&f" + this.conquestsCapped + " caps&7) &6x &a125pts/each &7= &f" + this.conquestsCapped * 125 + " pts",
         "&2&lExtra &6Added Points: &f" + this.addedPoints + " pts",
         "&d&lDouble &6Points: &f" + this.doublePoints + " pts",
         "&c&lExtra &6Deleted Points: &f-" + this.removedPoints + " pts",
         "&6Sum Points: &f" + basePoints
      };
   }

   private String getSystemTeamLocation() {
      String eventName = this.name.replaceAll("(?i)\\s*koth$", "");
      Event event = Foxtrot.getInstance().getEventHandler().getEvent(eventName);
      if (event == null && !eventName.equals(this.name)) {
         event = Foxtrot.getInstance().getEventHandler().getEvent(this.name);
      }

      if (event instanceof KOTH) {
         KOTH koth = (KOTH)event;
         BlockVector cap = koth.getCapLocation();
         return cap.getBlockX() + ", " + cap.getBlockZ() + ChatColor.GRAY + " (" + LocationUtil.realmName(koth.getWorld()) + ")";
      } else if (this.HQ != null) {
         return this.HQ.getBlockX() + ", " + this.HQ.getBlockZ() + ChatColor.GRAY + " (" + LocationUtil.realmName(this.HQ.getWorld()) + ")";
      } else if (!this.claims.isEmpty()) {
         Claim claim = this.claims.get(0);
         Location min = claim.getMinimumPoint();
         Location max = claim.getMaximumPoint();
         return (min.getBlockX() + max.getBlockX()) / 2
            + ", "
            + (min.getBlockZ() + max.getBlockZ()) / 2
            + ChatColor.GRAY
            + " ("
            + LocationUtil.realmName(claim.getWorld())
            + ")";
      } else {
         return null;
      }
   }

   public void sendTeamInfo(Player player) {
      if (this.getOwner() == null) {
         player.sendMessage(GRAY_LINE);
         player.sendMessage(this.getName(player));
         String location = this.getSystemTeamLocation();
         player.sendMessage(ChatColor.YELLOW + "Location: " + ChatColor.WHITE + (location == null ? "None" : location));

         if (this.getName().equalsIgnoreCase("Glowstone")) {
            long resetMs = Foxtrot.getInstance().getGlowHandler().getResetTime();
            int resetSeconds = (int)Math.max(0L, resetMs / 1000L);
            player.sendMessage(ChatColor.YELLOW + "Next Reset: " + ChatColor.WHITE + TimeUtils.formatIntoDetailedString(resetSeconds));
         }

         player.sendMessage(GRAY_LINE);
      } else {
         KillsMap killsMap = Foxtrot.getInstance().getKillsMap();
         DeathbanMap deathbanMap = Foxtrot.getInstance().getDeathbanMap();
         Player owner = Foxtrot.getInstance().getServer().getPlayer(this.getOwner());
         StringBuilder allies = new StringBuilder();
         FancyMessage coleadersJson = new FancyMessage("Co-Leaders: ").color(ChatColor.YELLOW);
         FancyMessage captainsJson = new FancyMessage("Captains: ").color(ChatColor.YELLOW);
         if (player.hasPermission("foxtrot.manage")) {
            captainsJson.command("/manageteam demote " + this.getName()).tooltip("§bClick to demote captains");
         }

         FancyMessage membersJson = new FancyMessage("Members: ").color(ChatColor.YELLOW);
         if (player.hasPermission("foxtrot.manage")) {
            membersJson.command("/manageteam promote " + this.getName()).tooltip("§bClick to promote members");
         }

         int onlineMembers = 0;

         for (ObjectId allyId : this.getAllies()) {
            Team ally = Foxtrot.getInstance().getTeamHandler().getTeam(allyId);
            if (ally != null) {
               allies.append(ally.getName(player))
                  .append(ChatColor.YELLOW)
                  .append("[")
                  .append(ChatColor.GREEN)
                  .append(ally.getOnlineMemberAmount())
                  .append("/")
                  .append(ally.getSize())
                  .append(ChatColor.YELLOW)
                  .append("]")
                  .append(ChatColor.GRAY)
                  .append(", ");
            }
         }

         for (Player onlineMember : this.getOnlineMembers()) {
            onlineMembers++;
            if (!this.isOwner(onlineMember.getUniqueId())) {
               FancyMessage appendTo = membersJson;
               if (this.isCoLeader(onlineMember.getUniqueId())) {
                  appendTo = coleadersJson;
               } else if (this.isCaptain(onlineMember.getUniqueId())) {
                  appendTo = captainsJson;
               }

               if (!ChatColor.stripColor(appendTo.toOldMessageFormat()).endsWith("s: ")) {
                  appendTo.then(", ").color(ChatColor.GRAY);
               }

               appendTo.then(onlineMember.getName()).color(ChatColor.GREEN).then("[").color(ChatColor.YELLOW);
               appendTo.then(killsMap.getKills(onlineMember.getUniqueId()) + "").color(ChatColor.GREEN);
               appendTo.then("]").color(ChatColor.YELLOW);
            }
         }

         for (UUID offlineMember : this.getOfflineMembers()) {
            if (!this.isOwner(offlineMember)) {
               FancyMessage appendTo = membersJson;
               if (this.isCoLeader(offlineMember)) {
                  appendTo = coleadersJson;
               } else if (this.isCaptain(offlineMember)) {
                  appendTo = captainsJson;
               }

               if (!ChatColor.stripColor(appendTo.toOldMessageFormat()).endsWith("s: ")) {
                  appendTo.then(", ").color(ChatColor.GRAY);
               }

               appendTo.then(Proton.getInstance().getUuidCache().name(offlineMember))
                  .color(deathbanMap.isDeathbanned(offlineMember) ? ChatColor.RED : ChatColor.GRAY);
               appendTo.then("[").color(ChatColor.YELLOW).then("" + killsMap.getKills(offlineMember)).color(ChatColor.GREEN);
               appendTo.then("]").color(ChatColor.YELLOW);
            }
         }

         player.sendMessage(GRAY_LINE);
         String homeStr = this.HQ == null ? "None" : this.HQ.getBlockX() + ", " + this.HQ.getBlockZ();
         String line1 = ChatColor.translateAlternateColorCodes(
            '&', "&6&l" + this.getName() + " &7[" + onlineMembers + "/" + this.getSize() + "] &eHome: &f" + homeStr
         );
         FancyMessage teamLine = new FancyMessage();
         teamLine.text(line1).command("/f focus " + this.getName()).tooltip(ChatColor.GREEN + "Click to focus this team!");
         if (player.hasPermission("neutron.staff")) {
            teamLine.then()
               .text(ChatColor.translateAlternateColorCodes('&', " &e[Manage]"))
               .command("/manageteam manage " + this.getName())
               .tooltip("§bClick to manage team");
         }

         teamLine.send(player);
         if (allies.length() > 2) {
            allies.setLength(allies.length() - 2);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eAllies: " + allies));
         }

         String leaderName = Proton.getInstance().getUuidCache().name(this.getOwner());
         boolean leaderOnline = owner != null && !ModHandler.INSTANCE.isInVanish(owner.getUniqueId());
         String leaderColor = leaderOnline ? "&a" : "&7";
         String leaderKills = String.valueOf(killsMap.getKills(this.getOwner()));
         FancyMessage leader = new FancyMessage(
            ChatColor.translateAlternateColorCodes('&', "&eLeader: " + leaderColor + leaderName + " &7[" + leaderKills + "]")
         );
         if (player.hasPermission("neutron.staff")) {
            leader.command("/manageteam leader " + this.getName()).tooltip("§bClick to change leader");
         }

         leader.send(player);
         if (onlineMembers > 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eOnline Members:"));
            if (!ChatColor.stripColor(coleadersJson.toOldMessageFormat()).endsWith("s: ")) {
               coleadersJson.send(player);
            }

            if (!ChatColor.stripColor(captainsJson.toOldMessageFormat()).endsWith("s: ")) {
               captainsJson.send(player);
            }

            if (!ChatColor.stripColor(membersJson.toOldMessageFormat()).endsWith("s: ")) {
               membersJson.send(player);
            }
         }

         StringBuilder offlineList = new StringBuilder();

         for (UUID offlineMember : this.getOfflineMembers()) {
            if (!this.isOwner(offlineMember)) {
               String name = Proton.getInstance().getUuidCache().name(offlineMember);
               String color = deathbanMap.isDeathbanned(offlineMember) ? "&c" : "&7";
               if (offlineList.length() > 0) {
                  offlineList.append("&7, ");
               }

               offlineList.append(color).append(name);
            }
         }

         if (offlineList.length() > 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eOffline Members:"));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', offlineList.toString()));
         }

         player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eBalance: &a$" + Math.round(this.getBalance())));
         int totalHearts = (int)Math.ceil(this.getMaxDTR());
         int currentHearts = (int)Math.ceil(this.getDTR());
         StringBuilder aids = new StringBuilder();

         for (int i = 0; i < totalHearts; i++) {
            if (this.isRaidable() && DTRHandler.isRegenerating(this)) {
               aids.append(ChatColor.GOLD + "❤");
            } else if (this.isRaidable()) {
               aids.append(ChatColor.DARK_RED + "❤");
            } else if (i >= currentHearts) {
               if (DTRHandler.isRegenerating(this)) {
                  aids.append(ChatColor.GOLD + "❤");
               } else {
                  aids.append(ChatColor.GRAY + "❤");
               }
            } else if (this.DTR / this.getMaxDTR() <= 0.25 && !this.isRaidable()) {
               aids.append(ChatColor.YELLOW + "❤");
            } else {
               aids.append(ChatColor.GREEN + "❤");
            }
         }

         String dtrDisplay = Foxtrot.getInstance().getDTRDisplayMap().isHearts(player.getUniqueId())
            ? this.getDTRColor() + aids.toString() + this.getDTRSuffix()
            : this.getDTRColor() + formatDtrValue(this.getDTR()) + this.getDTRSuffix();
         FancyMessage dtrMessage = new FancyMessage(ChatColor.translateAlternateColorCodes('&', "&eDTR: " + dtrDisplay));
         if (player.hasPermission("foxtrot.manage")) {
            dtrMessage.command("/manageteam dtr " + this.getName()).tooltip("§bClick to modify team DTR");
         }

         dtrMessage.send(player);
         if (!Foxtrot.getInstance().getServerHandler().isTeams()) {
            if (this.recalculateGems() > 0) {
               player.sendMessage(ChatColor.YELLOW + "Gems: " + ChatColor.RED + this.recalculateGems());
            }

            if (this.trappingPoints > 0) {
               player.sendMessage(ChatColor.YELLOW + "Trapping Points: " + ChatColor.RED + this.trappingPoints);
            }
         }

         if (this.recalculatePoints() > 0) {
            player.sendMessage(ChatColor.YELLOW + "Points: " + ChatColor.RED + this.recalculatePoints());
         }

         if (this.kothCaptures > 0) {
            player.sendMessage(ChatColor.YELLOW + "KOTH Captures: " + ChatColor.RED + this.getKothCaptures());
         }

         if (this.spawnersInClaim > 0) {
            player.sendMessage(ChatColor.YELLOW + "Spawners: " + ChatColor.RED + this.getSpawnersInClaim());
         }

         if (this.citadelsCapped > 0) {
            player.sendMessage(ChatColor.YELLOW + "Citadel Captures: " + ChatColor.RED + this.getCitadelsCapped());
         }

         if (DTRHandler.isOnCooldown(this)) {
            if (!player.isOp()) {
               player.sendMessage(
                  ChatColor.translateAlternateColorCodes(
                     '&', "&eDTR Regen: &f" + TimeUtils.formatIntoDetailedString((int)(this.getDTRCooldown() - System.currentTimeMillis()) / 1000).trim()
                  )
               );
            } else {
               FancyMessage message = new FancyMessage(ChatColor.translateAlternateColorCodes('&', "&eDTR Regen: "))
                  .tooltip(ChatColor.GREEN + "Click to remove regeneration timer")
                  .command("/startdtrregen " + this.getName());
               message.then(TimeUtils.formatIntoDetailedString((int)(this.getDTRCooldown() - System.currentTimeMillis()) / 1000))
                  .color(ChatColor.WHITE)
                  .tooltip(ChatColor.GREEN + "Click to remove regeneration timer")
                  .command("/startdtrregen " + this.getName());
               message.send(player);
            }
         }

         if (this.isMember(player.getUniqueId()) && this.announcement != null && !this.announcement.equals("null")) {
            player.sendMessage(ChatColor.YELLOW + "Announcement: " + ChatColor.LIGHT_PURPLE + this.announcement);
         }

         player.sendMessage(GRAY_LINE);
      }
   }

   public String formatDTR() {
      return this.getDTRColor().toString() + formatDtrValue(this.getDTR()) + this.getDTRSuffix();
   }

   public List<TrapDoor> findTrapDoors() {
      if (Bukkit.isPrimaryThread()) {
         throw new RuntimeException("Cannot call Team#findSpawners on main thread");
      }

      ArrayList<TrapDoor> list = new ArrayList<>();

      for (Claim claim : this.getClaims()) {
         World world = Bukkit.getWorld(claim.getWorld());
         Location minPoint = claim.getMinimumPoint();
         Location maxPoint = claim.getMaximumPoint();
         int minChunkX = (int)minPoint.getX() >> 4;
         int minChunkZ = (int)minPoint.getZ() >> 4;
         int maxChunkX = (int)maxPoint.getX() >> 4;
         int maxChunkZ = (int)maxPoint.getZ() >> 4;

         for (int chunkX = minChunkX; chunkX < maxChunkX + 1; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ < maxChunkZ + 1; chunkZ++) {
               Chunk chunk = world.getChunkAt(chunkX, chunkZ);

               for (BlockState blockState : chunk.getTileEntities()) {
                  if (blockState instanceof TrapDoor) {
                     Location loc = blockState.getLocation();
                     if (loc.getX() >= minPoint.getX() && loc.getZ() >= minPoint.getZ() && loc.getX() <= maxPoint.getX() && loc.getZ() <= maxPoint.getZ()) {
                        list.add((TrapDoor)blockState);
                     }
                  }
               }
            }
         }
      }

      return list;
   }

   @Override
   public int hashCode() {
      return this.uniqueId.hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof Team)) {
         return false;
      }

      Team other = (Team)obj;
      return other.uniqueId.equals(this.uniqueId);
   }

   public ChatColor getDTRColor() {
      ChatColor dtrColor = ChatColor.GREEN;
      if (this.DTR / this.getMaxDTR() <= 0.25) {
         dtrColor = this.isRaidable() ? ChatColor.DARK_RED : ChatColor.YELLOW;
      }

      return dtrColor;
   }

   public String getDTRSuffix() {
      if (DTRHandler.isRegenerating(this)) {
         return this.getOnlineMemberAmount() == 0 ? ChatColor.GRAY + "◀" : ChatColor.GREEN + "▲";
      } else {
         return DTRHandler.isOnCooldown(this) ? ChatColor.RED + "■" : ChatColor.GREEN + "◀";
      }
   }

   public LCWaypoint getHomeWaypoint() {
      return this.homeWaypoint;
   }

   public void setHomeWaypoint(LCWaypoint homeWaypoint) {
      this.homeWaypoint = homeWaypoint;
   }

   public LCWaypoint getFocusWaypoint() {
      return this.focusWaypoint;
   }

   public void setFocusWaypoint(LCWaypoint focusWaypoint) {
      this.focusWaypoint = focusWaypoint;
   }

   public LCWaypoint getRallyWaypoint() {
      return this.rallyWaypoint;
   }

   public void setRallyWaypoint(LCWaypoint rallyWaypoint) {
      this.rallyWaypoint = rallyWaypoint;
   }

   public boolean isNeedsSave() {
      return this.needsSave;
   }

   public boolean isLoading() {
      return this.loading;
   }

   public ObjectId getUniqueId() {
      return this.uniqueId;
   }

   public void setUniqueId(ObjectId uniqueId) {
      this.uniqueId = uniqueId;
   }

   public String getName() {
      return this.name;
   }

   public Location getHQ() {
      return this.HQ;
   }

   public ObjectId getRaidStealTeam() {
      return this.raidStealTeam;
   }

   public void setRaidStealTeam(ObjectId raidStealTeam) {
      this.raidStealTeam = raidStealTeam;
   }

   public double getBalance() {
      return this.balance;
   }

   public int getBaseTokens() {
      return this.baseTokens;
   }

   public void setBaseTokens(int baseTokens) {
      this.baseTokens = baseTokens;
   }

   public int getFallTrapTokens() {
      return this.fallTrapTokens;
   }

   public void setFallTrapTokens(int fallTrapTokens) {
      this.fallTrapTokens = fallTrapTokens;
   }

   public double getDTR() {
      return this.DTR;
   }

   public int getTrapDoors() {
      return this.trapDoors;
   }

   public void setTrapDoors(int trapDoors) {
      this.trapDoors = trapDoors;
   }

   public long getDTRCooldown() {
      return this.DTRCooldown;
   }

   public List<Claim> getClaims() {
      return this.claims;
   }

   public List<Subclaim> getSubclaims() {
      return this.subclaims;
   }

   public UUID getOwner() {
      return this.owner;
   }

   public Set<UUID> getMembers() {
      return this.members;
   }

   public Set<UUID> getCaptains() {
      return this.captains;
   }

   public Set<UUID> getColeaders() {
      return this.coleaders;
   }

   public Set<UUID> getInvitations() {
      return this.invitations;
   }

   public Map<UUID, Role> getRoster() {
      return this.roster;
   }

   public Set<ObjectId> getAllies() {
      return this.allies;
   }

   public Set<ObjectId> getRequestedAllies() {
      return this.requestedAllies;
   }

   public List<PotionEffectType> getPurchasedEffects() {
      return this.purchasedEffects;
   }

   public List<UpgradeType> getPurchasedUpgrades() {
      return this.purchasedUpgrades;
   }

   public String getAnnouncement() {
      return this.announcement;
   }

   public int getMaxOnline() {
      return this.maxOnline;
   }

   public boolean isPowerFaction() {
      return this.powerFaction;
   }

   public int getLives() {
      return this.lives;
   }

   public int getKills() {
      return this.kills;
   }

   public int getAddedGems() {
      return this.addedGems;
   }

   public void setAddedGems(int addedGems) {
      this.addedGems = addedGems;
   }

   public int getRemovedGems() {
      return this.removedGems;
   }

   public int getKothCaptures() {
      return this.kothCaptures;
   }

   public int getMiniKothCaptures() {
      return this.miniKothCaptures;
   }

   public int getFurysCaptured() {
      return this.furysCaptured;
   }

   public int getDoublePoints() {
      return this.doublePoints;
   }

   public int getDoubleKillPoints() {
      return this.doubleKillPoints;
   }

   public int getAddedPoints() {
      return this.addedPoints;
   }

   public int getTrappingPoints() {
      return this.trappingPoints;
   }

   public void setTrappingPoints(int trappingPoints) {
      this.trappingPoints = trappingPoints;
   }

   public int getPlaytimeGems() {
      return this.playtimeGems;
   }

   public void setPlaytimeGems(int playtimeGems) {
      this.playtimeGems = playtimeGems;
   }

   public int getPoints() {
      return this.points;
   }

   public int getGems() {
      return this.gems;
   }

   public void setGems(int gems) {
      this.gems = gems;
   }

   public int getRemovedPoints() {
      return this.removedPoints;
   }

   public int getFactionsMadeRaidable() {
      return this.factionsMadeRaidable;
   }

   public void setFactionsMadeRaidable(int factionsMadeRaidable) {
      this.factionsMadeRaidable = factionsMadeRaidable;
   }

   public int getCitadelsCapped() {
      return this.citadelsCapped;
   }

   public int getConquestsCapped() {
      return this.conquestsCapped;
   }

   public int getSpawnersInClaim() {
      return this.spawnersInClaim;
   }

   public int getDiamondsMined() {
      return this.diamondsMined;
   }

   public int getDeaths() {
      return this.deaths;
   }

   public boolean isClaimLocked() {
      return this.claimLocked;
   }

   public boolean isEotwCapped() {
      return this.eotwCapped;
   }

   public Location getRallyPoint() {
      return this.rallyPoint;
   }

   public void setRallyPoint(Location rallyPoint) {
      this.rallyPoint = rallyPoint;
   }

   public int getForceInvites() {
      return this.forceInvites;
   }

   public Set<UUID> getHistoricalMembers() {
      return this.historicalMembers;
   }

   public UUID getFocused() {
      return this.focused;
   }

   public void setFocused(UUID focused) {
      this.focused = focused;
   }

   public Team getFocusedTeam() {
      return this.focusedTeam;
   }

   public void setFocusedTeam(Team focusedTeam) {
      this.focusedTeam = focusedTeam;
   }

   public long getLastRequestReport() {
      return this.lastRequestReport;
   }

   public void setLastRequestReport(long lastRequestReport) {
      this.lastRequestReport = lastRequestReport;
   }

   public Player getRallyPlayer() {
      return this.rallyPlayer;
   }

   public void setRallyPlayer(Player rallyPlayer) {
      this.rallyPlayer = rallyPlayer;
   }

   public int getBards() {
      return this.bards;
   }

   public void setBards(int bards) {
      this.bards = bards;
   }

   public int getArchers() {
      return this.archers;
   }

   public void setArchers(int archers) {
      this.archers = archers;
   }

   public int getRogues() {
      return this.rogues;
   }

   public void setRogues(int rogues) {
      this.rogues = rogues;
   }

   private static String formatDtrValue(double var0) {
      return String.format(Locale.ROOT, "%.2f", var0);
   }
}
