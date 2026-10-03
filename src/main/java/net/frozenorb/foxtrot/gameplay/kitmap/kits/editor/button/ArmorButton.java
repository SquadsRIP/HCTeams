package net.frozenorb.foxtrot.gameplay.kitmap.kits.editor.button;

import cc.fyre.proton.menu.Button;
import java.util.List;
import net.minecraft.util.com.google.common.base.Preconditions;
import net.minecraft.util.com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

final class ArmorButton extends Button {
   private final ItemStack item;

   ArmorButton(ItemStack item) {
      this.item = (ItemStack)Preconditions.checkNotNull(item, "item");
   }

   @Override
   public ItemStack getButtonItem(Player player) {
      ItemStack newItem = this.item.clone();
      ItemMeta itemMeta = newItem.getItemMeta();
      if (itemMeta != null) {
         itemMeta.setLore(ImmutableList.of("", ChatColor.YELLOW + "This is automatically equipped."));
         newItem.setItemMeta(itemMeta);
      }

      return newItem;
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
}
