package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class FirstJoinFixListener implements Listener {
   @EventHandler(priority = EventPriority.HIGH)
   public void onPlayerJoin(PlayerJoinEvent event) {
      Player player = event.getPlayer();
      if (!player.hasPlayedBefore()) {
         Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            World world = Foxtrot.getInstance().getServer().getWorld("world");
            if (world == null) {
               world = Foxtrot.getInstance().getServer().getWorlds().get(0);
            }

            player.teleport(world.getSpawnLocation());
            player.sendMessage("Welcome to the overworld!");
         }, 10L);
      }
   }
}
