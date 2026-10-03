package net.frozenorb.foxtrot.gameplay.loot.battlepass.command;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class BattlePassWipeCommand {
   @Command(names = {"battlepass wipe", "bp wipe"}, description = "Dump a player's BattlePass progress", permission = "battlepass.dump", async = true)
   public static void execute(CommandSender sender) {
      if (!(sender instanceof ConsoleCommandSender)) {
         sender.sendMessage(ChatColor.YELLOW + "This command can only be executed through console!");
      } else if (Foxtrot.getInstance().getBattlePassHandler() == null) {
         sender.sendMessage(ChatColor.YELLOW + "BattlePass is not enabled on this server!");
      } else {
         Foxtrot.getInstance().getBattlePassHandler().wipe();
      }
   }
}
