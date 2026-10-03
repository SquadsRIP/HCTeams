package net.frozenorb.foxtrot.map.menu;

import cc.fyre.proton.menu.Button;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

class HelpMenu$13 extends Button {
   // $VF: synthetic field
   final HelpMenu this$0;

   HelpMenu$13(HelpMenu this$0) {
      this.this$0 = this$0;
   }

   @Override
   public String getName(Player player) {
      return ChatColor.translate("&4&lSchedule");
   }

   @Override
   public List<String> getDescription(Player player) {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("&7View everything that's supposed");
      toReturn.add("&7to happen during the map.");
      toReturn.add("");
      toReturn.add("&aClick here to view the schedule.");
      return toReturn.stream().<String>map(ChatColor::translate).collect(Collectors.toList());
   }

   @Override
   public Material getMaterial(Player player) {
      return Material.SKULL_ITEM;
   }

   @Override
   public byte getDamageValue(Player player) {
      return 3;
   }

   @Override
   public String getSkullTexture(Player player) {
      return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmZlOGNmZjc1ZjdkNDMzMjYwYWYxZWNiMmY3NzNiNGJjMzgxZDk1MWRlNGUyZWI2NjE0MjM3NzlhNTkwZTcyYiJ9fX0=";
   }

   @Override
   public void clicked(Player player, int slot, ClickType clickType) {
      player.closeInventory();
      player.chat("/schedule");
   }
}
