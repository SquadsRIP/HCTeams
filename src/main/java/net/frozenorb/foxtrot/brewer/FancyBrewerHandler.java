package net.frozenorb.foxtrot.brewer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import net.frozenorb.foxtrot.config.ConfigMessages;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 5, 1},
   k = 1,
   xi = 48,
   d1 = "\u0000\\\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0005\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\nJ\u000e\u0010\u0017\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\nJ\u000e\u0010\u0018\u001a\u00020\u00062\u0006\u0010\u0019\u001a\u00020\u001aJ\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\n0\u001cJ\u0012\u0010\u001d\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00110\u000fJ\u0012\u0010\u001e\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00110\u000fJ\u0010\u0010\u001f\u001a\u0004\u0018\u00010\n2\u0006\u0010 \u001a\u00020\tJ\u0010\u0010!\u001a\u0004\u0018\u00010\n2\u0006\u0010\"\u001a\u00020\rJ\u0010\u0010#\u001a\u0004\u0018\u00010\n2\u0006\u0010$\u001a\u00020%J\u000e\u0010&\u001a\u00020\u00112\u0006\u0010'\u001a\u00020\u0006J\u0010\u0010(\u001a\u0004\u0018\u00010\n2\u0006\u0010\"\u001a\u00020\rJ\u0018\u0010)\u001a\u00020\u00152\u0006\u0010$\u001a\u00020%2\b\u0010\u0016\u001a\u0004\u0018\u00010\nR\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R*\u0010\u0007\u001a\u001e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\n0\bj\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\n`\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R*\u0010\f\u001a\u001e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\n0\bj\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\n`\u000bX\u0082\u0004¢\u0006\u0002\n\u0000RN\u0010\u000e\u001aB\u0012\f\u0012\n \u0010*\u0004\u0018\u00010\t0\t\u0012\f\u0012\n \u0010*\u0004\u0018\u00010\u00110\u0011 \u0010* \u0012\f\u0012\n \u0010*\u0004\u0018\u00010\t0\t\u0012\f\u0012\n \u0010*\u0004\u0018\u00010\u00110\u0011\u0018\u00010\u000f0\u000fX\u0082\u0004¢\u0006\u0002\n\u0000RN\u0010\u0012\u001aB\u0012\f\u0012\n \u0010*\u0004\u0018\u00010\t0\t\u0012\f\u0012\n \u0010*\u0004\u0018\u00010\u00110\u0011 \u0010* \u0012\f\u0012\n \u0010*\u0004\u0018\u00010\t0\t\u0012\f\u0012\n \u0010*\u0004\u0018\u00010\u00110\u0011\u0018\u00010\u000f0\u000fX\u0082\u0004¢\u0006\u0002\n\u0000R*\u0010\u0013\u001a\u001e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\n0\bj\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\n`\u000bX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006*",
   d2 = {
         "Lnet/frozenorb/foxtrot/brewer/FancyBrewerHandler;",
         "",
         "()V",
         "BREWER_DROP_METADATA",
         "",
         "ITEM",
         "Lorg/bukkit/inventory/ItemStack;",
         "brewers",
         "Ljava/util/HashMap;",
         "Ljava/util/UUID;",
         "Lnet/frozenorb/foxtrot/brewer/FancyBrewer;",
         "Lkotlin/collections/HashMap;",
         "brewersByLocation",
         "Lorg/bukkit/Location;",
         "deleteQueue",
         "Ljava/util/concurrent/ConcurrentHashMap$KeySetView;",
         "kotlin.jvm.PlatformType",
         "",
         "updateQueue",
         "viewers",
         "addBrewer",
         "",
         "brewer",
         "addUpdate",
         "createItemStack",
         "amount",
         "",
         "getAllBrewers",
         "",
         "getAllDeletes",
         "getAllUpdates",
         "getBrewerById",
         "id",
         "getBrewerByLocation",
         "location",
         "getBrewerByViewer",
         "player",
         "Lorg/bukkit/entity/Player;",
         "isBrewer",
         "item",
         "removeBrewer",
         "setBrewerByViewer",
         "HCTeams"
   }
)
public final class FancyBrewerHandler {
   @NotNull
   public static final FancyBrewerHandler INSTANCE = new FancyBrewerHandler();
   @NotNull
   private static final HashMap<UUID, FancyBrewer> viewers = new HashMap<>();
   @NotNull
   private static final HashMap<UUID, FancyBrewer> brewers = new HashMap<>();
   @NotNull
   private static final HashMap<Location, FancyBrewer> brewersByLocation = new HashMap<>();
   private static final KeySetView<UUID, Boolean> updateQueue = ConcurrentHashMap.newKeySet();
   private static final KeySetView<UUID, Boolean> deleteQueue = ConcurrentHashMap.newKeySet();
   @NotNull
   private static final ItemStack ITEM;
   @NotNull
   public static final String BREWER_DROP_METADATA = "FANCY_BREWER_DROP";

   private FancyBrewerHandler() {
   }

