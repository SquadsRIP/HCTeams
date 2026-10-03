package net.frozenorb.foxtrot.gameplay.loot.treasurecove.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.treasurecove.TreasureCoveHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TreasureRespawnChestsCommand {
   public static void treasureRespawnChests(Player sender) {
      sender.sendMessage(
         TreasureCoveHandler.PREFIX
            + " "
            + ChatColor.YELLOW
            + "Respawned "
            + Foxtrot.getInstance().getTreasureCoveHandler().respawnTreasureChests()
            + " treasure chests."
      );
   }
}
