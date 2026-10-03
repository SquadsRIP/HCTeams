package net.frozenorb.foxtrot.gameplay.events.cavenite.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.cavenite.CaveNiteHandler;
import net.frozenorb.foxtrot.gameplay.events.cavenite.CaveNiteState;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CaveNiteJoinCommand {
   public static void execute(Player player) {
      CaveNiteHandler caveNiteHandler = Foxtrot.getInstance().getCaveNiteHandler();
      if (caveNiteHandler.getGameState() == CaveNiteState.INACTIVE) {
         player.sendMessage(ChatColor.YELLOW + "The game isn't active!");
      } else if (caveNiteHandler.getGameState() != CaveNiteState.WAITING) {
         player.sendMessage(ChatColor.YELLOW + "The game has already started!");
      } else if (caveNiteHandler.getOnlinePlayers().contains(player)) {
         player.sendMessage(ChatColor.YELLOW + "You are already in the game queue!");
      } else {
         caveNiteHandler.addPlayer(player);
      }
   }

   public static void addSpec(Player player) {
      CaveNiteHandler caveNiteHandler = Foxtrot.getInstance().getCaveNiteHandler();
      if (caveNiteHandler.getGameState() == CaveNiteState.INACTIVE) {
         player.sendMessage(ChatColor.YELLOW + "The game isn't active!");
      } else if (caveNiteHandler.getOnlinePlayers().contains(player)) {
         player.sendMessage(ChatColor.YELLOW + "You are already a player!");
      } else if (caveNiteHandler.getOnlineSpectators().contains(player)) {
         player.sendMessage(ChatColor.YELLOW + "You are already a spectator!");
      } else {
         caveNiteHandler.addSpectator(player);
      }
   }
}
