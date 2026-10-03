package net.frozenorb.foxtrot.server.keyalls.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.keyalls.KeyAll;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class MainMenu extends PaginatedMenu {
   @Override
   public String getPrePaginatedTitle(Player player) {
      return "Main Menu";
   }

   @Override
   public Map<Integer, Button> getAllPagesButtons(Player player) {
      HashMap<Integer, Button> toReturn = new HashMap<>();

      for (Entry<String, KeyAll> entry : Foxtrot.getInstance().getKeyAllHandler().getCache().entrySet()) {
         toReturn.put(toReturn.size(), new Button() {
            @Override
            public String getName(Player player) {
               return null;
            }

            @Override
            public List<String> getDescription(Player player) {
               return null;
            }

            @Override
            public Material getMaterial(Player player) {
               return null;
            }
         });
      }

      return toReturn;
   }
}
