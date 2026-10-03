package net.frozenorb.foxtrot.persist.maps;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.persist.PersistMap;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PvPTimerMap extends PersistMap<Integer> {
   public PvPTimerMap() {
      super("PvPTimers", "PvPTimer", false);
      (new BukkitRunnable() {
            @Override
            public void run() {
               for (Player player : PvPTimerMap.getOnlinePlayersList()) {
                  if (PvPTimerMap.this.hasTimer(player.getUniqueId()) && !DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
                     int newValue = PvPTimerMap.this.getValue(player.getUniqueId()) - 1;
                     if (newValue % 60 == 0) {
                        int minutes = newValue / 60;
                        if (minutes <= 0) {
                           player.removeMetadata("PVP_TIMER_BYPASS", Foxtrot.getInstance());
                           player.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Your protection has expired!");
                        } else {
                           player.sendMessage(
                              ChatColor.translateAlternateColorCodes(
                                 '&', "&aYou have &a&l" + minutes + " &r&aminute" + (minutes == 1 ? "" : "s") + " of protection remaining."
                              )
                           );
                        }
                     }

                     PvPTimerMap.this.updateValueAsync(player.getUniqueId(), newValue);
                  }
               }
            }
         })
         .runTaskTimerAsynchronously(Foxtrot.getInstance(), 20L, 20L);
   }

   public String getRedisValue(Integer time) {
      return String.valueOf(time);
   }

   public Integer getJavaObject(String str) {
      return Integer.parseInt(str);
   }

   public Object getMongoValue(Integer time) {
      return time;
   }

   public void removeTimer(UUID update) {
      this.updateValueAsync(update, 0);
      Foxtrot.getInstance().getStartingPvPTimerMap().set(update, false);
   }

   public void createTimer(UUID update, int seconds) {
      this.updateValueAsync(update, seconds);
   }

   public void createStartingTimer(UUID update, int seconds) {
      this.createTimer(update, seconds);
      Foxtrot.getInstance().getStartingPvPTimerMap().set(update, true);
   }

   public boolean hasTimer(UUID check) {
      return this.getSecondsRemaining(check) > 0;
   }

   public int getSecondsRemaining(UUID check) {
      if (!Foxtrot.getInstance().getServerHandler().isPreEOTW() && !Foxtrot.getInstance().getMapHandler().isKitMap()) {
         return this.contains(check) ? this.getValue(check) : 0;
      } else {
         return 0;
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
}
