package net.frozenorb.foxtrot.gameplay.blockshop.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.util.ItemBuilder;
import java.beans.ConstructorProperties;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class BackButton extends Button {
   private final ClickHandler clickHandler;

   @Override
   public void clicked(Player player, int i, ClickType clickType) {
      this.clickHandler.onClick(player, i, clickType);
   }

   @Override
   public ItemStack getButtonItem(Player player) {
      return ItemBuilder.of(Material.ARROW).name("§aBack").build();
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

   @ConstructorProperties("clickHandler")
   public BackButton(ClickHandler clickHandler) {
      this.clickHandler = clickHandler;
   }
}
