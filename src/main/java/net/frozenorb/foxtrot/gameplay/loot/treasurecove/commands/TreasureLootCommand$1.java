package net.frozenorb.foxtrot.gameplay.loot.treasurecove.commands;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.util.HashMap;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

final class TreasureLootCommand$1 extends Menu {
   @Override
   public String getTitle(Player player) {
      return "Treasure Cove Loot";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      Map<Integer, Button> toReturn = new HashMap<>();

      for (ItemStack itemStack : Foxtrot.getInstance().getTreasureCoveHandler().getTreasureLoot()) {
         toReturn.put(toReturn.size(), new TreasureLootCommand$1$1(this, itemStack));
      }

      return toReturn;
   }
}
