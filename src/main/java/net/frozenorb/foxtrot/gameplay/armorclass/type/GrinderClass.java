package net.frozenorb.foxtrot.gameplay.armorclass.type;

import cc.fyre.proton.util.ItemBuilder;
import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClassHandler;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorPiece;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.inventory.ItemStack;

public class GrinderClass extends ArmorClass {
   @Override
   public String getId() {
      return "grinder";
   }

   @Override
   public String getDisplayName() {
      return ChatColor.GREEN + ChatColor.BOLD.toString() + "Grinder";
   }

   @Override
   public int getSlot() {
      return 11;
   }

   @Override
   public Material getDisplayItem() {
      return Material.EXP_BOTTLE;
   }

   @Override
   public ChatColor getChatColor() {
      return ChatColor.GREEN;
   }

   @Override
   public List<String> getPerks() {
      ArrayList<String> toReturn = new ArrayList<>();
      toReturn.add("2x permanent XP multiplier");
      return toReturn;
   }

   @EventHandler(priority = EventPriority.MONITOR)
   private void onDamage(PlayerExpChangeEvent event) {
      Player player = event.getPlayer();
      if (this.isWearing(player)) {
         if (ArmorClassHandler.isAllowed(player.getLocation())) {
            event.setAmount(event.getAmount() * 3);
         }
      }
   }

   @Override
   public ItemStack createPiece(ArmorPiece armorPiece) {
      ItemBuilder itemBuilder = ItemBuilder.of(armorPiece.getDefaultMaterial())
         .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
         .enchant(Enchantment.DURABILITY, 3)
         .name(this.getDisplayName() + " " + this.getPieceName(armorPiece))
         .addToLore("", this.getChatColor() + "Armor Class: &f" + ChatColor.stripColor(this.getDisplayName()), this.getChatColor() + "Perks:");

      for (String perk : this.getPerks()) {
         itemBuilder.addToLore(this.getChatColor() + "❙ &f" + perk);
      }

      itemBuilder.addToLore("");
      if (armorPiece.getDefaultMaterial().equals(Material.DIAMOND_CHESTPLATE)) {
         itemBuilder.addToLore("&eFireResistance I");
      }

      if (armorPiece.getDefaultMaterial().equals(Material.DIAMOND_LEGGINGS)) {
         itemBuilder.addToLore("&eImplants IV");
      }

      if (armorPiece.getDefaultMaterial().equals(Material.DIAMOND_BOOTS)) {
         itemBuilder.enchant(Enchantment.PROTECTION_FALL, 4);
         itemBuilder.addToLore("&eSpeed II");
      }

      return itemBuilder.build();
   }
}
