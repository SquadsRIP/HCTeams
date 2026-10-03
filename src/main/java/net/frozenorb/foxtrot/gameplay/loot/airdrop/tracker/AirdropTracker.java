package net.frozenorb.foxtrot.gameplay.loot.airdrop.tracker;

import java.beans.ConstructorProperties;
import java.util.Map;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;

public class AirdropTracker {
   private UUID trackingID;
   private UUID givenTo;
   private UUID givenBy;
   private int initalAmount;
   private ItemStack itemStack;
   private boolean airdropAll;
   private Map<String, Long> actions;

   @ConstructorProperties({"trackingID", "givenTo", "givenBy", "initalAmount", "itemStack", "airdropAll", "actions"})
   public AirdropTracker(UUID trackingID, UUID givenTo, UUID givenBy, int initalAmount, ItemStack itemStack, boolean airdropAll, Map<String, Long> actions) {
      this.trackingID = trackingID;
      this.givenTo = givenTo;
      this.givenBy = givenBy;
      this.initalAmount = initalAmount;
      this.itemStack = itemStack;
      this.airdropAll = airdropAll;
      this.actions = actions;
   }

   public UUID getTrackingID() {
      return this.trackingID;
   }

   public UUID getGivenTo() {
      return this.givenTo;
   }

   public UUID getGivenBy() {
      return this.givenBy;
   }

   public int getInitalAmount() {
      return this.initalAmount;
   }

   public ItemStack getItemStack() {
      return this.itemStack;
   }

   public boolean isAirdropAll() {
      return this.airdropAll;
   }

   public Map<String, Long> getActions() {
      return this.actions;
   }
}
