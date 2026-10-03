package net.frozenorb.foxtrot.gameplay.kitmap.kits;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.gem.menu.upgrades.GemShopUpgradeKitMenu;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.upgrades.Upgrades;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class Kit {
   private String name;
   private String original;
   private ItemStack[] inventoryContents = new ItemStack[36];
   private ItemStack[] armorContents = new ItemStack[4];
   private static final String[] ROMAN_NUMERALS = new String[]{"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};

   public Kit(String name) {
      this.name = name;
   }

   public Kit(DefaultKit original) {
      if (original != null) {
         this.name = original.getName();
         this.original = original.getName();
         this.inventoryContents = Arrays.copyOf(original.getInventoryContents(), original.getInventoryContents().length);
         this.armorContents = Arrays.copyOf(original.getArmorContents(), original.getArmorContents().length);
      }
   }

   public void setName(String name) {
      this.name = name;
   }

   public void apply(Player player) {
      ItemStack[] inventory = new ItemStack[this.inventoryContents.length];

      for (int i = 0; i < inventory.length; i++) {
         inventory[i] = this.inventoryContents[i] == null ? null : this.inventoryContents[i].clone();
      }

      ItemStack[] armor = new ItemStack[this.armorContents.length];

      for (int i = 0; i < this.armorContents.length; i++) {
         armor[i] = this.armorContents[i] == null ? null : this.armorContents[i].clone();
      }

      this.doKitUpgradesMagic(player, inventory);
      this.doKitUpgradesMagic(player, armor);
      player.getInventory().setContents(inventory);
      player.getInventory().setArmorContents(armor);
      player.updateInventory();
   }

   private void doKitUpgradesMagic(Player player, ItemStack[] items) {
      Map<Material, Upgrades> map = Foxtrot.getInstance().getMapHandler().getKitUpgradesHandler().getUpgrades(player);
      if (map != null) {
         for (ItemStack item : items) {
            if (item != null) {
               doKitUpgradesMagic(map, item);
            }
         }
      }
   }

   public static void doKitUpgradesMagic(Map<Material, Upgrades> map, ItemStack item) {
      Upgrades upgrades = map.get(item.getType());
      if (upgrades != null) {
         for (Enchantment enchantment : upgrades.getEnchantmentList()) {
            item.addUnsafeEnchantment(enchantment, GemShopUpgradeKitMenu.LEVEL_MAP.get(enchantment.getName()));
         }

         for (String enchantment : upgrades.getCustomEnchantmentList()) {
            int level = GemShopUpgradeKitMenu.LEVEL_MAP.get(enchantment);
            String numeral = ROMAN_NUMERALS[level - 1];
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.getLore();
            if (lore == null) {
               lore = new ArrayList<>();
            }

            lore.add(CC.RED + enchantment + " " + numeral);
            meta.setLore(lore);
            item.setItemMeta(meta);
         }
      }
   }

   public void update(PlayerInventory inventory) {
      this.inventoryContents = (ItemStack[])inventory.getContents().clone();
      this.armorContents = (ItemStack[])inventory.getArmorContents().clone();
   }

   public int countHeals() {
      return ItemUtils.countStacksMatching(this.inventoryContents, ItemUtils.INSTANT_HEAL_POTION_PREDICATE);
   }

   public int countDebuffs() {
      return ItemUtils.countStacksMatching(this.inventoryContents, ItemUtils.DEBUFF_POTION_PREDICATE);
   }

   public int countFood() {
      return ItemUtils.countStacksMatching(this.inventoryContents, ItemUtils.EDIBLE_PREDICATE);
   }

   public int countPearls() {
      return ItemUtils.countStacksMatching(this.inventoryContents, v -> v.getType() == Material.ENDER_PEARL);
   }

   public DefaultKit getOriginal() {
      return this.original == null ? null : Foxtrot.getInstance().getMapHandler().getKitManager().getDefaultKit(this.original);
   }

   public ItemStack[] getAllContents() {
      ItemStack[] items = new ItemStack[40];
      System.arraycopy(this.inventoryContents, 0, items, 0, this.inventoryContents.length);
      System.arraycopy(this.armorContents, 0, items, this.inventoryContents.length, this.armorContents.length);
      return items;
   }

   public String getName() {
      return this.name;
   }

   public ItemStack[] getInventoryContents() {
      return this.inventoryContents;
   }

   public ItemStack[] getArmorContents() {
      return this.armorContents;
   }

   public void setOriginal(String original) {
      this.original = original;
   }

   public void setInventoryContents(ItemStack[] inventoryContents) {
      this.inventoryContents = inventoryContents;
   }

   public void setArmorContents(ItemStack[] armorContents) {
      this.armorContents = armorContents;
   }
}
