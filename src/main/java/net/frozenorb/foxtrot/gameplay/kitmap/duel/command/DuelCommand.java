package net.frozenorb.foxtrot.gameplay.kitmap.duel.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.duel.DuelHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DuelCommand {
   @Command(names = "duel", permission = "")
   public static void duel(Player sender, @Parameter(name = "player") Player target) {
      if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
         sender.sendMessage(ChatColor.YELLOW + "This command is only available on KitMap!");
      } else {
         sender.sendMessage(ChatColor.YELLOW + "Duels have been temporarily blocked!");
      }
   }

   private static void lambda$duel$4f331581$1(Player sender, DuelHandler duelHandler, Player target, Integer wager) {
      sender.closeInventory();
      duelHandler.sendDuelRequest(sender, target, wager);
   }
}
