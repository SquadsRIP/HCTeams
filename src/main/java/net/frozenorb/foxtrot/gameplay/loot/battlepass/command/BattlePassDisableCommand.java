package net.frozenorb.foxtrot.gameplay.loot.battlepass.command;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class BattlePassDisableCommand {
   @Command(names = {"battlepass disable", "bp disable"}, description = "Disable the BattlePass", permission = "battlepass.disable", async = true)
   public static void execute(CommandSender sender) {
      if (Foxtrot.getInstance().getBattlePassHandler() == null) {
         sender.sendMessage(ChatColor.YELLOW + "BattlePass is not enabled on this server!");
      } else {
         Foxtrot.getInstance().getBattlePassHandler().setAdminDisabled(!Foxtrot.getInstance().getBattlePassHandler().isAdminDisabled());
         if (Foxtrot.getInstance().getBattlePassHandler().isAdminDisabled()) {
            sender.sendMessage(ChatColor.YELLOW + "BattlePass has been temporarily disabled!");
         } else {
            sender.sendMessage(ChatColor.GREEN + "BattlePass has been enabled!");
         }
      }
   }
}
