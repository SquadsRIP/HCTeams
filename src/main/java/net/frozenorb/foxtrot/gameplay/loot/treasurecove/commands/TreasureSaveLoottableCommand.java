package net.frozenorb.foxtrot.gameplay.loot.treasurecove.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.treasurecove.TreasureCoveHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TreasureSaveLoottableCommand {
   public static void treasureSaveLoottable(Player sender) {
      Foxtrot.getInstance().getTreasureCoveHandler().getTreasureLoot().clear();

      for (ItemStack itemStack : sender.getInventory().getContents()) {
         if (itemStack != null && itemStack.getType() != Material.AIR) {
            Foxtrot.getInstance().getTreasureCoveHandler().getTreasureLoot().add(itemStack);
         }
      }

      Foxtrot.getInstance().getTreasureCoveHandler().saveTreasureInfo();
      sender.sendMessage(TreasureCoveHandler.PREFIX + " " + ChatColor.YELLOW + "Saved treasure loot from your inventory.");
   }
}
