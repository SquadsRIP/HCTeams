package net.frozenorb.foxtrot.map.menu;

import cc.fyre.proton.menu.Button;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

class HelpMenu$12 extends Button {
   // $VF: synthetic field
   final HelpMenu this$0;

   HelpMenu$12(HelpMenu this$0) {
      this.this$0 = this$0;
   }

   @Override
   public String getName(Player player) {
      return ChatColor.translate("&4&lSkins");
   }

   @Override
   public List<String> getDescription(Player player) {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("&7View all skins, what they look");
      toReturn.add("&7like and how you can get them.");
      toReturn.add("");
      toReturn.add("&aClick here to view all skins.");
      return toReturn.stream().<String>map(ChatColor::translate).collect(Collectors.toList());
   }

   @Override
   public Material getMaterial(Player player) {
      return Material.SADDLE;
   }

   @Override
   public void clicked(Player player, int slot, ClickType clickType) {
      player.sendMessage(ChatColor.RED + "Skins will be enabled soon!");
   }
}
