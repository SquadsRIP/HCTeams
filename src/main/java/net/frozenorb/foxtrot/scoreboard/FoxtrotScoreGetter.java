package net.frozenorb.foxtrot.scoreboard;

import cc.fyre.modsuite.mod.ModHandler;
import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.Proton;
import cc.fyre.proton.scoreboard.construct.ScoreFunction;
import cc.fyre.proton.scoreboard.construct.ScoreGetter;
import cc.fyre.proton.util.TimeUtils;
import cc.fyre.universe.Universe;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.commands.EOTWCommand;
import net.frozenorb.foxtrot.commands.KillTheKingCommand;
import net.frozenorb.foxtrot.config.SquadsText;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.type.AntiBlockup;
import net.frozenorb.foxtrot.gameplay.ability.type.Combo;
import net.frozenorb.foxtrot.gameplay.ability.type.NinjaStar;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import net.frozenorb.foxtrot.gameplay.events.cavenite.CaveNiteHandler;
import net.frozenorb.foxtrot.gameplay.events.cavenite.CaveNiteState;
import net.frozenorb.foxtrot.gameplay.events.conquest.enums.ConquestCapzone;
import net.frozenorb.foxtrot.gameplay.events.conquest.game.ConquestGame;
import net.frozenorb.foxtrot.gameplay.events.dtc.DTC;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.gameplay.events.mini.MiniEvent;
import net.frozenorb.foxtrot.gameplay.events.mini.MiniEventsHandler;
import net.frozenorb.foxtrot.gameplay.events.outposts.OutpostHandler;
import net.frozenorb.foxtrot.gameplay.events.outposts.type.EndOutpost;
import net.frozenorb.foxtrot.gameplay.events.outposts.type.NetherOutpost;
import net.frozenorb.foxtrot.gameplay.extra.stats.StatsEntry;
import net.frozenorb.foxtrot.gameplay.kitmap.duel.Duel;
import net.frozenorb.foxtrot.gameplay.kitmap.game.Game;
import net.frozenorb.foxtrot.gameplay.loot.treasurecove.listeners.TreasureCoveListener;
import net.frozenorb.foxtrot.gameplay.loot.voteparty.VotePartyHandler;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClass;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.gameplay.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.foxtrot.listener.AntiCleanListener;
import net.frozenorb.foxtrot.listener.CrappleLimitListener;
import net.frozenorb.foxtrot.listener.EndListener;
import net.frozenorb.foxtrot.listener.GoldenAppleListener;
import net.frozenorb.foxtrot.listener.event.TimerEndEvent;
import net.frozenorb.foxtrot.server.ServerHandler;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.server.keyalls.KeyAll;
import net.frozenorb.foxtrot.server.keyalls.KeyAllHandler;
import net.frozenorb.foxtrot.server.pearl.EnderpearlCooldownHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.commands.team.TeamStuckCommand;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Logout;
import net.valorhcf.ThreadingManager;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.cavepvp.profiles.Profiles;

public class FoxtrotScoreGetter implements ScoreGetter {
   private static final int MAX_SCOREBOARD_LINE_LENGTH = 47;
   private static FileConfiguration sbConfig;

   private static void loadConfig() {
      try {
         File file = new File(Foxtrot.getInstance().getDataFolder(), "scoreboard.yml");
         if (!file.exists()) {
            Foxtrot.getInstance().saveResource("scoreboard.yml", false);
         }

         YamlConfiguration var10000 = YamlConfiguration.loadConfiguration(file);
         SquadsText.configuration(var10000, "scoreboard.yml");
         sbConfig = var10000;
         String footer;
         String header;
         if ((header = var10000.getString("header", "")).equals(footer = sbConfig.getString("footer", "")) && !header.isEmpty()) {
            sbConfig.set("header", "&a" + header);
            sbConfig.set("footer", "&b" + footer);

            try {
               sbConfig.save(file);
            } catch (Exception var4) {
            }
         }
      } catch (Exception e) {
         sbConfig = new YamlConfiguration();
      }
   }

   public static void reloadConfig() {
      sbConfig = null;
      loadConfig();
   }

   private static String sc(String key, String def) {
      if (sbConfig == null) {
         loadConfig();
      }

      return ChatColor.translateAlternateColorCodes('&', sbConfig.getString(key, def));
   }

