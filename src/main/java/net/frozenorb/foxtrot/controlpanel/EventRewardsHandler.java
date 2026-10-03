package net.frozenorb.foxtrot.controlpanel;

import java.io.File;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class EventRewardsHandler {
   private static FileConfiguration config;

   public static void loadConfig() {
      File file = new File(Foxtrot.getInstance().getDataFolder(), "schedule.yml");
      if (!file.exists()) {
         Foxtrot.getInstance().saveResource("schedule.yml", false);
      }

      config = YamlConfiguration.loadConfiguration(file);
   }

   public static FileConfiguration getConfig() {
      if (config == null) {
         loadConfig();
      }

      return config;
   }

   public static void giveKothRewards(Player capturer, String kothName) {
      giveRewards(capturer, kothName, "koth");
   }

   public static void giveCitadelRewards(Player capturer) {
      giveRewards(capturer, "Citadel", "citadel");
   }

   public static void giveConquestRewards(Player capturer) {
      giveRewards(capturer, "Conquest", "conquest");
   }

   private static void giveRewards(Player capturer, String eventName, String rewardType) {
      if (capturer != null) {
         FileConfiguration cfg = getConfig();
         String basePath = "rewards." + rewardType;
         int crates = cfg.getInt(basePath + ".crates", 3);
         String crateType = cfg.getString(basePath + ".crate-type", "KOTH");
         int points = cfg.getInt(basePath + ".points", 25);

         for (String cmd : cfg.getStringList(basePath + ".commands")) {
            String formatted = cmd.replace("{player}", capturer.getName()).replace("{faction}", getFactionName(capturer));
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), formatted);
         }

         Bukkit.broadcastMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + eventName);
         Bukkit.broadcastMessage(ChatColor.GRAY + "Captured by " + ChatColor.WHITE + capturer.getName());
         Bukkit.broadcastMessage(
            ChatColor.GRAY + "Rewards: " + ChatColor.RED + crates + "x " + crateType + " Crates" + ChatColor.GRAY + ", " + ChatColor.RED + points + " Points"
         );
         Foxtrot.getInstance()
            .getLogger()
            .info("[EventRewards] Gave " + capturer.getName() + " rewards for " + eventName + ": " + crates + " crates, " + points + " points");
      }
   }

   private static String getFactionName(Player player) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
      return team != null ? team.getName() : "";
   }
}
