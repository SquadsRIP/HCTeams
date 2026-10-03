package net.frozenorb.foxtrot.gameplay.blockshop.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenorb.foxtrot.gameplay.blockshop.ShopItem;
import org.bukkit.entity.Player;

public class CategoryMenu extends Menu {
   private final List<ShopItem> items;

   @Override
   public String getTitle(Player player) {
      return "Block Shop";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      HashMap<Integer, Button> buttons = new HashMap<>();
      buttons.put(40, new BackButton((player1, i, clickType) -> new BlockShopMenu().openMenu(player)));

      for (ShopItem item : this.items) {
         buttons.put(item.getIndex(), new ItemButton(item));
      }

      return buttons;
   }

   @ConstructorProperties("items")
   public CategoryMenu(List<ShopItem> items) {
      this.items = items;
   }
}
