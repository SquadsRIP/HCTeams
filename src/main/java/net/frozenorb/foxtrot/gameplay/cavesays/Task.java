package net.frozenorb.foxtrot.gameplay.cavesays;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.extra.stats.StatsEntry;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class Task implements Listener {
   private final Map<UUID, Integer> points = new HashMap<>();
   private int airdrops;

   public abstract String getTaskID();

   public abstract String getTaskDisplayName();

   public abstract int getPointsToWin();

   public void addProgress(Player player) {
      if (player.getGameMode() != GameMode.CREATIVE) {
         this.points.put(player.getUniqueId(), this.points.getOrDefault(player.getUniqueId(), 0) + 1);
         if (this.points.get(player.getUniqueId()) >= this.getPointsToWin()) {
            this.deactivate(player);
         } else {
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
            player.sendMessage(
               ChatColor.translate(
                  "&6You have obtained a point towards this task! (&f" + this.points.get(player.getUniqueId()) + "/" + this.getPointsToWin() + "&6)"
               )
            );
         }
      }
   }

   public void activate() {
      Foxtrot instance = Foxtrot.getInstance();
      this.airdrops = ThreadLocalRandom.current().nextInt(0, 100) <= 50 ? 2 : 3;
      instance.getServer().getPluginManager().registerEvents(this, instance);

      for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
         onlinePlayer.sendMessage("");
         onlinePlayer.sendMessage(ChatColor.translate("&7███████"));
         onlinePlayer.sendMessage(ChatColor.translate("&7█&4█████&7█ &4&lSquads Says"));
         onlinePlayer.sendMessage(ChatColor.translate("&7█&4█&7█████ &7First person to"));
         onlinePlayer.sendMessage(ChatColor.translate("&7█&4█&7█████ &f" + this.getTaskDisplayName()));
         onlinePlayer.sendMessage(ChatColor.translate("&7█&4█&7█████ &cwill receive"));
         onlinePlayer.sendMessage(ChatColor.translate("&7█&4█████&7█ &b&l" + this.airdrops + " Airdrops"));
         onlinePlayer.sendMessage(ChatColor.translate("&7███████"));
         onlinePlayer.sendMessage("");
      }
   }

   public void deactivate(Player winner) {
      Foxtrot instance = Foxtrot.getInstance();
      CaveSaysHandler caveSaysHandler = instance.getCaveSaysHandler();
      this.points.clear();
      caveSaysHandler.setActiveTask(null);
      HandlerList.unregisterAll(this);
      if (winner == null) {
         instance.getServer().broadcastMessage("");
         instance.getServer().broadcastMessage(ChatColor.translate("&4&lSquads Says"));
         instance.getServer().broadcastMessage(ChatColor.translate(this.getTaskDisplayName() + " &7has been forcefully cancelled."));
         instance.getServer().broadcastMessage("");
      } else {
         winner.playSound(winner.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
         instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), "airdrops give " + winner.getName() + " " + this.airdrops);
         instance.getServer().broadcastMessage("");
         instance.getServer().broadcastMessage(ChatColor.translate("&4&lSquads Says"));
         instance.getServer().broadcastMessage("");
         instance.getServer()
            .broadcastMessage(
               ChatColor.translate(winner.getDisplayName() + " &7has completed the task first and received &b&l" + this.airdrops + "x Airdrops&7.")
            );
         instance.getServer().broadcastMessage("");
         StatsEntry statsEntry = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(winner.getUniqueId());
         if (statsEntry != null) {
            statsEntry.addCaveSaysCompleted();
         }
      }
   }

   public Map<UUID, Integer> getPoints() {
      return this.points;
   }

   public int getAirdrops() {
      return this.airdrops;
   }

   public void setAirdrops(int airdrops) {
      this.airdrops = airdrops;
   }
}
