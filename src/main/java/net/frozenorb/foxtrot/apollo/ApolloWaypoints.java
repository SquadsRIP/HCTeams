package net.frozenorb.foxtrot.apollo;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.common.location.ApolloBlockLocation;
import com.lunarclient.apollo.module.waypoint.Waypoint;
import com.lunarclient.apollo.module.waypoint.WaypointModule;
import com.lunarclient.apollo.player.ApolloPlayer;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import java.awt.Color;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

// Only touched after ApolloService has confirmed Apollo is loaded, so the Apollo classes resolve.
final class ApolloWaypoints {
   private ApolloWaypoints() {
   }

   static void display(Player player, LCWaypoint waypoint) {
      Optional<ApolloPlayer> apolloPlayer = Apollo.getPlayerManager().getPlayer(player.getUniqueId());
      String world = worldName(waypoint.getWorld());
      if (apolloPlayer.isPresent() && world != null) {
         Waypoint apolloWaypoint = Waypoint.builder()
            .name(name(waypoint))
            .location(ApolloBlockLocation.builder().world(world).x(waypoint.getX()).y(waypoint.getY()).z(waypoint.getZ()).build())
            .color(new Color(waypoint.getColor()))
            .preventRemoval(false)
            .hidden(false)
            .showBeam(true)
            .highlightBlock(false)
            .build();
         Apollo.getModuleManager().getModule(WaypointModule.class).displayWaypoint(apolloPlayer.get(), apolloWaypoint);
      }
   }

   static void remove(Player player, LCWaypoint waypoint) {
      Optional<ApolloPlayer> apolloPlayer = Apollo.getPlayerManager().getPlayer(player.getUniqueId());
      if (apolloPlayer.isPresent()) {
         Apollo.getModuleManager().getModule(WaypointModule.class).removeWaypoint(apolloPlayer.get(), name(waypoint));
      }
   }

   // LCWaypoint stores the legacy API's world identifier (the world UUID), but Apollo wants the world name.
   private static String worldName(String identifier) {
      if (identifier == null) {
         return null;
      }

      try {
         World world = Bukkit.getWorld(UUID.fromString(identifier));
         if (world != null) {
            return world.getName();
         }
      } catch (IllegalArgumentException var2) {
      }

      World world = Bukkit.getWorld(identifier);
      return world == null ? null : world.getName();
   }

   private static String name(LCWaypoint waypoint) {
      return ChatColor.stripColor(waypoint.getName());
   }
}