   @Override
   public String[] getScores(LinkedList<String> scores, Player player) {
      if (Foxtrot.getInstance().getDeathbanArenaHandler().isDeathbanArena(player)) {
         if (!Foxtrot.getInstance().getDeathbanMap().isDeathbanned(player.getUniqueId())) {
            Foxtrot.getInstance().getDeathbanArenaHandler().revive(player.getUniqueId());
            return finishScores(scores);
         } else {
            long seconds = Foxtrot.getInstance().getDeathbanMap().getDeathban(player.getUniqueId()) - System.currentTimeMillis();
            scores.add("&c&lDeathban&7: &f" + ScoreFunction.TIME_FANCY.apply((float)seconds / 1000.0F));
            scores.add("&a&lLives&7: &f" + Foxtrot.getInstance().getFriendLivesMap().getLives(player.getUniqueId()));
            scores.add("&6&lArena Kills&7: &f" + Foxtrot.getInstance().getDeathbanArenaHandler().getCache().getOrDefault(player.getUniqueId(), 0) + "/5");
            scores.addFirst("&a&7&m--------------------");
            scores.add("&b&7&m--------------------");
            return finishScores(scores);
         }
      } else {
         Game ongoingGame;
         if (Foxtrot.getInstance().getMapHandler().getGameHandler() != null
            && Foxtrot.getInstance().getMapHandler().getGameHandler().isOngoingGame()
            && (ongoingGame = Foxtrot.getInstance().getMapHandler().getGameHandler().getOngoingGame()).isPlayingOrSpectating(player.getUniqueId())) {
            ongoingGame.getScoreboardLines(player, scores);
            scores.addFirst("&a&7&m--------------------");
            scores.add("&b&7&m--------------------");
            return finishScores(scores);
         }

         if (Foxtrot.getInstance().getInDuelPredicate().test(player)) {
            Duel duel = Foxtrot.getInstance().getMapHandler().getDuelHandler().getDuel(player);
            scores.add("&6Opponent: &f" + Proton.getInstance().getUuidCache().name(duel.getOpponent(player.getUniqueId())));
            scores.add("&6Wager: &a" + duel.getWager() + " Gems");
            scores.addFirst("&a&7&m--------------------");
            scores.add("&b&7&m--------------------");
            return finishScores(scores);
         }

         Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
         Long antiCleanTime = AntiCleanListener.TIME_CACHE.get(team);
         if (antiCleanTime != null) {
            Team fightOpponent = AntiCleanListener.CACHE.get(team);
            String fightFaction = fightOpponent != null ? fightOpponent.getName() : "Unknown";
            long timeLeft = antiCleanTime - System.currentTimeMillis();
            String fightTime;
            if (timeLeft <= 0L) {
               fightTime = "0s";
            } else {
               int seconds = (int)(timeLeft / 1000L);
               fightTime = seconds + "s";
            }

            scores.add(sc("fight-format", "&4Fight: &7<faction> &4(<time>)").replace("<faction>", fightFaction).replace("<time>", fightTime));
            if (System.currentTimeMillis() > antiCleanTime) {
               String opponentName = fightOpponent != null ? fightOpponent.getName() : "Unknown";
               AntiCleanListener.CACHE.remove(team);
               AntiCleanListener.TIME_CACHE.remove(team);
               team.sendMessage(ChatColor.translateAlternateColorCodes('&', "&fYour &4Fight&f against &4" + opponentName + "&f is now over."));
            }
         }

         String spawnTagScore = this.getSpawnTagScore(player);
         String enderpearlScore = this.getEnderpearlScore(player);
         String abilityScore = this.getAbilityScore(player);
         String pvpTimerScore = this.getPvPTimerScore(player);
         String archerMarkScore = this.getArcherMarkScore(player);
         String effectScore = getEffectScore(player);
         String energyScore = this.getEnergyScore(player);
         String fstuckScore = this.getFStuckScore(player);
         String logoutScore = this.getLogoutScore(player);
         String goppleScore = this.getGoppleScore(player);
         String homeScore = this.getHomeScore(player);
         String appleScore = this.getAppleScore(player);
         String classUltimateScore = this.getClassUltimateEnergyScore(player);
         String gemBoosterScore = this.getGemBooster(player);
         String warmupScore = this.getClassWarmupScore(player);
         String getBooster = this.getBooster(player);
         if (Foxtrot.getInstance().getMapHandler().isKitMap() && DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
            StatsEntry stats = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(player.getUniqueId());
            scores.add("&6&lKills&7: &f" + stats.getKills());
            scores.add("&6&lDeaths&7: &f" + stats.getDeaths());
            scores.add("&2&lGems&7: &f" + Foxtrot.getInstance().getGemMap().getGems(player));
         }

         for (Event event : Foxtrot.getInstance().getEventHandler().getEvents()) {
            if (event.isActive() && !event.isHidden()) {
               String displayName;
               switch (event.getName()) {
                  case "End":
                     displayName = ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "End";
                     break;
                  case "Hell":
                     displayName = ChatColor.YELLOW + ChatColor.BOLD.toString() + "Hell";
                     break;
                  case "EOTW":
                     displayName = ChatColor.GOLD.toString() + ChatColor.BOLD + "EOTW";
                     break;
                  case "Citadel":
                     displayName = ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Citadel";
                     break;
                  case "NetherCitadel":
                     displayName = ChatColor.GOLD.toString() + ChatColor.BOLD + "Citadel";
                     break;
                  default:
                     displayName = ChatColor.BLUE.toString() + ChatColor.BOLD + event.getName();
               }

               if (event.getType() == EventType.DTC) {
                  scores.add(
                     sc("dtc-format", "<name>&7: &c<points>")
                        .replace("<name>", displayName)
                        .replace("<points>", String.valueOf(((DTC)event).getCurrentPoints()))
                  );
               } else {
                  scores.add(
                     sc("koth-format", "<name>&7: &c<time>")
                        .replace("<name>", displayName)
                        .replace("<time>", ScoreFunction.TIME_SIMPLE.apply((float)((KOTH)event).getRemainingCapTime()))
                  );
               }
            }
         }

         PvPClass pvpClass = PvPClassHandler.getPvPClass(player);
         if (classUltimateScore != null && pvpClass != null) {
            scores.add(sc("class-ultimate-format", "&b&lUltimate&7: &b<energy>").replace("<energy>", classUltimateScore));
         }

         if (spawnTagScore != null) {
            scores.add(sc("spawn-tag-format", "&c&lSpawn Tag&7: &c<time>").replace("<time>", spawnTagScore));
         }

         if (homeScore != null) {
            scores.add(sc("home-format", "&9&lHome&7: &9<time>").replace("<time>", homeScore));
         }

         if (goppleScore != null) {
            scores.add(sc("gopple-format", "&6&lGopple&7: &6<time>").replace("<time>", goppleScore));
         }

         if (appleScore != null) {
            scores.add(sc("apple-format", "&6&lApple&7: &6<time>").replace("<time>", appleScore));
         }

         if (enderpearlScore != null) {
            scores.add(sc("enderpearl-format", "&5&lEnderpearl&7: &5<time>").replace("<time>", enderpearlScore));
         }

         if (warmupScore != null) {
            scores.add(sc("class-warmup-format", "&b&lClass Warmup&7: &b<time>").replace("<time>", warmupScore));
         }

         if (pvpTimerScore != null) {
            scores.add(sc("pvp-timer-format", "&a&lPvP Timer&7: &a<time>").replace("<time>", pvpTimerScore));
         }

         if (gemBoosterScore != null) {
            scores.add(sc("gem-booster-format", "&2&l2x Gem Booster&7: &2<time>").replace("<time>", gemBoosterScore));
         }

         if (abilityScore != null) {
            scores.add(sc("ability-global-format", "&d&lAbility Item&7: &d<time>").replace("<time>", abilityScore));
         }

         if (Foxtrot.getInstance().getNetworkBoosterHandler().isFrenzy()) {
            scores.add(sc("frenzy-booster-format", "&b&lFrenzy Booster&7: &b<time>").replace("<time>", getBooster));
         }

         Iterator<Entry<String, Long>> iterator = CustomTimerCreateCommand.getCustomTimers().entrySet().iterator();

         while (iterator.hasNext()) {
            Entry<String, Long> timer = iterator.next();
            if (timer.getValue() < System.currentTimeMillis()) {
               iterator.remove();
               TimerEndEvent timerEndEvent = new TimerEndEvent(timer.getValue(), timer.getKey());
               Foxtrot.getInstance().getServer().getPluginManager().callEvent(timerEndEvent);
            } else {
               String display = this.getCustomTimerDisplay(player, timer.getKey(), timer.getValue());
               if (display != null) {
                  scores.add(display);
               }
            }
         }

         KeyAllHandler keyAllHandler = Foxtrot.getInstance().getKeyAllHandler();

         for (KeyAll keyAll : keyAllHandler.findScoreboardKeyalls()) {
            long difference = keyAll.getGiveAllTime() - System.currentTimeMillis();
            scores.add(formatTimerLine(keyAll.getScoreboardDisplay(), ScoreFunction.TIME_SIMPLE.apply((float)difference / 1000.0F)));
         }

         if (EOTWCommand.isFfaEnabled()) {
            long ffaEnabledAt = EOTWCommand.getFfaActiveAt();
            if (System.currentTimeMillis() < ffaEnabledAt) {
               long difference = ffaEnabledAt - System.currentTimeMillis();
               scores.add(sc("ffa-format", "&6&lFFA&7: &6<time>").replace("<time>", ScoreFunction.TIME_SIMPLE.apply((float)difference / 1000.0F)));
            }
         }

         if (archerMarkScore != null) {
            scores.add(sc("archer-mark-format", "&6&lArcher Mark&7: &6<time>").replace("<time>", archerMarkScore));
         }

         if (effectScore != null && pvpClass != null) {
            scores.add(
               sc("class-effect-format", "&a&l<classname> Effect&7: &a<time>").replace("<classname>", pvpClass.getName()).replace("<time>", effectScore)
            );
         }

         if (energyScore != null && pvpClass != null) {
            scores.add(
               sc("class-energy-format", "&b&l<classname> Energy&7: &b<energy>").replace("<classname>", pvpClass.getName()).replace("<energy>", energyScore)
            );
         }

         if (fstuckScore != null) {
            scores.add(sc("stuck-format", "&6&lStuck&7: &6<time>").replace("<time>", fstuckScore));
         }

         if (logoutScore != null) {
            scores.add(sc("logout-format", "&6&lLogout&7: &6<time>").replace("<time>", logoutScore));
         }

         if (player.getWorld().getEnvironment().equals(Environment.NETHER) && !Foxtrot.getInstance().getMapHandler().isKitMap()) {
            int amount = CrappleLimitListener.consumed.getOrDefault(player.getUniqueId(), new AtomicInteger(0)).get();
            scores.add(sc("apple-limit-format", "&6&lApples: &6<current>/12").replace("<current>", String.valueOf(amount)));
         }

         if (Proton.getInstance().getAutoRebootHandler().isRebooting()) {
            scores.add(
               sc("reboot-format", "&6&lReboot&7: &6<time>")
                  .replace("<time>", TimeUtils.formatIntoMMSS(Proton.getInstance().getAutoRebootHandler().getRebootSecondsRemaining()))
            );
         }

         VotePartyHandler votePartyHandler;
         if ((votePartyHandler = Foxtrot.getInstance().getVotePartyHandler()) != null
            && votePartyHandler.getCurrentVotes() >= votePartyHandler.getVotePartyRequirement() - 25) {
            scores.add(
               ChatColor.translateAlternateColorCodes(
                  '&',
                  sc("vote-party-format", "&6&lVote Party&7: &6<remaining> away")
                     .replace("<remaining>", String.valueOf(votePartyHandler.getVotePartyRequirement() - votePartyHandler.getCurrentVotes()))
               )
            );
         }

         if (this.getAbilityCooldownsScore(player) != null) {
            scores.addAll(this.getAbilityCooldownsScore(player));
         }

         if (AntiBlockup.cache.containsKey(player.getUniqueId())) {
            if (AntiBlockup.cache.get(player.getUniqueId()) < System.currentTimeMillis()) {
               AntiBlockup.cache.remove(player.getUniqueId());
            } else {
               scores.add(
                  sc("anti-blockup-format", "&d&lAnti-Blockup&7: &d<time>")
                     .replace(
                        "<time>", ScoreFunction.TIME_FANCY.apply((float)(AntiBlockup.cache.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000.0F)
                     )
               );
            }
         }

         if (Foxtrot.getInstance().getTeamfightModeMap().isTeamfight(player.getUniqueId())) {
            if (!scores.isEmpty()) {
               scores.add("&3&7&m--------------------");
            }

            OutpostHandler outpostHandler = Foxtrot.getInstance().getOutpostHandler();
            EndOutpost endOutpost = (EndOutpost)outpostHandler.findOutpost("End");
            NetherOutpost netherOutpost = (NetherOutpost)outpostHandler.findOutpost("Nether");
            if (FoxtrotTitleGetter.domain) {
               scores.add(
                  "&5End: &e"
                     + (endOutpost.getPercentage().get() == 0.0 ? ChatColor.YELLOW : ChatColor.GREEN)
                     + String.format("%.2f", endOutpost.getPercentage().get())
                     + "%"
               );
               scores.add("&6&7• &fStatus: &e" + endOutpost.getStatus().getDisplayName());
            } else {
               scores.add(
                  "&6Nether: &e"
                     + (netherOutpost.getPercentage().get() == 0.0 ? ChatColor.YELLOW : ChatColor.GREEN)
                     + String.format("%.2f", netherOutpost.getPercentage().get())
                     + "%"
               );
               scores.add("&7• &fStatus: &e" + netherOutpost.getStatus().getDisplayName());
            }

            if (team != null && team.getFocusedTeam() != null) {
               Team focusedTeam = team.getFocusedTeam();
               scores.add("&7▶ &d" + focusedTeam.getName() + ": &a" + focusedTeam.formatDTR());
            }
         }

         Location location;
         if (!Foxtrot.getInstance().getTeamfightModeMap().isTeamfight(player.getUniqueId())
            && this.showTimer(player, "treasure")
            && Foxtrot.getInstance().getTreasureCoveHandler() != null
            && TreasureCoveListener.SPAWN_IN != 0L
            && TreasureCoveListener.SPAWN_IN > System.currentTimeMillis()
            && (location = Foxtrot.getInstance().getTreasureCoveHandler().getCentralChest()) != null) {
            if (!scores.isEmpty()) {
               scores.add("&5&7&m--------------------");
            }

            scores.add("&6&lTreasure Cove");
            scores.add("&7A gift card");
            scores.add("&7drops in &f" + getTimerScore(TreasureCoveListener.SPAWN_IN));
            scores.add(ChatColor.YELLOW + "Location: &f" + location.getBlockX() + ", " + location.getBlockZ() + " &7[Nether]");
         }

         EnderDragon enderDragon = EndListener.ENDER_DRAGON;
         if (EndListener.ENDER_DRAGON != null && !enderDragon.isDead()) {
            Location location2 = enderDragon.getLocation();
            if (!scores.isEmpty()) {
               scores.add("&1&7&m--------------------");
            }

            scores.add("&5&lEnder Dragon");
            scores.add("&5┃ &fHealth: &d" + getDragonHealthPercent(enderDragon) + "%");
            scores.add("&5┃ &fLocation: &d" + location2.getBlockX() + ", " + location2.getBlockZ());
         }

         long difference2 = Foxtrot.getInstance().getCouponDropsHandler().getDropsIn() - System.currentTimeMillis();
         if (!Foxtrot.getInstance().getTeamfightModeMap().isTeamfight(player.getUniqueId())
            && Foxtrot.getInstance().getCouponDropsHandler().getDropsIn() != 0L
            && difference2 > 0L) {
            Location location3 = Foxtrot.getInstance().getCouponDropsHandler().getLocation().clone();
            if (!scores.isEmpty()) {
               scores.add("&d&7&m--------------------");
            }

            scores.add("&6&lDiscount Drop");
            scores.add("&7drops in &f" + getTimerScore(Foxtrot.getInstance().getCouponDropsHandler().getDropsIn()));
            scores.add(ChatColor.YELLOW + "Location: &f" + location3.getBlockX() + ", " + location3.getBlockZ());
         }

         Player king;
         if (Foxtrot.getInstance().getMapHandler().isKitMap()
            && KillTheKingCommand.king != null
            && (king = Foxtrot.getInstance().getServer().getPlayer(KillTheKingCommand.king)) != null) {
            if (!scores.isEmpty()) {
               scores.add("&6&7&m--------------------");
            }

            scores.add("&6&lKill The King");
            scores.add("&6┃ &fKing: &e" + king.getName());
            scores.add("&6┃ &fLocation: &e" + king.getLocation().getBlockX() + ", " + king.getLocation().getBlockZ());
            if (king.getWorld().getName().equalsIgnoreCase(player.getLocation().getWorld().getName())) {
               scores.add("&6┃ &fDistance: &e" + (int)Math.round(king.getLocation().distance(player.getLocation())) + "m");
            }
         }

         MiniEventsHandler miniEventsHandler = Foxtrot.getInstance().getMiniEventsHandler();
         if (!Foxtrot.getInstance().getTeamfightModeMap().isTeamfight(player.getUniqueId())
            && player.getWorld().getEnvironment() == Environment.NORMAL
            && miniEventsHandler != null
            && miniEventsHandler.getActiveEvent() != null) {
            MiniEvent miniEvent = miniEventsHandler.getActiveEvent();
            if (!scores.isEmpty()) {
               scores.add("&0&7&m--------------------");
            }

            scores.add("&6&l" + miniEvent.getObjective() + " Event");
            scores.add("&7ends in &f" + ScoreFunction.TIME_FANCY.apply((float)((miniEvent.getEndsAt() - System.currentTimeMillis()) / 1000L)));
            scores.add("&a/minievent");
         }

         CaveNiteHandler caveNiteHandler;
         if ((caveNiteHandler = Foxtrot.getInstance().getCaveNiteHandler()) != null && caveNiteHandler.getGameState() != CaveNiteState.INACTIVE) {
            if (!scores.isEmpty()) {
               scores.add("&1&7&m--------------------");
            }

            scores.addAll(caveNiteHandler.getScoreboardLines());
         }

         if (team != null && team.getFocusedTeam() != null && !Foxtrot.getInstance().getTeamfightModeMap().isTeamfight(player.getUniqueId())) {
            Team focusedTeam = team.getFocusedTeam();
            if (!scores.isEmpty()) {
               scores.add("&9&7&m--------------------");
            }

            Location hq = focusedTeam.getHQ();
            scores.add(sc("focus-team-format", "&6&lTeam: &e<name>").replace("<name>", ChatColor.stripColor(focusedTeam.getName())));
            if (hq != null) {
               scores.add(
                  sc("focus-home-format", "&6&lHome: &e<x>, <z>").replace("<x>", String.valueOf(hq.getBlockX())).replace("<z>", String.valueOf(hq.getBlockZ()))
               );
            }

            if (focusedTeam.getOwner() != null) {
               String dtrFormat = focusedTeam.formatDTR();
               if (Foxtrot.getInstance().getDTRDisplayMap().isHearts(player.getUniqueId())) {
                  int currentHearts = (int)Math.ceil(focusedTeam.getDTR());
                  dtrFormat = focusedTeam.getDTRColor().toString() + currentHearts + "❤" + focusedTeam.getDTRSuffix();
               }

               scores.add(sc("focus-dtr-format", "&6&lDTR: &e<dtr>").replace("<dtr>", dtrFormat));
               scores.add(sc("focus-online-format", "&6&lOnline: &e<count>").replace("<count>", String.valueOf(focusedTeam.getOnlineMembers().size())));
            }
         }

         ConquestGame conquest;
         if ((conquest = Foxtrot.getInstance().getConquestHandler().getGame()) != null) {
            if (scores.size() != 0) {
               scores.add("&e&7&m--------------------");
            }

            int i = 0;
            ArrayList<String> conquestScores = new ArrayList<>();

            for (int x = 0; x < 2; x++) {
               boolean first = true;
               StringBuilder text = new StringBuilder();

               for (int y = 0; y < 2; y++) {
                  ConquestCapzone zone = ConquestCapzone.values()[i];
                  Event event = Foxtrot.getInstance().getEventHandler().getEvent("Conquest-" + zone.getName());
                  if (event == null) {
                     i++;
                  } else if (!(event instanceof KOTH)) {
                     i++;
                  } else {
                     KOTH koth = (KOTH)event;
                     if (!first) {
                        text.append(" ");
                     }

                     long time = koth.getRemainingCapTime() <= 0 ? 30L : koth.getRemainingCapTime();
                     text.append(zone.getChatColor().toString()).append("⬛ ").append(ChatColor.YELLOW).append(TimeUtils.formatIntoMMSS((int)time));
                     i++;
                     first = !first;
                  }
               }

               if (!text.toString().equalsIgnoreCase("")) {
                  conquestScores.add(text.toString());
               }
            }

            int displayed = 0;

            for (Entry<ObjectId, Integer> entry : conquest.getTeamPoints().entrySet()) {
               if (entry.getValue() > 0) {
                  Team resolved = Foxtrot.getInstance().getTeamHandler().getTeam(entry.getKey());
                  if (resolved != null) {
                     if (displayed == 0) {
                        conquestScores.add(" ");
                     }

                     conquestScores.add(ChatColor.YELLOW + resolved.getName() + "&7: &e" + entry.getValue());
                     displayed++;
                  }

                  if (displayed == 3) {
                  }
               }
            }

            scores.addAll(conquestScores);
         }

         if (player.hasMetadata("STAFF_BOARD")) {
            if (!scores.isEmpty()) {
               scores.add("&f&7&m--------------------");
            }

            if (player.getName().equalsIgnoreCase("SimplyTrash")) {
               Integer[] array = ThreadingManager.getTickCounter().getTicksPerSecond();
               Integer last = array[array.length - 1];
               scores.add(ChatColor.AQUA + ChatColor.BOLD.toString() + "TPS" + ChatColor.GRAY + ": " + ChatColor.YELLOW + this.formatTPS(last.intValue()));
            }

            scores.add(
               ChatColor.YELLOW
                  + ChatColor.BOLD.toString()
                  + "Online"
                  + ChatColor.GRAY
                  + ": "
                  + ChatColor.YELLOW
                  + getOnlinePlayerCount()
                  + "/"
                  + Foxtrot.getInstance().getServer().getMaxPlayers()
                  + " &7("
                  + Universe.getInstance().getUniverseHandler().getPlayersOnNetwork()
                  + ")"
            );
            scores.add(
               ChatColor.DARK_AQUA
                  + ChatColor.BOLD.toString()
                  + "Vanished"
                  + ChatColor.GRAY
                  + ": "
                  + (ModHandler.INSTANCE.isInVanish(player.getUniqueId()) ? ChatColor.GREEN + "Yes" : ChatColor.YELLOW + "No")
            );
            if (!player.getName().equalsIgnoreCase("SimplyTrash")) {
               scores.add(
                  CC.BLUE
                     + CC.BOLD
                     + "Staff Chat"
                     + CC.GRAY
                     + ": "
                     + (Profiles.getInstance().hasStaffChatEnabled(player) ? CC.GREEN + "Yes" : CC.YELLOW + "No")
               );
            }
         }

         while (scores.size() > 13) {
            scores.remove(scores.getLast());
         }

         if (!scores.isEmpty()) {
            scores.addFirst(sc("header", "&a&7&m--------------------"));
            scores.add(" ");
            scores.add(getCyclingWebsiteLine());
            scores.add(sc("footer", "&b&7&m--------------------"));
         }

         return finishScores(scores);
      }
   }

   private static String getCyclingWebsiteLine() {
      return "§7§oComing Soon!";
   }

   public boolean showTimer(Player player, String timer) {
      if (player.hasMetadata("NO_TIMER") || player.getWorld().getEnvironment() != Environment.NORMAL) {
         return false;
      }

      if (timer.contains("ALL") || timer.contains("2x Points") || timer.contains("Double Gem")) {
         return true;
      }

      if (CustomTimerCreateCommand.isSOTWTimer() || Foxtrot.getInstance().getMapHandler().isKitMap()) {
         return true;
      }

      if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
         return true;
      }

      if (SpawnTagHandler.isTagged(player)) {
         return false;
      }

      if (!DTRBitmask.CITADEL.appliesAt(player.getLocation()) && !DTRBitmask.KOTH.appliesAt(player.getLocation())) {
         Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
         if (profile != null
            && profile.getActiveRank() != null
            && profile.getActiveRank().getName().equalsIgnoreCase(Foxtrot.getInstance().getConfig().getString("customRankName", "Partner"))) {
            return false;
         } else {
            return Foxtrot.getInstance().getTeamfightModeMap().isTeamfight(player.getUniqueId())
               ? false
               : Foxtrot.getInstance().getSaleTimersScoreboardMap().isSaleTimers(player.getUniqueId());
         }
      } else {
         return false;
      }
   }

