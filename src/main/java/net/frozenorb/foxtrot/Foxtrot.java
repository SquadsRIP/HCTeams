package net.frozenorb.foxtrot;

import cc.fyre.neutron.util.PlayerUtil;
import cc.fyre.proton.Proton;
import cc.fyre.proton.util.TimeUtils;
import com.comphenix.protocol.ProtocolLibrary;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.shampaggon.crackshot.CSUtility;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import net.buycraft.plugin.bukkit.BuycraftPlugin;
import net.frozenorb.foxtrot.antixray.AntiXrayHandler;
import net.frozenorb.foxtrot.brewer.FancyBrewerModule;
import net.frozenorb.foxtrot.chat.tips.TipsHandler;
import net.frozenorb.foxtrot.chat.trivia.TriviaHandler;
import net.frozenorb.foxtrot.cheatbreaker.CheatBreakerIntegration;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.config.ConfigurationFiles;
import net.frozenorb.foxtrot.config.SquadsCommandPolicy;
import net.frozenorb.foxtrot.deathmessage.DeathMessageHandler;
import net.frozenorb.foxtrot.economy.EconomyHandler;
import net.frozenorb.foxtrot.gameplay.ability.type.MidasTouch;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClassHandler;
import net.frozenorb.foxtrot.gameplay.boosters.NetworkBoosterHandler;
import net.frozenorb.foxtrot.gameplay.bosses.BossHandler;
import net.frozenorb.foxtrot.gameplay.cavesays.CaveSaysHandler;
import net.frozenorb.foxtrot.gameplay.clickitem.ClickItemHandler;
import net.frozenorb.foxtrot.gameplay.content.ContentHandler;
import net.frozenorb.foxtrot.gameplay.coupondrops.CouponDropsHandler;
import net.frozenorb.foxtrot.gameplay.events.EventHandler;
import net.frozenorb.foxtrot.gameplay.events.cavenite.CaveNiteHandler;
import net.frozenorb.foxtrot.gameplay.events.citadel.CitadelHandler;
import net.frozenorb.foxtrot.gameplay.events.conquest.ConquestHandler;
import net.frozenorb.foxtrot.gameplay.events.fury.FuryHandler;
import net.frozenorb.foxtrot.gameplay.events.mini.MiniEventsHandler;
import net.frozenorb.foxtrot.gameplay.events.outposts.OutpostHandler;
import net.frozenorb.foxtrot.gameplay.events.region.cavern.CavernHandler;
import net.frozenorb.foxtrot.gameplay.events.region.glowmtn.GlowHandler;
import net.frozenorb.foxtrot.gameplay.grounds.SpawnerGroundsHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.bounty.BountyManager;
import net.frozenorb.foxtrot.gameplay.kitmap.daily.DailyKitHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.game.Game;
import net.frozenorb.foxtrot.gameplay.kitmap.gem.GemHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.gemflip.GemFlipHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.partner.PartnerCrateHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.tokens.TokensHandler;
import net.frozenorb.foxtrot.gameplay.lettingIn.LettingInHandler;
import net.frozenorb.foxtrot.gameplay.loot.airdrop.AirDropHandler;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.BattlePassHandler;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.Challenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.serializer.ChallengeSerializer;
import net.frozenorb.foxtrot.gameplay.loot.crate.CrateHandler;
import net.frozenorb.foxtrot.gameplay.loot.itemboxes.ItemBoxesHandler;
import net.frozenorb.foxtrot.gameplay.loot.redeem.RedeemCreatorHandler;
import net.frozenorb.foxtrot.gameplay.loot.shop.ShopHandler;
import net.frozenorb.foxtrot.gameplay.loot.treasurechest.TreasureChestHandler;
import net.frozenorb.foxtrot.gameplay.loot.treasurecove.TreasureCoveHandler;
import net.frozenorb.foxtrot.gameplay.loot.voteparty.VotePartyHandler;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.gameplay.pvpclasses.mastery.MasteryUpgradeHandler;
import net.frozenorb.foxtrot.gameplay.quest.QuestHandler;
import net.frozenorb.foxtrot.gameplay.totem.TotemHandler;
import net.frozenorb.foxtrot.listener.CombatLoggerListener;
import net.frozenorb.foxtrot.map.MapHandler;
import net.frozenorb.foxtrot.nametag.modsuite.InventorySeeHandler;
import net.frozenorb.foxtrot.packetborder.PacketBorderThread;
import net.frozenorb.foxtrot.persist.RedisSaveTask;
import net.frozenorb.foxtrot.persist.maps.AbilityCooldownsScoreboardMap;
import net.frozenorb.foxtrot.persist.maps.AnnoyingBroadcastMap;
import net.frozenorb.foxtrot.persist.maps.ArcherTagColorMap;
import net.frozenorb.foxtrot.persist.maps.BountyCooldownMap;
import net.frozenorb.foxtrot.persist.maps.ChatModeMap;
import net.frozenorb.foxtrot.persist.maps.ChatSpyMap;
import net.frozenorb.foxtrot.persist.maps.CoalMinedMap;
import net.frozenorb.foxtrot.persist.maps.DeathbanMap;
import net.frozenorb.foxtrot.persist.maps.DeathsMap;
import net.frozenorb.foxtrot.persist.maps.DiamondMinedMap;
import net.frozenorb.foxtrot.persist.maps.EmeraldMinedMap;
import net.frozenorb.foxtrot.persist.maps.EnemyColorMap;
import net.frozenorb.foxtrot.persist.maps.FactionFilterMap;
import net.frozenorb.foxtrot.persist.maps.FirstJoinMap;
import net.frozenorb.foxtrot.persist.maps.FishingKitMap;
import net.frozenorb.foxtrot.persist.maps.FocusColorMap;
import net.frozenorb.foxtrot.persist.maps.FriendLivesMap;
import net.frozenorb.foxtrot.persist.maps.GemBoosterMap;
import net.frozenorb.foxtrot.persist.maps.GemMap;
import net.frozenorb.foxtrot.persist.maps.GoldMinedMap;
import net.frozenorb.foxtrot.persist.maps.IronMinedMap;
import net.frozenorb.foxtrot.persist.maps.KDRMap;
import net.frozenorb.foxtrot.persist.maps.KillTagMap;
import net.frozenorb.foxtrot.persist.maps.KillsMap;
import net.frozenorb.foxtrot.persist.maps.KillstreakMap;
import net.frozenorb.foxtrot.persist.maps.KitmapTokensMap;
import net.frozenorb.foxtrot.persist.maps.LapisMinedMap;
import net.frozenorb.foxtrot.persist.maps.LastJoinMap;
import net.frozenorb.foxtrot.persist.maps.OppleMap;
import net.frozenorb.foxtrot.persist.maps.PlaytimeMap;
import net.frozenorb.foxtrot.persist.maps.PvPTimerMap;
import net.frozenorb.foxtrot.persist.maps.ReclaimMap;
import net.frozenorb.foxtrot.persist.maps.RedstoneMinedMap;
import net.frozenorb.foxtrot.persist.maps.SaleTimersScoreboardMap;
import net.frozenorb.foxtrot.persist.maps.StartingPvPTimerMap;
import net.frozenorb.foxtrot.persist.maps.TabListModeMap;
import net.frozenorb.foxtrot.persist.maps.TeamColorMap;
import net.frozenorb.foxtrot.persist.maps.TeamFocusColorMap;
import net.frozenorb.foxtrot.persist.maps.TeamfightModeMap;
import net.frozenorb.foxtrot.persist.maps.ToggleClaimMessageMap;
import net.frozenorb.foxtrot.persist.maps.ToggleDeathMessageMap;
import net.frozenorb.foxtrot.persist.maps.ToggleFoundDiamondsMap;
import net.frozenorb.foxtrot.persist.maps.ToggleGlobalChatMap;
import net.frozenorb.foxtrot.persist.maps.WrappedBalanceMap;
import net.frozenorb.foxtrot.persist.maps.toggle.CobblePickupMap;
import net.frozenorb.foxtrot.persist.maps.toggle.DTRDisplayMap;
import net.frozenorb.foxtrot.persist.maps.toggle.LCTeamViewMap;
import net.frozenorb.foxtrot.persist.maps.toggle.MobDropsPickupMap;
import net.frozenorb.foxtrot.persist.maps.toggle.TipsMap;
import net.frozenorb.foxtrot.protocol.AnvilRenamePacketAdapter;
import net.frozenorb.foxtrot.scoreboard.FoxtrotScoreboardConfiguration;
import net.frozenorb.foxtrot.serialization.BlockVectorAdapter;
import net.frozenorb.foxtrot.serialization.ItemStackAdapter;
import net.frozenorb.foxtrot.serialization.LocationAdapter;
import net.frozenorb.foxtrot.serialization.PotionAdapter;
import net.frozenorb.foxtrot.serialization.PotionEffectAdapter;
import net.frozenorb.foxtrot.serialization.VectorAdapter;
import net.frozenorb.foxtrot.server.ListenerHandler;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.foxtrot.server.customTimer.CustomTimerHandler;
import net.frozenorb.foxtrot.server.deathban.DeathbanArenaHandler;
import net.frozenorb.foxtrot.server.keyalls.KeyAllHandler;
import net.frozenorb.foxtrot.server.polls.PollHandler;
import net.frozenorb.foxtrot.server.voucher.VoucherHandler;
import net.frozenorb.foxtrot.tab.FoxtrotTabLayoutProvider;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.TeamHandler;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.PersistableLocation;
import net.frozenorb.foxtrot.util.RegenUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;
import org.cavepvp.profiles.playerProfiles.impl.stats.StatisticServer;

