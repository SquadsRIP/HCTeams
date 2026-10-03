package net.frozenorb.foxtrot.gameplay.pvpclasses.mastery;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class MasteryUpgradeHandler {
   private Foxtrot instance;
   private Map<UUID, Map<String, Integer>> cache = new HashMap<>();
   private File file;
   private FileConfiguration data;

   public MasteryUpgradeHandler(Foxtrot instance) {
      this.instance = instance;
      this.loadData();
   }

   public void loadData() {
      this.file = new File(Foxtrot.getInstance().getDataFolder(), "data/mastery-upgrades.yml");
      this.data = YamlConfiguration.loadConfiguration(this.file);
      if (!this.file.exists()) {
         try {
            this.file.createNewFile();
         } catch (IOException e) {
            e.printStackTrace();
         }
      } else if (this.data.get("upgrades") != null) {
         this.instance
            .getServer()
            .getScheduler()
            .runTaskLater(this.instance, () -> this.data.getConfigurationSection("upgrades").getKeys(false).forEach(it -> {
               UUID uuid = UUID.fromString(it);
               HashMap<String, Integer> toReturn = new HashMap<>();

               for (String key : this.data.getConfigurationSection("upgrades." + it).getKeys(false)) {
                  int score = this.data.getInt("upgrades." + it + "." + key);
                  toReturn.put(key, score);
               }

               this.cache.put(uuid, toReturn);
            }), 5L);
      }
   }

   public void saveData() {
      this.data.getValues(false).forEach((key, value) -> this.data.set(key, null));

      for (Entry<UUID, Map<String, Integer>> entry : this.cache.entrySet()) {
         for (Entry<String, Integer> entry2 : entry.getValue().entrySet()) {
            this.data.set("upgrades." + entry.getKey().toString() + "." + entry2.getKey(), entry2.getValue());
         }
      }

      try {
         this.data.save(this.file);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void addUpgrade(Player player, String pvpClass, int score) {
      this.cache.putIfAbsent(player.getUniqueId(), new HashMap<>());
      Map<String, Integer> entry = this.cache.get(player.getUniqueId());
      int currentScore = entry.getOrDefault(pvpClass, 0);
      MasteryUpgrades currentUpgrade = MasteryUpgrades.getUpgradeByScore(currentScore);
      int var8;
      entry.put(pvpClass, var8 = currentScore + score);
      MasteryUpgrades newUpgrade = MasteryUpgrades.getUpgradeByScore(var8);
      if (!currentUpgrade.equals(newUpgrade)) {
         this.instance.getGemMap().addGems(player.getUniqueId(), newUpgrade.gemPrize, true);
         if (newUpgrade.equals(MasteryUpgrades.LEVEL_FIVE)) {
            this.instance
               .getServer()
               .dispatchCommand(this.instance.getServer().getConsoleSender(), "chest give Treasure " + player.getName() + newUpgrade.treasureChests);
         }

         player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
         player.sendMessage("");
         player.sendMessage(CC.translate("&6&lMastery Upgrades"));
         player.sendMessage(CC.translate("&fYou have leveled up to level " + newUpgrade.displayName + "!"));
         player.sendMessage(CC.translate("&aYou have received " + newUpgrade.gemPrize + " Gems!"));
         player.sendMessage("");
      }
   }

   public Map<UUID, Map<String, Integer>> getCache() {
      return this.cache;
   }

   public File getFile() {
      return this.file;
   }

   public FileConfiguration getData() {
      return this.data;
   }
}
