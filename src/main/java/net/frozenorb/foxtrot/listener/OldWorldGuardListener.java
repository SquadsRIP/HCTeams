package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class OldWorldGuardListener implements Listener {
   private static final String OLD_WORLD = "world";
   private static final String BYPASS_PERMISSION = "foxtrot.oldworld.bypass";
   private static final String MOVED_MESSAGE = ChatColor.YELLOW + "The old map is closed. You have been moved to the new map's spawn with your inventory.";

   public OldWorldGuardListener() {
      Foxtrot.getInstance().getServer().getScheduler().runTaskTimer(Foxtrot.getInstance(), () -> {
         World oldWorld = Foxtrot.getInstance().getServer().getWorld(OLD_WORLD);
         if (oldWorld != null && isGuarded()) {
            for (Player player : oldWorld.getPlayers()) {
               this.moveToSpawn(player);
            }
         }
      }, 40L, 40L);
   }

   private static boolean isGuarded() {
      return !Foxtrot.getInstance().getMapHandler().isKitMap();
   }

   private static boolean isOldWorld(World world) {
      return world != null && world.getName().equals(OLD_WORLD);
   }

   private static boolean canBypass(Player player) {
      return player.isOp() || player.hasPermission(BYPASS_PERMISSION);
   }

   private void moveToSpawn(Player player) {
      if (player.isOnline() && !player.isDead() && isOldWorld(player.getWorld()) && !canBypass(player)) {
         if (player.teleport(Foxtrot.getInstance().getServerHandler().getSpawnLocation())) {
            player.sendMessage(MOVED_MESSAGE);
            Foxtrot.getInstance().getLogger().info("[OldWorldGuard] Moved " + player.getName() + " out of the old map.");
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onJoin(PlayerJoinEvent event) {
      if (isGuarded()) {
         Player player = event.getPlayer();
         Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> this.moveToSpawn(player), 20L);
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
   public void onTeleport(PlayerTeleportEvent event) {
      Location to = event.getTo();
      if (isGuarded() && to != null && isOldWorld(to.getWorld()) && !isOldWorld(event.getFrom().getWorld()) && !canBypass(event.getPlayer())) {
         event.setTo(Foxtrot.getInstance().getServerHandler().getSpawnLocation());
         event.getPlayer().sendMessage(ChatColor.RED + "The old map is closed. Sending you to the new map's spawn instead.");
      }
   }
}
