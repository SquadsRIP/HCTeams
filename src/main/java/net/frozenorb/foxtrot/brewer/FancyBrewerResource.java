package net.frozenorb.foxtrot.brewer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 5, 1},
   k = 1,
   xi = 48,
   d1 = "\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0011\b\u0086\u0001\u0018\u0000 \u001b2\b\u0012\u0004\u0012\u00020\u00000\u00012\u0012\u0012\u0004\u0012\u00020\u00000\u0002j\b\u0012\u0004\u0012\u00020\u0000`\u0003:\u0001\u001bB3\b\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0012\u0010\t\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u000b0\n\"\u00020\u000b¢\u0006\u0002\u0010\fJ\u0018\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u00002\u0006\u0010\u0010\u001a\u00020\u0000H\u0016J\u0013\u0010\u0011\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u000b0\n¢\u0006\u0002\u0010\u0012J\u0006\u0010\u0013\u001a\u00020\u0005J\u0006\u0010\u0014\u001a\u00020\u0005J\u0006\u0010\u0015\u001a\u00020\bJ\u0006\u0010\u0016\u001a\u00020\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0018\u0010\t\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u000b0\nX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\rj\u0002\b\u0017j\u0002\b\u0018j\u0002\b\u0019j\u0002\b\u001a¨\u0006\u001c",
   d2 = {
         "Lnet/frozenorb/foxtrot/brewer/FancyBrewerResource;",
         "",
         "Ljava/util/Comparator;",
         "Lkotlin/Comparator;",
         "slot",
         "",
         "index",
         "required",
         "",
         "types",
         "",
         "Lorg/bukkit/Material;",
         "(Ljava/lang/String;IIIZ[Lorg/bukkit/Material;)V",
         "[Lorg/bukkit/Material;",
         "compare",
         "role",
         "other",
         "getAllTypes",
         "()[Lorg/bukkit/Material;",
         "getIndex",
         "getSlot",
         "isRequired",
         "next",
         "WART",
         "TYPE",
         "INCREASE",
         "SPLASH",
         "Companion",
         "HCTeams"
   }
)
public enum FancyBrewerResource implements Comparator<FancyBrewerResource> {
   WART(10, 0, true, Material.NETHER_STALK),
   TYPE(19, 1, true, Material.SUGAR, Material.SPIDER_EYE, Material.GHAST_TEAR,
      Material.MAGMA_CREAM, Material.BLAZE_POWDER, Material.GOLDEN_CARROT,
      Material.SPECKLED_MELON, Material.FERMENTED_SPIDER_EYE, Material.RAW_FISH),
   INCREASE(28, 2, false, Material.GLOWSTONE_DUST, Material.REDSTONE, Material.FERMENTED_SPIDER_EYE),
   SPLASH(37, 3, false, Material.SULPHUR);

   @NotNull
   public static final FancyBrewerResource.Companion Companion = new FancyBrewerResource.Companion();
   private final int slot;
   private final int index;
   private final boolean required;
   @NotNull
   private final Material[] types;
   @NotNull
   private static final Map<Integer, FancyBrewerResource> values;
   @NotNull
   private static final Map<Material, FancyBrewerResource> valuesByType;
   @NotNull
   private static final HashMap<Material, PotionEffectType> RESULT_TABLE;

   FancyBrewerResource(int slot, int index, boolean required, Material... types) {
      this.slot = slot;
      this.index = index;
      this.required = required;
      this.types = types;
   }

   @NotNull
   public final FancyBrewerResource next() {
      List values = Companion.getAllResources();
      return this.ordinal() >= CollectionsKt.getLastIndex(values) ? (FancyBrewerResource)values.get(0) : (FancyBrewerResource)values.get(this.ordinal() + 1);
   }

   public final int getSlot() {
      return this.slot;
   }

   public final int getIndex() {
      return this.index;
   }

   public final boolean isRequired() {
      return this.required;
   }

   @NotNull
   public final Material[] getAllTypes() {
      return this.types;
   }

