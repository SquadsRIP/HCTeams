package net.frozenorb.foxtrot.gameplay.kitmap.kits.editor.menu;

import cc.fyre.proton.menu.Button;
import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Optional;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.DefaultKit;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.Kit;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.editor.button.EditKitMenu;
import net.minecraft.util.com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

final class KitEditButton extends Button {
   private final Optional<Kit> kitOpt;
   private final DefaultKit originalKit;

   @Override
   public String getName(Player player) {
      return ChatColor.YELLOW.toString() + ChatColor.BOLD + "Load/Edit";
   }

   @Override
   public List<String> getDescription(Player player) {
      return ImmutableList.of("", ChatColor.GRAY + "Click to " + ChatColor.YELLOW + "edit" + ChatColor.GRAY + " this kit!");
   }

   @Override
   public Material getMaterial(Player player) {
      return Material.BOOK;
   }

   @Override
   public void clicked(Player player, int slot, ClickType clickType) {
      Kit resolvedKit;
      if (this.kitOpt.isPresent()) {
         resolvedKit = this.kitOpt.get();
      } else {
         resolvedKit = new Kit(this.originalKit);
         Foxtrot.getInstance().getMapHandler().getKitManager().trackUserKit(player.getUniqueId(), resolvedKit);
      }

      new EditKitMenu(resolvedKit).openMenu(player);
   }

   @ConstructorProperties({"kitOpt", "originalKit"})
   public KitEditButton(Optional<Kit> kitOpt, DefaultKit originalKit) {
      this.kitOpt = kitOpt;
      this.originalKit = originalKit;
   }
}
