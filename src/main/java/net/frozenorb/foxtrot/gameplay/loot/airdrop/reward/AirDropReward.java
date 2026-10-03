package net.frozenorb.foxtrot.gameplay.loot.airdrop.reward;

import java.beans.ConstructorProperties;
import org.bukkit.inventory.ItemStack;

public class AirDropReward {
   private ItemStack itemStack;
   private double chance;
   private String command;
   private boolean grantItem;

   @ConstructorProperties({"itemStack", "chance", "command", "grantItem"})
   public AirDropReward(ItemStack itemStack, double chance, String command, boolean grantItem) {
      this.itemStack = itemStack;
      this.chance = chance;
      this.command = command;
      this.grantItem = grantItem;
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

   public boolean isGrantItem() {
      return this.grantItem;
   }
}