   public int compare(@NotNull FancyBrewerResource role, @NotNull FancyBrewerResource other) {
      Intrinsics.checkNotNullParameter(role, "role");
      Intrinsics.checkNotNullParameter(other, "other");
      return role.ordinal() >= other.ordinal() ? 1 : 0;
   }

   static {
      // Reconstruct the enum initialization and lookup maps from the class initializer.
      values = new LinkedHashMap<Integer, FancyBrewerResource>();
      valuesByType = new LinkedHashMap<Material, FancyBrewerResource>();
      for (FancyBrewerResource resource : values()) {
         values.put(resource.slot, resource);
         for (Material type : resource.getAllTypes()) {
            valuesByType.put(type, resource);
         }
      }
      RESULT_TABLE = new HashMap<Material, PotionEffectType>();
      RESULT_TABLE.put(Material.SUGAR, PotionEffectType.SPEED);
      RESULT_TABLE.put(Material.SPIDER_EYE, PotionEffectType.POISON);
      RESULT_TABLE.put(Material.GHAST_TEAR, PotionEffectType.REGENERATION);
      RESULT_TABLE.put(Material.MAGMA_CREAM, PotionEffectType.FIRE_RESISTANCE);
      RESULT_TABLE.put(Material.BLAZE_POWDER, PotionEffectType.INCREASE_DAMAGE);
      RESULT_TABLE.put(Material.GOLDEN_CARROT, PotionEffectType.NIGHT_VISION);
      RESULT_TABLE.put(Material.SPECKLED_MELON, PotionEffectType.HEAL);
      RESULT_TABLE.put(Material.FERMENTED_SPIDER_EYE, PotionEffectType.WEAKNESS);
      RESULT_TABLE.put(Material.RAW_FISH, PotionEffectType.WATER_BREATHING);
   }

   @Metadata(
      mv = {1, 5, 1},
      k = 1,
      xi = 48,
      d1 = "\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010%\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0010\"\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0012J\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\r0\u0014J\u0010\u0010\u0015\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u0016\u001a\u00020\rJ\u0010\u0010\u0017\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u0018\u001a\u00020\u0005R=\u0010\u0003\u001a.\u0012\u0004\u0012\u00020\u0005\u0012\f\u0012\n \u0007*\u0004\u0018\u00010\u00060\u00060\u0004j\u0016\u0012\u0004\u0012\u00020\u0005\u0012\f\u0012\n \u0007*\u0004\u0018\u00010\u00060\u0006`\b¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u001a\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\u000e0\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u000e0\u0010X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0019",
      d2 = {
            "Lnet/frozenorb/foxtrot/brewer/FancyBrewerResource$Companion;",
            "",
            "()V",
            "RESULT_TABLE",
            "Ljava/util/HashMap;",
            "Lorg/bukkit/Material;",
            "Lorg/bukkit/potion/PotionEffectType;",
            "kotlin.jvm.PlatformType",
            "Lkotlin/collections/HashMap;",
            "getRESULT_TABLE",
            "()Ljava/util/HashMap;",
            "values",
            "",
            "",
            "Lnet/frozenorb/foxtrot/brewer/FancyBrewerResource;",
            "valuesByType",
            "",
            "getAllResources",
            "",
            "getAllSlots",
            "",
            "getResourceBySlot",
            "slot",
            "getResourceByType",
            "type",
            "HCTeams"
      }
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final Set<Integer> getAllSlots() {
         return FancyBrewerResource.values.keySet();
      }

      @NotNull
      public final List<FancyBrewerResource> getAllResources() {
         return CollectionsKt.toList(FancyBrewerResource.values.values());
      }

      @Nullable
      public final FancyBrewerResource getResourceBySlot(int slot) {
         return FancyBrewerResource.values.get(slot);
      }

      @Nullable
      public final FancyBrewerResource getResourceByType(@NotNull Material type) {
         Intrinsics.checkNotNullParameter(type, "type");
         return FancyBrewerResource.valuesByType.get(type);
      }

      @NotNull
      public final HashMap<Material, PotionEffectType> getRESULT_TABLE() {
         return FancyBrewerResource.RESULT_TABLE;
      }
   }
}
