package net.frozenorb.foxtrot.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 5, 1},
   k = 1,
   xi = 48,
   d1 = "\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0019\u001a\u00020\u00152\u0006\u0010\u001a\u001a\u00020\u0005H\u0007J\u0010\u0010\u001b\u001a\u00020\u00182\u0006\u0010\u001a\u001a\u00020\u0005H\u0007J\u0010\u0010\u001c\u001a\u00020\u00182\u0006\u0010\u001a\u001a\u00020\u0005H\u0007J\u0010\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001a\u001a\u00020\u0005H\u0007R<\u0010\u0003\u001a\"\u0012\f\u0012\n \u0006*\u0004\u0018\u00010\u00050\u00050\u0004j\u0010\u0012\f\u0012\n \u0006*\u0004\u0018\u00010\u00050\u0005`\u00078\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\b\u0010\u0002\u001a\u0004\b\t\u0010\nR\u001c\u0010\u000b\u001a\u00020\f8\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\r\u0010\u0002\u001a\u0004\b\u000e\u0010\u000fR\u001c\u0010\u0010\u001a\u00020\f8\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u0011\u0010\u0002\u001a\u0004\b\u0012\u0010\u000fR*\u0010\u0013\u001a\u001e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00150\u0014j\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u0015`\u0016X\u0082\u0004¢\u0006\u0002\n\u0000R*\u0010\u0017\u001a\u001e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00180\u0014j\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u0018`\u0016X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001f",
   d2 = {
         "Lnet/frozenorb/foxtrot/util/ThePotionUtil;",
         "",
         "()V",
         "DEBUFFS",
         "Ljava/util/ArrayList;",
         "Lorg/bukkit/potion/PotionEffectType;",
         "kotlin.jvm.PlatformType",
         "Lkotlin/collections/ArrayList;",
         "getDEBUFFS$annotations",
         "getDEBUFFS",
         "()Ljava/util/ArrayList;",
         "MAX_POTION_DURATION",
         "",
         "getMAX_POTION_DURATION$annotations",
         "getMAX_POTION_DURATION",
         "()J",
         "MAX_POTION_DURATION_TICK",
         "getMAX_POTION_DURATION_TICK$annotations",
         "getMAX_POTION_DURATION_TICK",
         "colors",
         "Ljava/util/HashMap;",
         "Lorg/bukkit/ChatColor;",
         "Lkotlin/collections/HashMap;",
         "names",
         "",
         "getColor",
         "type",
         "getDisplayName",
         "getName",
         "isDebuff",
         "",
         "HCTeams"
   }
)
public final class ThePotionUtil {
   @NotNull
   public static final ThePotionUtil INSTANCE = new ThePotionUtil();
   @NotNull
   private static final ArrayList<PotionEffectType> DEBUFFS;
   private static final long MAX_POTION_DURATION = TimeUnit.MINUTES.toMillis(8L);
   private static final long MAX_POTION_DURATION_TICK = TimeUnit.MINUTES.toSeconds(8L) * 20L;
   @NotNull
   private static final HashMap<PotionEffectType, String> names = new HashMap<>();
   @NotNull
   private static final HashMap<PotionEffectType, ChatColor> colors = new HashMap<>();

   private ThePotionUtil() {
   }

   @NotNull
   public static final ArrayList<PotionEffectType> getDEBUFFS() {
      return DEBUFFS;
   }

   /** @deprecated */
   // $VF: synthetic method
   @JvmStatic
   public static void getDEBUFFS$annotations() {
   }

   public static final long getMAX_POTION_DURATION() {
      return MAX_POTION_DURATION;
   }

   /** @deprecated */
   // $VF: synthetic method
   @JvmStatic
   public static void getMAX_POTION_DURATION$annotations() {
   }

   public static final long getMAX_POTION_DURATION_TICK() {
      return MAX_POTION_DURATION_TICK;
   }

   /** @deprecated */
   // $VF: synthetic method
   @JvmStatic
   public static void getMAX_POTION_DURATION_TICK$annotations() {
   }

