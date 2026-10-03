package net.frozenorb.foxtrot.map.menu;

import cc.fyre.proton.menu.Button;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

class HelpMenu$14 extends Button {
   // $VF: synthetic field
   final HelpMenu this$0;

   HelpMenu$14(HelpMenu this$0) {
      this.this$0 = this$0;
   }

   @Override
   public String getName(Player player) {
      return ChatColor.translate("&4&lLeaderboards");
   }

   @Override
   public List<String> getDescription(Player player) {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("&7View the leaderboards for this map.");
      toReturn.add("");
      toReturn.add("&aClick here to view the leaderboards.");
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
      return "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzJiYWYwYzU4OWE2YjU4MzUxMWQ4M2MyNjgyNDA4NDJkMzM2NDc3NGVjOWY1NjZkMWZkNGQzNDljZjQyZmIifX19";
   }

   @Override
   public void clicked(Player player, int slot, ClickType clickType) {
      player.closeInventory();
      player.chat("/lb");
   }
}
