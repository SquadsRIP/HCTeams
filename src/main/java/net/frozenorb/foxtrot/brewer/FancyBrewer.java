package net.frozenorb.foxtrot.brewer;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonClass;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IndexedValue;
import kotlin.collections.MapsKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.Intrinsics;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.brewer.button.FancyBrewerButton;
import net.frozenorb.foxtrot.brewer.button.type.BrewerStartButton;
import net.frozenorb.foxtrot.brewer.button.type.BrewerStorageButton;
import net.frozenorb.foxtrot.brewer.button.type.BrewerTransferButton;
import net.frozenorb.foxtrot.brewer.button.type.BrewerValueButton;
import net.frozenorb.foxtrot.brewer.inventory.FancyBrewerInventory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonClass(generateAdapter = true)
@Metadata(
   mv = {1, 5, 1},
   k = 1,
   xi = 48,
   d1 = "\u0000\u008e\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010%\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u001a\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0087\b\u0018\u0000 \u0088\u00012\u00020\u0001:\u0002\u0088\u0001B\u001f\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007J\u000e\u0010a\u001a\u00020b2\u0006\u0010c\u001a\u00020\u0012J\u0018\u0010d\u001a\u00020\u001f2\u0006\u0010e\u001a\u00020\u001f2\u0006\u0010f\u001a\u00020DH\u0002J\t\u0010g\u001a\u00020\u0003HÆ\u0003J\t\u0010h\u001a\u00020\u0003HÆ\u0003J\t\u0010i\u001a\u00020\u0006HÆ\u0003J\u000e\u0010j\u001a\u00020-2\u0006\u0010k\u001a\u00020>J'\u0010l\u001a\u00020\u00002\b\b\u0003\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006HÆ\u0001J\u0013\u0010m\u001a\u00020-2\b\u0010n\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\u0006\u0010o\u001a\u00020bJ\u0006\u0010p\u001a\u00020\u0012J\u0013\u0010q\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\t¢\u0006\u0002\u0010\fJ\u0013\u0010r\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\t¢\u0006\u0002\u0010\fJ\u0006\u0010s\u001a\u00020\u0012J\u0010\u0010t\u001a\u0004\u0018\u00010\n2\u0006\u0010u\u001a\u00020\u0012J\u0010\u0010v\u001a\u0004\u0018\u00010\n2\u0006\u0010k\u001a\u00020>J\t\u0010w\u001a\u00020\u0012HÖ\u0001J\u0006\u0010x\u001a\u00020bJ\u0016\u0010y\u001a\u00020-2\u0006\u0010u\u001a\u00020\u00122\u0006\u0010f\u001a\u00020\nJ\u0006\u0010z\u001a\u00020-J\u000e\u0010{\u001a\u00020b2\u0006\u0010|\u001a\u00020}J\b\u0010~\u001a\u00020bH\u0002J\u000e\u0010\u007f\u001a\u00020bH\u0000¢\u0006\u0003\b\u0080\u0001J\u0019\u0010\r\u001a\u00020b2\u0007\u0010\u0081\u0001\u001a\u00020\u00122\b\u0010f\u001a\u0004\u0018\u00010\nJ\u0018\u0010(\u001a\u00020b2\b\u0010$\u001a\u0004\u0018\u00010%H\u0007¢\u0006\u0003\b\u0082\u0001J\u0019\u0010\u0083\u0001\u001a\u00020b2\u0006\u0010k\u001a\u00020>2\b\u0010f\u001a\u0004\u0018\u00010\nJ\t\u0010\u0084\u0001\u001a\u00020bH\u0002J\u0007\u0010\u0085\u0001\u001a\u00020bJ\u000b\u0010\u0086\u0001\u001a\u00030\u0087\u0001HÖ\u0001R$\u0010\b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\tX\u0086\u000e¢\u0006\u0010\n\u0002\u0010\u000f\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR&\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u00130\u0011X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\u001e\u0010\u0018\u001a\u0004\u0018\u00010\u0012X\u0086\u000e¢\u0006\u0010\n\u0002\u0010\u001d\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001cR\u001c\u0010\u001e\u001a\u0004\u0018\u00010\u001fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b \u0010!\"\u0004\b\"\u0010#R\u001c\u0010$\u001a\u0004\u0018\u00010%X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b&\u0010'\"\u0004\b(\u0010)R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b*\u0010+R\u001a\u0010,\u001a\u00020-X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b.\u0010/\"\u0004\b0\u00101R\u001a\u00102\u001a\u000203X\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\b4\u00105\"\u0004\b6\u00107R\u001a\u00108\u001a\u00020\u0012X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b9\u0010:\"\u0004\b;\u0010<R\u001c\u0010=\u001a\u0004\u0018\u00010>X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b?\u0010@\"\u0004\bA\u0010BR\u001c\u0010C\u001a\u0004\u0018\u00010DX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bE\u0010F\"\u0004\bG\u0010HR\u001a\u0010I\u001a\u00020\u0012X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bJ\u0010:\"\u0004\bK\u0010<R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\bL\u0010MR\u001a\u0010N\u001a\u00020-X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\bO\u0010/\"\u0004\bP\u00101R\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\bQ\u0010+R$\u0010R\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\tX\u0086\u000e¢\u0006\u0010\n\u0002\u0010\u000f\u001a\u0004\bS\u0010\f\"\u0004\bT\u0010\u000eR\u001a\u0010U\u001a\u00020VX\u0086.¢\u0006\u000e\n\u0000\u001a\u0004\bW\u0010X\"\u0004\bY\u0010ZR\u001a\u0010[\u001a\u00020\\X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b]\u0010^\"\u0004\b_\u0010`¨\u0006\u0089\u0001",
   d2 = {
         "Lnet/frozenorb/foxtrot/brewer/FancyBrewer;",
         "",
         "id",
         "Ljava/util/UUID;",
         "owner",
         "location",
         "Lorg/bukkit/Location;",
         "(Ljava/util/UUID;Ljava/util/UUID;Lorg/bukkit/Location;)V",
         "bottles",
         "",
         "Lorg/bukkit/inventory/ItemStack;",
         "getBottles",
         "()[Lorg/bukkit/inventory/ItemStack;",
         "setBottles",
         "([Lorg/bukkit/inventory/ItemStack;)V",
         "[Lorg/bukkit/inventory/ItemStack;",
         "brewed",
         "",
         "",
         "Ljava/util/concurrent/atomic/AtomicInteger;",
         "getBrewed",
         "()Ljava/util/Map;",
         "setBrewed",
         "(Ljava/util/Map;)V",
         "currentAmount",
         "getCurrentAmount",
         "()Ljava/lang/Integer;",
         "setCurrentAmount",
         "(Ljava/lang/Integer;)V",
         "Ljava/lang/Integer;",
         "currentlyBrewing",
         "Lorg/bukkit/potion/Potion;",
         "getCurrentlyBrewing",
         "()Lorg/bukkit/potion/Potion;",
         "setCurrentlyBrewing",
         "(Lorg/bukkit/potion/Potion;)V",
         "hopper",
         "Lorg/bukkit/block/Hopper;",
         "getHopper",
         "()Lorg/bukkit/block/Hopper;",
         "setHopper",
         "(Lorg/bukkit/block/Hopper;)V",
         "getId",
         "()Ljava/util/UUID;",
         "initialized",
         "",
         "getInitialized",
         "()Z",
         "setInitialized",
         "(Z)V",
         "inventory",
         "Lnet/frozenorb/foxtrot/brewer/inventory/FancyBrewerInventory;",
         "getInventory",
         "()Lnet/frozenorb/foxtrot/brewer/inventory/FancyBrewerInventory;",
         "setInventory",
         "(Lnet/frozenorb/foxtrot/brewer/inventory/FancyBrewerInventory;)V",
         "lastHopperTick",
         "getLastHopperTick",
         "()I",
         "setLastHopperTick",
         "(I)V",
         "lastResource",
         "Lnet/frozenorb/foxtrot/brewer/FancyBrewerResource;",
         "getLastResource",
         "()Lnet/frozenorb/foxtrot/brewer/FancyBrewerResource;",
         "setLastResource",
         "(Lnet/frozenorb/foxtrot/brewer/FancyBrewerResource;)V",
         "lastResourceType",
         "Lorg/bukkit/Material;",
         "getLastResourceType",
         "()Lorg/bukkit/Material;",
         "setLastResourceType",
         "(Lorg/bukkit/Material;)V",
         "lastTick",
         "getLastTick",
         "setLastTick",
         "getLocation",
         "()Lorg/bukkit/Location;",
         "locked",
         "getLocked",
         "setLocked",
         "getOwner",
         "resources",
         "getResources",
         "setResources",
         "stand",
         "Lorg/bukkit/block/BrewingStand;",
         "getStand",
         "()Lorg/bukkit/block/BrewingStand;",
         "setStand",
         "(Lorg/bukkit/block/BrewingStand;)V",
         "state",
         "Lnet/frozenorb/foxtrot/brewer/FancyBrewerState;",
         "getState",
         "()Lnet/frozenorb/foxtrot/brewer/FancyBrewerState;",
         "setState",
         "(Lnet/frozenorb/foxtrot/brewer/FancyBrewerState;)V",
         "addBottles",
         "",
         "amount",
         "combine",
         "potion",
         "item",
         "component1",
         "component2",
         "component3",
         "containsResource",
         "resource",
         "copy",
         "equals",
         "other",
         "flagForSave",
         "getAllBottles",
         "getAllDrops",
         "getAllResources",
         "getEstimatedTime",
         "getResourceBySlot",
         "slot",
         "getResourceByType",
         "hashCode",
         "init",
         "isAllowedToInsert",
         "isEmpty",
         "openInventory",
         "player",
         "Lorg/bukkit/entity/Player;",
         "refreshBottles",
         "refreshButtons",
         "refreshButtons$HCTeams",
         "index",
         "setHopper1",
         "setResource",
         "stop",
         "tick",
         "toString",
         "",
         "Companion",
         "HCTeams"
   }
)
public final class FancyBrewer {
   @NotNull
   public static final FancyBrewer.Companion Companion = new FancyBrewer.Companion();
   @NotNull
   private final UUID id;
   @NotNull
   private final UUID owner;
   @NotNull
   private final Location location;
   @NotNull
   private FancyBrewerState state;
   private boolean locked;
   private int lastTick;
   private int lastHopperTick;
   @Nullable
   private FancyBrewerResource lastResource;
   @Nullable
   private Material lastResourceType;
   @NotNull
   private ItemStack[] bottles;
   @NotNull
   private ItemStack[] resources;
   @NotNull
   private Map<Integer, AtomicInteger> brewed;
   @Nullable
   private Integer currentAmount;
   @Nullable
   private Potion currentlyBrewing;
   private transient boolean initialized;
   @Nullable
   private transient Hopper hopper;
   public transient BrewingStand stand;
   public transient FancyBrewerInventory inventory;
   @NotNull
   private static final HashMap<Integer, FancyBrewerButton> BUTTONS;
   @NotNull
   private static final int[] GLASS_SLOTS;
   private static final int MAX_BOTTLES;
   public static final int HOPPER_TICK = 3;
   public static final int BREW_INTERVAL = 5;
   public static final int BOTTLES_PER_RESULT = 3;
   public static final int REQUIRED_ROTATIONS = 3;
   @NotNull
   private static final ItemStack BLANK_BUTTON;

