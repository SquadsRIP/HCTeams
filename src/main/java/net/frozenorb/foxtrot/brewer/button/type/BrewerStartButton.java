package net.frozenorb.foxtrot.brewer.button.type;

import cc.fyre.proton.util.TimeUtils;
import java.util.ArrayList;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import net.frozenorb.foxtrot.brewer.FancyBrewer;
import net.frozenorb.foxtrot.brewer.FancyBrewerResource;
import net.frozenorb.foxtrot.brewer.FancyBrewerState;
import net.frozenorb.foxtrot.brewer.button.FancyBrewerButton;
import net.frozenorb.foxtrot.util.ThePotionUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 5, 1},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J \u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016J\u0010\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0007\u001a\u00020\bH\u0016¨\u0006\r",
   d2 = {
         "Lnet/frozenorb/foxtrot/brewer/button/type/BrewerStartButton;",
         "Lnet/frozenorb/foxtrot/brewer/button/FancyBrewerButton;",
         "()V",
         "handleClick",
         "",
         "player",
         "Lorg/bukkit/entity/Player;",
         "brewer",
         "Lnet/frozenorb/foxtrot/brewer/FancyBrewer;",
         "click",
         "Lorg/bukkit/event/inventory/ClickType;",
         "render",
         "Lorg/bukkit/inventory/ItemStack;",
         "HCTeams"
   }
)
public final class BrewerStartButton extends FancyBrewerButton {
   @NotNull
   public static final BrewerStartButton INSTANCE = new BrewerStartButton();

   private BrewerStartButton() {
   }

