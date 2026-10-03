package net.frozenorb.foxtrot.gameplay.kitmap.game.impl.minestrike;

import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameState;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class MineStrikeListeners implements Listener {
   private final GameHandler gameHandler = Foxtrot.getInstance().getMapHandler().getGameHandler();

   @EventHandler
   public void onDeath(PlayerDeathEvent event) {
      Player player = event.getEntity();
      if (this.gameHandler.isOngoingGame() && this.gameHandler.getOngoingGame() instanceof MineStrikeGame) {
         MineStrikeGame ongoingGame = (MineStrikeGame)this.gameHandler.getOngoingGame();
         if (!ongoingGame.isPlaying(player.getUniqueId())) {
            return;
         }

         ongoingGame.eliminatePlayer(player, player.getKiller());
         event.setKeepLevel(true);
         event.setDroppedExp(0);
         event.getDrops().clear();
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onDamage(WeaponDamageEntityEvent event) {
      if (this.gameHandler.isOngoingGame() && this.gameHandler.getOngoingGame() instanceof MineStrikeGame) {
         MineStrikeGame ongoingGame = (MineStrikeGame)this.gameHandler.getOngoingGame();
         if (event.getVictim() instanceof Player) {
            Player victim = (Player)event.getVictim();
            Player damager = event.getPlayer();
            if (ongoingGame.getBlueTeam().contains(victim.getUniqueId()) && ongoingGame.getBlueTeam().contains(damager.getUniqueId())) {
               event.setCancelled(true);
               damager.sendMessage(ChatColor.YELLOW + "You may not damage your teammate!");
            } else {
               if (ongoingGame.getRedTeam().contains(damager.getUniqueId()) && ongoingGame.getRedTeam().contains(victim.getUniqueId())) {
                  event.setCancelled(true);
                  damager.sendMessage(ChatColor.YELLOW + "You may not damage your teammate!");
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onDamage(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
         if (this.gameHandler.isOngoingGame() && this.gameHandler.getOngoingGame() instanceof MineStrikeGame) {
            MineStrikeGame ongoingGame = (MineStrikeGame)this.gameHandler.getOngoingGame();
            Player player = (Player)event.getEntity();
            Player damage = (Player)event.getDamager();
            if (ongoingGame.isPlaying(player.getUniqueId()) || ongoingGame.isPlaying(damage.getUniqueId())) {
               event.setCancelled(true);
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onDamage(EntityDamageEvent event) {
      if (event.getEntity() instanceof Player) {
         Player player = (Player)event.getEntity();
         if (this.gameHandler.isOngoingGame() && this.gameHandler.getOngoingGame() instanceof MineStrikeGame) {
            MineStrikeGame ongoingGame = (MineStrikeGame)this.gameHandler.getOngoingGame();
            if (ongoingGame.isPlaying(player.getUniqueId()) && ongoingGame.getState() != GameState.RUNNING) {
               event.setCancelled(true);
            }
         }
      }
   }
}
