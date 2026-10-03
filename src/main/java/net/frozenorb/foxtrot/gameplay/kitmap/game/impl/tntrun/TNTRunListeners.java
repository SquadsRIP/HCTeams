package net.frozenorb.foxtrot.gameplay.kitmap.game.impl.tntrun;

import cc.fyre.proton.cuboid.Cuboid.CuboidDirection;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameState;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class TNTRunListeners implements Listener {
   private final GameHandler gameHandler = Foxtrot.getInstance().getMapHandler().getGameHandler();

   @EventHandler(ignoreCancelled = true)
   public void onPlayerMove(PlayerMoveEvent event) {
      if (this.gameHandler.isOngoingGame() && this.gameHandler.getOngoingGame() instanceof TNTRunGame) {
         TNTRunGame ongoingGame = (TNTRunGame)this.gameHandler.getOngoingGame();
         if (ongoingGame.getState() == GameState.RUNNING) {
            if (ongoingGame.getVotedArena().getBounds().expand(CuboidDirection.DOWN, 256).contains(event.getTo())) {
               if (ongoingGame.isPlaying(event.getPlayer().getUniqueId())) {
                  if (ongoingGame.isStarted()) {
                     Block from = event.getFrom().getBlock();
                     Block to = event.getTo().getBlock();
                     if (from.getType().name().contains("WATER")
                        || to.getType().name().contains("WATER")
                        || from.getType().name().contains("LAVA")
                        || to.getType().name().contains("LAVA")) {
                        ongoingGame.eliminatePlayer(event.getPlayer(), null);
                     }
                  }
               }
            }
         }
      }
   }
}