   @NotNull
   public final List<FancyBrewer> getAllBrewers() {
      Collection<FancyBrewer> collection = brewersByLocation.values();
      Intrinsics.checkNotNullExpressionValue(collection, "brewersByLocation.values");
      return CollectionsKt.toList(collection);
   }

   public final void addBrewer(@NotNull FancyBrewer brewer) {
      Intrinsics.checkNotNullParameter(brewer, "brewer");
      brewer.init();
      if (brewer.getInitialized()) {
         brewers.put(brewer.getId(), brewer);
         brewersByLocation.put(brewer.getLocation(), brewer);
      }
   }

   @Nullable
   public final FancyBrewer removeBrewer(@NotNull Location location) {
      Intrinsics.checkNotNullParameter(location, "location");
      FancyBrewer fancyBrewer2 = brewersByLocation.remove(location);
      if (fancyBrewer2 == null) {
         return null;
      }

      FancyBrewer brewer = fancyBrewer2;
      brewers.remove(brewer.getId());
      updateQueue.remove(brewer.getId());
      deleteQueue.add(brewer.getId());
      return brewer;
   }

   @Nullable
   public final FancyBrewer getBrewerById(@NotNull UUID id) {
      Intrinsics.checkNotNullParameter(id, "id");
      return brewers.get(id);
   }

   @Nullable
   public final FancyBrewer getBrewerByLocation(@NotNull Location location) {
      Intrinsics.checkNotNullParameter(location, "location");
      Location normalized = location.clone();
      normalized.setYaw(0.0F);
      normalized.setPitch(0.0F);
      return brewersByLocation.get(normalized);
   }

   @Nullable
   public final FancyBrewer getBrewerByViewer(@NotNull Player player) {
      Intrinsics.checkNotNullParameter(player, "player");
      return viewers.get(player.getUniqueId());
   }

   public final void setBrewerByViewer(@NotNull Player player, @Nullable FancyBrewer brewer) {
      Intrinsics.checkNotNullParameter(player, "player");
      if (brewer == null) {
         viewers.remove(player.getUniqueId());
      } else {
         HashMap<UUID, FancyBrewer> map = viewers;
         UUID uUID = player.getUniqueId();
         Intrinsics.checkNotNullExpressionValue(uUID, "player.uniqueId");
         boolean bl = false;
         map.put(uUID, brewer);
      }
   }

   @NotNull
   public final KeySetView<UUID, Boolean> getAllUpdates() {
      KeySetView<UUID, Boolean> keySetView = updateQueue;
      Intrinsics.checkNotNullExpressionValue(keySetView, "updateQueue");
      return keySetView;
   }

   @NotNull
   public final KeySetView<UUID, Boolean> getAllDeletes() {
      KeySetView<UUID, Boolean> keySetView = deleteQueue;
      Intrinsics.checkNotNullExpressionValue(keySetView, "deleteQueue");
      return keySetView;
   }

   @NotNull
   public final ItemStack createItemStack(int amount) {
      ItemStack itemStack = ITEM.clone();
      Intrinsics.checkNotNullExpressionValue(itemStack, "ITEM.clone()");
      boolean bl = false;
      boolean bl2 = false;
      ItemStack $this$createItemStack_u24lambda_u2d1 = itemStack;
      boolean bl3 = false;
      $this$createItemStack_u24lambda_u2d1.setAmount(amount);
      return itemStack;
   }

   public final boolean isBrewer(@NotNull ItemStack item) {
      Intrinsics.checkNotNullParameter(item, "item");
      return item.getType() == ITEM.getType()
         && item.hasItemMeta()
         && Intrinsics.areEqual(item.getItemMeta().getDisplayName(), ITEM.getItemMeta().getDisplayName())
         && item.getItemMeta().getLore().size() == ITEM.getItemMeta().getLore().size();
   }

   public final void addUpdate(@NotNull FancyBrewer brewer) {
      Intrinsics.checkNotNullParameter(brewer, "brewer");
      updateQueue.add(brewer.getId());
   }

   static {
      boolean bl = false;
      bl = false;
      bl = false;
      ItemStack itemStack = new ItemStack(Material.BREWING_STAND_ITEM);
      boolean bl2 = false;
      boolean bl3 = false;
      ItemStack $this$ITEM_u24lambda_u2d2 = itemStack;
      boolean bl4 = false;
      ItemMeta itemMeta = $this$ITEM_u24lambda_u2d2.getItemMeta();
      itemMeta.setDisplayName(ChatColor.AQUA.toString() + ChatColor.BOLD + "Fancy Brewer");
      String[] lore = new String[]{
         ChatColor.GRAY + "A brewing stand... but fancy.",
         ChatColor.GRAY + "Place the brewer and open to get started.",
         "",
         ChatColor.GREEN + "Learn more at " + ConfigMessages.link("fancy-brewers", "Coming Soon!")
      };
      itemMeta.setLore(Arrays.asList(lore));
      $this$ITEM_u24lambda_u2d2.setItemMeta(itemMeta);
      ITEM = itemStack;
   }
}
