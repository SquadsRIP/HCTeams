package net.frozenorb.foxtrot.gameplay.blockshop;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.gameplay.blockshop.menu.BlockShopMenu;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BlockShopCommand {
   @Command(names = {"blockshop", "bs"}, permission = "")
   public static void blockShop(Player player) {
      if (Foxtrot.getInstance().getServerHandler().isTeams()) {
         player.sendMessage(ChatColor.YELLOW + "This feature is disabled on " + Foxtrot.getInstance().getServerHandler().getServerName() + ".");
      } else {
         boolean inSpawn = DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation());
         boolean inCombat = SpawnTagHandler.isTagged(player);
         boolean SOTW = CustomTimerCreateCommand.isSOTWTimer();
         if (!Foxtrot.getInstance().getMapHandler().isKitMap() && !SOTW && !inSpawn && inCombat) {
            player.sendMessage(ChatColor.YELLOW + "You can't open the Block Shop while combat tagged.");
         } else {
            new BlockShopMenu().openMenu(player);
         }
      }
   }
}
