package net.frozenorb.foxtrot.brewer.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import net.frozenorb.foxtrot.brewer.FancyBrewer;
import net.frozenorb.foxtrot.brewer.FancyBrewerResource;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftInventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 5, 1},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u0000 \u00072\u00020\u0001:\u0001\u0007B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0006\u0010\u0005\u001a\u00020\u0006R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\b",
   d2 = {
         "Lnet/frozenorb/foxtrot/brewer/inventory/FancyBrewerInventory;",
         "Lorg/bukkit/craftbukkit/v1_7_R4/inventory/CraftInventory;",
         "brewer",
         "Lnet/frozenorb/foxtrot/brewer/FancyBrewer;",
         "(Lnet/frozenorb/foxtrot/brewer/FancyBrewer;)V",
         "fill",
         "",
         "Companion",
         "HCTeams"
   }
)
public final class FancyBrewerInventory extends CraftInventory {
   @NotNull
   public static final FancyBrewerInventory.Companion Companion = new FancyBrewerInventory.Companion();
   @NotNull
   private final FancyBrewer brewer;
   public static final int SIZE = 54;
   @NotNull
   private static final String TITLE = "Fancy Brewer";
   @NotNull
   private static final Integer[] NON_EMPTY_SLOTS;

   public FancyBrewerInventory(@NotNull FancyBrewer brewer) {
      super(new IFancyBrewerInventory(checkedBrewer(brewer)));
      this.brewer = brewer;
      Object[] $this$forEach$iv = this.brewer.getResources();
      int $i$f$forEach = 0;

      for (Object element$iv : $this$forEach$iv) {
         ItemStack it = (ItemStack)element$iv;
         int var9/* $VF was: $i$a$-forEach-FancyBrewerInventory$1 */ = 0;
         if (it != null) {
            FancyBrewerResource.Companion var10000 = FancyBrewerResource.Companion;
            Material var10 = it.getType();
            Intrinsics.checkNotNullExpressionValue(var10, "it.type");
            FancyBrewerResource var11 = var10000.getResourceByType(var10);
            if (var11 != null) {
               FancyBrewerResource resource = var11;
               this.setItem(resource.getSlot(), it);
            }
         }
      }
   }

   private static FancyBrewer checkedBrewer(FancyBrewer brewer) {
      Intrinsics.checkNotNullParameter(brewer, "brewer");
      return brewer;
   }

   public final void fill() {
      Iterable $this$forEach$iv = new IntRange(0, this.getSize() - 1);
      int $i$f$filter = 0;
      Iterable $this$filterTo$iv$iv = $this$forEach$iv;
      ArrayList destination$iv$iv = new ArrayList();
      int $i$f$filterTo = 0;

      for (Object element$iv$iv : $this$filterTo$iv$iv) {
         int it = ((Number)element$iv$iv).intValue();
         int var9/* $VF was: $i$a$-filter-FancyBrewerInventory$fill$1 */ = 0;
         if (!ArraysKt.contains(NON_EMPTY_SLOTS, Integer.valueOf(it))) {
            destination$iv$iv.add(element$iv$iv);
         }
      }

      $this$forEach$iv = (Iterable & List)destination$iv$iv;
      $i$f$filter = 0;

      for (Object element$iv : $this$forEach$iv) {
         $i$f$filterTo = ((Number)element$iv).intValue();
         int var15/* $VF was: $i$a$-forEach-FancyBrewerInventory$fill$2 */ = 0;
         this.setItem($i$f$filterTo, FancyBrewer.Companion.getBLANK_BUTTON());
      }
   }

   static {
      Set $this$toTypedArray$iv = CollectionsKt.toMutableSet(FancyBrewerResource.Companion.getAllSlots());
      int $i$f$toTypedArray = 0;
      boolean thisCollection$iv = false;
      Set $this$NON_EMPTY_SLOTS_u24lambda_u2d3 = $this$toTypedArray$iv;
      int var4/* $VF was: $i$a$-apply-FancyBrewerInventory$Companion$NON_EMPTY_SLOTS$1 */ = 0;
      $this$NON_EMPTY_SLOTS_u24lambda_u2d3.addAll(ArraysKt.toSet(FancyBrewer.Companion.getGLASS_SLOTS()));
      Set var5 = FancyBrewer.Companion.getBUTTONS().keySet();
      Intrinsics.checkNotNullExpressionValue(var5, "FancyBrewer.BUTTONS.keys");
      $this$NON_EMPTY_SLOTS_u24lambda_u2d3.addAll(CollectionsKt.toSet(var5));
      $this$toTypedArray$iv = $this$toTypedArray$iv;
      $i$f$toTypedArray = 0;
      Collection thisCollection$ivx = $this$toTypedArray$iv;
      Object[] var10000 = thisCollection$ivx.toArray(new Integer[0]);
      if (var10000 == null) {
         throw new NullPointerException("null cannot be cast to non-null type kotlin.Array<T>");
      }

      NON_EMPTY_SLOTS = (Integer[])var10000;
   }

   @Metadata(
      mv = {1, 5, 1},
      k = 1,
      xi = 48,
      d1 = "\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u0019\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004¢\u0006\n\n\u0002\u0010\b\u001a\u0004\b\u0006\u0010\u0007R\u000e\u0010\t\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\u00020\u000bX\u0086D¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r¨\u0006\u000e",
      d2 = {
            "Lnet/frozenorb/foxtrot/brewer/inventory/FancyBrewerInventory$Companion;",
            "",
            "()V",
            "NON_EMPTY_SLOTS",
            "",
            "",
            "getNON_EMPTY_SLOTS",
            "()[Ljava/lang/Integer;",
            "[Ljava/lang/Integer;",
            "SIZE",
            "TITLE",
            "",
            "getTITLE",
            "()Ljava/lang/String;",
            "HCTeams"
      }
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final String getTITLE() {
         return FancyBrewerInventory.TITLE;
      }

      @NotNull
      public final Integer[] getNON_EMPTY_SLOTS() {
         return FancyBrewerInventory.NON_EMPTY_SLOTS;
      }

   }
}
