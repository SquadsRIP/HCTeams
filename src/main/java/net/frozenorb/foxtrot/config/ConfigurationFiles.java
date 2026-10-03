package net.frozenorb.foxtrot.config;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class ConfigurationFiles {
   private static final Map<String, List<String>> FILE_ROOTS;
   private final Foxtrot plugin;
   private volatile Map<String, YamlConfiguration> configurations = Collections.emptyMap();

   public ConfigurationFiles(Foxtrot plugin) {
      this.plugin = plugin;
   }

   public void initialize() throws IOException, InvalidConfigurationException {
      FileConfiguration legacy = this.plugin.getConfig();

      for (Entry<String, List<String>> entry : FILE_ROOTS.entrySet()) {
         File file = new File(this.plugin.getDataFolder(), entry.getKey());
         if (!file.exists()) {
            this.plugin.saveResource(entry.getKey(), false);
            YamlConfiguration created = new YamlConfiguration();
            created.load(file);
            boolean migrated = false;

            for (String root : entry.getValue()) {
               if (legacy.contains(root)) {
                  copyRoot(legacy, created, root);
                  migrated = true;
               }
            }

            if (migrated) {
               created.save(file);
               this.plugin.getLogger().info("Migrated existing settings into " + entry.getKey() + ".");
            }
         }
      }

      this.reload();
   }

   public void reload() throws IOException, InvalidConfigurationException {
      LinkedHashMap<String, YamlConfiguration> loaded = new LinkedHashMap<>();

      for (String fileName : FILE_ROOTS.keySet()) {
         YamlConfiguration configuration = new YamlConfiguration();
         configuration.load(new File(this.plugin.getDataFolder(), fileName));
         SquadsText.configuration(configuration, fileName);
         loaded.put(fileName, configuration);
      }

      this.configurations = Collections.unmodifiableMap(loaded);
   }

   public FileConfiguration get(String fileName) {
      YamlConfiguration configuration = this.configurations.get(fileName);
      if (configuration == null) {
         throw new IllegalArgumentException("Unknown configuration file: " + fileName);
      } else {
         return configuration;
      }
   }

   public void save(String fileName) throws IOException {
      YamlConfiguration configuration = this.configurations.get(fileName);
      if (configuration == null) {
         throw new IllegalArgumentException("Unknown configuration file: " + fileName);
      }

      configuration.save(new File(this.plugin.getDataFolder(), fileName));
   }

   private static void copyRoot(FileConfiguration source, YamlConfiguration target, String root) {
      ConfigurationSection section = source.getConfigurationSection(root);
      if (section == null) {
         target.set(root, source.get(root));
      } else {
         for (String child : section.getKeys(true)) {
            if (!section.isConfigurationSection(child)) {
               target.set(root + "." + child, section.get(child));
            }
         }
      }
   }

   static {
      LinkedHashMap<String, List<String>> roots = new LinkedHashMap<>();
      roots.put("branding.yml", Arrays.asList("branding", "serverName", "networkWebsite", "statsRoot", "links", "scoreboard", "tab"));
      roots.put("messages.yml", Arrays.asList("messages", "broadcasts"));
      roots.put("map.yml", Collections.singletonList("map"));
      roots.put("gameplay.yml", Arrays.asList("gameplay", "tasks", "world-settings", "sotw", "conquestWinPoints"));
      roots.put("potions.yml", Collections.singletonList("potions"));
      roots.put("classes.yml", Arrays.asList("pvpClasses", "minerWorld"));
      roots.put("gems.yml", Collections.singletonList("gems"));
      roots.put("tips.yml", Collections.singletonList("tips"));
      roots.put("trivia.yml", Collections.singletonList("trivia"));
      roots.put("reclaims.yml", Collections.singletonList("reclaims"));
      FILE_ROOTS = Collections.unmodifiableMap(roots);
   }
}
