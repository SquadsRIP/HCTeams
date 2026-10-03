package net.frozenorb.foxtrot.gameplay.loot.crate.item;

import java.beans.ConstructorProperties;
import org.bukkit.inventory.ItemStack;

public class CrateItem {
   private ItemStack itemStack;
   private double chance;
   private String command;
   private boolean giveItem;
   private boolean broadcast;

   @ConstructorProperties({"itemStack", "chance", "command", "giveItem", "broadcast"})
   public CrateItem(ItemStack itemStack, double chance, String command, boolean giveItem, boolean broadcast) {
      this.itemStack = itemStack;
      this.chance = chance;
      this.command = command;
      this.giveItem = giveItem;
      this.broadcast = broadcast;
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }

   public double getChance() {
      return this.chance;
   }

   public String getCommand() {
      return this.command;
   }

   public boolean isGiveItem() {
      return this.giveItem;
   }

   public boolean isBroadcast() {
      return this.broadcast;
   }
}
