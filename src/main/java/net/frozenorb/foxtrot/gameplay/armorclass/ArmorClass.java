package net.frozenorb.foxtrot.gameplay.armorclass;

import cc.fyre.proton.util.ItemBuilder;
import java.util.Arrays;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public abstract class ArmorClass implements Listener {
   private Entity entity;

   public abstract String getId();

   public abstract String getDisplayName();

   public abstract int getSlot();

   public abstract Material getDisplayItem();

   public abstract ChatColor getChatColor();

   public abstract List<String> getPerks();

   public abstract ItemStack createPiece(ArmorPiece var1);

   public ArmorClass() {
      Foxtrot.getInstance().getServer().getPluginManager().registerEvents(this, Foxtrot.getInstance());
   }

   public Category getCategory() {
      return Category.ALL;
   }

   public List<Material> findApplicableItems() {
      return Arrays.asList(Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS);
   }

   public boolean isAllowedAtLocation(Location location) {
      return Foxtrot.getInstance().getMapHandler().isKitMap()
         ? !DTRBitmask.KOTH.appliesAt(location)
            && !DTRBitmask.CONQUEST.appliesAt(location)
            && !DTRBitmask.CITADEL.appliesAt(location)
            && !DTRBitmask.DTC.appliesAt(location)
            && !DTRBitmask.SAFE_ZONE.appliesAt(location)
         : !Foxtrot.getInstance().getServerHandler().isWarzone(location)
            && location.getWorld().getEnvironment() == Environment.NORMAL
            && !DTRBitmask.KOTH.appliesAt(location)
            && !DTRBitmask.CONQUEST.appliesAt(location)
            && !DTRBitmask.CITADEL.appliesAt(location)
            && !DTRBitmask.DTC.appliesAt(location)
            && !DTRBitmask.SAFE_ZONE.appliesAt(location);
   }

   public void apply(Player player) {
   }

   public void unapply(Player player) {
   }

   public ItemStack getRedeemItem() {
      ItemBuilder itemBuilder = ItemBuilder.of(Material.CHEST).name(this.getDisplayName() + " Armor Class").addToLore("", this.getChatColor() + "Perks:");

      for (String perk : this.getPerks()) {
         itemBuilder.addToLore(this.getChatColor() + "❙ &f" + perk);
      }

      itemBuilder.addToLore("", "&aClick to redeem this armor set");
      return itemBuilder.build();
   }

   public boolean isWearing(Player player) {
      if (!ArmorClassHandler.isAllowed(player.getLocation())) {
         return false;
      }

      for (ItemStack itemStack : player.getInventory().getArmorContents()) {
         if (itemStack == null || !this.isPiece(itemStack)) {
            return false;
         }
      }

      return true;
   }

   public boolean isPiece(ItemStack itemStack) {
      if (itemStack == null || itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null) {
         return false;
      } else if (itemStack.getItemMeta().getLore() != null && !itemStack.getItemMeta().getLore().isEmpty()) {
         List lore = itemStack.getItemMeta().getLore();
         return lore.stream().anyMatch(it -> ChatColor.stripColor((String)it).equalsIgnoreCase("Armor Class: " + ChatColor.stripColor(this.getDisplayName())));
      } else {
         return false;
      }
   }

   public boolean isRedeemable(ItemStack itemStack) {
      if (itemStack == null || itemStack.getItemMeta() == null || itemStack.getType() == Material.AIR) {
         return false;
      } else if (itemStack.getItemMeta().getDisplayName() == null) {
         return false;
      } else {
         return itemStack.getItemMeta().getLore() != null && !itemStack.getItemMeta().getLore().isEmpty()
            ? itemStack.getType() == this.getRedeemItem().getType()
               && ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).startsWith(ChatColor.stripColor(this.getDisplayName()))
               && itemStack.getItemMeta().getLore().get(0).equals(this.getRedeemItem().getItemMeta().getLore().get(0))
            : false;
      }
   }

   public String getPieceName(ArmorPiece armorPiece) {
      switch (armorPiece) {
         case HELMET:
            return "Helmet";
         case CHESTPLATE:
            return "Chestplate";
         case LEGGINGS:
            return "Leggings";
         case BOOTS:
            return "Boots";
         case SWORD:
            return "Sword";
         case AXE:
            return "Axe";
         case BOW:
            return "Bow";
         default:
            return "";
      }
   }

   public Entity getEntity() {
      return this.entity;
   }

   public void setEntity(Entity entity) {
      this.entity = entity;
   }
}
