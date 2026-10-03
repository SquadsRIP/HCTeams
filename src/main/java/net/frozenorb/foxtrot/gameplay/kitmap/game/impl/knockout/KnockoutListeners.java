package net.frozenorb.foxtrot.gameplay.kitmap.game.impl.knockout;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class KnockoutListeners implements Listener {
   private final GameHandler gameHandler = Foxtrot.getInstance().getMapHandler().getGameHandler();

   @EventHandler(ignoreCancelled = true)
   public void onDamage(EntityDamageEvent event) {
      if (event.getEntity() instanceof Player) {
         if (this.gameHandler.isOngoingGame() && this.gameHandler.getOngoingGame() instanceof KnockoutGame) {
            Player player = (Player)event.getEntity();
            KnockoutGame ongoingGame = (KnockoutGame)this.gameHandler.getOngoingGame();
            if (ongoingGame.getPlayers().contains(player)) {
               if (event.getCause() == DamageCause.LAVA) {
                  ongoingGame.eliminatePlayer(player, null);
                  event.setCancelled(true);
               }
            }
         }
      }
   }
}
