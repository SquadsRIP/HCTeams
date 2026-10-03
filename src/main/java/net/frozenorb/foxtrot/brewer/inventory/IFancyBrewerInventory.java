package net.frozenorb.foxtrot.brewer.inventory;

import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.frozenorb.foxtrot.brewer.FancyBrewer;
import net.frozenorb.foxtrot.brewer.FancyBrewerResource;
import net.minecraft.server.v1_7_R4.EntityHuman;
import net.minecraft.server.v1_7_R4.IInventory;
import net.minecraft.server.v1_7_R4.ItemStack;
import net.minecraft.server.v1_7_R4.Items;
import org.bukkit.block.BrewingStand;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 5, 1},
   k = 1,
   xi = 48,
   d1 = "\u0000p\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\n\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0012\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H\u0016J\u001a\u0010\u0016\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020\n2\b\u0010\u0018\u001a\u0004\u0018\u00010\u0007H\u0016J\b\u0010\u0019\u001a\u00020\u001aH\u0016J\u0015\u0010\u001b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0006H\u0016¢\u0006\u0002\u0010\u001cJ\b\u0010\u001d\u001a\u00020\u001eH\u0016J\u0012\u0010\u001f\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u0017\u001a\u00020\nH\u0016J\b\u0010 \u001a\u00020\nH\u0016J\b\u0010!\u001a\u00020\"H\u0016J\b\u0010#\u001a\u00020\nH\u0016J\u0006\u0010$\u001a\u00020\u000eJ\u000e\u0010%\u001a\b\u0012\u0004\u0012\u00020\u00110&H\u0016J\b\u0010'\u001a\u00020\u0013H\u0016J\u0010\u0010(\u001a\u00020\u001a2\u0006\u0010)\u001a\u00020*H\u0016J\u0010\u0010+\u001a\u00020\u001a2\u0006\u0010)\u001a\u00020*H\u0016J\u001a\u0010,\u001a\u00020\u001a2\u0006\u0010\u0017\u001a\u00020\n2\b\u0010\u0018\u001a\u0004\u0018\u00010\u0007H\u0016J\u0010\u0010-\u001a\u00020\u001a2\u0006\u0010.\u001a\u00020\nH\u0016J\u001a\u0010/\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u0017\u001a\u00020\n2\u0006\u00100\u001a\u00020\nH\u0016J\u0012\u00101\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u0017\u001a\u00020\nH\u0016J\b\u00102\u001a\u00020\u001aH\u0016J\b\u00103\u001a\u00020\u001aH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0018\u0010\u0005\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0006X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\bR\u000e\u0010\t\u001a\u00020\nX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u0010X\u0082\u0004¢\u0006\u0002\n\u0000¨\u00064",
   d2 = {
         "Lnet/frozenorb/foxtrot/brewer/inventory/IFancyBrewerInventory;",
         "Lnet/minecraft/server/v1_7_R4/IInventory;",
         "brewer",
         "Lnet/frozenorb/foxtrot/brewer/FancyBrewer;",
         "(Lnet/frozenorb/foxtrot/brewer/FancyBrewer;)V",
         "items",
         "",
         "Lnet/minecraft/server/v1_7_R4/ItemStack;",
         "[Lnet/minecraft/server/v1_7_R4/ItemStack;",
         "maxStack",
         "",
         "owner",
         "Lorg/bukkit/block/BrewingStand;",
         "type",
         "Lorg/bukkit/event/inventory/InventoryType;",
         "viewers",
         "",
         "Lorg/bukkit/entity/HumanEntity;",
         "a",
         "",
         "entityhuman",
         "Lnet/minecraft/server/v1_7_R4/EntityHuman;",
         "b",
         "i",
         "itemstack",
         "closeContainer",
         "",
         "getContents",
         "()[Lnet/minecraft/server/v1_7_R4/ItemStack;",
         "getInventoryName",
         "",
         "getItem",
         "getMaxStackSize",
         "getOwner",
         "Lorg/bukkit/inventory/InventoryHolder;",
         "getSize",
         "getType",
         "getViewers",
         "",
         "k_",
         "onClose",
         "who",
         "Lorg/bukkit/craftbukkit/v1_7_R4/entity/CraftHumanEntity;",
         "onOpen",
         "setItem",
         "setMaxStackSize",
         "size",
         "splitStack",
         "j",
         "splitWithoutUpdate",
         "startOpen",
         "update",
         "HCTeams"
   }
)
public final class IFancyBrewerInventory implements IInventory {
   @NotNull
   private final FancyBrewer brewer;
   @NotNull
   private final ItemStack[] items;
   private int maxStack;
   @NotNull
   private final List<HumanEntity> viewers;
   @NotNull
   private final InventoryType type;
   @NotNull
   private final BrewingStand owner;

   public IFancyBrewerInventory(@NotNull FancyBrewer brewer) {
      Intrinsics.checkNotNullParameter(brewer, "brewer");
      this.brewer = brewer;
      this.items = new ItemStack[54];
      this.maxStack = 64;
      boolean bl = false;
      this.viewers = new ArrayList<>();
      this.type = InventoryType.CHEST;
      this.owner = this.brewer.getStand();
   }

   public int getSize() {
      return this.items.length;
   }

   @Nullable
   public ItemStack getItem(int i) {
      return this.items[i];
   }

   @NotNull
   public InventoryHolder getOwner() {
      return this.owner;
   }