   public FancyBrewer(@Json(name = "_id") @NotNull UUID id, @NotNull UUID owner, @NotNull Location location) {
      Intrinsics.checkNotNullParameter(id, "id");
      Intrinsics.checkNotNullParameter(owner, "owner");
      Intrinsics.checkNotNullParameter(location, "location");
      this.id = id;
      this.owner = owner;
      this.location = location;
      this.state = FancyBrewerState.IDLE;
      this.lastHopperTick = 3;
      this.bottles = new ItemStack[3];
      this.resources = new ItemStack[FancyBrewerResource.Companion.getAllResources().size()];
      boolean var4 = false;
      this.brewed = new LinkedHashMap<>();
   }

   @NotNull
   public final UUID getId() {
      return this.id;
   }

   @NotNull
   public final UUID getOwner() {
      return this.owner;
   }

   @NotNull
   public final Location getLocation() {
      return this.location;
   }

   @NotNull
   public final FancyBrewerState getState() {
      return this.state;
   }

   public final void setState(@NotNull FancyBrewerState var1) {
      Intrinsics.checkNotNullParameter(var1/* $VF was: <set-?> */, "<set-?>");
      this.state = var1/* $VF was: <set-?> */;
   }

   public final boolean getLocked() {
      return this.locked;
   }

   public final void setLocked(boolean var1) {
      this.locked = var1/* $VF was: <set-?> */;
   }

   public final int getLastTick() {
      return this.lastTick;
   }

   public final void setLastTick(int var1) {
      this.lastTick = var1/* $VF was: <set-?> */;
   }

