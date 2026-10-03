package net.frozenorb.foxtrot.gameplay.kitmap.gem.gemboost;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GemBoostCommand {
   @Command(names = "gemboost", permission = "op")
   public static void gemBoost(CommandSender sender, @Parameter(name = "target") Player target, @Parameter(name = "minutes") int minutes) {
      if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
         sender.sendMessage(ChatColor.YELLOW + "This is a KitMap only command.");
      } else {
         Foxtrot.getInstance().getGemBoosterMap().giveGemBooster(target, minutes);
         sender.sendMessage(
            ChatColor.DARK_GREEN
               + "[Gem Booster] "
               + ChatColor.WHITE
               + "You have given "
               + ChatColor.LIGHT_PURPLE
               + target.getName()
               + ChatColor.WHITE
               + " a Gem Booster of "
               + ChatColor.LIGHT_PURPLE
               + minutes
               + ChatColor.WHITE
               + " minutes."
         );
         if (sender != target) {
            target.sendMessage(
               ChatColor.DARK_GREEN
                  + "[Gem Booster] "
                  + ChatColor.LIGHT_PURPLE
                  + sender.getName()
                  + ChatColor.WHITE
                  + "has given you a Gem Booster of "
                  + ChatColor.LIGHT_PURPLE
                  + minutes
                  + ChatColor.WHITE
                  + " minutes."
            );
         }
      }
   }
}
