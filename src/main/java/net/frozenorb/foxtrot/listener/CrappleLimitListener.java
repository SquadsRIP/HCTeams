package net.frozenorb.foxtrot.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class CrappleLimitListener implements Listener {
   public static Map<UUID, AtomicInteger> consumed = new HashMap<>();

   @EventHandler
   private void onJoin(PlayerJoinEvent event) {
      if (event.getPlayer().getWorld().getEnvironment().equals(Environment.NORMAL)) {
         consumed.putIfAbsent(event.getPlayer().getUniqueId(), new AtomicInteger());
      }
   }

   @EventHandler
   private void onChange(PlayerChangedWorldEvent event) {
      if (!event.getFrom().getEnvironment().equals(Environment.NORMAL) && event.getPlayer().getWorld().getEnvironment().equals(Environment.NORMAL)) {
         consumed.remove(event.getPlayer().getUniqueId());
      } else if (event.getFrom().getEnvironment().equals(Environment.NORMAL) && !event.getPlayer().getWorld().getEnvironment().equals(Environment.NORMAL)) {
         consumed.put(event.getPlayer().getUniqueId(), new AtomicInteger());
      }
   }

   @EventHandler(priority = EventPriority.NORMAL)
   private void onPlayerItemConsume(PlayerItemConsumeEvent event) {
      Player player = event.getPlayer();
      if (event.getItem() != null && event.getItem().getType() == Material.GOLDEN_APPLE) {
         if (event.getItem().getType() != Material.GOLDEN_APPLE || event.getItem().getDurability() == 0) {
            if (player.getWorld().getEnvironment() != Environment.NORMAL) {
               if (!event.isCancelled()) {
                  consumed.putIfAbsent(player.getUniqueId(), new AtomicInteger());
                  AtomicInteger consumednum = consumed.getOrDefault(player.getUniqueId(), new AtomicInteger(0));
                  if (consumednum.get() >= 12) {
                     event.setCancelled(true);
                     player.sendMessage(
                        ChatColor.YELLOW
                           + "You have already consumed 12 Golden Apples in the "
                           + WordUtils.capitalizeFully(player.getWorld().getEnvironment().name().replace("_", " "))
                           + "!"
                     );
                  } else {
                     consumed.put(player.getUniqueId(), new AtomicInteger(consumednum.incrementAndGet()));
                  }
               }
            }
         }
      }
   }
}
