package net.frozenorb.foxtrot.gameplay.loot.treasurecove.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.treasurecove.TreasureCoveHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TreasureSaveCommand {
   public static void execute(Player sender) {
      Foxtrot.getInstance().getTreasureCoveHandler().saveTreasureInfo();
      sender.sendMessage(TreasureCoveHandler.PREFIX + " " + ChatColor.YELLOW + "Saved treasure info to file.");
   }
}