   public List<String> getAbilityCooldownsScore(Player player) {
      if (Foxtrot.getInstance().getServerHandler().isTeams()) {
         return null;
      }

      if (Foxtrot.getInstance().getMapHandler().getAbilityHandler() == null) {
         return null;
      }

      if (!Foxtrot.getInstance().getAbilityCooldownsScoreboardMap().isScoreboard(player.getUniqueId())) {
         return null;
      }

      ArrayList<String> toReturn = new ArrayList<>();
      Map<String, Ability> abilities = Foxtrot.getInstance().getMapHandler().getAbilityHandler().getAbilities();
      if (abilities == null) {
         return null;
      }

      for (Ability ability : abilities.values()) {
         if (ability.hasCooldown(player, false)) {
            if (ability.getName().equalsIgnoreCase("Combo") && Combo.cache.containsKey(player.getUniqueId())) {
               int hits = Combo.cache.get(player.getUniqueId());
               toReturn.add(ability.getDisplayName() + ChatColor.GRAY + ": " + ChatColor.YELLOW + hits + " hits");
            } else {
               toReturn.add(
                  ability.getDisplayName()
                     + ChatColor.GRAY
                     + ": "
                     + ChatColor.YELLOW
                     + ScoreFunction.TIME_FANCY.apply((float)ability.getRemaining(player) / 1000.0F)
               );
            }
         }
      }

      if (NinjaStar.teleportCooldowns.containsKey(player.getUniqueId())) {
         if (NinjaStar.teleportCooldowns.get(player.getUniqueId()) < System.currentTimeMillis()) {
            NinjaStar.teleportCooldowns.remove(player.getUniqueId());
         } else {
            toReturn.add(
               "&b&lTeleport Item"
                  + ChatColor.GRAY
                  + ": "
                  + ChatColor.YELLOW
                  + ScoreFunction.TIME_FANCY.apply((float)(NinjaStar.teleportCooldowns.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000.0F)
            );
         }
      }

      return toReturn.isEmpty() ? null : toReturn;
   }

   public String getCustomTimerDisplay(Player player, String display, long time) {
      String timerScore = getTimerScore(time);
      String plainDisplay = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', display));
      if (plainDisplay.equalsIgnoreCase("SOTW")) {
         return CustomTimerCreateCommand.hasSOTWEnabled(player.getUniqueId())
            ? sc("sotw-enabled-format", "&4&l&mSOTW&4 ends in <time>").replace("<time>", timerScore)
            : sc("sotw-active-format", "&4&lSOTW &4ends in <time>").replace("<time>", timerScore);
      } else if (plainDisplay.equalsIgnoreCase("SOTW SALE")) {
         return ChatColor.translateAlternateColorCodes('&', "&a&lSOTW SALE&7: &a" + timerScore);
      } else if (plainDisplay.equalsIgnoreCase("dragon")) {
         return ChatColor.translateAlternateColorCodes('&', "&5&lDragon &5spawns in " + timerScore);
      } else {
         return this.showTimer(player, display) ? formatTimerLine(ChatColor.translateAlternateColorCodes('&', display), timerScore) : null;
      }
   }

   private static String formatTimerLine(String display, String time) {
      String suffix = "&7: &e" + time;
      int displayLength = Math.max(0, 47 - suffix.length());
      return limitScoreboardText(display, displayLength) + suffix;
   }

   private static String[] finishScores(LinkedList<String> scores) {
      for (int index = 0; index < scores.size(); index++) {
         scores.set(index, limitScoreboardText(scores.get(index), 47));
      }

      return new String[0];
   }

   private static String limitScoreboardText(String text, int maximumLength) {
      if (text != null && text.length() > maximumLength) {
         int end = maximumLength;
         if (end > 0 && isColorCodeMarker(text.charAt(end - 1))) {
            end--;
         }

         return text.substring(0, end);
      } else {
         return text;
      }
   }

   private static boolean isColorCodeMarker(char character) {
      return character == '&' || character == 167;
   }

   public String getAppleScore(Player player) {
      float diff;
      return GoldenAppleListener.getCrappleCooldown().containsKey(player.getUniqueId())
            && GoldenAppleListener.getCrappleCooldown().get(player.getUniqueId()) >= System.currentTimeMillis()
            && (diff = (float)(GoldenAppleListener.getCrappleCooldown().get(player.getUniqueId()) - System.currentTimeMillis())) >= 0.0F
         ? ScoreFunction.TIME_FANCY.apply(diff / 1000.0F)
         : null;
   }

   public String getHomeScore(Player player) {
      float diff;
      return ServerHandler.getHomeTimer().containsKey(player.getName())
            && ServerHandler.getHomeTimer().get(player.getName()) >= System.currentTimeMillis()
            && (diff = (float)(ServerHandler.getHomeTimer().get(player.getName()) - System.currentTimeMillis())) >= 0.0F
         ? ScoreFunction.TIME_FANCY.apply(diff / 1000.0F)
         : null;
   }

   public String getFStuckScore(Player player) {
      float diff;
      return TeamStuckCommand.getWarping().containsKey(player.getName())
            && (diff = (float)(TeamStuckCommand.getWarping().get(player.getName()) - System.currentTimeMillis())) >= 0.0F
         ? ScoreFunction.TIME_FANCY.apply(diff / 1000.0F)
         : null;
   }

   public String getLogoutScore(Player player) {
      Logout logout = ServerHandler.getTasks().get(player.getName());
      float diff;
      return logout != null && (diff = (float)(logout.getLogoutTime() - System.currentTimeMillis())) >= 0.0F
         ? ScoreFunction.TIME_FANCY.apply(diff / 1000.0F)
         : null;
   }

   public String getSpawnTagScore(Player player) {
      float diff;
      return SpawnTagHandler.isTagged(player) && (diff = (float)SpawnTagHandler.getTag(player)) >= 0.0F
         ? ScoreFunction.TIME_SIMPLE.apply(diff / 1000.0F)
         : null;
   }

   public String getEnderpearlScore(Player player) {
      float diff;
      return EnderpearlCooldownHandler.getEnderpearlCooldown().containsKey(player.getName())
            && EnderpearlCooldownHandler.getEnderpearlCooldown().get(player.getName()) >= System.currentTimeMillis()
            && (diff = (float)(EnderpearlCooldownHandler.getEnderpearlCooldown().get(player.getName()) - System.currentTimeMillis())) >= 0.0F
         ? ScoreFunction.TIME_FANCY.apply(diff / 1000.0F)
         : null;
   }

   public String getAbilityScore(Player player) {
      try {
         if (Foxtrot.getInstance().getMapHandler().getAbilityHandler() == null) {
            return null;
         }

         Map<UUID, Long> cooldowns = Foxtrot.getInstance().getMapHandler().getAbilityHandler().getGlobalCooldowns();
         if (cooldowns != null && cooldowns.containsKey(player.getUniqueId())) {
            Long cooldownEnd = cooldowns.get(player.getUniqueId());
            if (cooldownEnd != null && cooldownEnd >= System.currentTimeMillis()) {
               float diff = (float)(cooldownEnd - System.currentTimeMillis());
               return diff >= 0.0F ? ScoreFunction.TIME_FANCY.apply(diff / 1000.0F) : null;
            } else {
               return null;
            }
         } else {
            return null;
         }
      } catch (Exception e) {
         return null;
      }
   }

   public String getPvPTimerScore(Player player) {
      int secondsRemaining;
      return Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())
            && (secondsRemaining = Foxtrot.getInstance().getPvPTimerMap().getSecondsRemaining(player.getUniqueId())) >= 0
         ? ScoreFunction.TIME_SIMPLE.apply((float)secondsRemaining)
         : null;
   }

