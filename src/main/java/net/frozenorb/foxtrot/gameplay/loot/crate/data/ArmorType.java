package net.frozenorb.foxtrot.gameplay.loot.crate.data;

import java.beans.ConstructorProperties;
import org.bukkit.Material;

public enum ArmorType {
   DIAMOND(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS),
   GOLD(Material.GOLD_HELMET, Material.GOLD_CHESTPLATE, Material.GOLD_LEGGINGS, Material.GOLD_BOOTS),
   LEATHER(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS),
   CHAINMAIL(Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS);

   Material helmet;
   Material chestplate;
   Material leggings;
   Material boots;

   @ConstructorProperties({"helmet", "chestplate", "leggings", "boots"})
   ArmorType(Material helmet, Material chestplate, Material leggings, Material boots) {
      this.helmet = helmet;
      this.chestplate = chestplate;
      this.leggings = leggings;
      this.boots = boots;
   }

   public Material getHelmet() {
      return this.helmet;
   }

   public Material getChestplate() {
      return this.chestplate;
   }

   public Material getLeggings() {
      return this.leggings;
   }

   public Material getBoots() {
      return this.boots;
   }
}