public class Foxtrot extends JavaPlugin {
   public static String MONGO_DB_NAME = "Teams";
   public static final Gson GSON = new GsonBuilder()
      .registerTypeHierarchyAdapter(PotionEffect.class, new PotionEffectAdapter())
      .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
      .registerTypeHierarchyAdapter(Location.class, new LocationAdapter())
      .registerTypeHierarchyAdapter(Vector.class, new VectorAdapter())
      .registerTypeHierarchyAdapter(BlockVector.class, new BlockVectorAdapter())
      .registerTypeHierarchyAdapter(PotionAdapter.class, new PotionAdapter())
      .registerTypeAdapter(Challenge.class, new ChallengeSerializer())
      .setPrettyPrinting()
      .serializeNulls()
      .create();
   public static Foxtrot instance;
   private ConfigurationFiles configurationFiles;
   private boolean startupComplete;
   public MongoClient mongoPool;
   private BuycraftPlugin buycraftPlugin;
   private EconomyHandler economyHandler;
   private DailyKitHandler dailyKitHandler;
   private ContentHandler contentHandler;
   private LettingInHandler lettingInHandler;
   private OutpostHandler outpostHandler;
   private PvPClassHandler pvpClassHandler;
   private ItemBoxesHandler itemBoxesHandler;
   private ClickItemHandler clickItemHandler;
   private ArmorClassHandler armorClassHandler;
   private QuestHandler questHandler;
   private BattlePassHandler battlePassHandler;
   private WorldEditPlugin worldEdit;
   private KeyAllHandler keyAllHandler;
   private CouponDropsHandler couponDropsHandler;
   private TeamHandler teamHandler;
   public ServerHandler serverHandler;
   private MapHandler mapHandler;
   private TipsHandler tipsHandler;
   private CitadelHandler citadelHandler;
   private EventHandler eventHandler;
   private GemFlipHandler gemFlipHandler;
   private CaveSaysHandler caveSaysHandler;
   private TokensHandler tokensHandler;
   private ConquestHandler conquestHandler;
   private DeathbanArenaHandler deathbanArenaHandler;
   private PollHandler pollHandler;
   private VoucherHandler voucherHandler;
   private TreasureChestHandler treasureChestHandler;
   private MiniEventsHandler miniEventsHandler;
   private SpawnerGroundsHandler spawnerGroundsHandler;
   private MasteryUpgradeHandler masteryUpgradeHandler;
   private CavernHandler cavernHandler;
   private CaveNiteHandler caveNiteHandler;
   private GlowHandler glowHandler;
   private CrateHandler crateHandler;
   private BossHandler bossHandler;
   private NetworkBoosterHandler networkBoosterHandler;
   private RedeemCreatorHandler redeemCreatorHandler;
   private CustomTimerHandler customTimerHandler;
   private ShopHandler shopHandler;
   private TotemHandler totemHandler;
   private PlaytimeMap playtimeMap;
   private OppleMap oppleMap;
   private DeathbanMap deathbanMap;
   private PvPTimerMap PvPTimerMap;
   private SaleTimersScoreboardMap saleTimersScoreboardMap;
   private StartingPvPTimerMap startingPvPTimerMap;
   private DeathsMap deathsMap;
   private KillsMap killsMap;
   private FactionFilterMap factionFilterMap;
   private KitmapTokensMap kitmapTokensMap;
   private AirDropHandler airDropHandler;
   private GemHandler gemHandler;
   private PartnerCrateHandler partnerCrateHandler;
   private BountyManager bountyManager;
   private KillstreakMap killstreakMap;
   private KillTagMap killTagMap;
   private TeamColorMap teamColorMap;
   private EnemyColorMap enemyColorMap;
   private ArcherTagColorMap archerTagColorMap;
   private FocusColorMap focusColorMap;
   private TeamFocusColorMap teamFocusColorMap;
   private AnnoyingBroadcastMap annoyingBroadcastMap;
   private ChatModeMap chatModeMap;
   private FishingKitMap fishingKitMap;
   private ToggleGlobalChatMap toggleGlobalChatMap;
   private ChatSpyMap chatSpyMap;
   private DiamondMinedMap diamondMinedMap;
   private GoldMinedMap goldMinedMap;
   private IronMinedMap ironMinedMap;
   private CoalMinedMap coalMinedMap;
   private RedstoneMinedMap redstoneMinedMap;
   private LapisMinedMap lapisMinedMap;
   private EmeraldMinedMap emeraldMinedMap;
   private FirstJoinMap firstJoinMap;
   private LastJoinMap lastJoinMap;
   private FriendLivesMap friendLivesMap;
   private AbilityCooldownsScoreboardMap abilityCooldownsScoreboardMap;
   private WrappedBalanceMap wrappedBalanceMap;
   private TreasureCoveHandler treasureCoveHandler;
   private ToggleFoundDiamondsMap toggleFoundDiamondsMap;
   private ToggleDeathMessageMap toggleDeathMessageMap;
   private ToggleClaimMessageMap toggleClaimMessageMap;
   private TabListModeMap tabListModeMap;
   private CobblePickupMap cobblePickupMap;
   private MobDropsPickupMap mobDropsPickupMap;
   private KDRMap kdrMap;
   private BountyCooldownMap bountyCooldownMap;
   private ReclaimMap reclaimMap;
   private TriviaHandler triviaHandler;
   private FuryHandler furyHandler;
   private DTRDisplayMap DTRDisplayMap;
   private TeamfightModeMap teamfightModeMap;
   private LCTeamViewMap lcTeamViewMap;
   private TipsMap tipsMap;
   private CSUtility csUtility;
   private GemMap gemMap;
   private GemBoosterMap gemBoosterMap;
   private WorldEditPlugin worldEditPlugin;
   private CombatLoggerListener combatLoggerListener;
   private InventorySeeHandler inventorySeeHandler;
   private FancyBrewerModule fancyBrewerModule;
   private VotePartyHandler votePartyHandler;
   private Predicate<Player> inDuelPredicate = player -> this.mapHandler.isKitMap() && this.mapHandler.getDuelHandler().isInDuel(player);
   private Predicate<Player> inEventPredicate = player -> this.mapHandler.isKitMap()
      && this.mapHandler.getGameHandler().isOngoingGame()
      && this.mapHandler.getGameHandler().getOngoingGame().isPlaying(player.getUniqueId());
   private final List<Integer> sotwReminders = Arrays.asList(300, 600, 900, 1800, 3600, 7200);
   private AnvilRenamePacketAdapter anvilRenamePacketAdapter;
   private CheatBreakerIntegration cheatBreakerIntegration;
   private AntiXrayHandler antiXrayHandler;

