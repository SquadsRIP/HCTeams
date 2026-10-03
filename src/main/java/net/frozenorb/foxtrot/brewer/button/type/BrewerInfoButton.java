package net.frozenorb.foxtrot.brewer.button.type;

import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.ItemUtils;
import java.util.ArrayList;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.frozenorb.foxtrot.brewer.FancyBrewer;
import net.frozenorb.foxtrot.brewer.button.FancyBrewerButton;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 5, 1},
   k = 1,
   xi = 48,
   d1 = "\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0002J \u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0016J\u0010\u0010\u000f\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\fH\u0016¨\u0006\u0010",
   d2 = {
         "Lnet/frozenorb/foxtrot/brewer/button/type/BrewerInfoButton;",
         "Lnet/frozenorb/foxtrot/brewer/button/FancyBrewerButton;",
         "()V",
         "format",
         "",
         "item",
         "Lorg/bukkit/inventory/ItemStack;",
         "handleClick",
         "",
         "player",
         "Lorg/bukkit/entity/Player;",
         "brewer",
         "Lnet/frozenorb/foxtrot/brewer/FancyBrewer;",
         "click",
         "Lorg/bukkit/event/inventory/ClickType;",
         "render",
         "HCTeams"
   }
)
public final class BrewerInfoButton extends FancyBrewerButton {
   @NotNull
   public static final BrewerInfoButton INSTANCE = new BrewerInfoButton();

   private BrewerInfoButton() {
   }

   @NotNull
   @Override
   public ItemStack render(@NotNull FancyBrewer brewer) {
      Intrinsics.checkNotNullParameter(brewer, "brewer");
      boolean var3 = false;
      ArrayList lore = new ArrayList();
      if (brewer.isEmpty()) {
         lore.add(ChatColor.GRAY + "Empty");
      } else {
         ItemStack[] var7 = brewer.getBottles();
         int var4 = 0;
         int var5 = var7.length;

         while (var4 < var5) {
            ItemStack bottle = var7[var4];
            var4++;
            if (bottle != null) {
               lore.add(this.format(bottle));
            }
         }

         ItemStack[] var8 = brewer.getResources();
         var4 = 0;
         var5 = var8.length;

         while (var4 < var5) {
            ItemStack resource = var8[var4];
            var4++;
            if (resource != null) {
               lore.add(this.format(resource));
            }
         }
      }

      ItemStack var9 = ItemBuilder.of(Material.NAME_TAG).name("" + ChatColor.GREEN + ChatColor.BOLD + "Resources").setLore(lore).build();
      Intrinsics.checkNotNullExpressionValue(
         var9, "of(Material.NAME_TAG)\n            .name(\"${ChatColor.GREEN}${ChatColor.BOLD}Resources\")\n            .setLore(lore)\n            .build()"
      );
      return var9;
   }

   @Override
   public void handleClick(@NotNull Player player, @NotNull FancyBrewer brewer, @NotNull ClickType click) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(brewer, "brewer");
      Intrinsics.checkNotNullParameter(click, "click");
   }

   private final String format(ItemStack item) {
      boolean var2 = false;
      boolean var3 = false;
      StringBuilder var8 = new StringBuilder();
      boolean var4 = false;
      boolean var5 = false;
      StringBuilder $this$format_u24lambda_u2d0 = var8;
      int var7/* $VF was: $i$a$-buildString-BrewerInfoButton$format$1 */ = 0;
      $this$format_u24lambda_u2d0.append("" + ChatColor.GRAY + item.getAmount() + "x ");
      $this$format_u24lambda_u2d0.append(ChatColor.WHITE + ItemUtils.getName(item));
      String var10000 = var8.toString();
      Intrinsics.checkNotNullExpressionValue(var10000, "StringBuilder().apply(builderAction).toString()");
      return var10000;
   }
}
