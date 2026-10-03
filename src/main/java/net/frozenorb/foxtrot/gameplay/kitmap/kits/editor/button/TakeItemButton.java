package net.frozenorb.foxtrot.gameplay.kitmap.kits.editor.button;

import cc.fyre.proton.menu.Button;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.minecraft.util.com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

final class TakeItemButton extends Button {
   private final ItemStack item;

   TakeItemButton(ItemStack item) {
      this.item = (ItemStack)Preconditions.checkNotNull(item, "item");
   }

   @Override
   public ItemStack getButtonItem(Player player) {
      return this.item;
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

   @Override
   public void clicked(Player player, int slot, ClickType clickType) {
      Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> player.getOpenInventory().getTopInventory().setItem(slot, this.item), 4L);
   }

   @Override
   public boolean shouldCancel(Player player, int slot, ClickType clickType) {
      return false;
   }
}
