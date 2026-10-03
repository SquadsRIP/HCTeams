package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import java.util.HashMap;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KOTHRewardKeyCommand {
   @Command(names = "kothrewardkey", permission = "op")
   public static void kothRewardKey(
      Player sender,
      @Parameter(name = "player", defaultValue = "self") Player player,
      @Parameter(name = "koth") String koth,
      @Parameter(name = "amount", defaultValue = "1") int amount
   ) {
      if (sender.getGameMode() != GameMode.CREATIVE) {
         sender.sendMessage(ChatColor.YELLOW + "This command must be ran in creative.");
      } else if (player == null) {
         sender.sendMessage(ChatColor.YELLOW + "Unable to locate player.");
      } else if (amount != 0 && 32 >= amount) {
         ItemStack stack = InventoryUtils.generateKOTHRewardKey(koth);
         stack.setAmount(amount);
         HashMap failed = player.getInventory().addItem(stack);
         if (amount == 1) {
            String msg = ChatColor.YELLOW
               + "Gave "
               + player.getName()
               + " a KOTH reward key."
               + (failed != null && !failed.isEmpty() ? " " + failed.size() + " didn't fit." : "");
            org.bukkit.command.Command.broadcastCommandMessage(sender, msg);
         } else {
            String msg = ChatColor.YELLOW
               + "Gave "
               + player.getName()
               + " "
               + amount
               + " KOTH reward keys."
               + (failed != null && !failed.isEmpty() ? " " + failed.size() + " didn't fit." : "");
            org.bukkit.command.Command.broadcastCommandMessage(sender, msg);
         }
      } else {
         sender.sendMessage(ChatColor.YELLOW + "Illegal amount! Must be between 1 and 32.");
      }
   }
}
