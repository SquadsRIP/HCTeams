package net.frozenorb.foxtrot.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public final class SignShopFeedback {
   private static final Map<UUID, Map<Location, Integer>> tasks = new HashMap<>();

   private SignShopFeedback() {
   }

   public static void show(Player player, Sign sign, int line, String text) {
      Location location = sign.getLocation();
      String[] lines = (String[])sign.getLines().clone();
      lines[line] = text;
      player.sendSignChange(location, lines);
      UUID playerId = player.getUniqueId();
      Map<Location, Integer> playerTasks = tasks.computeIfAbsent(playerId, key -> new HashMap<>());
      BukkitScheduler scheduler = Foxtrot.getInstance().getServer().getScheduler();
      Integer previousTask = playerTasks.remove(location);
      if (previousTask != null) {
         scheduler.cancelTask(previousTask);
      }

      int taskId = scheduler.scheduleSyncDelayedTask(
         Foxtrot.getInstance(),
         () -> {
            playerTasks.remove(location);
            if (playerTasks.isEmpty()) {
               tasks.remove(playerId);
            }

            if (player.isOnline()
               && player.getWorld().equals(location.getWorld())
               && location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
               BlockState current = location.getBlock().getState();
               if (current instanceof Sign) {
                  player.sendSignChange(location, ((Sign)current).getLines());
               }
            }
         },
         60L
      );
      playerTasks.put(location, taskId);
   }

   public static void clear(UUID playerId) {
      Map<Location, Integer> playerTasks = tasks.remove(playerId);
      if (playerTasks != null) {
         BukkitScheduler scheduler = Foxtrot.getInstance().getServer().getScheduler();

         for (Integer taskId : playerTasks.values()) {
            scheduler.cancelTask(taskId);
         }
      }
   }
}
