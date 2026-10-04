package net.frozenorb.foxtrot.server;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.apollo.ApolloListener;
import net.frozenorb.foxtrot.apollo.ApolloNametagListener;
import net.frozenorb.foxtrot.apollo.ApolloService;
import net.frozenorb.foxtrot.apollo.ApolloSyncService;
import net.frozenorb.foxtrot.apollo.ApolloWaypointListener;
import net.frozenorb.foxtrot.chat.listeners.ChatListener;
import net.frozenorb.foxtrot.commands.LegacySpectatorMode;
import net.frozenorb.foxtrot.commands.RatioCommand;
import net.frozenorb.foxtrot.commands.SpectatorCommand;
import net.frozenorb.foxtrot.controlpanel.ControlPanelListener;
import net.frozenorb.foxtrot.controlpanel.ControlPanelService;
import net.frozenorb.foxtrot.gameplay.kitmap.game.arena.select.SelectionListeners;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.ffa.FFAListeners;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.knockout.KnockoutListeners;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.minestrike.MineStrikeListeners;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.oitq.OITQListeners;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.parkour.ParkourListener;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.shuffle.ShuffleListeners;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.spleef.SpleefListeners;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.sumo.SumoListeners;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.tntrun.TNTRunListeners;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.tnttag.TNTTagListeners;
import net.frozenorb.foxtrot.gameplay.kitmap.game.listener.GameListeners;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.listener.KitEditorListener;
import net.frozenorb.foxtrot.gameplay.loot.partnercrate.listener.PartnerCrateListener;
import net.frozenorb.foxtrot.listener.AntiGlitchListener;
import net.frozenorb.foxtrot.listener.AntiGriefListener;
import net.frozenorb.foxtrot.listener.AntiPrimeListener;
import net.frozenorb.foxtrot.listener.AnvilInventorySyncListener;
import net.frozenorb.foxtrot.listener.ArmorDamageListener;
import net.frozenorb.foxtrot.listener.AutoRebuildListener;
import net.frozenorb.foxtrot.listener.BasicPreventionListener;
import net.frozenorb.foxtrot.listener.BlockConvenienceListener;
import net.frozenorb.foxtrot.listener.BlockRegenListener;
import net.frozenorb.foxtrot.listener.BossCleanupListener;
import net.frozenorb.foxtrot.listener.BorderListener;
import net.frozenorb.foxtrot.listener.ClickableKitListener;
import net.frozenorb.foxtrot.listener.CombatLoggerListener;
import net.frozenorb.foxtrot.listener.ConfigSyncListener;
import net.frozenorb.foxtrot.listener.CrappleLimitListener;
import net.frozenorb.foxtrot.listener.CrowbarListener;
import net.frozenorb.foxtrot.listener.DomainListener;
import net.frozenorb.foxtrot.listener.DoorListener;
import net.frozenorb.foxtrot.listener.DupeGlitchListener;
import net.frozenorb.foxtrot.listener.ElevatorsListener;
import net.frozenorb.foxtrot.listener.CreativeItemGuardListener;
import net.frozenorb.foxtrot.listener.EnchantmentLimiterListener;
import net.frozenorb.foxtrot.listener.EndListener;
import net.frozenorb.foxtrot.listener.EntityPortalListener;
import net.frozenorb.foxtrot.listener.FastBowListener;
import net.frozenorb.foxtrot.listener.FirstJoinFixListener;
import net.frozenorb.foxtrot.listener.FixListener;
import net.frozenorb.foxtrot.listener.FlyListener;
import net.frozenorb.foxtrot.listener.FoundDiamondsListener;
import net.frozenorb.foxtrot.listener.FoxListener;
import net.frozenorb.foxtrot.listener.GoldenAppleListener;
import net.frozenorb.foxtrot.listener.HardcoreCommandListener;
import net.frozenorb.foxtrot.listener.KOTHRewardKeyListener;
import net.frozenorb.foxtrot.listener.KitMapListener;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import net.frozenorb.foxtrot.listener.MapListener;
import net.frozenorb.foxtrot.listener.MegaFixListener;
import net.frozenorb.foxtrot.listener.OldWorldGuardListener;
import net.frozenorb.foxtrot.listener.MobStackListener;
import net.frozenorb.foxtrot.listener.ModernClientAnvilListener;
import net.frozenorb.foxtrot.listener.NetherPortalListener;
import net.frozenorb.foxtrot.listener.OwnerClickableKitListener;
import net.frozenorb.foxtrot.listener.PearlListener;
import net.frozenorb.foxtrot.listener.PhaseGlitchListener;
import net.frozenorb.foxtrot.listener.PortalTrapListener;
import net.frozenorb.foxtrot.listener.PotionLimiterListener;
import net.frozenorb.foxtrot.listener.PracticeListener;
import net.frozenorb.foxtrot.listener.PvPTimerListener;
import net.frozenorb.foxtrot.listener.RedstoneCrashListener;
import net.frozenorb.foxtrot.listener.ReputationListener;
import net.frozenorb.foxtrot.listener.RodCooldownListener;
import net.frozenorb.foxtrot.listener.SellAllListener;
import net.frozenorb.foxtrot.listener.SignShopListener;
import net.frozenorb.foxtrot.listener.SignSubclaimListener;
import net.frozenorb.foxtrot.listener.SpawnListener;
import net.frozenorb.foxtrot.listener.SpawnTagListener;
import net.frozenorb.foxtrot.listener.SpawnerLimiterListener;
import net.frozenorb.foxtrot.listener.SpawnerTrackerListener;
import net.frozenorb.foxtrot.listener.StaffUtilsListener;
import net.frozenorb.foxtrot.listener.StatTrackerListener;
import net.frozenorb.foxtrot.listener.SugeListener;
import net.frozenorb.foxtrot.listener.TeamListener;
import net.frozenorb.foxtrot.listener.TitleListener;
import net.frozenorb.foxtrot.listener.WebsiteListener;
import net.frozenorb.foxtrot.map.listener.BlockDecayListeners;
import net.frozenorb.foxtrot.server.pearl.EnderpearlCooldownHandler;
import net.frozenorb.foxtrot.spongetrader.SpongeTraderHandler;
import net.frozenorb.foxtrot.team.commands.team.TeamClaimCommand;
import net.frozenorb.foxtrot.team.commands.team.TeamSetLocationCommand;
import net.frozenorb.foxtrot.team.commands.team.subclaim.TeamSubclaimCommand;
import net.frozenorb.foxtrot.team.upgrade.listener.UpgradeListener;

