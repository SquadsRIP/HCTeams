package net.frozenorb.foxtrot.team.upgrade.effects;

import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

public enum PurchaseableEffects {
   SPEED(
      ChatColor.AQUA + ChatColor.BOLD.toString() + "Speed II",
      Arrays.asList(ChatColor.GRAY + "Purchase the Speed II", ChatColor.GRAY + "passive effect for your claim."),
      Material.SUGAR,
      11,
      15,
      PotionEffectType.SPEED
   ),
   RESISTANCE(
      ChatColor.WHITE + ChatColor.BOLD.toString() + "Resistance I",
      Arrays.asList(ChatColor.GRAY + "Purchase the Resistance I", ChatColor.GRAY + "passive effect for your claim."),
      Material.IRON_INGOT,
      12,
      25,
      PotionEffectType.DAMAGE_RESISTANCE
   ),
   STRENGTH(
      ChatColor.YELLOW + ChatColor.BOLD.toString() + "Strength I",
      Arrays.asList(ChatColor.GRAY + "Purchase Strength I", ChatColor.GRAY + "passive effect for your claim."),
      Material.BLAZE_POWDER,
      13,
      50,
      PotionEffectType.INCREASE_DAMAGE
   ),
   FIRE_RESISTANCE(
      ChatColor.GOLD + ChatColor.BOLD.toString() + "Fire Resistance I",
      Arrays.asList(ChatColor.GRAY + "Purchase the Fire Resistance I", ChatColor.GRAY + "passive effect for your claim."),
      Material.MAGMA_CREAM,
      14,
      10,
      PotionEffectType.FIRE_RESISTANCE
   ),
   REGENERATION(
      ChatColor.YELLOW + ChatColor.BOLD.toString() + "Regeneration I",
      Arrays.asList(ChatColor.GRAY + "Purchase the Regeneration I", ChatColor.GRAY + "passive effect for your claim."),
      Material.GHAST_TEAR,
      15,
      25,
      PotionEffectType.REGENERATION
   );

   String displayName;
   List<String> description;
   Material material;
   int slot;
   int cost;
   PotionEffectType potionEffectType;

   @ConstructorProperties({"displayName", "description", "material", "slot", "cost", "potionEffectType"})
   PurchaseableEffects(String displayName, List<String> description, Material material, int slot, int cost, PotionEffectType potionEffectType) {
      this.displayName = displayName;
      this.description = description;
      this.material = material;
      this.slot = slot;
      this.cost = cost;
      this.potionEffectType = potionEffectType;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public List<String> getDescription() {
      return this.description;
   }

   public Material getMaterial() {
      return this.material;
   }

   public int getSlot() {
      return this.slot;
   }

   public int getCost() {
      return this.cost;
   }

   public PotionEffectType getPotionEffectType() {
      return this.potionEffectType;
   }
}
