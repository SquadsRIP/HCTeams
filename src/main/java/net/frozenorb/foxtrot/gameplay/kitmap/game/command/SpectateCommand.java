package net.frozenorb.foxtrot.gameplay.kitmap.game.command;

import cc.fyre.modsuite.mod.ModHandler;
import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.Game;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.util.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SpectateCommand {
   @Command(names = {"spec", "spectate", "game spec", "game spectate"}, description = "Join an ongoing event", permission = "")
   public static void execute(Player player) {
      if (!player.isOp()) {
         player.sendMessage(ChatColor.YELLOW + "Spectating is currently disabled");
      } else if (!Foxtrot.getInstance().getMapHandler().getGameHandler().isOngoingGame()) {
         player.sendMessage(ChatColor.YELLOW + "There is no ongoing event.");
      } else {
         Game ongoingGame = Foxtrot.getInstance().getMapHandler().getGameHandler().getOngoingGame();
         if (ongoingGame.isPlayingOrSpectating(player.getUniqueId())) {
            player.sendMessage(ChatColor.YELLOW + "You are already in the event.");
         } else if (ModHandler.INSTANCE.isInModMode(player.getUniqueId())) {
            player.sendMessage(ChatColor.YELLOW + "You can't join the event while in mod-mode.");
         } else if (SpawnTagHandler.isTagged(player)) {
            player.sendMessage(ChatColor.YELLOW + "You can't join the event while spawn-tagged.");
         } else if (!ItemUtils.hasEmptyInventory(player)) {
            player.sendMessage(ChatColor.YELLOW + "You need to have an empty inventory to join the event.");
         } else {
            ongoingGame.addSpectator(player);
         }
      }
   }
}
