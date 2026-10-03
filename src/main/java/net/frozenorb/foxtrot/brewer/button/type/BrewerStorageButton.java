package net.frozenorb.foxtrot.brewer.button.type;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import net.frozenorb.foxtrot.brewer.FancyBrewer;
import net.frozenorb.foxtrot.brewer.button.FancyBrewerButton;
import net.frozenorb.foxtrot.util.ThePotionUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 5, 1},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J \u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016J\u0010\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0007\u001a\u00020\bH\u0016¨\u0006\r",
   d2 = {
         "Lnet/frozenorb/foxtrot/brewer/button/type/BrewerStorageButton;",
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
public final class BrewerStorageButton extends FancyBrewerButton {
   @NotNull
   public static final BrewerStorageButton INSTANCE = new BrewerStorageButton();

   private BrewerStorageButton() {
   }

   @NotNull
   @Override
   public ItemStack render(@NotNull FancyBrewer brewer) {
      Intrinsics.checkNotNullParameter(brewer, "brewer");
      if (brewer.getBrewed().isEmpty()) {
         ItemStack var18 = new ItemStack(Material.MINECART);
         boolean var20 = false;
         boolean var22 = false;
         ItemStack $this$render_u24lambda_u2d0 = var18;
         int var28/* $VF was: $i$a$-apply-BrewerStorageButton$render$1 */ = 0;
         ItemMeta itemMeta = $this$render_u24lambda_u2d0.getItemMeta();
         itemMeta.setDisplayName("" + ChatColor.BLUE + ChatColor.BOLD + "Storage");
         itemMeta.setLore(CollectionsKt.listOf(ChatColor.GRAY + "Empty"));
         $this$render_u24lambda_u2d0.setItemMeta(itemMeta);
         return var18;
      }

      int total = 0;
      ArrayList lore = new ArrayList();
      total = 0;
      Iterable<Entry<Integer, AtomicInteger>> $this$sortedBy$iv = brewer.getBrewed().entrySet();
      int $i$f$sortedBy = 0;
      boolean $this$render_u24lambda_u2d3 = false;

      for (Entry var23 : CollectionsKt.sortedWith($this$sortedBy$iv, new BrewerStorageButton$render$$inlined$sortedBy$1<Entry<Integer, AtomicInteger>>())) {
         Entry var29 = var23;
         boolean var8 = false;
         $i$f$sortedBy = ((Number)var29.getKey()).intValue();
         Entry var33 = var23;
         boolean itemMeta = false;
         AtomicInteger amount = (AtomicInteger)var33.getValue();
         total += amount.get();
         if ($i$f$sortedBy != 0) {
            Potion potion = Potion.fromDamage($i$f$sortedBy);
            if (potion.getType() != PotionType.WATER) {
               itemMeta = false;
               boolean var10 = false;
               StringBuilder var38 = new StringBuilder();
               boolean var11 = false;
               boolean var12 = false;
               StringBuilder $this$render_u24lambda_u2d2 = var38;
               ArrayList var16 = lore;
               int var14/* $VF was: $i$a$-buildString-BrewerStorageButton$render$3 */ = 0;
               $this$render_u24lambda_u2d2.append("" + ChatColor.GRAY + amount.get() + 'x');
               $this$render_u24lambda_u2d2.append(" ");
               if (potion != null && potion.getType() != null) {
                  PotionEffectType var15 = potion.getType().getEffectType();
                  Intrinsics.checkNotNullExpressionValue(var15, "potion.type.effectType");
                  $this$render_u24lambda_u2d2.append(ThePotionUtil.getColor(var15));
                  if (potion.isSplash()) {
                     $this$render_u24lambda_u2d2.append("Splash");
                     $this$render_u24lambda_u2d2.append(" ");
                  }

                  var15 = potion.getType().getEffectType();
                  Intrinsics.checkNotNullExpressionValue(var15, "potion.type.effectType");
                  $this$render_u24lambda_u2d2.append(ThePotionUtil.getDisplayName(var15));
                  $this$render_u24lambda_u2d2.append(" ");
                  $this$render_u24lambda_u2d2.append(potion.getLevel() == 2 ? "II" : "");
               } else {
                  $this$render_u24lambda_u2d2.append(ChatColor.WHITE + "???");
               }

               Unit var17 = Unit.INSTANCE;
               String var10001 = var38.toString();
               Intrinsics.checkNotNullExpressionValue(var10001, "StringBuilder().apply(builderAction).toString()");
               var16.add(var10001);
            }
         }
      }

      ItemStack var21 = new ItemStack(Material.HOPPER_MINECART);
      boolean var24 = false;
      boolean var27 = false;
      ItemStack $this$render_u24lambda_u2d3x = var21;
      int var35/* $VF was: $i$a$-apply-BrewerStorageButton$render$4 */ = 0;
      ItemMeta itemMeta = $this$render_u24lambda_u2d3x.getItemMeta();
      itemMeta.setDisplayName("" + ChatColor.BLUE + ChatColor.BOLD + "Storage");
      itemMeta.setLore(lore);
      $this$render_u24lambda_u2d3x.setItemMeta(itemMeta);
      return var21;
   }

   @Override
   public void handleClick(@NotNull Player player, @NotNull FancyBrewer brewer, @NotNull ClickType click) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(brewer, "brewer");
      Intrinsics.checkNotNullParameter(click, "click");
   }
}
