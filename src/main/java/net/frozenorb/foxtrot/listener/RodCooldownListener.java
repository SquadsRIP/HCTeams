package net.frozenorb.foxtrot.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClassHandler;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;

public class RodCooldownListener implements Listener {
   public static Map<UUID, Long> cache = new HashMap<>();

   @EventHandler(priority = EventPriority.LOW)
   private void onFish(PlayerFishEvent event) {
      Player player = event.getPlayer();
      if ((player.getWorld().getEnvironment() != Environment.NORMAL || Foxtrot.getInstance().getServerHandler().isAu()) && event.getState() == State.FISHING) {
         if (PvPClassHandler.getPvPClass(player) == null) {
            if ((!cache.containsKey(player.getUniqueId()) || System.currentTimeMillis() < cache.get(player.getUniqueId()))
               && cache.containsKey(player.getUniqueId())) {
               long millisRemaining = cache.get(player.getUniqueId()) - System.currentTimeMillis();
               double value = millisRemaining / 1000.0;
               double sec = value > 0.1 ? Math.round(10.0 * value) / 10.0 : 0.1;
               event.setCancelled(true);
               player.sendMessage(ChatColor.YELLOW + "You may not use a fishing rod for another " + ChatColor.BOLD + sec + ChatColor.YELLOW + " seconds!");
            } else {
               cache.remove(player.getUniqueId());
               cache.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(1L));
            }
         }
      }
   }
}
