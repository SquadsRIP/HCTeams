package net.frozenorb.foxtrot.apollo;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ApolloListener implements Listener {
   @EventHandler(priority = EventPriority.MONITOR)
   public void onJoin(PlayerJoinEvent event) {
      final Player player = event.getPlayer();
      (new BukkitRunnable() {
         @Override
         public void run() {
            if (player.isOnline()) {
               ApolloService.disableNickHider(player);
            }
         }
      }).runTaskLater(Foxtrot.getInstance(), 40L);
   }
}
