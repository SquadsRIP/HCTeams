package net.frozenorb.foxtrot.team.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.Callback;
import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Map;
import net.frozenorb.foxtrot.team.menu.button.BooleanButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ConfirmMenu extends Menu {
   private String title;
   private Callback<Boolean> response;

   @Override
   public String getTitle(Player player) {
      return this.title.length() > 32 ? this.title.substring(0, 31) : this.title;
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      HashMap<Integer, Button> buttons = new HashMap<>();

      for (int i = 0; i < 9; i++) {
         if (i == 3) {
            buttons.put(i, new BooleanButton(true, this.response));
         } else if (i == 5) {
            buttons.put(i, new BooleanButton(false, this.response));
         } else {
            buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)14));
         }
      }

      return buttons;
   }

   @ConstructorProperties({"title", "response"})
   public ConfirmMenu(String title, Callback<Boolean> response) {
      this.title = title;
      this.response = response;
   }
}
