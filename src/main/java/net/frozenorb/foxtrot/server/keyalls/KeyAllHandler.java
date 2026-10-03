package net.frozenorb.foxtrot.server.keyalls;

import cc.fyre.proton.Proton;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.keyalls.command.parameter.KeyAllParameter;
import net.frozenorb.foxtrot.server.keyalls.task.KeyAllService;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KeyAllHandler {
   private File file;
   private FileConfiguration data;
   private Map<String, KeyAll> cache = new HashMap<>();

   public KeyAllHandler(Foxtrot instance) {
      new KeyAllService(instance, this).runTaskTimer(instance, 20L, 20L);
      Proton.getInstance().getCommandHandler().registerParameterType(KeyAll.class, new KeyAllParameter());
      this.loadData();
   }

   public void loadData() {
      this.file = new File(Foxtrot.getInstance().getDataFolder(), "data/keyalls.yml");
      this.data = YamlConfiguration.loadConfiguration(this.file);
      if (!this.file.exists()) {
         try {
            this.file.createNewFile();
         } catch (IOException e) {
            e.printStackTrace();
         }
      } else if (this.data.getConfigurationSection("keyalls") != null) {
         for (String path : this.data.getConfigurationSection("keyalls").getKeys(false)) {
            KeyAll keyAll = new KeyAll(path);
            ArrayList<ItemStack> items = new ArrayList<>();
            if (this.data.getConfigurationSection("keyalls." + path + ".items") != null) {
               for (String itemPath : this.data.getConfigurationSection("keyalls." + path + ".items").getKeys(false)) {
                  items.add(this.data.getItemStack("keyalls." + path + ".items." + itemPath));
               }
            }

            keyAll.setItems(items);
            keyAll.setEnd(this.data.getLong("keyalls." + path + ".end"));
            keyAll.setGiving(this.data.getBoolean("keyalls." + path + ".giving"));
            keyAll.setGiveAllTime(this.data.getLong("keyalls." + path + ".giveAllTime"));
            keyAll.setDisplayName(this.data.getString("keyalls." + path + ".displayName"));
            keyAll.setScoreboardDisplay(this.data.getString("keyalls." + path + ".scoreboardDisplay"));
            ArrayList<UUID> uuids = new ArrayList<>();
            if (this.data.getConfigurationSection("keyalls." + path + ".redeemed") != null) {
               for (String uuid : this.data.getConfigurationSection("keyalls." + path + ".redeemed").getKeys(false)) {
                  uuids.add(UUID.fromString(uuid));
               }
            }

            keyAll.setRedeemed(uuids);
            this.cache.put(path, keyAll);
         }
      }
   }

   public void saveData() {
      this.data.getValues(false).forEach((key, value) -> this.data.set(key, null));

      for (Entry<String, KeyAll> entry : this.cache.entrySet()) {
         KeyAll keyAll = entry.getValue();
         int i = 0;

         for (ItemStack item : keyAll.getItems()) {
            this.data.set("keyalls." + entry.getKey() + ".items.REWARD_" + ++i, item.clone());
         }

         this.data.set("keyalls." + entry.getKey() + ".end", keyAll.getEnd());
         this.data.set("keyalls." + entry.getKey() + ".giving", keyAll.isGiving());
         this.data.set("keyalls." + entry.getKey() + ".giveAllTime", keyAll.getGiveAllTime());
         this.data.set("keyalls." + entry.getKey() + ".displayName", keyAll.getDisplayName());
         this.data.set("keyalls." + entry.getKey() + ".scoreboardDisplay", keyAll.getScoreboardDisplay());

         for (UUID uuid : keyAll.getRedeemed()) {
            this.data.set("keyalls." + entry.getKey() + ".redeemed." + uuid.toString(), true);
         }
      }

      try {
         this.data.save(this.file);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public KeyAll findKeyAll(String input) {
      return this.cache.entrySet().stream().filter(it -> it.getKey().equalsIgnoreCase(input)).map(Entry::getValue).findFirst().orElse(null);
   }

   public List<KeyAll> findScoreboardKeyalls() {
      ArrayList<KeyAll> toReturn = new ArrayList<>();

      for (KeyAll keyAll : this.cache.values()) {
         if (keyAll.getGiveAllTime() != 0L
            && keyAll.getGiveAllTime() >= System.currentTimeMillis()
            && keyAll.getGiveAllTime() - System.currentTimeMillis() <= TimeUnit.MINUTES.toMillis(10L)) {
            toReturn.add(keyAll);
         }
      }

      return toReturn;
   }

   public List<KeyAll> findAvailableKeyAlls(Player player) {
      ArrayList<KeyAll> toReturn = new ArrayList<>();

      for (KeyAll value : this.cache.values()) {
         if (value.isGiving()
            && !value.getItems().isEmpty()
            && !value.getRedeemed().contains(player.getUniqueId())
            && value.getEnd() >= System.currentTimeMillis()) {
            toReturn.add(value);
         }
      }

      return toReturn;
   }

   public File getFile() {
      return this.file;
   }

   public FileConfiguration getData() {
      return this.data;
   }

   public Map<String, KeyAll> getCache() {
      return this.cache;
   }
}
