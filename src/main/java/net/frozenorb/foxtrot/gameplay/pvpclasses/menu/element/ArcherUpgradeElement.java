package net.frozenorb.foxtrot.gameplay.pvpclasses.menu.element;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.util.ItemBuilder;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.DefaultKit;
import net.frozenorb.foxtrot.gameplay.pvpclasses.archer.ArcherColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ArcherUpgradeElement extends Button {
   private ArcherColor color;

   @Override
   public String getName(Player player) {
      return null;
   }

   @Override
   public List<String> getDescription(Player player) {
      ArrayList<String> toReturn = new ArrayList<>();

      for (String s : this.color.getLore()) {
         toReturn.add(ChatColor.GRAY + s);
      }

      toReturn.add("");
      toReturn.add(ChatColor.YELLOW + "Click to apply this archer upgrade");
      return toReturn;
   }

   @Override
   public Material getMaterial(Player player) {
      return null;
   }

   @Override
   public ItemStack getButtonItem(Player player) {
      return ItemBuilder.of(Material.LEATHER_HELMET)
         .color(this.color.getColor())
         .name(this.color.getChatColor() + ChatColor.BOLD.toString() + this.color.getName())
         .setLore(this.getDescription(player))
         .build();
   }

   @Override
   public void clicked(Player player, int i, ClickType clickType) {
      DefaultKit kit = Foxtrot.getInstance().getMapHandler().getKitManager().getDefaultKit("Archer");
      if (kit == null) {
         player.sendMessage(ChatColor.YELLOW + "This kit is currently unavailable!");
      } else {
         player.getInventory().clear();
         kit.apply(player);
         ItemStack[] contents = player.getInventory().getArmorContents();
         Arrays.stream(contents).filter(Objects::nonNull).forEach(it -> it = ItemBuilder.copyOf(it).color(this.color.getColor()).build());
         player.getInventory().setArmorContents(contents);
         player.updateInventory();
         player.closeInventory();
      }
   }

   @ConstructorProperties("color")
   public ArcherUpgradeElement(ArcherColor color) {
      this.color = color;
   }
}