   @Override
   public void onEnable() {
      if (Bukkit.getServerName().contains(" ")) {
         System.out.println("*********************************************");
         System.out.println("               ATTENTION");
         System.out.println("SET server-name VALUE IN server.properties TO");
         System.out.println("A PROPER SERVER NAME. THIS WILL BE USED AS THE");
         System.out.println("MONGO DATABASE NAME.");
         System.out.println("*********************************************");
         this.getServer().shutdown();
      } else {
         instance = this;
         this.saveDefaultConfig();
         File dataDirectory = new File(this.getDataFolder(), "data");
         if ((dataDirectory.exists() || dataDirectory.mkdirs()) && dataDirectory.isDirectory()) {
            this.configurationFiles = new ConfigurationFiles(this);

            try {
               this.configurationFiles.initialize();
            } catch (IOException | InvalidConfigurationException exception) {
               this.getLogger().severe("Could not load HCTeams configuration files: " + exception.getMessage());
               exception.printStackTrace();
               this.getServer().getPluginManager().disablePlugin(this);
               return;
            }

            try {
               String mongoHost = this.getConfig().getString("Mongo.Host", "127.0.0.1");
               int mongoPort = this.getConfig().getInt("Mongo.Port", 27017);
               int mongoPortSeparator = mongoHost.lastIndexOf(58);
               if (mongoPortSeparator > 0) {
                  mongoPort = Integer.parseInt(mongoHost.substring(mongoPortSeparator + 1));
                  mongoHost = mongoHost.substring(0, mongoPortSeparator);
               }

               ServerAddress mongoAddress = new ServerAddress(mongoHost, mongoPort);
               if (!this.getConfig().getString("Mongo.Pass").equalsIgnoreCase("")) {
                  MongoCredential mongoCredential = MongoCredential.createCredential(
                     this.getConfig().getString("Mongo.User", "admin"),
                     this.getConfig().getString("Mongo.Database", "admin"),
                     this.getConfig().getString("Mongo.Pass").toCharArray()
                  );
                  this.mongoPool = new MongoClient(mongoAddress, Collections.singletonList(mongoCredential));
               } else {
                  this.mongoPool = new MongoClient(mongoAddress);
               }

               MONGO_DB_NAME = this.getConfig().getString("Mongo.DBName");
            } catch (Exception e) {
               e.printStackTrace();
            }

            this.applyWorldSettings();
            this.antiXrayHandler = new AntiXrayHandler(this);
            this.antiXrayHandler.start();
            DTRHandler var10000 = new DTRHandler();
            Math.max(0L, this.getConfiguration("gameplay.yml").getLong("tasks.dtr.initial-delay-ticks", 20L));
            Math.max(1L, this.getConfiguration("gameplay.yml").getLong("tasks.dtr.interval-ticks", 2400L));
            var10000.runTaskTimer(this, 20L, 20L);
            new RedisSaveTask()
               .runTaskTimerAsynchronously(
                  this,
                  Math.max(0L, this.getConfiguration("gameplay.yml").getLong("tasks.redis-save.initial-delay-ticks", 1200L)),
                  Math.max(1L, this.getConfiguration("gameplay.yml").getLong("tasks.redis-save.interval-ticks", 1200L))
               );
            new PacketBorderThread().start();
            Plugin plugin = this.getServer().getPluginManager().getPlugin("BuycraftX");
            if (plugin != null && plugin.isEnabled() && plugin instanceof BuycraftPlugin) {
               this.buycraftPlugin = (BuycraftPlugin)plugin;
            }

            this.setupHandlers();
            this.setupPersistence();
            new ListenerHandler(this);
            this.cheatBreakerIntegration = new CheatBreakerIntegration(this);
            this.cheatBreakerIntegration.reload();
            this.setupTasks();
            Plugin wep = Bukkit.getPluginManager().getPlugin("WorldEdit");
            this.worldEdit = wep instanceof WorldEditPlugin && wep.isEnabled() ? (WorldEditPlugin)wep : null;
            ConfigurationSerialization.registerClass(PersistableLocation.class);
            Proton.getInstance().getTabHandler().setLayoutProvider(new FoxtrotTabLayoutProvider());
            this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
            this.worldEditPlugin = (WorldEditPlugin)this.getServer().getPluginManager().getPlugin("WorldEdit");
            this.anvilRenamePacketAdapter = new AnvilRenamePacketAdapter(this);
            ProtocolLibrary.getProtocolManager().addPacketListener(this.anvilRenamePacketAdapter);
            this.startupComplete = true;
            SquadsCommandPolicy.install(this);
         } else {
            this.getLogger().severe("Could not create the plugin data directory: " + dataDirectory.getAbsolutePath());
            this.getServer().getPluginManager().disablePlugin(this);
         }
      }
   }

