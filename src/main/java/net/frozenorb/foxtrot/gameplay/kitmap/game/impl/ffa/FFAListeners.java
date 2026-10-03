package net.frozenorb.foxtrot.gameplay.kitmap.game.impl.ffa;

import cc.fyre.proton.util.ItemBuilder;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class FFAListeners implements Listener {
   private static final ItemStack GOLDEN_APPLE = ItemBuilder.of(Material.GOLDEN_APPLE).build();
   private final GameHandler gameHandler = Foxtrot.getInstance().getMapHandler().getGameHandler();

   @EventHandler
   public void onPlayerDeath(PlayerDeathEvent event) {
      Player player = event.getEntity();
      if (this.gameHandler.isOngoingGame() && this.gameHandler.getOngoingGame() instanceof FFAGame) {
         FFAGame ongoingGame = (FFAGame)this.gameHandler.getOngoingGame();
         if (!ongoingGame.isPlaying(player.getUniqueId())) {
            return;
         }

         Player killer = player.getKiller();
         ongoingGame.eliminatePlayer(player, killer);
         if (killer != null && ongoingGame.isPlaying(killer.getUniqueId())) {
            killer.getInventory().addItem(GOLDEN_APPLE);
            killer.sendMessage(
               ChatColor.YELLOW
                  + "You have received "
                  + ChatColor.GOLD
                  + "1 Golden Apple"
                  + ChatColor.YELLOW
                  + " for killing "
                  + player.getDisplayName()
                  + ChatColor.YELLOW
                  + "!"
            );
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void onItemDrop(PlayerDropItemEvent event) {
      if (this.gameHandler.isOngoingGame()
         && this.gameHandler.getOngoingGame() instanceof FFAGame
         && ((FFAGame)this.gameHandler.getOngoingGame()).isPlaying(event.getPlayer().getUniqueId())
         && event.getItemDrop().getItemStack().getType() == Material.GLASS_BOTTLE) {
         event.getItemDrop().remove();
      }
   }
}
