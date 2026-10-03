package net.frozenorb.foxtrot.gameplay.kitmap.game;

import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public final class GameUtils {
   public static void resetPlayer(Player player) {
      InventoryUtils.resetInventoryNow(player);
      player.setGameMode(GameMode.SURVIVAL);
      player.getInventory().setItem(4, GameItems.LEAVE_EVENT);
      player.updateInventory();
   }

   private GameUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
