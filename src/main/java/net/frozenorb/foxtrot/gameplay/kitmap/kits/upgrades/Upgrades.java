package net.frozenorb.foxtrot.gameplay.kitmap.kits.upgrades;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.enchantments.Enchantment;

public class Upgrades {
   private final List<Enchantment> enchantmentList = new ArrayList<>();
   private final List<String> customEnchantmentList = new ArrayList<>();

   public Upgrades protection() {
      this.enchantmentList.add(Enchantment.PROTECTION_ENVIRONMENTAL);
      return this;
   }

   public Upgrades sharpness() {
      this.enchantmentList.add(Enchantment.DAMAGE_ALL);
      return this;
   }

   public Upgrades power() {
      this.enchantmentList.add(Enchantment.ARROW_DAMAGE);
      return this;
   }

   public Upgrades efficiency() {
      this.enchantmentList.add(Enchantment.DIG_SPEED);
      return this;
   }

   public Upgrades fireResistance() {
      this.customEnchantmentList.add("FireResistance");
      return this;
   }

   public Upgrades glowing() {
      this.customEnchantmentList.add("Glowing");
      return this;
   }

   public Upgrades repair() {
      this.customEnchantmentList.add("HellForged");
      return this;
   }

   public Upgrades saturation() {
      this.customEnchantmentList.add("Implants");
      return this;
   }

   public Upgrades bubble() {
      this.customEnchantmentList.add("Mermaid");
      return this;
   }

   public Upgrades recover() {
      this.customEnchantmentList.add("Recover");
      return this;
   }

   public Upgrades speed() {
      this.customEnchantmentList.add("Speed");
      return this;
   }

   public List<Enchantment> getEnchantmentList() {
      return this.enchantmentList;
   }

   public List<String> getCustomEnchantmentList() {
      return this.customEnchantmentList;
   }
}
