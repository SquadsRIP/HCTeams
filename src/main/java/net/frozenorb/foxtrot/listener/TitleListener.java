package net.frozenorb.foxtrot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TitleListener implements Listener {
   @EventHandler
   private void onJoin(PlayerJoinEvent event) {
      Player player = event.getPlayer();
      if (player.hasPlayedBefore()) {
         ;
      }
   }
}
