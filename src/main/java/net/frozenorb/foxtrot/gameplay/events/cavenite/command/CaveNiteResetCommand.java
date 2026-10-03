package net.frozenorb.foxtrot.gameplay.events.cavenite.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.cavenite.CaveNiteHandler;
import net.frozenorb.foxtrot.gameplay.events.cavenite.CaveNiteState;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CaveNiteResetCommand {
   public static void reset(Player player) {
      CaveNiteHandler caveNiteHandler = Foxtrot.getInstance().getCaveNiteHandler();
      player.sendMessage(ChatColor.translate("&cReset squadsnite"));
      Foxtrot.getInstance().getCaveNiteHandler().setGameState(CaveNiteState.INACTIVE);
      Foxtrot.getInstance().getCaveNiteHandler().setScatterIn(0);
      Foxtrot.getInstance().getCaveNiteHandler().setStarted(0L);
      Foxtrot.getInstance().getCaveNiteHandler().setStartIn(0);
   }

   public static void resetAll(Player player) {
      CaveNiteHandler caveNiteHandler = Foxtrot.getInstance().getCaveNiteHandler();
      player.sendMessage(ChatColor.translate("&cReset Squads Nite All"));
      Foxtrot.getInstance().getCaveNiteHandler().setGameState(CaveNiteState.INACTIVE);
      Foxtrot.getInstance().getCaveNiteHandler().setScatterIn(0);
      Foxtrot.getInstance().getCaveNiteHandler().setStarted(0L);
      Foxtrot.getInstance().getCaveNiteHandler().setStartIn(0);
      Foxtrot.getInstance().getCaveNiteHandler().getChestLocations().clear();
      Foxtrot.getInstance().getCaveNiteHandler().getChestLoot().clear();
      Foxtrot.getInstance().getCaveNiteHandler().getLocations().clear();
      Foxtrot.getInstance().getCaveNiteHandler().getPlayersRemaining().clear();
      Foxtrot.getInstance().getCaveNiteHandler().getSpectators().clear();
   }
}
