package net.frozenorb.foxtrot.apollo;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.common.location.ApolloBlockLocation;
import com.lunarclient.apollo.module.waypoint.Waypoint;
import com.lunarclient.apollo.module.waypoint.WaypointModule;
import com.lunarclient.apollo.player.ApolloPlayer;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import java.awt.Color;
import java.util.Optional;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

// Only touched after ApolloService has confirmed Apollo is loaded, so the Apollo classes resolve.
final class ApolloWaypoints {
   private ApolloWaypoints() {
   }

   static void display(Player player, LCWaypoint waypoint) {
      Optional<ApolloPlayer> apolloPlayer = Apollo.getPlayerManager().getPlayer(player.getUniqueId());
      if (apolloPlayer.isPresent()) {
         Waypoint apolloWaypoint = Waypoint.builder()
            .name(name(waypoint))
            .location(ApolloBlockLocation.builder().world(waypoint.getWorld()).x(waypoint.getX()).y(waypoint.getY()).z(waypoint.getZ()).build())
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

   private static String name(LCWaypoint waypoint) {
      return ChatColor.stripColor(waypoint.getName());
   }
}
