package net.frozenorb.foxtrot.gameplay.kitmap.kits.editor.menu;

import cc.fyre.proton.menu.Button;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.DefaultKit;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.Kit;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.editor.button.EditKitMenu;
import net.frozenorb.foxtrot.util.Formats;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

final class KitIconButton extends Button {
   private final Optional<Kit> kitOpt;
   private final DefaultKit originalKit;

   @Override
   public String getName(Player player) {
      if (this.kitOpt.isPresent()) {
         return !this.kitOpt.get().getName().equals(this.originalKit.getName())
            ? ChatColor.GREEN.toString() + ChatColor.BOLD + "Edit `" + this.kitOpt.get().getName() + "` Kit"
            : ChatColor.GREEN.toString() + ChatColor.BOLD + "Edit " + this.originalKit.getName() + " Kit";
      } else {
         return ChatColor.GREEN.toString() + ChatColor.BOLD + "Create " + this.originalKit.getName() + " Kit";
      }
   }

   @Override
   public List<String> getDescription(Player player) {
      ArrayList<String> description = new ArrayList<>();
      description.add("");
      description.addAll(Formats.renderLines(ChatColor.GRAY.toString(), this.originalKit.getDescription()));
      return description;
   }

   @Override
   public Material getMaterial(Player player) {
      return this.originalKit.getIcon().getType();
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
   public KitIconButton(Optional<Kit> kitOpt, DefaultKit originalKit) {
      this.kitOpt = kitOpt;
      this.originalKit = originalKit;
   }
}