   public static String getTimerScore(long timer) {
      long diff = Math.max(0L, timer - System.currentTimeMillis());
      return ScoreFunction.TIME_FANCY.apply((float)diff / 1000.0F);
   }

   public String getArcherMarkScore(Player player) {
      long diff;
      return ArcherClass.isMarked(player) && (diff = ArcherClass.getMarkedPlayers().get(player.getName()) - System.currentTimeMillis()) > 0L
         ? ScoreFunction.TIME_FANCY.apply((float)diff / 1000.0F)
         : null;
   }

   public static String getEffectScore(Player player) {
      PvPClass pvpClass = PvPClassHandler.getPvPClass(player);
      float diff;
      return pvpClass != null
            && pvpClass.isEnergyBased()
            && Foxtrot.getInstance().getPvpClassHandler().getEnergyService().getLastEffectUsage().containsKey(player.getUniqueId())
            && Foxtrot.getInstance().getPvpClassHandler().getEnergyService().getLastEffectUsage().get(player.getUniqueId()) >= System.currentTimeMillis()
            && (
                  diff = (float)(
                     Foxtrot.getInstance().getPvpClassHandler().getEnergyService().getLastEffectUsage().get(player.getUniqueId()) - System.currentTimeMillis()
                  )
               )
               > 0.0F
         ? ScoreFunction.TIME_SIMPLE.apply(diff / 1000.0F)
         : null;
   }