   @Override
   public void onDisable() {
      if (this.antiXrayHandler != null) {
         this.antiXrayHandler.close();
         this.antiXrayHandler = null;
      }

      if (this.cheatBreakerIntegration != null) {
         this.cheatBreakerIntegration.close();
         this.cheatBreakerIntegration = null;
      }

      if (this.anvilRenamePacketAdapter != null) {
         ProtocolLibrary.getProtocolManager().removePacketListener(this.anvilRenamePacketAdapter);
         this.anvilRenamePacketAdapter = null;
      }

      if (this.startupComplete) {
         this.getEventHandler().saveEvents();

         for (Player player : getInstance().getServer().getOnlinePlayers()) {
            this.getPlaytimeMap().playerQuit(player.getUniqueId(), false);
            player.setMetadata("loggedout", new FixedMetadataValue(this, true));
         }

         for (String string : PvPClassHandler.getEquippedKits().keySet()) {
            PvPClassHandler.getEquippedKits().get(string).remove(this.getServer().getPlayerExact(string));
         }

         for (Entity entity : this.combatLoggerListener.getCombatLoggers()) {
            if (entity != null) {
               entity.remove();
            }
         }

         if (this.mapHandler.isKitMap() && this.mapHandler.getGameHandler().getOngoingGame() != null) {
            Game ongoingGame = this.mapHandler.getGameHandler().getOngoingGame();

            for (Player player : ongoingGame.getPlayers()) {
               ongoingGame.removePlayer(player);
            }

            for (Player spectator : ongoingGame.getSpectators()) {
               ongoingGame.removeSpectator(spectator);
            }

            ongoingGame.endGame();
         }

         for (Entry entry : MidasTouch.cache.entrySet()) {
            Block block = ((Location)entry.getKey()).getBlock();
            block.removeMetadata("MIDAS_TOUCH", getInstance());
            block.setType(MidasTouch.cache.remove(block.getLocation()));
         }

         RegenUtils.resetAll();
         Proton.getInstance().runRedisCommand(jedis -> {
            jedis.save();
            return null;
         });
         this.saveData();
         if (this.mapHandler.isKitMap()) {
            this.gemFlipHandler.onDisable();
         }

         if (getInstance().getMapHandler().getGameHandler() != null && getInstance().getMapHandler().getGameHandler().isOngoingGame()) {
            getInstance().getMapHandler().getGameHandler().getOngoingGame().endGame();
         }
      }
   }

   public void saveData() {
      RedisSaveTask.save(null, false);
      getInstance().getServerHandler().save();
      if (!this.getConfig().getBoolean("kits", false) && !this.getConfig().getBoolean("teams", false)) {
         this.crateHandler.saveData();
         this.treasureCoveHandler.saveTreasureInfo();
         this.networkBoosterHandler.saveBoosters();
         this.caveNiteHandler.saveLocations();
      }

      if (this.mapHandler.isKitMap()) {
         this.mapHandler.getKitUpgradesHandler().saveUpgrades();
         this.bountyManager.save();
         this.dailyKitHandler.saveData();
      }

      this.fancyBrewerModule.shutdown(this);
      this.airDropHandler.saveLootTable();
      this.customTimerHandler.saveData();
      if (this.mapHandler.getAbilityHandler() != null) {
         this.mapHandler.getAbilityHandler().saveStatistics();
      }

      this.tipsHandler.saveTips();
      this.voucherHandler.saveData();
      this.treasureChestHandler.saveData();
      this.outpostHandler.saveData();
      this.keyAllHandler.saveData();
      if (getInstance().getMapHandler().isKitMap()) {
         getInstance().getMapHandler().getStatsHandler().save();
      }
   }