   @NotNull
   @Override
   public ItemStack render(@NotNull FancyBrewer brewer) {
      Intrinsics.checkNotNullParameter(brewer, "brewer");
      boolean displayName = false;
      ArrayList lore = new ArrayList();
      String displayNamex = null;
      FancyBrewerState var4 = brewer.getState();
      String var22;
      switch (var4) {
         case IDLE:
            var22 = "" + ChatColor.GREEN + ChatColor.BOLD + "Start Brewing";
            int eta = brewer.getEstimatedTime();
            lore.add(ChatColor.GRAY + "Estimated Time: " + ChatColor.YELLOW + (eta == -1 ? "N/A" : TimeUtils.formatIntoHHMMSS(eta)));
            break;
         case BREWING:
            var22 = "" + ChatColor.GREEN + ChatColor.BOLD + "Brewing";
            lore.add(" ");
            if (brewer.getLastResource() != null) {
               ItemStack $this$render_u24lambda_u2d1 = brewer.getInventory().getItem(FancyBrewerResource.TYPE.getSlot());
               Material type = $this$render_u24lambda_u2d1 == null ? null : $this$render_u24lambda_u2d1.getType();

               for (FancyBrewerResource resource : FancyBrewerResource.Companion.getAllResources()) {
                  ItemStack var11 = brewer.getInventory().getItem(resource.getSlot());
                  Material var10 = var11 == null ? null : var11.getType();
                  if (var10 != null) {
                     Material item = var10;
                     boolean var31 = false;
                     boolean var32 = false;
                     StringBuilder var33 = new StringBuilder();
                     boolean var12 = false;
                     boolean var13 = false;
                     StringBuilder $this$render_u24lambda_u2d0 = var33;
                     ArrayList var19 = lore;
                     int var15/* $VF was: $i$a$-buildString-BrewerStartButton$render$1 */ = 0;
                     FancyBrewerResource var10001 = brewer.getLastResource();
                     Intrinsics.checkNotNull(var10001);
                     String var35;
                     if (var10001.compareTo(resource) > 0) {
                        var35 = String.valueOf(ChatColor.GREEN);
                     } else {
                        var10001 = brewer.getLastResource();
                        Intrinsics.checkNotNull(var10001);
                        if (var10001 == resource) {
                           var35 = String.valueOf(ChatColor.YELLOW);
                        } else {
                           var10001 = brewer.getLastResource();
                           Intrinsics.checkNotNull(var10001);
                           var35 = var10001.compareTo(resource) < 0 ? String.valueOf(ChatColor.GRAY) : "";
                        }
                     }

                     $this$render_u24lambda_u2d0.append(var35);
                     $this$render_u24lambda_u2d0.append("►");
                     $this$render_u24lambda_u2d0.append(" ");
                     $this$render_u24lambda_u2d0.append(String.valueOf(ChatColor.WHITE));
                     Object var38;
                     if (resource == FancyBrewerResource.WART) {
                        var38 = "Awkward";
                     } else if (resource == FancyBrewerResource.TYPE && type != null) {
                        var38 = FancyBrewerResource.Companion.getRESULT_TABLE().get(type);
                        Intrinsics.checkNotNull(var38);
                        Object var34 = var38;
                        Intrinsics.checkNotNullExpressionValue(var34, "FancyBrewerResource.RESULT_TABLE[type]!!");
                        var38 = ThePotionUtil.getName((PotionEffectType)var34);
                     } else if (resource == FancyBrewerResource.INCREASE && type != null) {
                        Material var16 = item;
                        switch (var16) {
                           case REDSTONE:
                              var38 = "Extend";
                              break;
                           case GLOWSTONE_DUST:
                              var38 = FancyBrewerResource.Companion.getRESULT_TABLE().get(type);
                              Intrinsics.checkNotNull(var38);
                              Object var18 = var38;
                              Intrinsics.checkNotNullExpressionValue(var18, "FancyBrewerResource.RESULT_TABLE[type]!!");
                              var38 = Intrinsics.stringPlus(ThePotionUtil.getName((PotionEffectType)var18), " II");
                              break;
                           default:
                              var38 = "";
                        }
                     } else {
                        var38 = resource == FancyBrewerResource.SPLASH ? "Splash" : Unit.INSTANCE;
                     }

                     $this$render_u24lambda_u2d0.append(var38);
                     Unit var20 = Unit.INSTANCE;
                     var38 = var33.toString();
                     Intrinsics.checkNotNullExpressionValue(var38, "StringBuilder().apply(builderAction).toString()");
                     var19.add(var38);
                  }
               }

               lore.add(" ");
               lore.add(ChatColor.RED + "Right click to stop brewing");
            } else {
               lore.add(ChatColor.GRAY + "Loading..");
            }
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      ItemStack var23 = new ItemStack(Material.BREWING_STAND_ITEM);
      boolean var24 = false;
      boolean var26 = false;
      ItemStack $this$render_u24lambda_u2d1 = var23;
      int var29/* $VF was: $i$a$-apply-BrewerStartButton$render$2 */ = 0;
      ItemMeta itemMeta = $this$render_u24lambda_u2d1.getItemMeta();
      itemMeta.setDisplayName(var22);
      itemMeta.setLore(lore);
      $this$render_u24lambda_u2d1.setItemMeta(itemMeta);
      return var23;
   }

   @Override
   public void handleClick(@NotNull Player player, @NotNull FancyBrewer brewer, @NotNull ClickType click) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(brewer, "brewer");
      Intrinsics.checkNotNullParameter(click, "click");
      if (brewer.getState() != FancyBrewerState.IDLE && click.isRightClick()) {
         FancyBrewerState var4 = brewer.getState();
         int var5 = BrewerStartButton.WhenMappings.$EnumSwitchMapping$0[var4.ordinal()];
         if (var5 == 2) {
            brewer.setState(FancyBrewerState.IDLE);
            brewer.refreshButtons$HCTeams();
         }
      } else if (brewer.getEstimatedTime() == -1) {
         player.sendMessage(ChatColor.RED + "This brewer does not have enough resources!");
      } else {
         brewer.setState(FancyBrewerState.BREWING);
      }
   }

   // $VF: synthetic class
   @Metadata(mv = {1, 5, 1}, k = 3, xi = 48)
   public static class WhenMappings {
      // $VF: synthetic field
      public static final int[] $EnumSwitchMapping$0;
      // $VF: synthetic field
      public static final int[] $EnumSwitchMapping$1;

      static {
         int[] var0 = new int[FancyBrewerState.values().length];
         var0[FancyBrewerState.IDLE.ordinal()] = 1;
         var0[FancyBrewerState.BREWING.ordinal()] = 2;
         $EnumSwitchMapping$0 = var0;
         var0 = new int[Material.values().length];
         var0[Material.REDSTONE.ordinal()] = 1;
         var0[Material.GLOWSTONE_DUST.ordinal()] = 2;
         $EnumSwitchMapping$1 = var0;
      }
   }
}
