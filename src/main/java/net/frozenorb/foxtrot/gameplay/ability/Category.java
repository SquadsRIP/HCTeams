package net.frozenorb.foxtrot.gameplay.ability;

import java.beans.ConstructorProperties;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum Category {
   KIT_MAP("", "", Material.AIR, (byte)0, 0),
   PORTABLE_BARD("", "", Material.AIR, (byte)0, 0),
   PARTNER_CRATE(ChatColor.translate("&a&lPartner Items"), "Partner Items", Material.EYE_OF_ENDER, (byte)5, 11),
   AIRDROPS(ChatColor.translate("&b&lAirdrop"), "Airdrop", Material.DROPPER, (byte)3, 12),
   SEASONAL_CRATE(ChatColor.translate("&4&l3.0 Crate"), "3.0 Crate", Material.TRIPWIRE_HOOK, (byte)2, 14),
   SEASONAL_LOOTBOX(ChatColor.translate("&c&lIndependence &b&lDay &f&lLootbox"), "Seasonal Lootbox", Material.CHEST, (byte)2, 15),
   TREASURE_CHEST(ChatColor.translate("&e&lTreasure Chest"), "Treasure Chest", Material.ENDER_CHEST, (byte)13, 16),
   ALL(ChatColor.translate("&4&lView All"), "View All", Material.NETHER_STAR, (byte)0, 13);

   String displayName;
   String simpleName;
   Material displayItem;
   byte glassData;
   int slot;

   public boolean isCategory(Ability ability, Category category) {
      return ability.getCategory() == category ? true : category == ALL;
   }

   @ConstructorProperties({"displayName", "simpleName", "displayItem", "glassData", "slot"})
   Category(String displayName, String simpleName, Material displayItem, byte glassData, int slot) {
      this.displayName = displayName;
      this.simpleName = simpleName;
      this.displayItem = displayItem;
      this.glassData = glassData;
      this.slot = slot;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public String getSimpleName() {
      return this.simpleName;
   }

   public Material getDisplayItem() {
      return this.displayItem;
   }

   public byte getGlassData() {
      return this.glassData;
   }

   public int getSlot() {
      return this.slot;
   }
}