   @JvmStatic
   @NotNull
   public static final String getName(@NotNull PotionEffectType type) {
      Intrinsics.checkNotNullParameter(type, "type");
      Map $this$getOrPut$iv = names;
      int $i$f$getOrPut = 0;
      Object value$iv = $this$getOrPut$iv.get(type);
      String var10;
      if (value$iv == null) {
         int var4/* $VF was: $i$a$-getOrPut-ThePotionUtil$getName$1 */ = 0;
         String var5 = type.getName();
         Intrinsics.checkNotNullExpressionValue(var5, "type.name");
         var5 = var5;
         boolean var6 = false;
         var10 = var5.toLowerCase(Locale.ROOT);
         Intrinsics.checkNotNullExpressionValue(var10, "(this as java.lang.Strin….toLowerCase(Locale.ROOT)");
         String var7 = WordUtils.capitalizeFully(StringsKt.replace(var10, " ", "", false));
         Intrinsics.checkNotNullExpressionValue(var7, "capitalizeFully(type.name.lowercase().replace(\" \",\"\"))");
         Object answer$iv = var7;
         $this$getOrPut$iv.put(type, answer$iv);
         var10 = (String)answer$iv;
      } else {
         var10 = (String)value$iv;
      }

      return var10;
   }

   @JvmStatic
   @NotNull
   public static final ChatColor getColor(@NotNull PotionEffectType type) {
      Intrinsics.checkNotNullParameter(type, "type");
      ChatColor var1 = colors.get(type);
      return var1 == null ? ChatColor.RED : var1;
   }

   @JvmStatic
   @NotNull
   public static final String getDisplayName(@NotNull PotionEffectType type) {
      Intrinsics.checkNotNullParameter(type, "type");
      StringBuilder var10000 = new StringBuilder();
      ChatColor var1 = colors.get(type);
      return var10000.append(var1 == null ? ChatColor.WHITE : var1).append(getName(type)).toString();
   }

   @JvmStatic
   public static final boolean isDebuff(@NotNull PotionEffectType type) {
      Intrinsics.checkNotNullParameter(type, "type");
      return DEBUFFS.contains(type);
   }

