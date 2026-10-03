package net.frozenorb.foxtrot.gameplay.loot.treasurecove.commands;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.treasurecove.TreasureCoveHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TreasureSetCentralChestCommand {
   public static void execute(Player sender) {
      Block standing = sender.getLocation().getBlock();
      if (standing.getType() != Material.CHEST) {
         sender.sendMessage(ChatColor.YELLOW + "You must be standing on a Chest!");
      } else {
         Foxtrot.getInstance().getTreasureCoveHandler().setCentralChest(standing.getLocation());
         sender.sendMessage(
            TreasureCoveHandler.PREFIX
               + " "
               + ChatColor.YELLOW
               + "Set the central chest location to "
               + ChatColor.WHITE
               + standing.getX()
               + ", "
               + standing.getY()
               + ", "
               + standing.getZ()
         );
      }
   }
}
