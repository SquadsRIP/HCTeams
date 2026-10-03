package net.frozenorb.foxtrot.gameplay.blockshop;

import java.beans.ConstructorProperties;
import org.bukkit.inventory.ItemStack;

public class ShopItem {
   private final ItemStack itemStack;
   private final int price;
   private final int index;

   @ConstructorProperties({"itemStack", "price", "index"})
   public ShopItem(ItemStack itemStack, int price, int index) {
      this.itemStack = itemStack;
      this.price = price;
      this.index = index;
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }

   public int getPrice() {
      return this.price;
   }

   public int getIndex() {
      return this.index;
   }
}
