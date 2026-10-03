package net.frozenorb.foxtrot.gameplay.ability.menu.page;

import cc.fyre.proton.menu.Button;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class PageButton extends Button {
   private int mod;
   private ItemPaginatedMenu menu;

   @Override
   public void clicked(Player player, int i, ClickType clickType) {
      if (this.hasNext(player)) {
         this.menu.modPage(player, this.mod);
         Button.playNeutral(player);
      } else {
         Button.playFail(player);
      }
   }

   private boolean hasNext(Player player) {
      int pg = this.menu.getPage() + this.mod;
      return pg > 0 && this.menu.getPages(player) >= pg;
   }

   @Override
   public String getName(Player player) {
      if (!this.hasNext(player)) {
         return this.mod > 0 ? "§7Last page" : "§7First page";
      } else {
         return this.mod > 0 ? "§a⟶" : "§c⟵";
      }
   }

   @Override
   public List<String> getDescription(Player player) {
      return new ArrayList<>();
   }

   @Override
   public byte getDamageValue(Player player) {
      return (byte)(this.hasNext(player) ? 11 : 7);
   }

   @Override
   public Material getMaterial(Player player) {
      return Material.CARPET;
   }

   @ConstructorProperties({"mod", "menu"})
   public PageButton(int mod, ItemPaginatedMenu menu) {
      this.mod = mod;
      this.menu = menu;
   }
}
