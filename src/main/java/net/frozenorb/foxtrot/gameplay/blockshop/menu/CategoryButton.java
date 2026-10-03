package net.frozenorb.foxtrot.gameplay.blockshop.menu;

import cc.fyre.proton.menu.Button;
import java.beans.ConstructorProperties;
import java.util.List;
import net.frozenorb.foxtrot.gameplay.blockshop.ShopItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class CategoryButton extends Button {
   private final ItemStack itemStack;
   private final List<ShopItem> items;

   @Override
   public void clicked(Player player, int i, ClickType clickType) {
      new CategoryMenu(this.items).openMenu(player);
   }

   @Override
   public ItemStack getButtonItem(Player player) {
      return this.itemStack;
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

   @ConstructorProperties({"itemStack", "items"})
   public CategoryButton(ItemStack itemStack, List<ShopItem> items) {
      this.itemStack = itemStack;
      this.items = items;
   }
}
