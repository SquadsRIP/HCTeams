package net.frozenorb.foxtrot.gameplay.kitmap.kits.editor.menu;

import cc.fyre.proton.menu.Button;
import java.beans.ConstructorProperties;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.Kit;
import net.minecraft.util.com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

final class KitDeleteButton extends Button {
   private final Kit kit;

   @Override
   public String getName(Player player) {
      return ChatColor.RED.toString() + ChatColor.BOLD + "Delete";
   }

   @Override
   public List<String> getDescription(Player player) {
      return ImmutableList.of(
         "",
         ChatColor.RED + "Click here to delete this kit",
         ChatColor.RED + "You will " + ChatColor.BOLD + "NOT" + ChatColor.RED + " be able to",
         ChatColor.RED + "recover this kit."
      );
   }

   @Override
   public Material getMaterial(Player player) {
      return Material.WOOL;
   }

   @Override
   public byte getDamageValue(Player player) {
      return DyeColor.RED.getWoolData();
   }

   @Override
   public void clicked(Player player, int slot, ClickType clickType) {
      Foxtrot.getInstance().getMapHandler().getKitManager().deleteUserKit(player.getUniqueId(), this.kit);
   }

   @ConstructorProperties("kit")
   public KitDeleteButton(Kit kit) {
      this.kit = kit;
   }
}
