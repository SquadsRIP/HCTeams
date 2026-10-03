package net.frozenorb.foxtrot.gameplay.loot.treasurecove.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.treasurecove.TreasureCoveHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TreasureLoadLootTableCommand {
   public static void treasureLoadLoottable(Player sender) {
      sender.getInventory().clear();
      int itemIndex = 0;

      for (ItemStack itemStack : Foxtrot.getInstance().getTreasureCoveHandler().getTreasureLoot()) {
         sender.getInventory().setItem(itemIndex, itemStack);
         itemIndex++;
      }

      sender.sendMessage(TreasureCoveHandler.PREFIX + " " + ChatColor.YELLOW + "Loaded treasure loot into your inventory.");
   }
}
