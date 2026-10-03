package net.frozenorb.foxtrot.persist.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.persist.PersistMap;
import net.frozenorb.foxtrot.persist.maps.event.SyncPlaytimeEvent;
import net.frozenorb.foxtrot.team.Team;
import net.minecraft.util.com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlaytimeMap extends PersistMap<Long> {
   private Map<UUID, Long> joinDate = new HashMap<>();
   private Map<UUID, Long> pendingReward = Maps.newConcurrentMap();
   private static final long HOUR_IN_MS = 3600000L;

   public PlaytimeMap() {
      super("PlayerPlaytimes", "Playtime");
      AtomicInteger ai = new AtomicInteger(30);
      Bukkit.getScheduler()
         .runTaskTimerAsynchronously(
            Foxtrot.getInstance(),
            () -> {
               ai.decrementAndGet();
               if (ai.get() <= 0) {
                  for (Player player : Bukkit.getOnlinePlayers()) {
                     Bukkit.getPluginManager().callEvent(new SyncPlaytimeEvent(player));
                  }

                  ai.set(30);
               }

               List<UUID> toRemove = new ArrayList<>();
               List<UUID> toUpdate = new ArrayList<>();

               for (Player player : Bukkit.getOnlinePlayers()) {
                  if (!this.pendingReward.containsKey(player.getUniqueId())) {
                     this.pendingReward.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1L));
                  }
               }

               for (UUID uuid : this.pendingReward.keySet()) {
                  long time = this.pendingReward.get(uuid);
                  if (Bukkit.getPlayer(uuid) == null) {
                     toRemove.add(uuid);
                  } else if (System.currentTimeMillis() >= time) {
                     toUpdate.add(uuid);
                  }
               }

               for (UUID uuid : toRemove) {
                  this.pendingReward.remove(uuid);
               }

               for (UUID uuid : toUpdate) {
                  Player player = Bukkit.getPlayer(uuid);
                  if (player != null) {
                     Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
                     if (team != null) {
                        team.addPlaytimeGems(5);
                        team.sendMessage(
                           ChatColor.GREEN
                              + "Your team received 5 gems thanks to "
                              + ChatColor.AQUA
                              + ChatColor.BOLD
                              + player.getName()
                              + ChatColor.GREEN
                              + "'s play-time."
                        );
                     }

                     this.pendingReward.put(uuid, System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1L));
                  }
               }
            },
            0L,
            20L
         );
   }

   public String getRedisValue(Long time) {
      return String.valueOf(time);
   }

   public Long getJavaObject(String str) {
      return Long.parseLong(str);
   }

   public Object getMongoValue(Long time) {
      return time.intValue();
   }

   public void playerJoined(UUID update) {
      this.joinDate.put(update, System.currentTimeMillis());
      if (!this.contains(update)) {
         this.updateValueAsync(update, 0L);
      }
   }

   public void playerQuit(UUID update, boolean async) {
      this.joinDate.putIfAbsent(update, System.currentTimeMillis());
      if (async) {
         this.updateValueAsync(update, this.getPlaytime(update) + (System.currentTimeMillis() - this.joinDate.get(update)) / 1000L);
      } else {
         this.updateValueSync(update, this.getPlaytime(update) + (System.currentTimeMillis() - this.joinDate.get(update)) / 1000L);
      }
   }

   public long getCurrentSession(UUID check) {
      return this.joinDate.containsKey(check) ? System.currentTimeMillis() - this.joinDate.get(check) : 0L;
   }

   public long getPlaytime(UUID check) {
      return this.contains(check) ? this.getValue(check) : 0L;
   }

   public boolean hasPlayed(UUID check) {
      return this.contains(check);
   }

   public void setPlaytime(UUID update, long playtime) {
      this.updateValueSync(update, playtime);
   }

   private long calculateNextRewardTime(UUID uuid) {
      return System.currentTimeMillis() + (3600000L - (this.getPlaytime(uuid) * 1000L + this.getCurrentSession(uuid)) % 7200000L);
   }
}