   private void applyWorldSettings() {
      FileConfiguration gameplay = this.getConfiguration("gameplay.yml");

      for (World world : Bukkit.getWorlds()) {
         world.setThundering(gameplay.getBoolean("world-settings.thundering", false));
         world.setStorm(gameplay.getBoolean("world-settings.storm", false));
         if (world.getEnvironment() == Environment.NORMAL) {
            world.setTime(gameplay.getLong("world-settings.normal-world-time", 18000L));
         }

         world.setWeatherDuration(gameplay.getInt("world-settings.weather-duration-ticks", Integer.MAX_VALUE));
         world.setGameRuleValue("doMobSpawning", Boolean.toString(gameplay.getBoolean("world-settings.game-rules.doMobSpawning", false)));
         world.setGameRuleValue("doFireTick", Boolean.toString(gameplay.getBoolean("world-settings.game-rules.doFireTick", false)));
         world.setGameRuleValue("doDaylightCycle", Boolean.toString(gameplay.getBoolean("world-settings.game-rules.doDaylightCycle", false)));
         world.setGameRuleValue("mobGriefing", Boolean.toString(gameplay.getBoolean("world-settings.game-rules.mobGriefing", false)));
      }
   }

   private void setupHandlers() {
      this.serverHandler = new ServerHandler();
      this.mapHandler = new MapHandler();
      if (this.getMapHandler().isKitMap()) {
         new WorldCreator("kits_events").environment(Environment.NORMAL).createWorld();
      } else {
         new WorldCreator("Spawn").environment(Environment.NORMAL).createWorld();
         new WorldCreator("Deathban").environment(Environment.NORMAL).createWorld();
         new WorldCreator("sg").environment(Environment.NORMAL).createWorld();
      }

      this.eventHandler = new EventHandler();
      this.mapHandler.load();
      this.economyHandler = new EconomyHandler(this);
      this.teamHandler = new TeamHandler();
      LandBoard.getInstance().loadFromTeams();
      this.citadelHandler = new CitadelHandler();
      this.pvpClassHandler = new PvPClassHandler();
      this.conquestHandler = new ConquestHandler();
      this.outpostHandler = new OutpostHandler(this);
      this.shopHandler = new ShopHandler(this);
      this.contentHandler = new ContentHandler(this);
      this.totemHandler = new TotemHandler(this);
      if (this.mapHandler.isKitMap()) {
         this.csUtility = new CSUtility();
         this.gemHandler = new GemHandler();
         this.gemHandler.loadChances();
         this.gemFlipHandler = new GemFlipHandler();
         this.partnerCrateHandler = new PartnerCrateHandler();
         this.bountyManager = new BountyManager();
         this.dailyKitHandler = new DailyKitHandler(this);
         this.masteryUpgradeHandler = new MasteryUpgradeHandler(this);
      }

      this.tokensHandler = new TokensHandler();
      if (this.getConfig().getBoolean("glowstoneMountain", false)) {
         this.glowHandler = new GlowHandler();
      }

      this.customTimerHandler = new CustomTimerHandler();
      this.voucherHandler = new VoucherHandler(this);
      this.pollHandler = new PollHandler();
      this.tipsHandler = new TipsHandler(this);
      this.inventorySeeHandler = new InventorySeeHandler();
      this.fancyBrewerModule = new FancyBrewerModule();
      this.fancyBrewerModule.init(this);
      this.deathbanArenaHandler = new DeathbanArenaHandler(this);
      this.questHandler = new QuestHandler(this);
      this.armorClassHandler = new ArmorClassHandler(this);
      this.treasureChestHandler = new TreasureChestHandler(this);
      if (!this.serverHandler.isTeams() && !this.mapHandler.isKitMap()) {
         this.crateHandler = new CrateHandler(this);
         this.redeemCreatorHandler = new RedeemCreatorHandler();
         this.itemBoxesHandler = new ItemBoxesHandler(this);
         this.clickItemHandler = new ClickItemHandler(this);
         this.furyHandler = new FuryHandler(this);
         this.battlePassHandler = new BattlePassHandler();
         this.treasureCoveHandler = new TreasureCoveHandler();
         this.lettingInHandler = new LettingInHandler(this);
         this.votePartyHandler = new VotePartyHandler(this);
         this.caveNiteHandler = new CaveNiteHandler(this);
         this.spawnerGroundsHandler = new SpawnerGroundsHandler(this);
      }

      this.bossHandler = new BossHandler(this);
      this.miniEventsHandler = new MiniEventsHandler(this);
      this.caveSaysHandler = new CaveSaysHandler(this);
      this.networkBoosterHandler = new NetworkBoosterHandler(this);
      this.couponDropsHandler = new CouponDropsHandler(this);
      this.triviaHandler = new TriviaHandler(this);
      this.airDropHandler = new AirDropHandler(this);
      this.keyAllHandler = new KeyAllHandler(this);
      if (this.getConfig().getBoolean("cavern", false)) {
         this.cavernHandler = new CavernHandler();
      }

      Proton.getInstance().getCommandHandler().registerAll(this);
      DeathMessageHandler.init();
   }