   static {
      PotionEffectType[] var0 = new PotionEffectType[]{
         PotionEffectType.HARM,
         PotionEffectType.SLOW,
         PotionEffectType.HUNGER,
         PotionEffectType.POISON,
         PotionEffectType.WITHER,
         PotionEffectType.WEAKNESS,
         PotionEffectType.CONFUSION,
         PotionEffectType.BLINDNESS,
         PotionEffectType.SLOW_DIGGING
      };
      DEBUFFS = CollectionsKt.arrayListOf(var0);
      boolean var4 = false;
      boolean var5 = false;
      Map var6 = names;
      PotionEffectType var1 = PotionEffectType.HEAL;
      Intrinsics.checkNotNullExpressionValue(var1, "HEAL");
      var1 = var1;
      String var2 = "Healing";
      boolean var3 = false;
      var6.put(var1, var2);
      Map var7 = names;
      var1 = PotionEffectType.HARM;
      Intrinsics.checkNotNullExpressionValue(var1, "HARM");
      var1 = var1;
      var2 = "Harming";
      var3 = false;
      var7.put(var1, var2);
      Map var8 = names;
      var1 = PotionEffectType.SLOW;
      Intrinsics.checkNotNullExpressionValue(var1, "SLOW");
      var1 = var1;
      var2 = "Slowness";
      var3 = false;
      var8.put(var1, var2);
      Map var9 = names;
      var1 = PotionEffectType.JUMP;
      Intrinsics.checkNotNullExpressionValue(var1, "JUMP");
      var1 = var1;
      var2 = "Jump Boost";
      var3 = false;
      var9.put(var1, var2);
      Map var10 = names;
      var1 = PotionEffectType.SPEED;
      Intrinsics.checkNotNullExpressionValue(var1, "SPEED");
      var1 = var1;
      var2 = "Speed";
      var3 = false;
      var10.put(var1, var2);
      Map var11 = names;
      var1 = PotionEffectType.HUNGER;
      Intrinsics.checkNotNullExpressionValue(var1, "HUNGER");
      var1 = var1;
      var2 = "Hunger";
      var3 = false;
      var11.put(var1, var2);
      Map var12 = names;
      var1 = PotionEffectType.POISON;
      Intrinsics.checkNotNullExpressionValue(var1, "POISON");
      var1 = var1;
      var2 = "Poison";
      var3 = false;
      var12.put(var1, var2);
      Map var13 = names;
      var1 = PotionEffectType.WITHER;
      Intrinsics.checkNotNullExpressionValue(var1, "WITHER");
      var1 = var1;
      var2 = "Wither";
      var3 = false;
      var13.put(var1, var2);
      Map var14 = names;
      var1 = PotionEffectType.WEAKNESS;
      Intrinsics.checkNotNullExpressionValue(var1, "WEAKNESS");
      var1 = var1;
      var2 = "Weakness";
      var3 = false;
      var14.put(var1, var2);
      Map var15 = names;
      var1 = PotionEffectType.CONFUSION;
      Intrinsics.checkNotNullExpressionValue(var1, "CONFUSION");
      var1 = var1;
      var2 = "Nausea";
      var3 = false;
      var15.put(var1, var2);
      Map var16 = names;
      var1 = PotionEffectType.BLINDNESS;
      Intrinsics.checkNotNullExpressionValue(var1, "BLINDNESS");
      var1 = var1;
      var2 = "Blindness";
      var3 = false;
      var16.put(var1, var2);
      Map var17 = names;
      var1 = PotionEffectType.SATURATION;
      Intrinsics.checkNotNullExpressionValue(var1, "SATURATION");
      var1 = var1;
      var2 = "Saturation";
      var3 = false;
      var17.put(var1, var2);
      Map var18 = names;
      var1 = PotionEffectType.ABSORPTION;
      Intrinsics.checkNotNullExpressionValue(var1, "ABSORPTION");
      var1 = var1;
      var2 = "Absorption";
      var3 = false;
      var18.put(var1, var2);
      Map var19 = names;
      var1 = PotionEffectType.REGENERATION;
      Intrinsics.checkNotNullExpressionValue(var1, "REGENERATION");
      var1 = var1;
      var2 = "Regeneration";
      var3 = false;
      var19.put(var1, var2);
      Map var20 = names;
      var1 = PotionEffectType.FAST_DIGGING;
      Intrinsics.checkNotNullExpressionValue(var1, "FAST_DIGGING");
      var1 = var1;
      var2 = "Haste";
      var3 = false;
      var20.put(var1, var2);
      Map var21 = names;
      var1 = PotionEffectType.SLOW_DIGGING;
      Intrinsics.checkNotNullExpressionValue(var1, "SLOW_DIGGING");
      var1 = var1;
      var2 = "Mining Fatigue";
      var3 = false;
      var21.put(var1, var2);
      Map var22 = names;
      var1 = PotionEffectType.NIGHT_VISION;
      Intrinsics.checkNotNullExpressionValue(var1, "NIGHT_VISION");
      var1 = var1;
      var2 = "Night Vision";
      var3 = false;
      var22.put(var1, var2);
      Map var23 = names;
      var1 = PotionEffectType.INVISIBILITY;
      Intrinsics.checkNotNullExpressionValue(var1, "INVISIBILITY");
      var1 = var1;
      var2 = "Invisibility";
      var3 = false;
      var23.put(var1, var2);
      Map var24 = names;
      var1 = PotionEffectType.HEALTH_BOOST;
      Intrinsics.checkNotNullExpressionValue(var1, "HEALTH_BOOST");
      var1 = var1;
      var2 = "Health Boost";
      var3 = false;
      var24.put(var1, var2);
      Map var25 = names;
      var1 = PotionEffectType.INCREASE_DAMAGE;
      Intrinsics.checkNotNullExpressionValue(var1, "INCREASE_DAMAGE");
      var1 = var1;
      var2 = "Strength";
      var3 = false;
      var25.put(var1, var2);
      Map var26 = names;
      var1 = PotionEffectType.WATER_BREATHING;
      Intrinsics.checkNotNullExpressionValue(var1, "WATER_BREATHING");
      var1 = var1;
      var2 = "Water Breathing";
      var3 = false;
      var26.put(var1, var2);
      Map var27 = names;
      var1 = PotionEffectType.FIRE_RESISTANCE;
      Intrinsics.checkNotNullExpressionValue(var1, "FIRE_RESISTANCE");
      var1 = var1;
      var2 = "Fire Resistance";
      var3 = false;
      var27.put(var1, var2);
      Map var28 = names;
      var1 = PotionEffectType.DAMAGE_RESISTANCE;
      Intrinsics.checkNotNullExpressionValue(var1, "DAMAGE_RESISTANCE");
      var1 = var1;
      var2 = "Resistance";
      var3 = false;
      var28.put(var1, var2);
      Map var29 = colors;
      var1 = PotionEffectType.HEAL;
      Intrinsics.checkNotNullExpressionValue(var1, "HEAL");
      var1 = var1;
      ChatColor var165 = ChatColor.RED;
      var3 = false;
      var29.put(var1, var165);
      Map var30 = colors;
      var1 = PotionEffectType.HARM;
      Intrinsics.checkNotNullExpressionValue(var1, "HARM");
      var1 = var1;
      ChatColor var166 = ChatColor.DARK_RED;
      var3 = false;
      var30.put(var1, var166);
      Map var31 = colors;
      var1 = PotionEffectType.SLOW;
      Intrinsics.checkNotNullExpressionValue(var1, "SLOW");
      var1 = var1;
      ChatColor var167 = ChatColor.GRAY;
      var3 = false;
      var31.put(var1, var167);
      Map var32 = colors;
      var1 = PotionEffectType.JUMP;
      Intrinsics.checkNotNullExpressionValue(var1, "JUMP");
      var1 = var1;
      ChatColor var168 = ChatColor.GREEN;
      var3 = false;
      var32.put(var1, var168);
      Map var33 = colors;
      var1 = PotionEffectType.SPEED;
      Intrinsics.checkNotNullExpressionValue(var1, "SPEED");
      var1 = var1;
      ChatColor var169 = ChatColor.AQUA;
      var3 = false;
      var33.put(var1, var169);
      Map var34 = colors;
      var1 = PotionEffectType.HUNGER;
      Intrinsics.checkNotNullExpressionValue(var1, "HUNGER");
      var1 = var1;
      ChatColor var170 = ChatColor.DARK_GREEN;
      var3 = false;
      var34.put(var1, var170);
      Map var35 = colors;
      var1 = PotionEffectType.POISON;
      Intrinsics.checkNotNullExpressionValue(var1, "POISON");
      var1 = var1;
      ChatColor var171 = ChatColor.DARK_GREEN;
      var3 = false;
      var35.put(var1, var171);
      Map var36 = colors;
      var1 = PotionEffectType.WITHER;
      Intrinsics.checkNotNullExpressionValue(var1, "WITHER");
      var1 = var1;
      ChatColor var172 = ChatColor.DARK_PURPLE;
      var3 = false;
      var36.put(var1, var172);
      Map var37 = colors;
      var1 = PotionEffectType.WEAKNESS;
      Intrinsics.checkNotNullExpressionValue(var1, "WEAKNESS");
      var1 = var1;
      ChatColor var173 = ChatColor.DARK_GRAY;
      var3 = false;
      var37.put(var1, var173);
      Map var38 = colors;
      var1 = PotionEffectType.CONFUSION;
      Intrinsics.checkNotNullExpressionValue(var1, "CONFUSION");
      var1 = var1;
      ChatColor var174 = ChatColor.DARK_PURPLE;
      var3 = false;
      var38.put(var1, var174);
      Map var39 = colors;
      var1 = PotionEffectType.BLINDNESS;
      Intrinsics.checkNotNullExpressionValue(var1, "BLINDNESS");
      var1 = var1;
      ChatColor var175 = ChatColor.DARK_GRAY;
      var3 = false;
      var39.put(var1, var175);
      Map var40 = colors;
      var1 = PotionEffectType.SATURATION;
      Intrinsics.checkNotNullExpressionValue(var1, "SATURATION");
      var1 = var1;
      ChatColor var176 = ChatColor.RED;
      var3 = false;
      var40.put(var1, var176);
      Map var41 = colors;
      var1 = PotionEffectType.ABSORPTION;
      Intrinsics.checkNotNullExpressionValue(var1, "ABSORPTION");
      var1 = var1;
      ChatColor var177 = ChatColor.YELLOW;
      var3 = false;
      var41.put(var1, var177);
      Map var42 = colors;
      var1 = PotionEffectType.REGENERATION;
      Intrinsics.checkNotNullExpressionValue(var1, "REGENERATION");
      var1 = var1;
      ChatColor var178 = ChatColor.LIGHT_PURPLE;
      var3 = false;
      var42.put(var1, var178);
      Map var43 = colors;
      var1 = PotionEffectType.FAST_DIGGING;
      Intrinsics.checkNotNullExpressionValue(var1, "FAST_DIGGING");
      var1 = var1;
      ChatColor var179 = ChatColor.BLUE;
      var3 = false;
      var43.put(var1, var179);
      Map var44 = colors;
      var1 = PotionEffectType.SLOW_DIGGING;
      Intrinsics.checkNotNullExpressionValue(var1, "SLOW_DIGGING");
      var1 = var1;
      ChatColor var180 = ChatColor.RED;
      var3 = false;
      var44.put(var1, var180);
      Map var45 = colors;
      var1 = PotionEffectType.NIGHT_VISION;
      Intrinsics.checkNotNullExpressionValue(var1, "NIGHT_VISION");
      var1 = var1;
      ChatColor var181 = ChatColor.DARK_BLUE;
      var3 = false;
      var45.put(var1, var181);
      Map var46 = colors;
      var1 = PotionEffectType.INVISIBILITY;
      Intrinsics.checkNotNullExpressionValue(var1, "INVISIBILITY");
      var1 = var1;
      ChatColor var182 = ChatColor.GRAY;
      var3 = false;
      var46.put(var1, var182);
      Map var47 = colors;
      var1 = PotionEffectType.HEALTH_BOOST;
      Intrinsics.checkNotNullExpressionValue(var1, "HEALTH_BOOST");
      var1 = var1;
      ChatColor var183 = ChatColor.RED;
      var3 = false;
      var47.put(var1, var183);
      Map var48 = colors;
      var1 = PotionEffectType.INCREASE_DAMAGE;
      Intrinsics.checkNotNullExpressionValue(var1, "INCREASE_DAMAGE");
      var1 = var1;
      ChatColor var184 = ChatColor.RED;
      var3 = false;
      var48.put(var1, var184);
      Map var49 = colors;
      var1 = PotionEffectType.WATER_BREATHING;
      Intrinsics.checkNotNullExpressionValue(var1, "WATER_BREATHING");
      var1 = var1;
      ChatColor var185 = ChatColor.BLUE;
      var3 = false;
      var49.put(var1, var185);
      Map var50 = colors;
      var1 = PotionEffectType.FIRE_RESISTANCE;
      Intrinsics.checkNotNullExpressionValue(var1, "FIRE_RESISTANCE");
      var1 = var1;
      ChatColor var186 = ChatColor.GOLD;
      var3 = false;
      var50.put(var1, var186);
      Map var51 = colors;
      var1 = PotionEffectType.DAMAGE_RESISTANCE;
      Intrinsics.checkNotNullExpressionValue(var1, "DAMAGE_RESISTANCE");
      var1 = var1;
      ChatColor var187 = ChatColor.GRAY;
      var3 = false;
      var51.put(var1, var187);
   }
}
