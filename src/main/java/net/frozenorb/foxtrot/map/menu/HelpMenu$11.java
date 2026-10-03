package net.frozenorb.foxtrot.map.menu;

import cc.fyre.proton.menu.Button;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

class HelpMenu$11 extends Button {
   // $VF: synthetic field
   final HelpMenu this$0;

   HelpMenu$11(HelpMenu this$0) {
      this.this$0 = this$0;
   }

   @Override
   public String getName(Player player) {
      return ChatColor.translate("&4&lXP Shop");
   }

   @Override
   public List<String> getDescription(Player player) {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("&7View the XP Shop, the rewards");
      toReturn.add("&7the prices and what you can buy.");
      toReturn.add("");
      toReturn.add("&aClick here to view the XP Shop.");
      return toReturn.stream().<String>map(ChatColor::translate).collect(Collectors.toList());
   }

   @Override
   public Material getMaterial(Player player) {
      return Material.EXP_BOTTLE;
   }

   @Override
   public void clicked(Player player, int slot, ClickType clickType) {
      player.closeInventory();
      player.chat("/xpshop");
   }
}
