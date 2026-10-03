package net.frozenorb.foxtrot.gameplay.kitmap.gem;

import java.util.HashMap;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.gameplay.kitmap.gem.listener.GemRewardListeners;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class GemHandler {
   public static final String DOUBLE_GEM_PREFIX = "&2&lDouble Gem";
   private final Map<Material, Double> oreChances = new HashMap<>();

   public GemHandler() {
      Bukkit.getPluginManager().registerEvents(new GemRewardListeners(), Foxtrot.getInstance());
   }

   public void loadChances() {
      this.oreChances.clear();
      FileConfiguration config = Foxtrot.getInstance().getConfiguration("gems.yml");
      if (config.contains("gems")) {
         for (String key : config.getConfigurationSection("gems").getKeys(false)) {
            this.oreChances.put(Material.valueOf(key), config.getDouble("gems." + key));
         }
      }

      this.oreChances.putIfAbsent(Material.STONE, 0.1);
      this.oreChances.putIfAbsent(Material.COAL_ORE, 0.5);
      this.oreChances.putIfAbsent(Material.IRON_ORE, 1.0);
      this.oreChances.putIfAbsent(Material.GOLD_ORE, 2.0);
      this.oreChances.putIfAbsent(Material.REDSTONE_ORE, 0.5);
      this.oreChances.putIfAbsent(Material.LAPIS_ORE, 5.0);
      this.oreChances.putIfAbsent(Material.DIAMOND_ORE, 5.0);
      this.oreChances.putIfAbsent(Material.EMERALD_ORE, 15.0);
   }

   public static boolean isDoubleGem() {
      return CustomTimerCreateCommand.getCustomTimers().containsKey("&2&lDouble Gem");
   }

   public Map<Material, Double> getOreChances() {
      return this.oreChances;
   }
}