   private void setupPersistence() {
      this.playtimeMap = new PlaytimeMap();
      this.playtimeMap.loadFromRedis();
      this.oppleMap = new OppleMap();
      this.oppleMap.loadFromRedis();
      this.deathbanMap = new DeathbanMap();
      this.deathbanMap.loadFromRedis();
      this.PvPTimerMap = new PvPTimerMap();
      this.PvPTimerMap.loadFromRedis();
      this.saleTimersScoreboardMap = new SaleTimersScoreboardMap();
      this.saleTimersScoreboardMap.loadFromRedis();
      this.startingPvPTimerMap = new StartingPvPTimerMap();
      this.startingPvPTimerMap.loadFromRedis();
      this.deathsMap = new DeathsMap();
      this.deathsMap.loadFromRedis();
      this.factionFilterMap = new FactionFilterMap();
      this.factionFilterMap.loadFromRedis();
      this.kitmapTokensMap = new KitmapTokensMap();
      this.kitmapTokensMap.loadFromRedis();
      this.killsMap = new KillsMap();
      this.killsMap.loadFromRedis();
      this.killstreakMap = new KillstreakMap();
      this.killstreakMap.loadFromRedis();
      this.killTagMap = new KillTagMap();
      this.killTagMap.loadFromRedis();
      this.chatModeMap = new ChatModeMap();
      this.chatModeMap.loadFromRedis();
      this.teamColorMap = new TeamColorMap();
      this.teamColorMap.loadFromRedis();
      this.enemyColorMap = new EnemyColorMap();
      this.enemyColorMap.loadFromRedis();
      this.archerTagColorMap = new ArcherTagColorMap();
      this.archerTagColorMap.loadFromRedis();
      this.teamFocusColorMap = new TeamFocusColorMap();
      this.teamFocusColorMap.loadFromRedis();
      this.focusColorMap = new FocusColorMap();
      this.focusColorMap.loadFromRedis();
      this.toggleGlobalChatMap = new ToggleGlobalChatMap();
      this.toggleGlobalChatMap.loadFromRedis();
      this.fishingKitMap = new FishingKitMap();
      this.fishingKitMap.loadFromRedis();
      this.friendLivesMap = new FriendLivesMap();
      this.friendLivesMap.loadFromRedis();
      this.chatSpyMap = new ChatSpyMap();
      this.chatSpyMap.loadFromRedis();
      this.diamondMinedMap = new DiamondMinedMap();
      this.diamondMinedMap.loadFromRedis();
      this.goldMinedMap = new GoldMinedMap();
      this.goldMinedMap.loadFromRedis();
      this.ironMinedMap = new IronMinedMap();
      this.ironMinedMap.loadFromRedis();
      this.coalMinedMap = new CoalMinedMap();
      this.coalMinedMap.loadFromRedis();
      this.redstoneMinedMap = new RedstoneMinedMap();
      this.redstoneMinedMap.loadFromRedis();
      this.annoyingBroadcastMap = new AnnoyingBroadcastMap();
      this.annoyingBroadcastMap.loadFromRedis();
      this.lapisMinedMap = new LapisMinedMap();
      this.lapisMinedMap.loadFromRedis();
      this.emeraldMinedMap = new EmeraldMinedMap();
      this.emeraldMinedMap.loadFromRedis();
      this.DTRDisplayMap = new DTRDisplayMap();
      this.DTRDisplayMap.loadFromRedis();
      this.teamfightModeMap = new TeamfightModeMap();
      this.teamfightModeMap.loadFromRedis();
      this.lcTeamViewMap = new LCTeamViewMap();
      this.lcTeamViewMap.loadFromRedis();
      this.tipsMap = new TipsMap();
      this.tipsMap.loadFromRedis();
      this.firstJoinMap = new FirstJoinMap();
      this.firstJoinMap.loadFromRedis();
      this.lastJoinMap = new LastJoinMap();
      this.lastJoinMap.loadFromRedis();
      this.wrappedBalanceMap = new WrappedBalanceMap();
      this.wrappedBalanceMap.loadFromRedis();
      this.toggleFoundDiamondsMap = new ToggleFoundDiamondsMap();
      this.toggleFoundDiamondsMap.loadFromRedis();
      this.toggleDeathMessageMap = new ToggleDeathMessageMap();
      this.toggleDeathMessageMap.loadFromRedis();
      this.toggleClaimMessageMap = new ToggleClaimMessageMap();
      this.toggleClaimMessageMap.loadFromRedis();
      this.abilityCooldownsScoreboardMap = new AbilityCooldownsScoreboardMap();
      this.abilityCooldownsScoreboardMap.loadFromRedis();
      this.tabListModeMap = new TabListModeMap();
      this.tabListModeMap.loadFromRedis();
      this.cobblePickupMap = new CobblePickupMap();
      this.cobblePickupMap.loadFromRedis();
      this.mobDropsPickupMap = new MobDropsPickupMap();
      this.mobDropsPickupMap.loadFromRedis();
      this.reclaimMap = new ReclaimMap();
      this.reclaimMap.loadFromRedis();
      this.kdrMap = new KDRMap();
      this.kdrMap.loadFromRedis();
      this.bountyCooldownMap = new BountyCooldownMap();
      this.bountyCooldownMap.loadFromRedis();
      this.gemMap = new GemMap();
      this.gemMap.loadFromRedis();
      this.gemBoosterMap = new GemBoosterMap();
      this.gemBoosterMap.loadFromRedis();
   }

