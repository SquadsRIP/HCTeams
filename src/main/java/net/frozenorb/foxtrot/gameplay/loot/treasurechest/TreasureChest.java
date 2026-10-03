package net.frozenorb.foxtrot.gameplay.loot.treasurechest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.treasurechest.reward.TreasureChestReward;
import net.frozenorb.foxtrot.util.PersistableLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class TreasureChest {
   private String id;
   private int slot;
   private int maxOpened;
   private String displayName;
   private Material material;
   private Location centralLocation;
   private List<Location> chests = new ArrayList<>();
   private List<TreasureChestReward> rewards = new ArrayList<>();
   private Map<UUID, Integer> cache = new HashMap<>();

   public TreasureChest(String id) {
      this.id = id;
   }

   public void loadLocations() {
      File file = new File(Foxtrot.getInstance().getTreasureChestHandler().getFolder(), this.id + ".yml");
      YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
      if (!file.exists()) {
         try {
            file.createNewFile();
         } catch (IOException e) {
            e.printStackTrace();
         }
      } else {
         this.slot = data.getInt("slot");
         this.displayName = data.getString("displayName");
         this.material = Material.valueOf(data.getString("material"));
         this.maxOpened = data.getInt("maxOpened");
         if (data.contains("centralLocation")) {
            this.centralLocation = ((PersistableLocation)data.get("centralLocation")).getLocation();
         }

         if (data.get("location") != null) {
            data.getConfigurationSection("location").getKeys(false).forEach(arg_0 -> this.lambda$loadLocations$0((FileConfiguration)data, arg_0));
         }

         if (data.get("players") != null) {
            for (String path : data.getConfigurationSection("players").getKeys(false)) {
               UUID uuid = UUID.fromString(path);
               this.cache.put(uuid, data.getInt("players." + path));
            }
         }
      }
   }

   public void loadRewards() {
      File file = new File(Foxtrot.getInstance().getTreasureChestHandler().getFolder(), this.id + ".yml");
      YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
      if (!file.exists()) {
         try {
            file.createNewFile();
         } catch (IOException e) {
            e.printStackTrace();
         }
      } else if (data.get("rewards") != null) {
         Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> this.lambda$loadRewards$2(data), 5L);
      }
   }

   public void saveCrate(File file, FileConfiguration data) {
      data.getValues(false).forEach((key, value) -> data.set(key, null));
      int i = 0;
      if (this.centralLocation != null) {
         data.set("centralLocation", new PersistableLocation(this.centralLocation));
      }

      data.set("displayName", this.displayName);
      data.set("slot", this.slot);
      data.set("maxOpened", this.maxOpened);
      data.set("material", this.material.name());

      for (Location location : this.chests) {
         data.set("location.location_" + ++i, new PersistableLocation(location));
      }

      for (Entry entry : this.cache.entrySet()) {
         data.set("players." + ((UUID)entry.getKey()).toString(), entry.getValue());
      }

      for (TreasureChestReward treasureChestReward : this.rewards) {
         data.set("rewards.reward_" + ++i + ".itemStack", treasureChestReward.getItemStack());
         data.set("rewards.reward_" + i + ".chance", treasureChestReward.getChance());
         data.set("rewards.reward_" + i + ".command", treasureChestReward.getCommand());
         data.set("rewards.reward_" + i + ".giveItem", treasureChestReward.isGrantItem());
         data.set("rewards.reward_" + i + ".broadcast", treasureChestReward.isBroadcast());
      }

      try {
         data.save(file);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public String getId() {
      return this.id;
   }

   public int getSlot() {
      return this.slot;
   }

   public void setSlot(int slot) {
      this.slot = slot;
   }

   public int getMaxOpened() {
      return this.maxOpened;
   }

   public void setMaxOpened(int maxOpened) {
      this.maxOpened = maxOpened;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public void setDisplayName(String displayName) {
      this.displayName = displayName;
   }

   public Material getMaterial() {
      return this.material;
   }

   public void setMaterial(Material material) {
      this.material = material;
   }

   public Location getCentralLocation() {
      return this.centralLocation;
   }

   public void setCentralLocation(Location centralLocation) {
      this.centralLocation = centralLocation;
   }

   public List<Location> getChests() {
      return this.chests;
   }

   public List<TreasureChestReward> getRewards() {
      return this.rewards;
   }

   public Map<UUID, Integer> getCache() {
      return this.cache;
   }

   private void lambda$loadRewards$2(FileConfiguration data) {
      data.getConfigurationSection("rewards")
         .getKeys(false)
         .forEach(
            it -> this.rewards
               .add(
                  new TreasureChestReward(
                     data.getItemStack("rewards." + it + ".itemStack"),
                     data.getDouble("rewards." + it + ".chance"),
                     data.getString("rewards." + it + ".command"),
                     data.getBoolean("rewards." + it + ".giveItem"),
                     data.getBoolean("rewards." + it + ".broadcast", false)
                  )
               )
         );
   }

   private void lambda$loadLocations$0(FileConfiguration data, String it) {
      this.chests.add(((PersistableLocation)data.get("location." + it)).getLocation());
   }
}
