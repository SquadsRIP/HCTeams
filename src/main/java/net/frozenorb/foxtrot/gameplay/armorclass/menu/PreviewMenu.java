package net.frozenorb.foxtrot.gameplay.armorclass.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorPiece;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public class PreviewMenu extends Menu {
   private ArmorClass armorClass;

   @Override
   public String getTitle(Player player) {
      return "Kit Preview";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      HashMap<Integer, Button> toReturn = new HashMap<>();
      ItemBuilder itemBuilder = ItemBuilder.of(Material.DIAMOND_SWORD)
         .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
         .enchant(Enchantment.DURABILITY, 3)
         .name(this.armorClass.getDisplayName() + " Sword")
         .addToLore(
            "",
            this.armorClass.getChatColor() + "Armor Class: &f" + ChatColor.stripColor(this.armorClass.getDisplayName()),
            this.armorClass.getChatColor() + "Perks:"
         );

      for (String perk : this.armorClass.getPerks()) {
         itemBuilder.addToLore(this.armorClass.getChatColor() + "❙ &f" + perk);
      }

      toReturn.put(11, Button.fromItem(itemBuilder.build()));
      toReturn.put(12, Button.fromItem(this.armorClass.createPiece(ArmorPiece.HELMET)));
      toReturn.put(13, Button.fromItem(this.armorClass.createPiece(ArmorPiece.CHESTPLATE)));
      toReturn.put(14, Button.fromItem(this.armorClass.createPiece(ArmorPiece.LEGGINGS)));
      toReturn.put(15, Button.fromItem(this.armorClass.createPiece(ArmorPiece.BOOTS)));
      return toReturn;
   }

   @Override
   public int size(Player player) {
      return 27;
   }

   @Override
   public void onClose(Player player) {
      Foxtrot.getInstance()
         .getServer()
         .getScheduler()
         .runTaskLater(Foxtrot.getInstance(), () -> new ArmorClassesMenu(this.armorClass.getCategory()).openMenu(player), 1L);
   }

   @Override
   public boolean isPlaceholder() {
      return true;
   }

   @ConstructorProperties("armorClass")
   public PreviewMenu(ArmorClass armorClass) {
      this.armorClass = armorClass;
   }
}
