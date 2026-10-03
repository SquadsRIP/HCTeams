package net.frozenorb.foxtrot.brewer.button.type;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.frozenorb.foxtrot.brewer.FancyBrewer;
import net.frozenorb.foxtrot.brewer.button.FancyBrewerButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 5, 1},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J \u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016J\u0012\u0010\u000b\u001a\u0004\u0018\u00010\f2\u0006\u0010\u0007\u001a\u00020\bH\u0016¨\u0006\r",
   d2 = {
         "Lnet/frozenorb/foxtrot/brewer/button/type/BrewerValueButton;",
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
public final class BrewerValueButton extends FancyBrewerButton {
   @NotNull
   public static final BrewerValueButton INSTANCE = new BrewerValueButton();

   private BrewerValueButton() {
   }

   @Nullable
   @Override
   public ItemStack render(@NotNull FancyBrewer brewer) {
      Intrinsics.checkNotNullParameter(brewer, "brewer");
      if (brewer.getCurrentlyBrewing() == null) {
         return new ItemStack(Material.POTION);
      }

      Potion potion = brewer.getCurrentlyBrewing();
      Intrinsics.checkNotNull(potion);
      Integer n = brewer.getCurrentAmount();
      return potion.toItemStack(n == null ? 1 : n);
   }

   @Override
   public void handleClick(@NotNull Player player, @NotNull FancyBrewer brewer, @NotNull ClickType click) {
      Intrinsics.checkNotNullParameter(player, "player");
      Intrinsics.checkNotNullParameter(brewer, "brewer");
      Intrinsics.checkNotNullParameter(click, "click");
   }
}
