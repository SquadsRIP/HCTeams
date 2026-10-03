package net.frozenorb.foxtrot.gameplay.pvpclasses.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import net.frozenorb.foxtrot.gameplay.pvpclasses.archer.ArcherColor;
import net.frozenorb.foxtrot.gameplay.pvpclasses.menu.element.ArcherUpgradeElement;
import org.bukkit.entity.Player;

public class ArcherUpgradesMenu extends Menu {
   @Override
   public int size(Player player) {
      return 27;
   }

   @Override
   public String getTitle(Player player) {
      return "Archer Upgrades";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      HashMap<Integer, Button> toReturn = new HashMap<>();
      AtomicInteger index = new AtomicInteger();
      Arrays.stream(ArcherColor.values()).forEach(it -> {
         Button cfr_ignored_0 = toReturn.put(10 + index.getAndIncrement() * 2, new ArcherUpgradeElement(it));
      });
      return toReturn;
   }
}