public class ListenerHandler {
   public ListenerHandler(Foxtrot foxtrot) {
      try {
         File file2 = new File("/config/HCTeams");
         File file3 = foxtrot.getDataFolder();
         if (file2.exists() && file3 != null) {
            if (!file3.exists()) {
               file3.mkdirs();
            }

            File[] fileArray;
            if ((fileArray = file2.listFiles((file, string) -> string.endsWith(".yml"))) != null) {
               File[] fileArray2 = fileArray;
               int n = fileArray.length;

               for (int n2 = 0; n2 < n; n2++) {
                  File file4 = fileArray2[n2];

                  try {
                     Files.copy(file4.toPath(), new File(file3, file4.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                  } catch (Exception var10) {
                  }
               }

               System.out.println("[HCTeams] Synced " + fileArray.length + " configs from /config/HCTeams/");
            }
         }
      } catch (Exception exception) {
         System.out.println("[HCTeams] Config sync failed: " + exception.getMessage());
      }

      new SpongeTraderHandler(foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new MapListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new ReputationListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new AntiGlitchListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new BasicPreventionListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new BorderListener(), foxtrot);
      foxtrot.setCombatLoggerListener(new CombatLoggerListener());
      foxtrot.getServer().getPluginManager().registerEvents(foxtrot.getCombatLoggerListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new CrowbarListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new CreativeItemGuardListener(), foxtrot);
      EnchantmentLimiterListener enchantmentLimiter = new EnchantmentLimiterListener();
      foxtrot.getServer().getPluginManager().registerEvents(enchantmentLimiter, foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new AnvilInventorySyncListener(foxtrot), foxtrot);
      ModernClientAnvilListener anvilListener = new ModernClientAnvilListener();
      foxtrot.getServer().getPluginManager().registerEvents(anvilListener, foxtrot);
      foxtrot.getServer().getScheduler().runTaskTimer(foxtrot, anvilListener::syncAnvils, 1L, 1L);
      foxtrot.getServer().getPluginManager().registerEvents(new EnderpearlCooldownHandler(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new ChatListener(), Foxtrot.getInstance());
      foxtrot.getServer().getPluginManager().registerEvents(new RedstoneCrashListener(foxtrot), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new EndListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new FoundDiamondsListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new FoxListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new FirstJoinFixListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new MobStackListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new MegaFixListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new OldWorldGuardListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new SignShopListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new RatioCommand(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new TitleListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new GoldenAppleListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new OwnerClickableKitListener(foxtrot), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new KOTHRewardKeyListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new PvPTimerListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new PearlListener(), foxtrot);
      PotionLimiterListener potionLimiterListener = new PotionLimiterListener();
      foxtrot.getServer().getPluginManager().registerEvents(potionLimiterListener, foxtrot);
      potionLimiterListener.registerBrewGuard(foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new PracticeListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new NetherPortalListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new RodCooldownListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new PortalTrapListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new SignSubclaimListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new DoorListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new ElevatorsListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new BlockDecayListeners(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new AntiGriefListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new SpawnListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new SpawnTagListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new StaffUtilsListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new TeamListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new PhaseGlitchListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new WebsiteListener(foxtrot), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new StatTrackerListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new DomainListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new TeamSubclaimCommand(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new TeamClaimCommand(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new SellAllListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new FlyListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new SpawnerTrackerListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new SpawnerLimiterListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new BossCleanupListener(), foxtrot);
      if (!Foxtrot.getInstance().getServerHandler().isTeams()) {
         foxtrot.getServer().getPluginManager().registerEvents(new BlockConvenienceListener(), foxtrot);
         foxtrot.getServer().getPluginManager().registerEvents(new FixListener(), foxtrot);
         foxtrot.getServer().getPluginManager().registerEvents(new SugeListener(), foxtrot);
         foxtrot.getServer().getPluginManager().registerEvents(new PartnerCrateListener(), foxtrot);
         foxtrot.getServer().getPluginManager().registerEvents(new ClickableKitListener(foxtrot), foxtrot);
         foxtrot.getServer().getPluginManager().registerEvents(new DupeGlitchListener(foxtrot), foxtrot);
         foxtrot.getServer().getPluginManager().registerEvents(new AntiPrimeListener(), foxtrot);
         foxtrot.getServer().getPluginManager().registerEvents(new UpgradeListener(), foxtrot);
         if (!foxtrot.getMapHandler().isKitMap()) {
            foxtrot.getServer().getPluginManager().registerEvents(new AutoRebuildListener(), foxtrot);
            foxtrot.getServer().getPluginManager().registerEvents(new CrappleLimitListener(), foxtrot);
         }
      }

      foxtrot.getServer().getPluginManager().registerEvents(new LunarClientListener(foxtrot), foxtrot);
      if (foxtrot.getServerHandler().isReduceArmorDamage()) {
         foxtrot.getServer().getPluginManager().registerEvents(new ArmorDamageListener(), foxtrot);
      }

      if (foxtrot.getServerHandler().isBlockEntitiesThroughPortals()) {
         foxtrot.getServer().getPluginManager().registerEvents(new EntityPortalListener(), foxtrot);
      }

      if (foxtrot.getServerHandler().isBlockRemovalEnabled()) {
         foxtrot.getServer().getPluginManager().registerEvents(new BlockRegenListener(), foxtrot);
      }

      if (foxtrot.getMapHandler().isKitMap()) {
         foxtrot.getServer().getWorld("world").setSpawnLocation(0, 73, 0);
         foxtrot.getServer().getPluginManager().registerEvents(new KitMapListener(), foxtrot);
         foxtrot.getServer().getPluginManager().registerEvents(new KitEditorListener(), foxtrot);
         foxtrot.getServer().getPluginManager().registerEvents(new FastBowListener(), foxtrot);
         foxtrot.getServer().getPluginManager().registerEvents(new GameListeners(), foxtrot);
         foxtrot.getServer().getPluginManager().registerEvents(new SumoListeners(), foxtrot);
         foxtrot.getServer().getPluginManager().registerEvents(new FFAListeners(), foxtrot);
         foxtrot.getServer().getPluginManager().registerEvents(new SpleefListeners(), foxtrot);
         foxtrot.getServer().getPluginManager().registerEvents(new ShuffleListeners(), foxtrot);
         foxtrot.getServer().getPluginManager().registerEvents(new SelectionListeners(), foxtrot);
         foxtrot.getServer().getPluginManager().registerEvents(new OITQListeners(), foxtrot);
         foxtrot.getServer().getPluginManager().registerEvents(new TNTRunListeners(), foxtrot);
         foxtrot.getServer().getPluginManager().registerEvents(new TNTTagListeners(), foxtrot);
         foxtrot.getServer().getPluginManager().registerEvents(new KnockoutListeners(), foxtrot);
         foxtrot.getServer().getPluginManager().registerEvents(new ParkourListener(), foxtrot);
         foxtrot.getServer().getPluginManager().registerEvents(new MineStrikeListeners(), foxtrot);
      }

      foxtrot.getServer().getPluginManager().registerEvents(new RedstoneCrashListener(foxtrot), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new HardcoreCommandListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new SpectatorCommand(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new LegacySpectatorMode(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new ConfigSyncListener(), foxtrot);
      ApolloService.init();
      foxtrot.getServer().getPluginManager().registerEvents(new ApolloListener(), foxtrot);
      foxtrot.getServer().getPluginManager().registerEvents(new ApolloNametagListener(), foxtrot);
      ApolloNametagListener.startTask(foxtrot);
      if (ApolloService.isApolloEnabled()) {
         ApolloSyncService.start(foxtrot);
      }

      if (!ApolloSyncService.isRunning()) {
         foxtrot.getServer().getPluginManager().registerEvents(new ApolloWaypointListener(), foxtrot);
         ApolloWaypointListener.startTask(foxtrot);
      }
      ControlPanelService.init();
      foxtrot.getServer().getPluginManager().registerEvents(new ControlPanelListener(), foxtrot);
      ControlPanelListener.startStatusTask();
      foxtrot.getServer().getPluginManager().registerEvents(new TeamSetLocationCommand(), foxtrot);
   }
}
