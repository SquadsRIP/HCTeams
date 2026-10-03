package net.frozenorb.foxtrot.gameplay.kitmap.gem.menu.upgrades;

import java.beans.ConstructorProperties;
import java.util.Map;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.upgrades.Upgrades;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class UpgradableKit {
   private final String kitName;
   private final ItemStack icon;
   private final Map<Material, Upgrades> upgrades;

   @ConstructorProperties({"kitName", "icon", "upgrades"})
   public UpgradableKit(String kitName, ItemStack icon, Map<Material, Upgrades> upgrades) {
      this.kitName = kitName;
      this.icon = icon;
      this.upgrades = upgrades;
   }

   public String getKitName() {
      return this.kitName;
   }

   public ItemStack getIcon() {
      return this.icon;
   }

   public Map<Material, Upgrades> getUpgrades() {
      return this.upgrades;
   }
}