   public String getEnergyScore(Player player) {
      PvPClass pvpClass = PvPClassHandler.getPvPClass(player);
      float energy;
      return pvpClass != null
            && pvpClass.isEnergyBased()
            && Foxtrot.getInstance().getPvpClassHandler().getEnergyService().getCache().containsKey(player.getUniqueId())
            && (energy = Foxtrot.getInstance().getPvpClassHandler().getEnergyService().getCache().get(player.getUniqueId())) > 0.0F
         ? String.valueOf(energy)
         : null;
   }

   public String getClassUltimateEnergyScore(Player player) {
      float ultimate;
      return PvPClassHandler.getUltimate().containsKey(player.getUniqueId()) && (ultimate = PvPClassHandler.getUltimate().get(player.getUniqueId())) > 0.0F
         ? String.valueOf(ultimate)
         : null;
   }

   public String getGoppleScore(Player player) {
      float diff;
      return Foxtrot.getInstance().getOppleMap().isOnCooldown(player.getUniqueId())
            && Foxtrot.getInstance().getOppleMap().getCooldown(player.getUniqueId()) >= System.currentTimeMillis()
            && (diff = (float)(Foxtrot.getInstance().getOppleMap().getCooldown(player.getUniqueId()) - System.currentTimeMillis())) >= 0.0F
         ? ScoreFunction.TIME_FANCY.apply(diff / 1000.0F)
         : null;
   }