   public final int getLastHopperTick() {
      return this.lastHopperTick;
   }

   public final void setLastHopperTick(int var1) {
      this.lastHopperTick = var1/* $VF was: <set-?> */;
   }

   @Nullable
   public final FancyBrewerResource getLastResource() {
      return this.lastResource;
   }

   public final void setLastResource(@Nullable FancyBrewerResource var1) {
      this.lastResource = var1/* $VF was: <set-?> */;
   }

   @Nullable
   public final Material getLastResourceType() {
      return this.lastResourceType;
   }

   public final void setLastResourceType(@Nullable Material var1) {
      this.lastResourceType = var1/* $VF was: <set-?> */;
   }

   @NotNull
   public final ItemStack[] getBottles() {
      return this.bottles;
   }

   public final void setBottles(@NotNull ItemStack[] var1) {
      Intrinsics.checkNotNullParameter(var1/* $VF was: <set-?> */, "<set-?>");
      this.bottles = var1/* $VF was: <set-?> */;
   }

   @NotNull
   public final ItemStack[] getResources() {
      return this.resources;
   }

   public final void setResources(@NotNull ItemStack[] var1) {
      Intrinsics.checkNotNullParameter(var1/* $VF was: <set-?> */, "<set-?>");
      this.resources = var1/* $VF was: <set-?> */;
   }

   @NotNull
   public final Map<Integer, AtomicInteger> getBrewed() {
      return this.brewed;
   }

   public final void setBrewed(@NotNull Map<Integer, AtomicInteger> var1) {
      Intrinsics.checkNotNullParameter(var1/* $VF was: <set-?> */, "<set-?>");
      this.brewed = var1/* $VF was: <set-?> */;
   }

   @Nullable
   public final Integer getCurrentAmount() {
      return this.currentAmount;
   }

   public final void setCurrentAmount(@Nullable Integer var1) {
      this.currentAmount = var1/* $VF was: <set-?> */;
   }

   @Nullable
   public final Potion getCurrentlyBrewing() {
      return this.currentlyBrewing;
   }

   public final void setCurrentlyBrewing(@Nullable Potion var1) {
      this.currentlyBrewing = var1/* $VF was: <set-?> */;
   }

   public final boolean getInitialized() {
      return this.initialized;
   }

   public final void setInitialized(boolean var1) {
      this.initialized = var1/* $VF was: <set-?> */;
   }

   @Nullable
   public final Hopper getHopper() {
      return this.hopper;
   }

   public final void setHopper(@Nullable Hopper var1) {
      this.hopper = var1/* $VF was: <set-?> */;
   }

   @NotNull
   public final BrewingStand getStand() {
      BrewingStand var1 = this.stand;
      if (var1 != null) {
         return var1;
      }

      Intrinsics.throwUninitializedPropertyAccessException("stand");
      throw null;
   }

   public final void setStand(@NotNull BrewingStand var1) {
      Intrinsics.checkNotNullParameter(var1/* $VF was: <set-?> */, "<set-?>");
      this.stand = var1/* $VF was: <set-?> */;
   }

   @NotNull
   public final FancyBrewerInventory getInventory() {
      FancyBrewerInventory var1 = this.inventory;
      if (var1 != null) {
         return var1;
      }

      Intrinsics.throwUninitializedPropertyAccessException("inventory");
      throw null;
   }

   public final void setInventory(@NotNull FancyBrewerInventory var1) {
      Intrinsics.checkNotNullParameter(var1/* $VF was: <set-?> */, "<set-?>");
      this.inventory = var1/* $VF was: <set-?> */;
   }

   public final void init() {
      if (this.location.getBlock().getState() instanceof BrewingStand) {
         BlockState var1 = this.location.getBlock().getState();
         if (var1 == null) {
            throw new NullPointerException("null cannot be cast to non-null type org.bukkit.block.BrewingStand");
         }

         this.setStand((BrewingStand)var1);
         Block var2 = this.location.getBlock().getRelative(BlockFace.DOWN);
         var1 = var2 == null ? null : var2.getState();
         this.hopper = var1 instanceof Hopper ? (Hopper)var1 : null;
         this.setInventory(new FancyBrewerInventory(this));
         Map<Integer, FancyBrewerButton> var10 = BUTTONS;
         int index = 0;

         for (Entry var11 : var10.entrySet()) {
            Entry indexx = var11;
            boolean item = false;
            index = ((Number)indexx.getKey()).intValue();
            Entry var17 = var11;
            boolean var6 = false;
            FancyBrewerButton button = (FancyBrewerButton)var17.getValue();
            ItemStack rendered = button.render(this);
            if (rendered == null) {
               this.getInventory().setItem(index, BLANK_BUTTON);
            } else {
               this.getInventory().setItem(index, rendered);
            }
         }

         ItemStack[] var9 = this.bottles;
         int var12 = 0;
         index = var9.length;

         while (var12 < index) {
            int indexx = var12;
            ItemStack item = var9[var12];
            var12++;
            this.getInventory().setItem(GLASS_SLOTS[indexx], item);
         }

         this.getInventory().fill();
         this.initialized = true;
      }
   }

   public final void openInventory(@NotNull Player player) {
      Intrinsics.checkNotNullParameter(player, "player");
      player.openInventory((Inventory)this.getInventory());
   }

   private final void refreshBottles() {
      int[] $this$map$iv = GLASS_SLOTS;
      int $i$f$map = 0;
      int[] thisCollection$iv = $this$map$iv;
      ArrayList destination$iv$iv = new ArrayList($this$map$iv.length);
      int $i$f$mapTo = 0;

      for (int item$iv$iv : thisCollection$iv) {
         int it = item$iv$iv;
         Collection var13 = destination$iv$iv;
         int var12/* $VF was: $i$a$-map-FancyBrewer$refreshBottles$bottles$1 */ = 0;
         ItemStack var14 = this.getInventory().getItem(it);
         var13.add(var14);
      }

      List var16 = (List)destination$iv$iv;
      $i$f$map = 0;
      Collection thisCollection$ivx = var16;
      Object[] var10000 = thisCollection$ivx.toArray(new ItemStack[0]);
      if (var10000 == null) {
         throw new NullPointerException("null cannot be cast to non-null type kotlin.Array<T>");
      }

      ItemStack[] bottles = (ItemStack[])var10000;
      ItemStack[] var17 = bottles;
      boolean var20 = false;
      int var23 = 0;

      for (ItemStack var31 : var17) {
         ItemStack var33 = var31;
         int var41 = var23;
         int var35/* $VF was: $i$a$-sumOfInt-FancyBrewer$refreshBottles$1 */ = 0;
         ItemStack var37 = var33;
         int var46;
         if (var37 == null) {
            var46 = 0;
         } else {
            int var39 = var37.getAmount();
            var46 = var39;
         }

         int var43 = var46;
         var23 = var41 + var43;
      }

      ItemStack[] var18 = this.bottles;
      int var42 = var23;
      var20 = false;
      int var24 = 0;

      for (ItemStack var32 : var18) {
         ItemStack var34 = var32;
         int var44 = var24;
         int var36/* $VF was: $i$a$-sumOfInt-FancyBrewer$refreshBottles$2 */ = 0;
         ItemStack var38 = var34;
         int var47;
         if (var38 == null) {
            var47 = 0;
         } else {
            int var40 = var38.getAmount();
            var47 = var40;
         }

         int var15 = var47;
         var24 = var44 + var15;
      }

      int var45 = var24;
      if (var42 != var45) {
         this.flagForSave();
         this.refreshButtons$HCTeams();
      }

      this.bottles = bottles;
   }

