package net.frozenorb.foxtrot.gameplay.loot.treasurecove.commands;

import cc.fyre.proton.menu.Button;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

class TreasureLootCommand$1$1 extends Button {
   // $VF: synthetic field
   final ItemStack val$itemStack;
   // $VF: synthetic field
   final TreasureLootCommand$1 this$0;

   TreasureLootCommand$1$1(TreasureLootCommand$1 this$0, ItemStack var2) {
      this.this$0 = this$0;
      this.val$itemStack = var2;
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
   public ItemStack getButtonItem(Player player) {
      return this.val$itemStack.clone();
   }
}
