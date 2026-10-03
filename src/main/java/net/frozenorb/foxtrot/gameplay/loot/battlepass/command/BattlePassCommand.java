package net.frozenorb.foxtrot.gameplay.loot.battlepass.command;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.BattlePassProgress;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.menu.BattlePassMenu;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BattlePassCommand {
   @Command(names = {"battlepass", "bp", "missions", "challenges"}, description = "Opens the BattlePass menu", permission = "")
   public static void execute(Player player) {
      if (Foxtrot.getInstance().getServerHandler().isTeams()) {
         player.sendMessage(ChatColor.YELLOW + "This feature is disabled on " + Foxtrot.getInstance().getServerHandler().getServerName() + ".");
      } else if (Foxtrot.getInstance().getBattlePassHandler() != null) {
         BattlePassProgress progress = Foxtrot.getInstance().getBattlePassHandler().getProgress(player.getUniqueId());
         if (progress != null) {
            new BattlePassMenu(progress).openMenu(player);
         } else {
            player.sendMessage(CC.YELLOW + "Couldn't open BattlePass!");
         }
      }
   }
}
