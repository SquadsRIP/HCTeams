package net.frozenorb.foxtrot.brewer.listener;

import java.util.List;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.brewer.FancyBrewer;
import net.frozenorb.foxtrot.brewer.FancyBrewerHandler;
import net.frozenorb.foxtrot.brewer.FancyBrewerResource;
import net.frozenorb.foxtrot.brewer.button.FancyBrewerButton;
import net.frozenorb.foxtrot.brewer.inventory.FancyBrewerInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Hopper;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 5, 1},
   k = 1,
   xi = 48,
   d1 = "\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0003J\u0010\u0010\u0007\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\bH\u0003J\u0010\u0010\t\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\nH\u0003J\u0010\u0010\u000b\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\fH\u0003J\u0010\u0010\r\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u000eH\u0003J\u0010\u0010\u000f\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0010H\u0003J\u0010\u0010\u0011\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0012H\u0003¨\u0006\u0013",
   d2 = {
         "Lnet/frozenorb/foxtrot/brewer/listener/FancyBrewerListener;",
         "Lorg/bukkit/event/Listener;",
         "()V",
         "onBlockBreak",
         "",
         "event",
         "Lorg/bukkit/event/block/BlockBreakEvent;",
         "onBlockDropItems",
         "Lorg/bukkit/event/block/BlockDropItemsEvent;",
         "onBlockPlace",
         "Lorg/bukkit/event/block/BlockPlaceEvent;",
         "onInventoryClick",
         "Lorg/bukkit/event/inventory/InventoryClickEvent;",
         "onInventoryClose",
         "Lorg/bukkit/event/inventory/InventoryCloseEvent;",
         "onInventoryMove",
         "Lorg/bukkit/event/inventory/InventoryMoveItemEvent;",
         "onPlayerInteract",
         "Lorg/bukkit/event/player/PlayerInteractEvent;",
         "HCTeams"
   }
)
public final class FancyBrewerListener implements Listener {
   @NotNull
   public static final FancyBrewerListener INSTANCE = new FancyBrewerListener();

