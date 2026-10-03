package net.frozenorb.foxtrot.brewer.button.type;

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
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 5, 1},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J \u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016J\u0010\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0007\u001a\u00020\bH\u0016¨\u0006\r",
   d2 = {
         "Lnet/frozenorb/foxtrot/brewer/button/type/BrewerTransferButton;",
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
public final class BrewerTransferButton extends FancyBrewerButton {
   @NotNull
   public static final BrewerTransferButton INSTANCE = new BrewerTransferButton();

   private BrewerTransferButton() {
   }

   @NotNull
   @Override
   public ItemStack render(@NotNull FancyBrewer brewer) {
      Intrinsics.checkNotNullParameter(brewer, "brewer");
      boolean bl = false;
      ArrayList<String> lore = new ArrayList<>();
      if (brewer.getHopper() == null) {
         lore.add(ChatColor.GRAY + "No Hopper");
      } else if (brewer.getLocked()) {
         lore.add(ChatColor.YELLOW + "Paused");
      } else {
         lore.add(ChatColor.GREEN + "Enabled");
      }

      ItemStack itemStack = new ItemStack(Material.HOPPER);
      boolean bl2 = false;
      boolean bl3 = false;
      ItemStack $this$render_u24lambda_u2d0 = itemStack;
      boolean bl4 = false;
      ItemMeta itemMeta = $this$render_u24lambda_u2d0.getItemMeta();
      itemMeta.setDisplayName(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Transfer");
      itemMeta.setLore(lore);
      $this$render_u24lambda_u2d0.setItemMeta(itemMeta);
      return itemStack;
   }

   @Override
   public void handleClick(@NotNull Player player, @NotNull FancyBrewer brewer, @NotNull ClickType click) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(brewer, "brewer");
      Intrinsics.checkNotNullParameter(click, "click");
      if (brewer.getHopper() != null) {
         brewer.setLocked(!brewer.getLocked());
         brewer.refreshButtons$HCTeams();
      }
   }
}
