package net.frozenorb.foxtrot.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpawnTagHandler {
   private static Map<String, Long> spawnTags = new ConcurrentHashMap<>();

   public static void removeTag(Player player) {
      spawnTags.remove(player.getName());
   }

   public static void addOffensiveSeconds(Player player, int seconds) {
      addSeconds(player, seconds);
   }

   public static void addPassiveSeconds(Player player, int seconds) {
      if (Foxtrot.getInstance().getServerHandler().isPassiveTagEnabled()) {
         addSeconds(player, seconds);
      }
   }

   private static void addSeconds(Player player, int seconds) {
      if (!Foxtrot.getInstance().getDeathbanArenaHandler().isDeathbanArena(player)) {
         if (!DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
            if (Foxtrot.getInstance().getMapHandler().getGameHandler() == null
               || !Foxtrot.getInstance().getMapHandler().getGameHandler().isOngoingGame()
               || !Foxtrot.getInstance().getMapHandler().getGameHandler().getOngoingGame().isPlayingOrSpectating(player.getUniqueId())) {
               if (Foxtrot.getInstance().getMapHandler().getGameHandler() == null
                  || !Foxtrot.getInstance().getMapHandler().getGameHandler().isOngoingGame()
                  || !Foxtrot.getInstance().getMapHandler().getGameHandler().getOngoingGame().isPlayingOrSpectating(player.getUniqueId())) {
                  if (isTagged(player)) {
                     int secondsTaggedFor = (int)((spawnTags.get(player.getName()) - System.currentTimeMillis()) / 1000L);
                     int newSeconds = Math.min(secondsTaggedFor + seconds, getMaxTagTime(player));
                     spawnTags.put(player.getName(), System.currentTimeMillis() + newSeconds * 1000L);
                  } else {
                     player.sendMessage(ChatColor.RED + "You have been spawn-tagged for " + ChatColor.RED + seconds + ChatColor.RED + " seconds!");
                     spawnTags.put(player.getName(), System.currentTimeMillis() + seconds * 1000L);
                  }
               }
            }
         }
      }
   }

   public static long getTag(Player player) {
      return spawnTags.get(player.getName()) - System.currentTimeMillis();
   }

   public static boolean isTagged(Player player) {
      return player == null ? false : spawnTags.containsKey(player.getName()) && spawnTags.get(player.getName()) > System.currentTimeMillis();
   }

   public static int getMaxTagTime(Player player) {
      return Foxtrot.getInstance().getConfig().getInt("spawnTagSeconds", 30);
   }

   public static Map<String, Long> getSpawnTags() {
      return spawnTags;
   }
}
