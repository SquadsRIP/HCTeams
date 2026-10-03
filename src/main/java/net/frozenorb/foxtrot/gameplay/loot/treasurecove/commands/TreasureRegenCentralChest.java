package net.frozenorb.foxtrot.gameplay.loot.treasurecove.commands;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TreasureRegenCentralChest {
   public static void execute(Player sender) {
      if (!Foxtrot.getInstance().getTreasureCoveHandler().generateCentralChest()) {
         sender.sendMessage(ChatColor.YELLOW + "Could not start central chest regeneration. Check the console for the BuycraftX error.");
      } else {
         sender.sendMessage(ChatColor.GREEN + "Central chest generation started; its gift card will be added after BuycraftX confirms it.");
      }
   }
}