   private FancyBrewerListener() {
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   private final void onBlockPlace(BlockPlaceEvent event) {
      if (event.getItemInHand() != null && event.getItemInHand().getType() != Material.AIR) {
         if (event.getItemInHand().getType() == Material.HOPPER) {
            Block block = event.getBlock().getRelative(BlockFace.UP);
            if (block != null && block.getState() instanceof BrewingStand) {
               Bukkit.getServer().getScheduler().runTask(Foxtrot.instance, () -> onBlockPlace$lambda_0(block, event));
            }
         } else {
            FancyBrewerHandler var10000 = FancyBrewerHandler.INSTANCE;
            ItemStack block = event.getItemInHand();
            Intrinsics.checkNotNullExpressionValue(block, "event.itemInHand");
            if (var10000.isBrewer(block)) {
               Location var4 = event.getBlockPlaced().getLocation().clone();
               boolean var5 = false;
               boolean var6 = false;
               Location $this$onBlockPlace_u24lambda_u2d1 = var4;
               int var8/* $VF was: $i$a$-apply-FancyBrewerListener$onBlockPlace$block$1 */ = 0;
               $this$onBlockPlace_u24lambda_u2d1.setYaw(0.0F);
               $this$onBlockPlace_u24lambda_u2d1.setPitch(0.0F);
               Location brewer = var4;
               Intrinsics.checkNotNullExpressionValue(
                  brewer, "event.blockPlaced.location.clone().apply{\n            this.yaw = 0.0F\n            this.pitch = 0.0F\n        }"
               );
               Location blockx = brewer;
               UUID var14 = UUID.randomUUID();
               Intrinsics.checkNotNullExpressionValue(var14, "randomUUID()");
               UUID var15 = event.getPlayer().getUniqueId();
               Intrinsics.checkNotNullExpressionValue(var15, "event.player.uniqueId");
               FancyBrewer brewerx = new FancyBrewer(var14, var15, blockx);
               event.getPlayer().sendMessage(ChatColor.translate("&aYou have placed a Fancy Brewer."));
               event.getPlayer().sendMessage(ChatColor.translate("&7Watch this tutorial on how to use a Fancy Brewer &fComing Soon!"));
               FancyBrewer var16 = brewerx;
               var5 = false;
               var6 = false;
               FancyBrewer var19 = var16;
               FancyBrewerHandler var9 = FancyBrewerHandler.INSTANCE;
               var8/* $VF was: $i$a$-apply-FancyBrewerListener$onBlockPlace$block$1 */ = 0;
               var19.flagForSave();
               Unit var10 = Unit.INSTANCE;
               var9.addBrewer(var16);
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   private final void onBlockBreak(BlockBreakEvent event) {
      if (event.getBlock().getType() == Material.HOPPER) {
         Block block = event.getBlock().getRelative(BlockFace.UP);
         if (block != null && block.getState() instanceof BrewingStand) {
            FancyBrewerHandler var10000 = FancyBrewerHandler.INSTANCE;
            Location var17 = block.getLocation();
            Intrinsics.checkNotNullExpressionValue(var17, "block.location");
            FancyBrewer var12 = var10000.getBrewerByLocation(var17);
            if (var12 != null) {
               var12.setHopper1(null);
            }
         }
      } else if (event.getBlock().getState() instanceof BrewingStand) {
         Location $this$forEach$iv = event.getBlock().getLocation().clone();
         int $i$f$forEach = 0;
         boolean var6 = false;
         Location $this$onBlockBreak_u24lambda_u2d3 = $this$forEach$iv;
         int var8/* $VF was: $i$a$-apply-FancyBrewerListener$onBlockBreak$block$1 */ = 0;
         $this$onBlockBreak_u24lambda_u2d3.setYaw(0.0F);
         $this$onBlockBreak_u24lambda_u2d3.setPitch(0.0F);
         Location brewer = $this$forEach$iv;
         Intrinsics.checkNotNullExpressionValue(
            brewer, "event.block.location.clone().apply{\n            this.yaw = 0.0F\n            this.pitch = 0.0F\n        }"
         );
         Location block = brewer;
         FancyBrewer var13 = FancyBrewerHandler.INSTANCE.removeBrewer(block);
         if (var13 != null) {
            FancyBrewer brewerx = var13;
            event.getBlock().setMetadata("FANCY_BREWER_DROP", new FixedMetadataValue(Foxtrot.instance, true));
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
               event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), FancyBrewerHandler.INSTANCE.createItemStack(1));
            }

            brewerx.getStand().getInventory().clear();
            List var14 = brewerx.getInventory().getViewers();
            Intrinsics.checkNotNullExpressionValue(var14, "brewer.inventory.viewers");
            Iterable $this$forEach$ivx = var14;
            $i$f$forEach = 0;

            for (Object element$iv : $this$forEach$ivx) {
               HumanEntity it = (HumanEntity)element$iv;
               int var9/* $VF was: $i$a$-forEach-FancyBrewerListener$onBlockBreak$1 */ = 0;
               if (it == null) {
                  throw new NullPointerException("null cannot be cast to non-null type org.bukkit.entity.Player");
               }

               FancyBrewerHandler.INSTANCE.setBrewerByViewer((Player)it, null);
            }

            Iterable $this$forEach$ivxx = ArraysKt.filterNotNull(brewerx.getAllDrops());
            $i$f$forEach = 0;

            for (Object element$iv : $this$forEach$ivxx) {
               ItemStack it = (ItemStack)element$iv;
               int var26/* $VF was: $i$a$-forEach-FancyBrewerListener$onBlockBreak$2 */ = 0;
               event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), it);
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   private final void onPlayerInteract(PlayerInteractEvent event) {
      if (event.getClickedBlock() != null) {
         if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!event.getPlayer().isSneaking()
               || event.getPlayer().getItemInHand() == null
               || event.getPlayer().getItemInHand().getType() == Material.AIR
               || !event.getPlayer().getItemInHand().getType().isBlock()) {
               FancyBrewerHandler var10000 = FancyBrewerHandler.INSTANCE;
               Location var4 = event.getClickedBlock().getLocation();
               Intrinsics.checkNotNullExpressionValue(var4, "event.clickedBlock.location");
               FancyBrewer var3 = var10000.getBrewerByLocation(var4);
               if (var3 != null) {
                  FancyBrewer brewer = var3;
                  Player var5 = event.getPlayer();
                  Intrinsics.checkNotNullExpressionValue(var5, "event.player");
                  brewer.openInventory(var5);
                  var10000 = FancyBrewerHandler.INSTANCE;
                  Player var6 = event.getPlayer();
                  Intrinsics.checkNotNullExpressionValue(var6, "event.player");
                  var10000.setBrewerByViewer(var6, brewer);
                  event.setCancelled(true);
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   private final void onInventoryClose(InventoryCloseEvent event) {
      FancyBrewerHandler var10000 = FancyBrewerHandler.INSTANCE;
      HumanEntity var2 = event.getPlayer();
      if (var2 == null) {
         throw new NullPointerException("null cannot be cast to non-null type org.bukkit.entity.Player");
      }

      var10000.setBrewerByViewer((Player)var2, null);
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   private final void onInventoryClick(InventoryClickEvent event) {
      HumanEntity inventory = event.getWhoClicked();
      Player brewer = inventory instanceof Player ? (Player)inventory : null;
      if (brewer != null) {
         Player player = brewer;
         if (event.getClickedInventory() != null) {
            FancyBrewer var8 = FancyBrewerHandler.INSTANCE.getBrewerByViewer(player);
            if (var8 != null) {
               FancyBrewer brewerx = var8;
               FancyBrewerInventory inventoryx = brewerx.getInventory();
               if (Intrinsics.areEqual(event.getClickedInventory(), inventoryx)) {
                  if (FancyBrewerResource.Companion.getResourceBySlot(event.getSlot()) == null) {
                     if (!ArraysKt.contains(FancyBrewer.Companion.getGLASS_SLOTS(), event.getSlot())) {
                        FancyBrewerButton var5 = FancyBrewer.Companion.getBUTTONS().get(event.getSlot());
                        if (var5 != null) {
                           ClickType var6 = event.getClick();
                           Intrinsics.checkNotNullExpressionValue(var6, "event.click");
                           var5.handleClick(player, brewerx, var6);
                        }

                        event.setCancelled(true);
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   private final void onInventoryMove(InventoryMoveItemEvent event) {
      InventoryHolder holder = event.getSource().getHolder();
      if (holder instanceof BrewingStand) {
         FancyBrewerHandler var10000 = FancyBrewerHandler.INSTANCE;
         Location var3 = ((BrewingStand)holder).getLocation();
         Intrinsics.checkNotNullExpressionValue(var3, "holder.location");
         if (var10000.getBrewerByLocation(var3) != null) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   private final void onBlockDropItems(BlockDropItemsEvent event) {
      if (event.getBlock().hasMetadata("FANCY_BREWER_DROP")) {
         event.getToDrop().clear();
      }
   }

   private static final void onBlockPlace$lambda_0/* $VF was: onBlockPlace$lambda-0 */(Block $block, BlockPlaceEvent $event) {
      Intrinsics.checkNotNullParameter($event, "$event");
      FancyBrewerHandler var10000 = FancyBrewerHandler.INSTANCE;
      Location var3 = $block.getLocation();
      Intrinsics.checkNotNullExpressionValue(var3, "block.location");
      FancyBrewer var2 = var10000.getBrewerByLocation(var3);
      if (var2 != null) {
         BlockState var4 = $event.getBlock().getState();
         if (var4 == null) {
            throw new NullPointerException("null cannot be cast to non-null type org.bukkit.block.Hopper");
         }

         var2.setHopper1((Hopper)var4);
      }
   }
}
