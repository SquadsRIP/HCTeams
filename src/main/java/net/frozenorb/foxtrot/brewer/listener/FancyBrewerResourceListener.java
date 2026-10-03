package net.frozenorb.foxtrot.brewer.listener;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref.IntRef;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.brewer.FancyBrewer;
import net.frozenorb.foxtrot.brewer.FancyBrewerHandler;
import net.frozenorb.foxtrot.brewer.FancyBrewerResource;
import net.frozenorb.foxtrot.brewer.inventory.FancyBrewerInventory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Hopper;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 5, 1},
   k = 1,
   xi = 48,
   d1 = "\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0003J\u0010\u0010\u0007\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\bH\u0003J\u0010\u0010\t\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\nH\u0003¨\u0006\u000b",
   d2 = {
         "Lnet/frozenorb/foxtrot/brewer/listener/FancyBrewerResourceListener;",
         "Lorg/bukkit/event/Listener;",
         "()V",
         "onInventoryClick",
         "",
         "event",
         "Lorg/bukkit/event/inventory/InventoryClickEvent;",
         "onInventoryDrag",
         "Lorg/bukkit/event/inventory/InventoryDragEvent;",
         "onInventoryMove",
         "Lorg/bukkit/event/inventory/InventoryMoveItemEvent;",
         "HCTeams"
   }
)
public final class FancyBrewerResourceListener implements Listener {
   @NotNull
   public static final FancyBrewerResourceListener INSTANCE = new FancyBrewerResourceListener();

   private FancyBrewerResourceListener() {
   }