   public final void refreshButtons$HCTeams() {
      Map<Integer, FancyBrewerButton> var2 = BUTTONS;
      int index = 0;

      for (Entry var7 : var2.entrySet()) {
         Entry button = var7;
         boolean rendered = false;
         index = ((Number)button.getKey()).intValue();
         Entry var10 = var7;
         boolean var6 = false;
         FancyBrewerButton buttonx = (FancyBrewerButton)var10.getValue();
         ItemStack renderedx = buttonx.render(this);
         if (renderedx == null) {
            if (this.getInventory().getItem(index) != null) {
               this.getInventory().setItem(index, BLANK_BUTTON);
            }
         } else {
            this.getInventory().setItem(index, renderedx);
         }
      }
   }

   public final boolean isEmpty() {
      Object[] $this$all$iv = this.bottles;
      int $i$f$all = 0;
      Object[] var3 = $this$all$iv;
      int var4 = var3.length;
      int var5 = 0;

      boolean var10000;
      while (true) {
         if (var5 >= var4) {
            var10000 = true;
            break;
         }

         Object element$iv = var3[var5];
         ItemStack it = (ItemStack)element$iv;
         int var8/* $VF was: $i$a$-all-FancyBrewer$isEmpty$1 */ = 0;
         if (it != null) {
            var10000 = false;
            break;
         }

         var5++;
      }

      if (var10000) {
         $this$all$iv = this.resources;
         $i$f$all = 0;

         label39: {
            for (Object element$iv : $this$all$iv) {
               ItemStack it = (ItemStack)element$iv;
               int var16/* $VF was: $i$a$-all-FancyBrewer$isEmpty$2 */ = 0;
               if (it != null) {
                  var10000 = false;
                  break label39;
               }
            }

            var10000 = true;
         }

         if (var10000) {
            return true;
         }
      }

      return false;
   }

   public final void setBottles(int index, @Nullable ItemStack item) {
      this.bottles[index] = item;
   }

   public final void setResource(@NotNull FancyBrewerResource resource, @Nullable ItemStack item) {
      Intrinsics.checkNotNullParameter(resource, "resource");
      this.resources[resource.getIndex()] = item;
   }

   public final boolean containsResource(@NotNull FancyBrewerResource resource) {
      Intrinsics.checkNotNullParameter(resource, "resource");
      return this.getInventory().getItem(resource.getSlot()) != null;
   }

   @Nullable
   public final ItemStack getResourceByType(@NotNull FancyBrewerResource resource) {
      Intrinsics.checkNotNullParameter(resource, "resource");
      return this.getInventory().getItem(resource.getSlot());
   }

   @Nullable
   public final ItemStack getResourceBySlot(int slot) {
      FancyBrewerResource var3 = FancyBrewerResource.Companion.getResourceBySlot(slot);
      if (var3 == null) {
         throw new IllegalArgumentException("No resource with slot " + slot + " found!");
      }

      FancyBrewerResource resource = var3;
      return this.getInventory().getItem(resource.getSlot());
   }

   public final int getAllBottles() {
      ItemStack[] var1 = this.bottles;
      boolean var2 = false;
      int var3 = 0;

      for (ItemStack var7 : var1) {
         ItemStack it = var7;
         int var12 = var3;
         int var9/* $VF was: $i$a$-sumOfInt-FancyBrewer$getAllBottles$1 */ = 0;
         ItemStack var10 = it;
         int var10000;
         if (var10 == null) {
            var10000 = 0;
         } else {
            int var11 = var10.getAmount();
            var10000 = var11;
         }

         int var13 = var10000;
         var3 = var12 + var13;
      }

      return var3;
   }

