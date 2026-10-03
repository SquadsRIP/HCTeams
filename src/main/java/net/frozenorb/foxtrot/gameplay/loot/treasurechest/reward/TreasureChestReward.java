package net.frozenorb.foxtrot.gameplay.loot.treasurechest.reward;

import java.beans.ConstructorProperties;
import org.bukkit.inventory.ItemStack;

public class TreasureChestReward {
   private ItemStack itemStack;
   private double chance;
   private String command;
   private boolean grantItem;
   private boolean broadcast;

   @ConstructorProperties({"itemStack", "chance", "command", "grantItem", "broadcast"})
   public TreasureChestReward(ItemStack itemStack, double chance, String command, boolean grantItem, boolean broadcast) {
      this.itemStack = itemStack;
      this.chance = chance;
      this.command = command;
      this.grantItem = grantItem;
      this.broadcast = broadcast;
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }

   public double getChance() {
      return this.chance;
   }

   public void setChance(double chance) {
      this.chance = chance;
   }

   public String getCommand() {
      return this.command;
   }

   public boolean isGrantItem() {
      return this.grantItem;
   }

   public boolean isBroadcast() {
      return this.broadcast;
   }
}