   @EventHandler(priority = EventPriority.LOWEST)
   private final void onInventoryClick(InventoryClickEvent event) {
      HumanEntity inventory = event.getWhoClicked();
      Player brewer = inventory instanceof Player ? (Player)inventory : null;
      if (brewer != null) {
         Player player = brewer;
         if (event.getClickedInventory() != null) {
            FancyBrewer var19 = FancyBrewerHandler.INSTANCE.getBrewerByViewer(player);
            if (var19 != null) {
               FancyBrewer brewerx = var19;
               FancyBrewerInventory inventoryx = brewerx.getInventory();
               ItemStack cursor = event.getCursor() != null ? (event.getCursor().getType() == Material.AIR ? null : event.getCursor()) : (ItemStack)null;
               ItemStack current = event.getCurrentItem() != null
                  ? (event.getCurrentItem().getType() == Material.AIR ? null : event.getCurrentItem())
                  : (ItemStack)null;
               if (Intrinsics.areEqual(event.getClickedInventory(), inventoryx)) {
                  InventoryAction var22 = event.getAction();
                  int var25 = var22 == null ? -1 : FancyBrewerResourceListener.WhenMappings.$EnumSwitchMapping$0[var22.ordinal()];
                  switch (var25) {
                     case 1:
                        ItemStack var27 = player.getInventory().getItem(event.getHotbarButton());
                        if (var27 == null) {
                           return;
                        }

                        ItemStack item = var27;
                        if (brewerx.isAllowedToInsert(event.getSlot(), item)) {
                           return;
                        }

                        event.setCancelled(true);
                        break;
                     case 2:
                        int var10001 = event.getSlot();
                        Intrinsics.checkNotNull(cursor);
                        if (brewerx.isAllowedToInsert(var10001, cursor)) {
                           return;
                        }

                        event.setCancelled(true);
                        break;
                     case 3:
                     case 4:
                     case 5:
                        if (cursor == null) {
                           return;
                        }

                        if (brewerx.isAllowedToInsert(event.getSlot(), cursor)) {
                           return;
                        }

                        event.setCancelled(true);
                  }
               } else if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                  if (current != null) {
                     if (current.getType() == Material.GLASS_BOTTLE) {
                        int available = FancyBrewer.Companion.getMAX_BOTTLES() - brewerx.getAllBottles();
                        if (available > 0) {
                           IntRef amount = new IntRef();
                           amount.element = current.getAmount();
                           if (amount.element > available) {
                              amount.element = available;
                           }

                           brewerx.addBottles(amount.element);
                           Bukkit.getServer().getScheduler().runTask(Foxtrot.instance, () -> onInventoryClick$lambda_1(player, event, current, amount));
                        }

                        event.setCancelled(true);
                     } else {
                        FancyBrewerResource.Companion var10000 = FancyBrewerResource.Companion;
                        Material value = current.getType();
                        Intrinsics.checkNotNullExpressionValue(value, "current.type");
                        FancyBrewerResource resource = var10000.getResourceByType(value);
                        if (resource == null) {
                           event.setCancelled(true);
                        } else {
                           ItemStack valuex = brewerx.getResourceByType(resource);
                           if (valuex != null) {
                              if (valuex.getAmount() == valuex.getMaxStackSize()) {
                                 event.setCancelled(true);
                              } else {
                                 int leftover = current.getAmount() + valuex.getAmount() - valuex.getMaxStackSize();
                                 if (leftover > 0) {
                                    event.setCancelled(true);
                                    ItemStack var10 = valuex;
                                    boolean var11 = false;
                                    boolean var12 = false;
                                    ItemStack $this$onInventoryClick_u24lambda_u2d2 = var10;
                                    FancyBrewerResource var16 = resource;
                                    FancyBrewer var15 = brewerx;
                                    int var14/* $VF was: $i$a$-apply-FancyBrewerResourceListener$onInventoryClick$2 */ = 0;
                                    $this$onInventoryClick_u24lambda_u2d2.setAmount($this$onInventoryClick_u24lambda_u2d2.getMaxStackSize());
                                    Unit var17 = Unit.INSTANCE;
                                    var15.setResource(var16, var10);
                                    Bukkit.getServer().getScheduler().runTask(Foxtrot.instance, () -> onInventoryClick$lambda_4(player, event, current, leftover));
                                 }
                              }
                           } else {
                              Bukkit.getServer().getScheduler().runTask(Foxtrot.instance, () -> onInventoryClick$lambda_5(player, resource, current, event));
                              event.setCancelled(true);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   private final void onInventoryMove(InventoryMoveItemEvent event) {
      InventoryHolder hopper = event.getSource().getHolder();
      if (hopper instanceof Hopper) {
         InventoryHolder stand = event.getDestination().getHolder();
         if (stand instanceof BrewingStand) {
            FancyBrewerHandler var10000 = FancyBrewerHandler.INSTANCE;
            Location var5 = ((BrewingStand)stand).getLocation();
            Intrinsics.checkNotNullExpressionValue(var5, "stand.location");
            FancyBrewer brewer = var10000.getBrewerByLocation(var5);
            if (brewer != null && !Intrinsics.areEqual(brewer.getHopper(), hopper)) {
               event.setCancelled(true);
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   private final void onInventoryDrag(InventoryDragEvent event) {
      HumanEntity var4 = event.getWhoClicked();
      Player brewer = var4 instanceof Player ? (Player)var4 : null;
      if (brewer != null) {
         Player player = brewer;
         if (event.getInventory() != null) {
            FancyBrewer brewerx = FancyBrewerHandler.INSTANCE.getBrewerByViewer(player);
            if (brewerx != null) {
               event.setCancelled(true);
            }
         }
      }
   }

   private static final void onInventoryClick$lambda_1/* $VF was: onInventoryClick$lambda-1 */(
      Player $player, InventoryClickEvent $event, ItemStack $current, IntRef $amount
   ) {
      Intrinsics.checkNotNullParameter($player, "$player");
      Intrinsics.checkNotNullParameter($event, "$event");
      Intrinsics.checkNotNullParameter($amount, "$amount");
      Inventory var10000 = $player.getOpenInventory().getBottomInventory();
      int var10001 = $event.getSlot();
      ItemStack var10002;
      if ($current.getAmount() - $amount.element == 0) {
         var10002 = (ItemStack)null;
      } else {
         ItemStack var4 = $current;
         boolean var5 = false;
         boolean var6 = false;
         ItemStack $this$onInventoryClick_u24lambda_u2d1_u24lambda_u2d0 = var4;
         int var10 = var10001;
         Inventory var9 = var10000;
         int var8/* $VF was: $i$a$-apply-FancyBrewerResourceListener$onInventoryClick$1$1 */ = 0;
         $this$onInventoryClick_u24lambda_u2d1_u24lambda_u2d0.setAmount($this$onInventoryClick_u24lambda_u2d1_u24lambda_u2d0.getAmount() - $amount.element);
         Unit var11 = Unit.INSTANCE;
         var10000 = var9;
         var10001 = var10;
         var10002 = var4;
      }

      var10000.setItem(var10001, var10002);
   }

   private static final void onInventoryClick$lambda_4/* $VF was: onInventoryClick$lambda-4 */(
      Player $player, InventoryClickEvent $event, ItemStack $current, int $leftover
   ) {
      Intrinsics.checkNotNullParameter($player, "$player");
      Intrinsics.checkNotNullParameter($event, "$event");
      Inventory var10000 = $player.getOpenInventory().getBottomInventory();
      int var10001 = $event.getSlot();
      ItemStack var4 = $current;
      boolean var5 = false;
      boolean var6 = false;
      ItemStack $this$onInventoryClick_u24lambda_u2d4_u24lambda_u2d3 = var4;
      int var10 = var10001;
      Inventory var9 = var10000;
      int var8/* $VF was: $i$a$-apply-FancyBrewerResourceListener$onInventoryClick$3$1 */ = 0;
      $this$onInventoryClick_u24lambda_u2d4_u24lambda_u2d3.setAmount($leftover);
      Unit var11 = Unit.INSTANCE;
      var9.setItem(var10, var4);
   }

   private static final void onInventoryClick$lambda_5/* $VF was: onInventoryClick$lambda-5 */(
      Player $player, FancyBrewerResource $resource, ItemStack $current, InventoryClickEvent $event
   ) {
      Intrinsics.checkNotNullParameter($player, "$player");
      Intrinsics.checkNotNullParameter($event, "$event");
      $player.getOpenInventory().getTopInventory().setItem($resource.getSlot(), $current);
      $player.getOpenInventory().getBottomInventory().setItem($event.getSlot(), null);
   }

   // $VF: synthetic class
   @Metadata(mv = {1, 5, 1}, k = 3, xi = 48)
   public static class WhenMappings {
      // $VF: synthetic field
      public static final int[] $EnumSwitchMapping$0;

      static {
         int[] var0 = new int[InventoryAction.values().length];
         var0[InventoryAction.HOTBAR_SWAP.ordinal()] = 1;
         var0[InventoryAction.SWAP_WITH_CURSOR.ordinal()] = 2;
         var0[InventoryAction.PLACE_ONE.ordinal()] = 3;
         var0[InventoryAction.PLACE_SOME.ordinal()] = 4;
         var0[InventoryAction.PLACE_ALL.ordinal()] = 5;
         $EnumSwitchMapping$0 = var0;
      }
   }
}