   private void setupTasks() {
      (new BukkitRunnable() {
         @Override
         public void run() {
            if (CustomTimerCreateCommand.isSOTWTimer()) {
               long endsAt = CustomTimerCreateCommand.getCustomTimers().get("&a&lSOTW");
               long remaining = endsAt - System.currentTimeMillis();
               int seconds = (int)(remaining / 1000L);
               List configuredReminders = Foxtrot.this.getConfiguration("gameplay.yml").getIntegerList("sotw.reminder-seconds");
               if ((configuredReminders.isEmpty() ? Foxtrot.this.sotwReminders : configuredReminders).contains(seconds)) {
                  for (Player onlinePlayer : Foxtrot.this.getServer().getOnlinePlayers()) {
                     PlayerUtil.sendTitle(onlinePlayer, "&a&lSOTW", "&f" + TimeUtils.formatIntoDetailedString(seconds) + " &7remaining!");
                  }
               }

               if (remaining <= TimeUnit.MINUTES.toMillis(Foxtrot.this.getConfiguration("gameplay.yml").getLong("sotw.claim-unlock-minutes", 10L))) {
                  for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
                     if (team.isClaimLocked()) {
                        team.setClaimLocked(false);
                        team.sendMessage(CC.YELLOW + "Your faction's claims have been unlocked due to SOTW ending in 10 minutes!");
                     }
                  }
               }
            }
         }
      }).runTaskTimerAsynchronously(getInstance(), 20L, 20L);
      (new BukkitRunnable() {
            @Override
            public void run() {
               Foxtrot.this.saveData();
            }
         })
         .runTaskTimerAsynchronously(
            getInstance(),
            Math.max(0L, this.getConfiguration("gameplay.yml").getLong("tasks.full-save.initial-delay-ticks", 6000L)),
            Math.max(1L, this.getConfiguration("gameplay.yml").getLong("tasks.full-save.interval-ticks", 6000L))
         );
   }

   public void reloadPluginConfiguration() throws IOException, InvalidConfigurationException {
      File configFile = new File(this.getDataFolder(), "config.yml");
      YamlConfiguration validation = new YamlConfiguration();
      validation.load(configFile);
      this.configurationFiles.reload();
      this.reloadConfig();
      if (this.cheatBreakerIntegration != null) {
         this.cheatBreakerIntegration.reload();
      }

      this.applyWorldSettings();
      if (this.serverHandler != null) {
         this.serverHandler.reloadSettings();
      }

      if (this.mapHandler != null) {
         this.mapHandler.reloadRuntimeSettings();
         Proton.getInstance().getScoreboardHandler().setConfiguration(FoxtrotScoreboardConfiguration.create());
      }

      if (this.gemHandler != null) {
         this.gemHandler.loadChances();
      }

      if (this.tipsHandler != null) {
         this.tipsHandler.reloadFromConfig();
      }

      if (this.triviaHandler != null) {
         this.triviaHandler.reloadFromConfig();
      }
   }

   public StatisticServer getStatisticServer() {
      if (getInstance().getMapHandler().isKitMap()) {
         return StatisticServer.KITS;
      } else {
         return getInstance().getServerHandler().isTeams() ? StatisticServer.CLANS : StatisticServer.TEAMS;
      }
   }

   public static Foxtrot getInstance() {
      return instance;
   }

   public FileConfiguration getConfiguration(String fileName) {
      return this.configurationFiles.get(fileName);
   }

   public void saveConfiguration(String fileName) throws IOException {
      this.configurationFiles.save(fileName);
   }

   public MongoClient getMongoPool() {
      return this.mongoPool;
   }

   public BuycraftPlugin getBuycraftPlugin() {
      return this.buycraftPlugin;
   }

   public EconomyHandler getEconomyHandler() {
      return this.economyHandler;
   }

   public DailyKitHandler getDailyKitHandler() {
      return this.dailyKitHandler;
   }

   public ContentHandler getContentHandler() {
      return this.contentHandler;
   }

   public LettingInHandler getLettingInHandler() {
      return this.lettingInHandler;
   }

   public OutpostHandler getOutpostHandler() {
      return this.outpostHandler;
   }

   public PvPClassHandler getPvpClassHandler() {
      return this.pvpClassHandler;
   }

   public ItemBoxesHandler getItemBoxesHandler() {
      return this.itemBoxesHandler;
   }

   public ClickItemHandler getClickItemHandler() {
      return this.clickItemHandler;
   }

   public ArmorClassHandler getArmorClassHandler() {
      return this.armorClassHandler;
   }

   public QuestHandler getQuestHandler() {
      return this.questHandler;
   }

   public BattlePassHandler getBattlePassHandler() {
      return this.battlePassHandler;
   }

   public WorldEditPlugin getWorldEdit() {
      return this.worldEdit;
   }

   public KeyAllHandler getKeyAllHandler() {
      return this.keyAllHandler;
   }

   public CouponDropsHandler getCouponDropsHandler() {
      return this.couponDropsHandler;
   }

   public TeamHandler getTeamHandler() {
      return this.teamHandler;
   }

   public ServerHandler getServerHandler() {
      return this.serverHandler;
   }

   public MapHandler getMapHandler() {
      return this.mapHandler;
   }

   public TipsHandler getTipsHandler() {
      return this.tipsHandler;
   }

   public CitadelHandler getCitadelHandler() {
      return this.citadelHandler;
   }

   public EventHandler getEventHandler() {
      return this.eventHandler;
   }

   public GemFlipHandler getGemFlipHandler() {
      return this.gemFlipHandler;
   }

   public CaveSaysHandler getCaveSaysHandler() {
      return this.caveSaysHandler;
   }

   public TokensHandler getTokensHandler() {
      return this.tokensHandler;
   }

   public ConquestHandler getConquestHandler() {
      return this.conquestHandler;
   }

   public DeathbanArenaHandler getDeathbanArenaHandler() {
      return this.deathbanArenaHandler;
   }

   public PollHandler getPollHandler() {
      return this.pollHandler;
   }

   public VoucherHandler getVoucherHandler() {
      return this.voucherHandler;
   }

   public TreasureChestHandler getTreasureChestHandler() {
      return this.treasureChestHandler;
   }

   public MiniEventsHandler getMiniEventsHandler() {
      return this.miniEventsHandler;
   }

   public SpawnerGroundsHandler getSpawnerGroundsHandler() {
      return this.spawnerGroundsHandler;
   }

   public MasteryUpgradeHandler getMasteryUpgradeHandler() {
      return this.masteryUpgradeHandler;
   }

   public CavernHandler getCavernHandler() {
      return this.cavernHandler;
   }

   public CaveNiteHandler getCaveNiteHandler() {
      return this.caveNiteHandler;
   }

   public GlowHandler getGlowHandler() {
      return this.glowHandler;
   }

   public CrateHandler getCrateHandler() {
      return this.crateHandler;
   }

   public BossHandler getBossHandler() {
      return this.bossHandler;
   }

   public NetworkBoosterHandler getNetworkBoosterHandler() {
      return this.networkBoosterHandler;
   }

   public RedeemCreatorHandler getRedeemCreatorHandler() {
      return this.redeemCreatorHandler;
   }

   public CustomTimerHandler getCustomTimerHandler() {
      return this.customTimerHandler;
   }

   public ShopHandler getShopHandler() {
      return this.shopHandler;
   }

   public TotemHandler getTotemHandler() {
      return this.totemHandler;
   }

   public PlaytimeMap getPlaytimeMap() {
      return this.playtimeMap;
   }

   public OppleMap getOppleMap() {
      return this.oppleMap;
   }

   public DeathbanMap getDeathbanMap() {
      return this.deathbanMap;
   }

   public PvPTimerMap getPvPTimerMap() {
      return this.PvPTimerMap;
   }

   public SaleTimersScoreboardMap getSaleTimersScoreboardMap() {
      return this.saleTimersScoreboardMap;
   }

   public StartingPvPTimerMap getStartingPvPTimerMap() {
      return this.startingPvPTimerMap;
   }

   public DeathsMap getDeathsMap() {
      return this.deathsMap;
   }

   public KillsMap getKillsMap() {
      return this.killsMap;
   }

   public FactionFilterMap getFactionFilterMap() {
      return this.factionFilterMap;
   }

   public KitmapTokensMap getKitmapTokensMap() {
      return this.kitmapTokensMap;
   }

   public AirDropHandler getAirDropHandler() {
      return this.airDropHandler;
   }

   public GemHandler getGemHandler() {
      return this.gemHandler;
   }

   public PartnerCrateHandler getPartnerCrateHandler() {
      return this.partnerCrateHandler;
   }

   public BountyManager getBountyManager() {
      return this.bountyManager;
   }

   public KillstreakMap getKillstreakMap() {
      return this.killstreakMap;
   }

   public KillTagMap getKillTagMap() {
      return this.killTagMap;
   }

   public TeamColorMap getTeamColorMap() {
      return this.teamColorMap;
   }

   public EnemyColorMap getEnemyColorMap() {
      return this.enemyColorMap;
   }

   public ArcherTagColorMap getArcherTagColorMap() {
      return this.archerTagColorMap;
   }

   public FocusColorMap getFocusColorMap() {
      return this.focusColorMap;
   }

   public TeamFocusColorMap getTeamFocusColorMap() {
      return this.teamFocusColorMap;
   }

   public AnnoyingBroadcastMap getAnnoyingBroadcastMap() {
      return this.annoyingBroadcastMap;
   }

   public ChatModeMap getChatModeMap() {
      return this.chatModeMap;
   }

   public FishingKitMap getFishingKitMap() {
      return this.fishingKitMap;
   }

   public ToggleGlobalChatMap getToggleGlobalChatMap() {
      return this.toggleGlobalChatMap;
   }

   public ChatSpyMap getChatSpyMap() {
      return this.chatSpyMap;
   }

   public DiamondMinedMap getDiamondMinedMap() {
      return this.diamondMinedMap;
   }

   public GoldMinedMap getGoldMinedMap() {
      return this.goldMinedMap;
   }

   public IronMinedMap getIronMinedMap() {
      return this.ironMinedMap;
   }

   public CoalMinedMap getCoalMinedMap() {
      return this.coalMinedMap;
   }

   public RedstoneMinedMap getRedstoneMinedMap() {
      return this.redstoneMinedMap;
   }

   public LapisMinedMap getLapisMinedMap() {
      return this.lapisMinedMap;
   }

   public EmeraldMinedMap getEmeraldMinedMap() {
      return this.emeraldMinedMap;
   }

   public FirstJoinMap getFirstJoinMap() {
      return this.firstJoinMap;
   }

   public LastJoinMap getLastJoinMap() {
      return this.lastJoinMap;
   }

   public FriendLivesMap getFriendLivesMap() {
      return this.friendLivesMap;
   }

   public AbilityCooldownsScoreboardMap getAbilityCooldownsScoreboardMap() {
      return this.abilityCooldownsScoreboardMap;
   }

   public WrappedBalanceMap getWrappedBalanceMap() {
      return this.wrappedBalanceMap;
   }

   public TreasureCoveHandler getTreasureCoveHandler() {
      return this.treasureCoveHandler;
   }

   public ToggleFoundDiamondsMap getToggleFoundDiamondsMap() {
      return this.toggleFoundDiamondsMap;
   }

   public ToggleDeathMessageMap getToggleDeathMessageMap() {
      return this.toggleDeathMessageMap;
   }

   public ToggleClaimMessageMap getToggleClaimMessageMap() {
      return this.toggleClaimMessageMap;
   }

   public TabListModeMap getTabListModeMap() {
      return this.tabListModeMap;
   }

   public CobblePickupMap getCobblePickupMap() {
      return this.cobblePickupMap;
   }

   public MobDropsPickupMap getMobDropsPickupMap() {
      return this.mobDropsPickupMap;
   }

   public KDRMap getKdrMap() {
      return this.kdrMap;
   }

   public BountyCooldownMap getBountyCooldownMap() {
      return this.bountyCooldownMap;
   }

   public ReclaimMap getReclaimMap() {
      return this.reclaimMap;
   }

   public TriviaHandler getTriviaHandler() {
      return this.triviaHandler;
   }

   public FuryHandler getFuryHandler() {
      return this.furyHandler;
   }

   public DTRDisplayMap getDTRDisplayMap() {
      return this.DTRDisplayMap;
   }

   public TeamfightModeMap getTeamfightModeMap() {
      return this.teamfightModeMap;
   }

   public LCTeamViewMap getLcTeamViewMap() {
      return this.lcTeamViewMap;
   }

   public TipsMap getTipsMap() {
      return this.tipsMap;
   }

   public CSUtility getCsUtility() {
      return this.csUtility;
   }

   public GemMap getGemMap() {
      return this.gemMap;
   }

   public GemBoosterMap getGemBoosterMap() {
      return this.gemBoosterMap;
   }

   public WorldEditPlugin getWorldEditPlugin() {
      return this.worldEditPlugin;
   }

   public CombatLoggerListener getCombatLoggerListener() {
      return this.combatLoggerListener;
   }

   public void setCombatLoggerListener(CombatLoggerListener combatLoggerListener) {
      this.combatLoggerListener = combatLoggerListener;
   }

   public InventorySeeHandler getInventorySeeHandler() {
      return this.inventorySeeHandler;
   }

   public FancyBrewerModule getFancyBrewerModule() {
      return this.fancyBrewerModule;
   }

   public VotePartyHandler getVotePartyHandler() {
      return this.votePartyHandler;
   }

   public Predicate<Player> getInDuelPredicate() {
      return this.inDuelPredicate;
   }

   public void setInDuelPredicate(Predicate<Player> inDuelPredicate) {
      this.inDuelPredicate = inDuelPredicate;
   }

   public Predicate<Player> getInEventPredicate() {
      return this.inEventPredicate;
   }

   public void setInEventPredicate(Predicate<Player> inEventPredicate) {
      this.inEventPredicate = inEventPredicate;
   }
}
