package net.frozenorb.foxtrot.team.upgrade;

import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum UpgradeType {
   REDUCED_DTR_REGEN(
      ChatColor.GOLD + ChatColor.BOLD.toString() + "Reduced DTR Regen",
      Arrays.asList(ChatColor.GRAY + "Purchase Reduced", ChatColor.GRAY + "DTR Regen time."),
      Material.REDSTONE,
      11,
      50
   ),
   INCREASED_MAX_BARD(
      ChatColor.GOLD + ChatColor.BOLD.toString() + "Increased Max Energy",
      Arrays.asList(ChatColor.GRAY + "Increase the maximum bard", ChatColor.GRAY + "energy from 100 to 120."),
      Material.GOLD_HELMET,
      12,
      50
   ),
   DOUBLE_GEMS(
      ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + "Double Gems",
      Arrays.asList(ChatColor.GRAY + "Get 2x Gems for everyone", ChatColor.GRAY + "currently in your team for 6 hours!"),
      Material.EMERALD,
      13,
      500
   ),
   DOUBLE_DROPS(
      ChatColor.GOLD + ChatColor.BOLD.toString() + "Double Drops",
      Arrays.asList(ChatColor.GRAY + "Get double the item drops", ChatColor.GRAY + "from all mobs and ores."),
      Material.SPIDER_EYE,
      13,
      15
   ),
   DOUBLE_XP(
      ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + "Double XP",
      Arrays.asList(ChatColor.GRAY + "Get double the XP from", ChatColor.GRAY + "all mob and ore drops."),
      Material.EXP_BOTTLE,
      14,
      25
   ),
   POTION_EFFECTS(
      ChatColor.YELLOW + ChatColor.BOLD.toString() + "Potion Effects",
      Arrays.asList(ChatColor.GRAY + "Receive passive potion", ChatColor.GRAY + "effects in your claim!"),
      Material.BREWING_STAND_ITEM,
      15,
      -1
   );

   String displayName;
   List<String> description;
   Material material;
   int slot;
   int cost;

   @ConstructorProperties({"displayName", "description", "material", "slot", "cost"})
   UpgradeType(String displayName, List<String> description, Material material, int slot, int cost) {
      this.displayName = displayName;
      this.description = description;
      this.material = material;
      this.slot = slot;
      this.cost = cost;
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
}