   @NotNull
   public final ItemStack[] getAllResources() {
      Collection $this$toTypedArray$iv = FancyBrewerResource.Companion.getAllResources();
      int $i$f$map = 0;
      Collection thisCollection$iv = $this$toTypedArray$iv;
      ArrayList destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$toTypedArray$iv, 10));
      int $i$f$mapTo = 0;

      for (Object item$iv$iv : thisCollection$iv) {
         FancyBrewerResource it = (FancyBrewerResource)item$iv$iv;
         Collection var12 = destination$iv$iv;
         int var9/* $VF was: $i$a$-map-FancyBrewer$getAllResources$1 */ = 0;
         ItemStack var13 = this.getInventory().getItem(it.getSlot());
         var12.add(var13);
      }

      List var10000 = (List)destination$iv$iv;
      int[] var15 = GLASS_SLOTS;
      Collection var27 = var10000;
      $i$f$map = 0;
      int[] var19 = var15;
      destination$iv$iv = new ArrayList(var15.length);
      $i$f$mapTo = 0;

      for (int item$iv$iv : var19) {
         int it = item$iv$iv;
         Collection var28 = destination$iv$iv;
         int var11/* $VF was: $i$a$-map-FancyBrewer$getAllResources$2 */ = 0;
         ItemStack var14 = this.getInventory().getItem(it);
         var28.add(var14);
      }

      List var29 = (List)destination$iv$iv;
      $this$toTypedArray$iv = CollectionsKt.filterNotNull(CollectionsKt.plus(var27, var29));
      $i$f$map = 0;
      thisCollection$iv = $this$toTypedArray$iv;
      Object[] var30 = thisCollection$iv.toArray(new ItemStack[0]);
      if (var30 == null) {
         throw new NullPointerException("null cannot be cast to non-null type kotlin.Array<T>");
      } else {
         return (ItemStack[])var30;
      }
   }

   @NotNull
   public final ItemStack[] getAllDrops() {
      Collection $this$toTypedArray$iv = FancyBrewerResource.Companion.getAllResources();
      int $i$f$map = 0;
      Collection thisCollection$iv = $this$toTypedArray$iv;
      ArrayList destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$toTypedArray$iv, 10));
      int $i$f$mapTo = 0;

      for (Object item$iv$iv : thisCollection$iv) {
         FancyBrewerResource i = (FancyBrewerResource)item$iv$iv;
         Collection var13 = destination$iv$iv;
         int var10/* $VF was: $i$a$-map-FancyBrewer$getAllDrops$drops$1 */ = 0;
         ItemStack var14 = this.getInventory().getItem(i.getSlot());
         var13.add(var14);
      }

      List drops = CollectionsKt.toMutableList((Collection & List)destination$iv$iv);
      int[] var16 = GLASS_SLOTS;
      List var41 = drops;
      $i$f$map = 0;
      int[] var23 = var16;
      destination$iv$iv = new ArrayList(var16.length);
      $i$f$mapTo = 0;

      for (int item$iv$iv : var23) {
         int it = item$iv$iv;
         Collection var42 = destination$iv$iv;
         int var12/* $VF was: $i$a$-map-FancyBrewer$getAllDrops$1 */ = 0;
         ItemStack var15 = this.getInventory().getItem(it);
         var42.add(var15);
      }

      List var43 = (List)destination$iv$iv;
      var41.addAll(var43);
      Map<Integer, AtomicInteger> var20 = this.brewed;
      boolean var24 = false;

      for (Entry var21 : var20.entrySet()) {
         Entry var28 = var21;
         boolean var31 = false;
         int key = ((Number)var28.getKey()).intValue();
         Entry var32 = var21;
         int i = 0;
         AtomicInteger value = (AtomicInteger)var32.getValue();
         Potion potion = Potion.fromDamage(key);
         i = 0;
         int var38 = value.get();
         if (i < var38) {
            while (true) {
               i++;
               drops.add(potion.toItemStack(1));
               if (i >= var38) {
                  break;
               }
            }
         }
      }

      $this$toTypedArray$iv = CollectionsKt.filterNotNull(drops);
      $i$f$map = 0;
      thisCollection$iv = $this$toTypedArray$iv;
      Object[] var10000 = thisCollection$iv.toArray(new ItemStack[0]);
      if (var10000 == null) {
         throw new NullPointerException("null cannot be cast to non-null type kotlin.Array<T>");
      } else {
         return (ItemStack[])var10000;
      }
   }

   public final int getEstimatedTime() {
      ItemStack[] lowest = this.bottles;
      int rotations = 0;
      int var4 = 0;

      for (ItemStack var8 : lowest) {
         ItemStack it = var8;
         int var13 = var4;
         int var10/* $VF was: $i$a$-sumOfInt-FancyBrewer$getEstimatedTime$bottles$1 */ = 0;
         ItemStack var11 = it;
         int var10000;
         if (var11 == null) {
            var10000 = 0;
         } else {
            int var12 = var11.getAmount();
            var10000 = var12;
         }

         int var14 = var10000;
         var4 = var13 + var14;
      }

      int bottles = var4;
      if (bottles < 3) {
         return -1;
      }

      int lowestx = bottles / 3;
      rotations = 0;

      for (FancyBrewerResource resource : FancyBrewerResource.Companion.getAllResources()) {
         ItemStack var20 = this.getInventory().getItem(resource.getSlot());
         if (var20 != null) {
            ItemStack item = var20;
            if (item.getAmount() < lowestx) {
               lowestx = item.getAmount();
            }

            int var21 = rotations;
            rotations = var21 + 1;
         }
      }

      return rotations < 3 ? -1 : lowestx * rotations * 5;
   }

   public final void tick() {
      if (!this.locked && this.hopper != null) {
         Map bottles = this.brewed;
         boolean resource = false;
         if (!bottles.isEmpty()) {
            FancyBrewer var17 = this;
            var17.lastHopperTick += -1;
            if (var17.lastHopperTick == 0) {
               Bukkit.getServer().getScheduler().runTask(Foxtrot.instance, () -> tick$lambda_12(this));
               this.lastHopperTick = 3;
            }
         }
      }

      if (this.state != FancyBrewerState.IDLE) {
         if (this.lastTick > 0) {
            FancyBrewer var19 = this;
            int var22 = var19.lastTick;
            var19.lastTick = var22 + -1;
         } else {
            if (this.lastResource != null) {
               if (this.currentlyBrewing == null || this.lastResourceType == null) {
                  this.stop();
                  return;
               }

               Potion var10002 = this.currentlyBrewing;
               Intrinsics.checkNotNull(this.currentlyBrewing);
               Material var10003 = this.lastResourceType;
               Intrinsics.checkNotNull(this.lastResourceType);
               this.currentlyBrewing = this.combine(var10002, var10003);
            }

            ItemStack[] var20 = this.bottles;
            boolean item = false;
            int attempts = 0;

            for (ItemStack var8 : var20) {
               ItemStack value$iv = var8;
               int var13 = attempts;
               int var10/* $VF was: $i$a$-sumOfInt-FancyBrewer$tick$bottles$1 */ = 0;
               ItemStack var11 = value$iv;
               int var10000;
               if (var11 == null) {
                  var10000 = 0;
               } else {
                  int var12 = var11.getAmount();
                  var10000 = var12;
               }

               int var14 = var10000;
               attempts = var13 + var14;
            }

            int bottles = attempts;
            FancyBrewerResource var61;
            if (this.lastResource == null) {
               var61 = FancyBrewerResource.WART;
            } else {
               var61 = this.lastResource;
               Intrinsics.checkNotNull(this.lastResource);
               FancyBrewerResource next = var61.next();
               attempts = 0;

               while (this.getInventory().getItem(next.getSlot()) == null) {
                  if (++attempts == 3) {
                     this.stop();
                     break;
                  }

                  next = next.next();
               }

               if (next == FancyBrewerResource.WART) {
                  int transferred = 0;
                  if (this.brewed.isEmpty() && !this.locked && this.hopper != null) {
                     Hopper var63 = this.hopper;
                     Intrinsics.checkNotNull(this.hopper);
                     ItemStack[] var38 = var63.getInventory().getContents();
                     Intrinsics.checkNotNullExpressionValue(var38, "this.hopper!!.inventory.contents");
                     ItemStack[] var32 = var38;
                     int var39 = 0;
                     int var43 = var32.length;

                     while (var39 < var43) {
                        ItemStack itemx = var32[var39];
                        var39++;
                        if (itemx == null) {
                           int var51 = transferred;
                           transferred = var51 + 1;
                        }
                     }
                  }

                  if (transferred > 0 && this.currentlyBrewing != null) {
                     int i = 0;
                     if (i < transferred) {
                        do {
                           i++;
                           Hopper var64 = this.hopper;
                           Intrinsics.checkNotNull(this.hopper);
                           Inventory var65 = var64.getInventory();
                           Intrinsics.checkNotNull(var65);
                           ItemStack[] var44 = new ItemStack[1];
                           Potion var74 = this.currentlyBrewing;
                           Intrinsics.checkNotNull(this.currentlyBrewing);
                           var44[0] = var74.toItemStack(1);
                           var65.addItem(var44);
                        } while (i < transferred);
                     }
                  }

                  Integer var10001 = this.currentAmount;
                  Intrinsics.checkNotNull(this.currentAmount);
                  this.currentAmount = var10001 - transferred;
                  if (this.currentlyBrewing != null) {
                     Integer var66 = this.currentAmount;
                     Intrinsics.checkNotNull(this.currentAmount);
                     if (var66 > 0) {
                        Map var34 = this.brewed;
                        Potion var67 = this.currentlyBrewing;
                        Intrinsics.checkNotNull(this.currentlyBrewing);
                        Object key$iv = Integer.valueOf(var67.toDamageValue());
                        int $i$f$getOrPut = 0;
                        Object value$iv = var34.get(key$iv);
                        AtomicInteger var68;
                        if (value$iv == null) {
                           int var52/* $VF was: $i$a$-getOrPut-FancyBrewer$tick$resource$1 */ = 0;
                           Object answer$iv = new AtomicInteger();
                           var34.put(key$iv, answer$iv);
                           var68 = (AtomicInteger)answer$iv;
                        } else {
                           var68 = (AtomicInteger)value$iv;
                        }

                        var68 = var68;
                        var10001 = this.currentAmount;
                        Intrinsics.checkNotNull(this.currentAmount);
                        var68.addAndGet(var10001);
                     }
                  }

                  (new BukkitRunnable() {
                     private int tick = 3;

                     @Override
                     public void run() {
                        this.tick += -1;
                     if (this.tick == 0) {
                           this.cancel();
                        }

                        FancyBrewer.this.getStand().getInventory().setItem(this.tick, null);
                     }
                  }).runTaskTimer(Foxtrot.instance, 3L, 3L);
                  if (this.lastResource == next) {
                     this.stop();
                     return;
                  }
               }

               if (bottles < 3) {
                  this.stop();
                  return;
               }

               var61 = next;
            }

            FancyBrewerResource resource = var61;
            ItemStack itemx = this.getInventory().getItem(resource.getSlot());
            if (itemx != null) {
               if (itemx.getAmount() == 1) {
                  this.getInventory().setItem(resource.getSlot(), null);
               } else {
                  FancyBrewerInventory var70 = this.getInventory();
                  int var73 = resource.getSlot();
                  ItemStack var26 = itemx;
                  boolean var29 = false;
                  boolean var35 = false;
                  ItemStack var41 = var26;
                  int var58 = var73;
                  FancyBrewerInventory var56 = var70;
                  int var46/* $VF was: $i$a$-apply-FancyBrewer$tick$2 */ = 0;
                  var41.setAmount(var41.getAmount() - 1);
                  Unit var15 = Unit.INSTANCE;
                  var56.setItem(var58, var26);
               }
            }

            if (resource == FancyBrewerResource.WART) {
               if (bottles <= 0) {
                  this.stop();
                  return;
               }

               byte var30 = 3;
               boolean var36 = false;
               attempts = Math.min(bottles, var30);
               this.currentAmount = attempts;
               this.currentlyBrewing = new Potion(PotionType.WATER);
               final ItemStack displayItem = new ItemStack(Material.GLASS_BOTTLE);
               (new BukkitRunnable() {
                  private int tick = 3;

                  @Override
                  public void run() {
                     this.tick += -1;
                     if (this.tick == 0) {
                        this.cancel();
                     }

                     FancyBrewer.this.getStand().getInventory().setItem(this.tick, displayItem);
                  }
               }).runTaskTimer(Foxtrot.instance, 10L, 2L);
               FancyBrewerInventory var71 = this.getInventory();
               ItemStack[] var37 = new ItemStack[1];
               ItemStack var42 = new ItemStack(Material.GLASS_BOTTLE);
               boolean var47 = false;
               boolean var50 = false;
               ItemStack var54 = var42;
               byte var60 = 0;
               ItemStack[] var59 = var37;
               FancyBrewerInventory var57 = var71;
               int var55/* $VF was: $i$a$-apply-FancyBrewer$tick$4 */ = 0;
               var54.setAmount(3);
               Unit var16 = Unit.INSTANCE;
               var59[var60] = var42;
               var57.removeItem(var37);
               this.refreshBottles();
            }

            this.lastTick = 5;
            this.lastResource = resource;
            this.lastResourceType = itemx.getType();
            this.refreshButtons$HCTeams();
            this.flagForSave();
         }
      }
   }

   private final void stop() {
      this.state = FancyBrewerState.IDLE;
      this.lastTick = 5;
      this.lastResource = null;
      this.lastResourceType = null;
      this.currentlyBrewing = null;
      this.refreshButtons$HCTeams();
      this.flagForSave();
   }

   private final Potion combine(Potion potion, Material item) {
      if (item == Material.NETHER_STALK) {
         ItemStack var17 = new ItemStack(Material.POTION);
         boolean var22 = false;
         boolean var27 = false;
         ItemStack $this$combine_u24lambda_u2d17 = var17;
         int var35/* $VF was: $i$a$-apply-FancyBrewer$combine$1 */ = 0;
         $this$combine_u24lambda_u2d17.setDurability((short)16);
         Potion var13 = Potion.fromItemStack(var17);
         Intrinsics.checkNotNullExpressionValue(var13, "fromItemStack(ItemStack(Material.POTION).apply{this.durability = 16})");
         return var13;
      }

      if (item == Material.SULPHUR) {
         Potion var12 = potion.splash();
         Intrinsics.checkNotNullExpressionValue(var12, "potion.splash()");
         return var12;
      }

      if (item == Material.REDSTONE) {
         if (potion.getType() != PotionType.POISON && potion.getType() != PotionType.SLOWNESS) {
            Potion var11 = potion.extend();
            Intrinsics.checkNotNullExpressionValue(var11, "potion.extend()");
            return var11;
         } else {
            return potion;
         }
      } else {
         PotionType type = potion.getType();
         if (item == Material.GLOWSTONE_DUST) {
            if (type != null && Foxtrot.instance.serverHandler.getPotionLimit(type) < 2) {
               return potion;
            }

            if (potion.getType() == null) {
               return potion;
            }

            Potion var16 = potion;
            boolean var21 = false;
            boolean var26 = false;
            Potion $this$combine_u24lambda_u2d18 = var16;
            int var34/* $VF was: $i$a$-apply-FancyBrewer$combine$2 */ = 0;
            $this$combine_u24lambda_u2d18.setLevel(2);
            return var16;
         } else {
            if (item == Material.FERMENTED_SPIDER_EYE) {
               if (potion.getType() == PotionType.NIGHT_VISION) {
                  Potion var15 = potion;
                  boolean var20 = false;
                  boolean var25 = false;
                  Potion $this$combine_u24lambda_u2d19 = var15;
                  int var33/* $VF was: $i$a$-apply-FancyBrewer$combine$3 */ = 0;
                  $this$combine_u24lambda_u2d19.setType(PotionType.INVISIBILITY);
                  return var15;
               }

               if (potion.getType() == PotionType.SPEED) {
                  Potion var14 = potion;
                  boolean var19 = false;
                  boolean var24 = false;
                  Potion $this$combine_u24lambda_u2d20 = var14;
                  int var32/* $VF was: $i$a$-apply-FancyBrewer$combine$4 */ = 0;
                  $this$combine_u24lambda_u2d20.setType(PotionType.SLOWNESS);
                  return var14;
               }
            }

            PotionEffectType var6 = FancyBrewerResource.Companion.getRESULT_TABLE().get(item);
            if (var6 == null) {
               return potion;
            } else {
               PotionEffectType resultType = var6;
               PotionEffectType result = resultType;
               PotionType resultTypex = PotionType.getByEffect(result);
               if (resultTypex != null && Foxtrot.instance.serverHandler.getPotionLimit(resultTypex) != 0) {
                  Potion var23 = potion;
                  boolean $this$combine_u24lambda_u2d20 = false;
                  boolean var8 = false;
                  Potion $this$combine_u24lambda_u2d21 = var23;
                  int var10/* $VF was: $i$a$-apply-FancyBrewer$combine$5 */ = 0;
                  $this$combine_u24lambda_u2d21.setType(resultTypex);
                  $this$combine_u24lambda_u2d21.setLevel(1);
                  return var23;
               } else {
                  return potion;
               }
            }
         }
      }
   }

   public final boolean isAllowedToInsert(int slot, @NotNull ItemStack item) {
      Intrinsics.checkNotNullParameter(item, "item");
      if (item.getType() == Material.GLASS_BOTTLE) {
         return ArraysKt.contains(GLASS_SLOTS, slot);
      }

      FancyBrewerResource.Companion var10000 = FancyBrewerResource.Companion;
      Material var5 = item.getType();
      Intrinsics.checkNotNullExpressionValue(var5, "item.type");
      FancyBrewerResource var4 = var10000.getResourceByType(var5);
      if (var4 == null) {
         return false;
      }

      FancyBrewerResource resource = var4;
      return resource.getSlot() == slot;
   }

   public final void addBottles(int amount) {
      int remaining = 0;
      remaining = amount;
      int[] var3 = GLASS_SLOTS;
      int var4 = 0;
      int var5 = var3.length;

      while (var4 < var5) {
         int slot = var3[var4];
         var4++;
         if (this.bottles[slot - 12] == null) {
            FancyBrewerInventory var35 = this.getInventory();
            ItemStack var17 = new ItemStack(Material.GLASS_BOTTLE);
            boolean var19 = false;
            boolean var21 = false;
            ItemStack var23 = var17;
            int var30 = slot;
            FancyBrewerInventory var28 = var35;
            int var25/* $VF was: $i$a$-apply-FancyBrewer$addBottles$1 */ = 0;
            var23.setAmount(remaining);
            Unit var32 = Unit.INSTANCE;
            var28.setItem(var30, var17);
            break;
         }

         ItemStack var10000 = this.bottles[slot - 12];
         Intrinsics.checkNotNull(this.bottles[slot - 12]);
         ItemStack bottles = var10000;
         if (bottles.getAmount() != Material.GLASS_BOTTLE.getMaxStackSize()) {
            if (bottles.getAmount() + remaining <= Material.GLASS_BOTTLE.getMaxStackSize()) {
               FancyBrewerInventory var34 = this.getInventory();
               ItemStack var18 = bottles;
               boolean var20 = false;
               boolean var22 = false;
               ItemStack var24 = var18;
               int var29 = slot;
               FancyBrewerInventory var27 = var34;
               int var26/* $VF was: $i$a$-apply-FancyBrewer$addBottles$3 */ = 0;
               var24.setAmount(var24.getAmount() + remaining);
               Unit var31 = Unit.INSTANCE;
               var27.setItem(var29, var18);
               break;
            }

            remaining -= Material.GLASS_BOTTLE.getMaxStackSize() - bottles.getAmount();
            FancyBrewerInventory var33 = this.getInventory();
            ItemStack var8 = bottles;
            boolean var9 = false;
            boolean $this$addBottles_u24lambda_u2d22 = false;
            ItemStack $this$addBottles_u24lambda_u2d24 = var8;
            int var14 = slot;
            FancyBrewerInventory var13 = var33;
            int var12/* $VF was: $i$a$-apply-FancyBrewer$addBottles$2 */ = 0;
            $this$addBottles_u24lambda_u2d24.setAmount(Material.GLASS_BOTTLE.getMaxStackSize());
            Unit var15 = Unit.INSTANCE;
            var13.setItem(var14, var8);
            if (remaining <= 0) {
               break;
            }
         }
      }
   }

   @JvmName(name = "setHopper1")
   public final void setHopper1(@Nullable Hopper hopper) {
      this.hopper = hopper;
      this.lastHopperTick = 3;
      this.refreshButtons$HCTeams();
   }

   public final void flagForSave() {
      FancyBrewerHandler.INSTANCE.addUpdate(this);
   }

   @NotNull
   public final UUID component1() {
      return this.id;
   }

   @NotNull
   public final UUID component2() {
      return this.owner;
   }

   @NotNull
   public final Location component3() {
      return this.location;
   }

   @NotNull
   public final FancyBrewer copy(@Json(name = "_id") @NotNull UUID id, @NotNull UUID owner, @NotNull Location location) {
      Intrinsics.checkNotNullParameter(id, "id");
      Intrinsics.checkNotNullParameter(owner, "owner");
      Intrinsics.checkNotNullParameter(location, "location");
      return new FancyBrewer(id, owner, location);
   }

   // $VF: synthetic method
   public static FancyBrewer copy$default(FancyBrewer var0, UUID var1, UUID var2, Location var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.id;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.owner;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.location;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "FancyBrewer(id=" + this.id + ", owner=" + this.owner + ", location=" + this.location + ')';
   }

   @Override
   public int hashCode() {
      int result = this.id.hashCode();
      result = result * 31 + this.owner.hashCode();
      return result * 31 + this.location.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof FancyBrewer)) {
         return false;
      } else {
         FancyBrewer var2 = (FancyBrewer)other;
         if (!Intrinsics.areEqual(this.id, var2.id)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.owner, var2.owner) ? false : Intrinsics.areEqual(this.location, var2.location);
         }
      }
   }

   private static final void tick$lambda_12/* $VF was: tick$lambda-12 */(FancyBrewer this$0) {
      Intrinsics.checkNotNullParameter(this$0, "this$0");
      Entry first = CollectionsKt.first(this$0.getBrewed().entrySet());
      Hopper var10000 = this$0.getHopper();
      Intrinsics.checkNotNull(var10000);
      ItemStack[] $this$firstOrNull$iv = var10000.getInventory().getContents();
      Intrinsics.checkNotNullExpressionValue($this$firstOrNull$iv, "this.hopper!!.inventory.contents");
      Iterable $this$firstOrNull$ivx = ArraysKt.withIndex($this$firstOrNull$iv);
      int $i$f$firstOrNull = 0;
      Iterator var5 = $this$firstOrNull$ivx.iterator();
      Object var11;

      while (true) {
         if (var5.hasNext()) {
            Object element$iv = var5.next();
            IndexedValue it = (IndexedValue)element$iv;
            int var8/* $VF was: $i$a$-firstOrNull-FancyBrewer$tick$1$slot$1 */ = 0;
            if (it.getValue() != null) {
               continue;
            }

            var11 = element$iv;
            break;
         }

         var11 = null;
         break;
      }

      IndexedValue slot = (IndexedValue)var11;
      if (slot != null) {
         var10000 = this$0.getHopper();
         Intrinsics.checkNotNull(var10000);
         Inventory var13 = var10000.getInventory();
         $this$firstOrNull$iv = new ItemStack[]{Potion.fromDamage(((Number)first.getKey()).intValue()).toItemStack(1)};
         var13.addItem($this$firstOrNull$iv);
         if (((AtomicInteger)first.getValue()).decrementAndGet() == 0) {
            this$0.getBrewed().remove(first.getKey());
         }

         if (this$0.getLastTick() > 0) {
            this$0.refreshButtons$HCTeams();
         }

         this$0.flagForSave();
      }
   }

   static {
      Pair[] var0 = new Pair[]{
         TuplesKt.to(16, BrewerStartButton.INSTANCE),
         TuplesKt.to(25, BrewerStorageButton.INSTANCE),
         TuplesKt.to(31, BrewerValueButton.INSTANCE),
         TuplesKt.to(34, BrewerTransferButton.INSTANCE)
      };
      BUTTONS = MapsKt.hashMapOf(var0);
      int[] var6 = new int[]{12, 13, 14};
      GLASS_SLOTS = var6;
      MAX_BOTTLES = 64 * GLASS_SLOTS.length;
      ItemStack var7 = new ItemStack(Material.STAINED_GLASS_PANE);
      boolean var1 = false;
      boolean var2 = false;
      ItemStack $this$BLANK_BUTTON_u24lambda_u2d25 = var7;
      int var4/* $VF was: $i$a$-apply-FancyBrewer$Companion$BLANK_BUTTON$1 */ = 0;
      ItemMeta itemMeta = $this$BLANK_BUTTON_u24lambda_u2d25.getItemMeta();
      itemMeta.setDisplayName(" ");
      $this$BLANK_BUTTON_u24lambda_u2d25.setItemMeta(itemMeta);
      $this$BLANK_BUTTON_u24lambda_u2d25.setDurability((short)15);
      BLANK_BUTTON = var7;
   }

   @Metadata(
      mv = {1, 5, 1},
      k = 1,
      xi = 48,
      d1 = "\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0015\n\u0002\b\b\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u000e\u0010\u0007\u001a\u00020\bX\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\bX\u0086T¢\u0006\u0002\n\u0000R-\u0010\n\u001a\u001e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\f0\u000bj\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\f`\r¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0010\u001a\u00020\u0011¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u000e\u0010\u0014\u001a\u00020\bX\u0086T¢\u0006\u0002\n\u0000R\u0011\u0010\u0015\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u000e\u0010\u0018\u001a\u00020\bX\u0086T¢\u0006\u0002\n\u0000¨\u0006\u0019",
      d2 = {
            "Lnet/frozenorb/foxtrot/brewer/FancyBrewer$Companion;",
            "",
            "()V",
            "BLANK_BUTTON",
            "Lorg/bukkit/inventory/ItemStack;",
            "getBLANK_BUTTON",
            "()Lorg/bukkit/inventory/ItemStack;",
            "BOTTLES_PER_RESULT",
            "",
            "BREW_INTERVAL",
            "BUTTONS",
            "Ljava/util/HashMap;",
            "Lnet/frozenorb/foxtrot/brewer/button/FancyBrewerButton;",
            "Lkotlin/collections/HashMap;",
            "getBUTTONS",
            "()Ljava/util/HashMap;",
            "GLASS_SLOTS",
            "",
            "getGLASS_SLOTS",
            "()[I",
            "HOPPER_TICK",
            "MAX_BOTTLES",
            "getMAX_BOTTLES",
            "()I",
            "REQUIRED_ROTATIONS",
            "HCTeams"
      }
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final HashMap<Integer, FancyBrewerButton> getBUTTONS() {
         return FancyBrewer.BUTTONS;
      }

      @NotNull
      public final int[] getGLASS_SLOTS() {
         return FancyBrewer.GLASS_SLOTS;
      }

      public final int getMAX_BOTTLES() {
         return FancyBrewer.MAX_BOTTLES;
      }

      @NotNull
      public final ItemStack getBLANK_BUTTON() {
         return FancyBrewer.BLANK_BUTTON;
      }

   }
}
