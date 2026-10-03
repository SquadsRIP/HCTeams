package net.frozenorb.foxtrot.gameplay.ability.menu.page;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.entity.Player;

public abstract class ItemPaginatedMenu extends Menu {
   private int page = 1;

   @Override
   public String getTitle(Player player) {
      return this.getPrePaginatedTitle(player) + " - " + this.page + "/" + this.getPages(player);
   }

   public final void modPage(Player player, int mod) {
      this.page += mod;
      this.getButtons().clear();
      this.openMenu(player);
   }

   public final int getPages(Player player) {
      int buttonAmount = this.getAllPagesButtons(player).size();
      return buttonAmount == 0 ? 1 : (int)Math.ceil((double)buttonAmount / this.getMaxItemsPerPage(player));
   }

   @Override
   public final Map<Integer, Button> getButtons(Player player) {
      int minIndex = (int)((double)(this.page - 1) * this.getMaxItemsPerPage(player));
      int maxIndex = (int)((double)this.page * this.getMaxItemsPerPage(player));
      HashMap<Integer, Button> buttons = new HashMap<>();
      buttons.put(1, new PageButton(-1, this));
      buttons.put(7, new PageButton(1, this));
      Map<Integer, Button> global = this.getGlobalButtons(player);
      if (global != null) {
         for (Entry<Integer, Button> gent : global.entrySet()) {
            buttons.put(gent.getKey(), gent.getValue());
         }
      }

      int i = 0;

      for (Entry<Integer, Button> entry : this.getAllPagesButtons(player).entrySet()) {
         int ind = entry.getKey();
         i++;
         if (ind >= minIndex && ind < maxIndex) {
            int var12;
            buttons.put(9 + (var12 = ind - ((int)((double)this.getMaxItemsPerPage(player) * (this.page - 1)) - 9)), entry.getValue());
         }
      }

      return buttons;
   }

   public int getMaxItemsPerPage(Player player) {
      return 18;
   }

   public Map<Integer, Button> getGlobalButtons(Player player) {
      return null;
   }

   public abstract String getPrePaginatedTitle(Player var1);

   public abstract Map<Integer, Button> getAllPagesButtons(Player var1);

   public int getPage() {
      return this.page;
   }
}