   @Nullable
   public ItemStack splitStack(int i, int j) {
      ItemStack itemStack = this.getItem(i);
      if (itemStack == null) {
         return null;
      }

      ItemStack stack = itemStack;
      ItemStack result = null;
      if (stack.count <= j) {
         this.setItem(i, null);
         result = stack;
      } else {
         ItemStack itemStack2 = CraftItemStack.copyNMSStack(stack, j);
         Intrinsics.checkNotNullExpressionValue(itemStack2, "copyNMSStack(stack, j)");
         result = itemStack2;
         itemStack2 = stack;
         itemStack2.count -= j;
         if (this.brewer.getInitialized()) {
            if (Intrinsics.areEqual(stack.getItem(), Items.GLASS_BOTTLE)) {
               this.brewer.setBottles(i - 12, CraftItemStack.asBukkitCopy(result));
               this.brewer.flagForSave();
               this.brewer.refreshButtons$HCTeams();
            } else {
               FancyBrewerResource resource2 = FancyBrewerResource.Companion.getResourceBySlot(i);
               if (resource2 != null) {
                  this.brewer.setResource(resource2, CraftItemStack.asBukkitCopy(result));
                  this.brewer.flagForSave();
                  this.brewer.refreshButtons$HCTeams();
               }
            }
         }
      }

      this.update();
      return result;
   }

   @Nullable
   public ItemStack splitWithoutUpdate(int i) {
      ItemStack itemStack = this.getItem(i);
      if (itemStack == null) {
         return null;
      }

      ItemStack stack = itemStack;
      ItemStack result = null;
      if (stack.count <= 1) {
         this.setItem(i, null);
         result = stack;
      } else {
         ItemStack itemStack2 = CraftItemStack.copyNMSStack(stack, 1);
         Intrinsics.checkNotNullExpressionValue(itemStack2, "copyNMSStack(stack, 1)");
         result = itemStack2;
         itemStack2 = stack;
         itemStack2.count--;
         if (this.brewer.getInitialized()) {
            if (Intrinsics.areEqual(stack.getItem(), Items.GLASS_BOTTLE)) {
               this.brewer.setBottles(i - 12, CraftItemStack.asBukkitCopy(result));
               this.brewer.flagForSave();
               this.brewer.refreshButtons$HCTeams();
            } else {
               FancyBrewerResource resource2 = FancyBrewerResource.Companion.getResourceBySlot(i);
               if (resource2 != null) {
                  this.brewer.setResource(resource2, CraftItemStack.asBukkitCopy(result));
                  this.brewer.flagForSave();
                  this.brewer.refreshButtons$HCTeams();
               }
            }
         }
      }

      return result;
   }

   public void setItem(int i, @Nullable ItemStack itemstack) {
      if (itemstack == null) {
         if (this.brewer.getInitialized() && this.items[i] != null) {
            ItemStack itemStack = this.items[i];
            Intrinsics.checkNotNull(itemStack);
            if (Intrinsics.areEqual(itemStack.getItem(), Items.GLASS_BOTTLE)) {
               this.brewer.setBottles(i - 12, null);
               this.brewer.flagForSave();
               this.brewer.refreshButtons$HCTeams();
            } else {
               FancyBrewerResource resource2 = FancyBrewerResource.Companion.getResourceBySlot(i);
               if (resource2 != null) {
                  this.brewer.setResource(resource2, null);
                  this.brewer.flagForSave();
                  this.brewer.refreshButtons$HCTeams();
               }
            }
         }

         this.items[i] = null;
      } else {
         this.items[i] = itemstack;
         if (this.getMaxStackSize() > 0 && itemstack.count > this.getMaxStackSize()) {
            itemstack.count = this.getMaxStackSize();
         }

         if (this.brewer.getInitialized()) {
            if (Intrinsics.areEqual(itemstack.getItem(), Items.GLASS_BOTTLE)) {
               this.brewer.setBottles(i - 12, CraftItemStack.asBukkitCopy(itemstack));
               this.brewer.flagForSave();
               this.brewer.refreshButtons$HCTeams();
            } else {
               FancyBrewerResource fancyBrewerResource = FancyBrewerResource.Companion.getResourceBySlot(i);
               if (fancyBrewerResource != null) {
                  FancyBrewerResource resource3 = fancyBrewerResource;
                  this.brewer.setResource(resource3, CraftItemStack.asBukkitCopy(itemstack));
                  this.brewer.flagForSave();
                  this.brewer.refreshButtons$HCTeams();
               }
            }
         }
      }
   }

   @NotNull
   public String getInventoryName() {
      return FancyBrewerInventory.Companion.getTITLE();
   }

   public int getMaxStackSize() {
      return this.maxStack;
   }

   public void setMaxStackSize(int size) {
      this.maxStack = size;
   }

   public void update() {
   }

   public boolean a(@Nullable EntityHuman entityhuman) {
      return true;
   }

   @NotNull
   public ItemStack[] getContents() {
      return this.items;
   }

   public void onOpen(@NotNull CraftHumanEntity who) {
      Intrinsics.checkNotNullParameter(who, "who");
      this.viewers.add(who);
   }

   public void onClose(@NotNull CraftHumanEntity who) {
      Intrinsics.checkNotNullParameter(who, "who");
      this.viewers.remove(who);
   }

   @NotNull
   public List<HumanEntity> getViewers() {
      return this.viewers;
   }

   @NotNull
   public final InventoryType getType() {
      return this.type;
   }

   public void closeContainer() {
   }

   public void startOpen() {
   }

   public boolean k_() {
      return false;
   }

   public boolean b(int i, @Nullable ItemStack itemstack) {
      return true;
   }
}