   public String getClassWarmupScore(Player player) {
      float diff;
      return PvPClassHandler.getWarmupTasks().containsKey(player.getName())
            && (diff = (float)(PvPClassHandler.getWarmupTasks().get(player.getName()).getTime() - System.currentTimeMillis())) >= 0.0F
         ? ScoreFunction.TIME_FANCY.apply(diff / 1000.0F)
         : null;
   }

   public String getBooster(Player player) {
      float diff;
      return Foxtrot.getInstance().getNetworkBoosterHandler().isFrenzy()
            && (
                  diff = (float)(
                     Foxtrot.getInstance().getNetworkBoosterHandler().findBooster("Frenzy").getActivatedAt()
                        + TimeUnit.HOURS.toMillis(1L)
                        - System.currentTimeMillis()
                  )
               )
               >= 0.0F
         ? ScoreFunction.TIME_FANCY.apply(diff / 1000.0F)
         : null;
   }

   public String getGemBooster(Player player) {
      long left = Foxtrot.getInstance().getGemBoosterMap().getGemBoosterMillisLeft(player);
      return left == 0L ? null : TimeUtils.formatIntoHHMMSS((int)(left / 1000L));
   }

   private String formatTPS(double tps) {
      return (tps > 18.0 ? ChatColor.GREEN : (tps > 16.0 ? ChatColor.YELLOW : ChatColor.YELLOW))
         + (tps > 20.0 ? "*" : "")
         + Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
   }

   private static int getDragonHealthPercent(EnderDragon dragon) {
      try {
         Method getHealth = LivingEntity.class.getMethod("getHealth");
         Method getMaxHealth = LivingEntity.class.getMethod("getMaxHealth");
         double health = ((Number)getHealth.invoke(dragon)).doubleValue();
         double maxHealth = ((Number)getMaxHealth.invoke(dragon)).doubleValue();
         return maxHealth <= 0.0 ? 0 : (int)Math.round(health * 100.0 / maxHealth);
      } catch (Exception e) {
         return 0;
      }
   }

   private static int getOnlinePlayerCount() {
      try {
         Method m = Bukkit.class.getMethod("getOnlinePlayers");
         Object result = m.invoke(null);
         if (result instanceof Collection) {
            return ((Collection)result).size();
         }

         if (result instanceof Object[]) {
            return ((Object[])result).length;
         }
      } catch (Exception var2) {
      }

      return 0;
   }
}
