package net.frozenorb.foxtrot.gameplay.extra.stats;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.param.ParameterType;
import cc.fyre.proton.serialization.LocationSerializer;
import cc.fyre.proton.util.UUIDUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.gameplay.extra.stats.command.StatsTopCommand;
import net.frozenorb.foxtrot.team.Team;
import net.minecraft.util.com.google.common.collect.Iterables;
import net.minecraft.util.com.google.common.collect.Lists;
import net.minecraft.util.com.google.common.collect.Maps;
import net.minecraft.util.com.google.common.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class StatsHandler implements Listener {
   private final Map<UUID, StatsEntry> stats = Maps.newConcurrentMap();
   private final Map<Location, Integer> leaderboardSigns = Maps.newHashMap();
   private final Map<Location, Integer> leaderboardHeads = Maps.newHashMap();
   private final Map<Location, Integer> leaderboardNPCs = Maps.newHashMap();
   private final Map<Location, StatsTopCommand.StatsObjective> objectives = Maps.newHashMap();
   private final Map<Integer, UUID> topKills = Maps.newConcurrentMap();
   private boolean firstUpdateComplete = false;

   public StatsHandler() {
      Proton.getInstance()
         .runRedisCommand(
            redis -> {
               for (String key : redis.keys(Bukkit.getServerName() + ":stats2:*")) {
                  UUID uuid = UUID.fromString(key.split(":")[2]);
                  StatsEntry entry = Proton.PLAIN_GSON.fromJson(redis.get(key), StatsEntry.class);
                  this.stats.put(uuid, entry);
               }

               Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Kit Map] Loaded " + this.stats.size() + " stats.");
               if (redis.exists(Bukkit.getServerName() + ":leaderboardSigns")) {
                  for (String sign : (List<String>)Proton.PLAIN_GSON
                     .fromJson(redis.get(Bukkit.getServerName() + ":leaderboardSigns"), (new TypeToken<List<String>>() {}).getType())) {
                     Location location = LocationSerializer.deserialize((BasicDBObject)JSON.parse(sign.split("----")[0]));
                     int place = Integer.parseInt(sign.split("----")[1]);
                     this.leaderboardSigns.put(location, place);
                  }

                  Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Kit Map] Loaded " + this.leaderboardSigns.size() + " leaderboard signs.");
               }

               if (redis.exists(Bukkit.getServerName() + ":leaderboardHeads")) {
                  for (String sign : (List<String>)Proton.PLAIN_GSON
                     .fromJson(redis.get(Bukkit.getServerName() + ":leaderboardHeads"), (new TypeToken<List<String>>() {}).getType())) {
                     Location location = LocationSerializer.deserialize((BasicDBObject)JSON.parse(sign.split("----")[0]));
                     int place = Integer.parseInt(sign.split("----")[1]);
                     this.leaderboardHeads.put(location, place);
                  }

                  Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[Kit Map] Loaded " + this.leaderboardHeads.size() + " leaderboard heads.");
               }

               return null;
            }
         );
      Bukkit.getPluginManager().registerEvents(this, Foxtrot.getInstance());
      Proton.getInstance()
         .getCommandHandler()
         .registerParameterType(StatsTopCommand.StatsObjective.class, new ParameterType<StatsTopCommand.StatsObjective>() {
            public StatsTopCommand.StatsObjective transform(CommandSender sender, String source) {
               for (StatsTopCommand.StatsObjective objective : StatsTopCommand.StatsObjective.values()) {
                  if (source.equalsIgnoreCase(objective.getName())) {
                     return objective;
                  }

                  for (String alias : objective.getAliases()) {
                     if (source.equalsIgnoreCase(alias)) {
                        return objective;
                     }
                  }
               }

               sender.sendMessage(ChatColor.RED + "Objective '" + source + "' not found.");
               return null;
            }

            @Override
            public List<String> tabComplete(Player sender, Set<String> flags, String source) {
               List<String> completions = Lists.newArrayList();

               for (StatsTopCommand.StatsObjective objective : StatsTopCommand.StatsObjective.values()) {
                  if (StringUtils.startsWithIgnoreCase(objective.getName().replace(" ", ""), source)) {
                     completions.add(objective.getName().replace(" ", ""));
                  } else {
                     for (String alias : objective.getAliases()) {
                        if (StringUtils.startsWithIgnoreCase(alias, source)) {
                           completions.add(alias);
                           break;
                        }
                     }
                  }
               }

               return completions;
            }
         });
      Bukkit.getScheduler().scheduleAsyncRepeatingTask(Foxtrot.getInstance(), this::save, 600L, 600L);
      Bukkit.getScheduler().scheduleAsyncRepeatingTask(Foxtrot.getInstance(), this::updateTopKillsMap, 600L, 600L);
      Bukkit.getScheduler().scheduleSyncRepeatingTask(Foxtrot.getInstance(), this::updatePhysicalLeaderboards, 1200L, 1200L);
   }

   public void save() {
      Proton.getInstance()
         .runRedisCommand(
            redis -> {
               List<String> serializedSigns = this.leaderboardSigns
                  .entrySet()
                  .stream()
                  .map(entryx -> LocationSerializer.serialize((Location)entryx.getKey()).toString() + "----" + entryx.getValue())
                  .collect(Collectors.toList());
               List<String> serializedHeads = this.leaderboardHeads
                  .entrySet()
                  .stream()
                  .map(entryx -> LocationSerializer.serialize((Location)entryx.getKey()).toString() + "----" + entryx.getValue())
                  .collect(Collectors.toList());
               List<String> serializedNPCs = this.leaderboardNPCs
                  .entrySet()
                  .stream()
                  .map(entryx -> LocationSerializer.serialize((Location)entryx.getKey()).toString() + "----" + entryx.getValue())
                  .collect(Collectors.toList());
               List<String> serializedObjectives = this.objectives
                  .entrySet()
                  .stream()
                  .map(
                     entryx -> LocationSerializer.serialize((Location)entryx.getKey()).toString()
                        + "----"
                        + ((StatsTopCommand.StatsObjective)entryx.getValue()).name()
                  )
                  .collect(Collectors.toList());
               redis.set(Bukkit.getServerName() + ":leaderboardSigns", Proton.PLAIN_GSON.toJson(serializedSigns));
               redis.set(Bukkit.getServerName() + ":leaderboardHeads", Proton.PLAIN_GSON.toJson(serializedHeads));
               redis.set(Foxtrot.MONGO_DB_NAME + ":leaderboardNPCs", Proton.PLAIN_GSON.toJson(serializedNPCs));
               redis.set(Bukkit.getServerName() + ":objectives", Proton.PLAIN_GSON.toJson(serializedObjectives));

               for (StatsEntry entry : this.stats.values()) {
                  redis.set(Bukkit.getServerName() + ":stats2:" + entry.getOwner().toString(), Proton.PLAIN_GSON.toJson(entry));
               }

               return null;
            }
         );
   }

   public StatsEntry getStats(Player player) {
      return this.getStats(player.getUniqueId());
   }

   public StatsEntry getStats(String name) {
      return this.getStats(UUIDUtils.uuid(name));
   }

   public StatsEntry getStats(UUID uuid) {
      this.stats.putIfAbsent(uuid, new StatsEntry(uuid));
      return this.stats.get(uuid);
   }

   private void updateTopKillsMap() {
      UUID oldFirstPlace = this.topKills.get(1);
      UUID oldSecondPlace = this.topKills.get(2);
      UUID oldThirdPlace = this.topKills.get(3);
      StatsEntry newFirst = this.get(StatsTopCommand.StatsObjective.KILLS, 1);
      StatsEntry newSecond = this.get(StatsTopCommand.StatsObjective.KILLS, 2);
      StatsEntry newThird = this.get(StatsTopCommand.StatsObjective.KILLS, 3);
      UUID newFirstPlace = newFirst == null ? null : newFirst.getOwner();
      UUID newSecondPlace = newSecond == null ? null : newSecond.getOwner();
      UUID newThirdPlace = newThird == null ? null : newThird.getOwner();
      if (!CustomTimerCreateCommand.isSOTWTimer() && this.firstUpdateComplete) {
         if (newFirstPlace != null && oldFirstPlace != null && newFirstPlace != oldFirstPlace) {
            Bukkit.broadcastMessage(
               ChatColor.translateAlternateColorCodes(
                  '&', "&6" + UUIDUtils.name(newFirstPlace) + "&f has surpassed &6" + UUIDUtils.name(oldFirstPlace) + "&f for &6#1&f in kills!"
               )
            );
         }

         if (newSecondPlace != null && oldSecondPlace != null && newSecondPlace != oldSecondPlace) {
            Bukkit.broadcastMessage(
               ChatColor.translateAlternateColorCodes(
                  '&', "&6" + UUIDUtils.name(newSecondPlace) + "&f has surpassed &6" + UUIDUtils.name(oldSecondPlace) + "&f for &6#2&f in kills!"
               )
            );
         }

         if (newThirdPlace != null && oldThirdPlace != null && newThirdPlace != oldThirdPlace) {
            Bukkit.broadcastMessage(
               ChatColor.translateAlternateColorCodes(
                  '&', "&6" + UUIDUtils.name(newThirdPlace) + "&f has surpassed &6" + UUIDUtils.name(oldThirdPlace) + "&f for &6#3&f in kills!"
               )
            );
         }
      }

      this.updateTop(1, newFirstPlace);
      this.updateTop(2, newSecondPlace);
      this.updateTop(3, newThirdPlace);
      this.firstUpdateComplete = true;
   }

   private void updateTop(int i, UUID id) {
      if (id == null) {
         this.topKills.remove(i);
      } else {
         this.topKills.put(i, id);
      }
   }

   public void updatePhysicalLeaderboards() {
      Iterator<Entry<Location, Integer>> iterator = this.leaderboardSigns.entrySet().iterator();

      while (iterator.hasNext()) {
         Entry<Location, Integer> entry = iterator.next();
         StatsEntry stats = this.get(this.objectives.get(entry.getKey()), entry.getValue());
         if (stats != null) {
            if (!(entry.getKey().getBlock().getState() instanceof Sign)) {
               iterator.remove();
            } else {
               Sign sign = (Sign)entry.getKey().getBlock().getState();
               sign.setLine(0, this.trim(ChatColor.RED.toString() + ChatColor.BOLD + this.beautify(entry.getKey())));
               sign.setLine(1, this.trim(ChatColor.AQUA.toString() + ChatColor.UNDERLINE + UUIDUtils.name(stats.getOwner())));
               sign.setLine(3, ChatColor.DARK_GRAY.toString() + stats.get(this.objectives.get(entry.getKey())));
               sign.update();
            }
         }
      }

      Iterator<Entry<Location, Integer>> headIterator = this.leaderboardHeads.entrySet().iterator();

      while (headIterator.hasNext()) {
         Entry<Location, Integer> entry = headIterator.next();
         StatsEntry stats = this.get(this.objectives.get(entry.getKey()), entry.getValue());
         if (stats != null) {
            if (!(entry.getKey().getBlock().getState() instanceof Skull)) {
               headIterator.remove();
            } else {
               Skull skull = (Skull)entry.getKey().getBlock().getState();
               skull.setOwner(UUIDUtils.name(stats.getOwner()));
               skull.update();
            }
         }
      }

      if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
      }
   }

   private String beautify(Location location) {
      StatsTopCommand.StatsObjective objective = this.objectives.get(location);
      switch (objective) {
         case DEATHS:
            return "Top Deaths";
         case HIGHEST_KILLSTREAK:
            return "Top KillStrk";
         case KD:
            return "Top KDR";
         case KILLS:
            return "Top Kills";
         default:
            return "Error";
      }
   }

   private String trim(String name) {
      return name.length() <= 15 ? name : name.substring(0, 15);
   }

   public StatsEntry get(StatsTopCommand.StatsObjective objective, int place) {
      Map<StatsEntry, Number> base = Maps.newHashMap();

      for (StatsEntry entry : this.stats.values()) {
         base.put(entry, entry.get(objective));
      }

      TreeMap<StatsEntry, Number> ordered = new TreeMap<>((first, second) -> first.get(objective).doubleValue() >= second.get(objective).doubleValue() ? -1 : 1);
      ordered.putAll(base);
      Map<StatsEntry, String> leaderboards = Maps.newLinkedHashMap();
      int index = 0;

      for (Entry<StatsEntry, Number> entry : ordered.entrySet()) {
         if (entry.getKey().getDeaths() >= 10 || objective != StatsTopCommand.StatsObjective.KD) {
            leaderboards.put(entry.getKey(), entry.getValue() + "");
            if (++index == place + 1) {
               break;
            }
         }
      }

      try {
         return (StatsEntry)Iterables.get(leaderboards.keySet(), place - 1);
      } catch (Exception e) {
         return null;
      }
   }

   public void clearAll() {
      this.stats.clear();
      Bukkit.getScheduler().scheduleAsyncDelayedTask(Foxtrot.getInstance(), this::save);
   }

   public void clearLeaderboards() {
      this.leaderboardHeads.clear();
      this.leaderboardSigns.clear();
      this.objectives.clear();
      Bukkit.getScheduler().scheduleAsyncDelayedTask(Foxtrot.getInstance(), this::save);
   }

   public Map<StatsEntry, String> getLeaderboards(StatsTopCommand.StatsObjective objective, int range) {
      if (objective != StatsTopCommand.StatsObjective.KD) {
         Map<StatsEntry, Number> base = Maps.newHashMap();

         for (StatsEntry entry : this.stats.values()) {
            base.put(entry, entry.get(objective));
         }

         TreeMap<StatsEntry, Number> ordered = new TreeMap<>(
            (first, second) -> first.get(objective).doubleValue() >= second.get(objective).doubleValue() ? -1 : 1
         );
         ordered.putAll(base);
         Map<StatsEntry, String> leaderboards = Maps.newLinkedHashMap();
         int index = 0;

         for (Entry<StatsEntry, Number> entry : ordered.entrySet()) {
            leaderboards.put(entry.getKey(), entry.getValue() + "");
            if (++index == range) {
               break;
            }
         }

         return leaderboards;
      } else {
         Map<StatsEntry, Double> base = Maps.newHashMap();

         for (StatsEntry entry : this.stats.values()) {
            base.put(entry, entry.getKD());
         }

         TreeMap<StatsEntry, Double> ordered = new TreeMap<>((first, second) -> first.getKD() > second.getKD() ? -1 : 1);
         ordered.putAll(base);
         Map<StatsEntry, String> leaderboards = Maps.newLinkedHashMap();
         int index = 0;

         for (Entry<StatsEntry, Double> entry : ordered.entrySet()) {
            if (entry.getKey().getDeaths() >= 10) {
               String kd = Team.DTR_FORMAT.format((double)entry.getKey().getKills() / entry.getKey().getDeaths());
               leaderboards.put(entry.getKey(), kd);
               if (++index == range) {
                  break;
               }
            }
         }

         return leaderboards;
      }
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onBlockBreak(BlockBreakEvent event) {
      Player player = event.getPlayer();
      if (this.leaderboardHeads.containsKey(event.getBlock().getLocation())) {
         this.leaderboardHeads.remove(event.getBlock().getLocation());
         player.sendMessage(ChatColor.YELLOW + "Removed this skull from leaderboards.");
         Bukkit.getScheduler().scheduleAsyncDelayedTask(Foxtrot.getInstance(), this::save);
      }

      if (this.leaderboardSigns.containsKey(event.getBlock().getLocation())) {
         this.leaderboardSigns.remove(event.getBlock().getLocation());
         player.sendMessage(ChatColor.YELLOW + "Removed this sign from leaderboards.");
         Bukkit.getScheduler().scheduleAsyncDelayedTask(Foxtrot.getInstance(), this::save);
      }
   }

   public Map<Location, Integer> getLeaderboardSigns() {
      return this.leaderboardSigns;
   }

   public Map<Location, Integer> getLeaderboardHeads() {
      return this.leaderboardHeads;
   }

   public Map<Location, Integer> getLeaderboardNPCs() {
      return this.leaderboardNPCs;
   }

   public Map<Location, StatsTopCommand.StatsObjective> getObjectives() {
      return this.objectives;
   }

   public Map<Integer, UUID> getTopKills() {
      return this.topKills;
   }
}
