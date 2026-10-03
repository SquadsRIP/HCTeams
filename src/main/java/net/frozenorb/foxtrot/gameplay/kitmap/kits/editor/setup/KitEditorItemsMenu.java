package net.frozenorb.foxtrot.gameplay.kitmap.kits.editor.setup;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.DefaultKit;
import net.minecraft.util.com.google.common.collect.ImmutableList;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KitEditorItemsMenu extends Menu {
   private DefaultKit kit;

   @Override
   public String getTitle(Player player) {
      return "Edit " + this.kit.getName() + " Editor Items";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      HashMap<Integer, Button> buttons = new HashMap<>();

      for (ItemStack itemStack : this.kit.getEditorItems()) {
         buttons.put(buttons.size(), new KitEditorItemsMenu.EditorItemButton(itemStack));
      }

      return buttons;
   }

   @ConstructorProperties("kit")
   public KitEditorItemsMenu(DefaultKit kit) {
      this.kit = kit;
   }

   public DefaultKit getKit() {
      return this.kit;
   }

   private class EditorItemButton extends Button {
      private ItemStack itemStack;

      @Override
      public String getName(Player player) {
         return "";
      }

      @Override
      public Material getMaterial(Player player) {
         return Material.AIR;
      }

      @Override
      public List<String> getDescription(Player player) {
         return ImmutableList.of();
      }

      @Override
      public ItemStack getButtonItem(Player player) {
         return this.itemStack.clone();
      }

      @Override
      public void clicked(Player player, int slot, ClickType clickType) {
         if (clickType.isRightClick()) {
            KitEditorItemsMenu.this.kit.getEditorItems().remove(this.itemStack);
         }
      }

      @ConstructorProperties("itemStack")
      public EditorItemButton(ItemStack itemStack) {
         this.itemStack = itemStack;
      }
   }
}
