package net.frozenorb.foxtrot.gameplay.kitmap.gem.menu.upgrades;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.util.ItemBuilder;
import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.upgrades.Upgrades;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class EnchantmentUpgradeButton extends Button {
   private final Material material;
   private final Enchantment enchantment;
   private final int cost;
   private final ItemStack icon;

   @Override
   public void clicked(Player player, int slot, ClickType clickType) {
      Map<Material, Upgrades> map = Foxtrot.getInstance().getMapHandler().getKitUpgradesHandler().getOrComputeUpgrades(player);
      Upgrades upgrades = map.get(this.material);
      if (upgrades != null && upgrades.getEnchantmentList().contains(this.enchantment)) {
         player.sendMessage(CC.YELLOW + "You already unlocked this upgrade!");
      } else if (!Foxtrot.getInstance().getGemMap().removeGems(player.getUniqueId(), this.cost)) {
         player.sendMessage(CC.YELLOW + "You need at least " + this.cost + " gems to buy this upgrade!");
      } else {
         map.computeIfAbsent(this.material, mat -> new Upgrades()).getEnchantmentList().add(this.enchantment);
         player.sendMessage(CC.GREEN + "Upgrade successfully purchased!");
      }
   }

   @Override
   public ItemStack getButtonItem(Player player) {
      ItemBuilder esmee = ItemBuilder.copyOf(this.icon.clone()).addToLore("").addToLore(CC.GOLD + "Cost: " + CC.GREEN + this.cost + " Gems");
      Map<Material, Upgrades> map = Foxtrot.getInstance().getMapHandler().getKitUpgradesHandler().getOrComputeUpgrades(player);
      Upgrades upgrades = map.get(this.material);
      esmee.addToLore("");
      if (upgrades != null && upgrades.getEnchantmentList().contains(this.enchantment)) {
         esmee.addToLore(CC.YELLOW + "You already unlocked this upgrade!");
      } else {
         esmee.addToLore(CC.GREEN + "Click to purchase this upgrade!");
      }

      return esmee.build();
   }

   @Override
   public String getName(Player player) {
      return null;
   }

   @Override
   public List<String> getDescription(Player player) {
      return null;
   }

   @Override
   public Material getMaterial(Player player) {
      return null;
   }

   @ConstructorProperties({"material", "enchantment", "cost", "icon"})
   public EnchantmentUpgradeButton(Material material, Enchantment enchantment, int cost, ItemStack icon) {
      this.material = material;
      this.enchantment = enchantment;
      this.cost = cost;
      this.icon = icon;
   }
}
