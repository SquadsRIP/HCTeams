package net.frozenorb.foxtrot.apollo;

import cc.fyre.modsuite.mod.ModHandler;
import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.common.location.ApolloBlockLocation;
import com.lunarclient.apollo.common.location.ApolloLocation;
import com.lunarclient.apollo.module.team.TeamMember;
import com.lunarclient.apollo.module.team.TeamModule;
import com.lunarclient.apollo.module.waypoint.Waypoint;
import com.lunarclient.apollo.module.waypoint.WaypointModule;
import com.lunarclient.apollo.player.ApolloPlayer;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.logging.Level;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.commands.DisableCommand;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.team.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockVector;

// Keeps every Lunar Client (Apollo) player's team view and waypoints in sync with their faction,
// the same way CheatBreakerService does for CheatBreaker. Polling instead of one-shot sends means
// players whose Apollo handshake finishes late, or who join/leave/switch teams, still converge.
// Only touched after ApolloService has confirmed Apollo is loaded, so the Apollo classes resolve.
public final class ApolloSyncService implements Listener {
   private static final long UPDATE_TICKS = 2L;
   private static final int WAYPOINT_EVERY = 10;
   private static ApolloSyncService instance;
   private final Foxtrot plugin;
   private final Map<UUID, ApolloSyncService.Session> sessions = new HashMap<>();
   private BukkitTask task;
   private int tick;
   private long lastErrorLog;

   private ApolloSyncService(Foxtrot plugin) {
      this.plugin = plugin;
   }

   public static void start(Foxtrot plugin) {
      if (instance == null && plugin.getConfig().getBoolean("lunar.enabled", true)) {
         instance = new ApolloSyncService(plugin);
         plugin.getServer().getPluginManager().registerEvents(instance, plugin);
         instance.task = plugin.getServer().getScheduler().runTaskTimer(plugin, instance::refresh, 20L, UPDATE_TICKS);
         plugin.getLogger().info("[Apollo] Team view and faction/spawn/KOTH waypoints enabled for Lunar Client.");
      }
   }

   public static boolean isRunning() {
      return instance != null;
   }

   private void refresh() {
      boolean waypoints = this.tick++ % WAYPOINT_EVERY == 0;

      for (Player viewer : this.plugin.getServer().getOnlinePlayers()) {
         Optional<ApolloPlayer> apolloPlayer = Apollo.getPlayerManager().getPlayer(viewer.getUniqueId());
         if (!apolloPlayer.isPresent()) {
            this.sessions.remove(viewer.getUniqueId());
         } else {
            try {
               ApolloSyncService.Session session = this.sessions.get(viewer.getUniqueId());
               if (session == null) {
                  session = new ApolloSyncService.Session();
                  this.sessions.put(viewer.getUniqueId(), session);
               }

               Team team = this.plugin.getTeamHandler().getTeam(viewer.getUniqueId());
               this.updateTeammates(viewer, apolloPlayer.get(), team, session);
               if (waypoints || session.waypoints == null) {
                  this.updateWaypoints(viewer, apolloPlayer.get(), team, session);
               }
            } catch (RuntimeException | LinkageError error) {
               this.logFailure(error);
            }
         }
      }
   }

   private void updateTeammates(Player viewer, ApolloPlayer apolloPlayer, Team team, ApolloSyncService.Session session) {
      List<TeamMember> members = new ArrayList<>();
      if (this.enabled("team-view")
         && !DisableCommand.teamView
         && !CustomTimerCreateCommand.isSOTWTimer()
         && this.plugin.getLcTeamViewMap().isLCTeamView(viewer.getUniqueId())
         && team != null) {
         for (Player member : team.getOnlineMembers()) {
            if (member.isOnline()
               && !member.isDead()
               && member != viewer
               && member.getWorld().equals(viewer.getWorld())
               && viewer.canSee(member)
               && !ModHandler.INSTANCE.isInVanish(member.getUniqueId())) {
               Location location = member.getLocation();
               members.add(
                  TeamMember.builder()
                     .playerUuid(member.getUniqueId())
                     .displayName(Component.text(member.getName(), NamedTextColor.GREEN))
                     .markerColor(Color.GREEN)
                     .location(
                        ApolloLocation.builder().world(location.getWorld().getName()).x(location.getX()).y(location.getY()).z(location.getZ()).build()
                     )
                     .build()
               );
            }
         }
      }

      TeamModule module = Apollo.getModuleManager().getModule(TeamModule.class);
      if (!members.isEmpty()) {
         module.updateTeamMembers(apolloPlayer, members);
         session.teamShown = true;
      } else if (session.teamShown) {
         module.resetTeamMembers(apolloPlayer);
         session.teamShown = false;
      }
   }

   private void updateWaypoints(Player viewer, ApolloPlayer apolloPlayer, Team team, ApolloSyncService.Session session) {
      Map<String, ApolloSyncService.Mark> desired = new LinkedHashMap<>();
      if (this.enabled("waypoints.spawn")) {
         this.addWaypoint(desired, "Spawn", this.getSpawnWaypointLocation(), Color.GREEN);
      }

      if (this.enabled("waypoints.faction") && team != null) {
         this.addWaypoint(desired, "HQ", team.getHQ(), Color.BLUE);
         this.addWaypoint(desired, "Rally", team.getRallyPoint(), Color.ORANGE);
         Team focus = team.getFocusedTeam();
         if (focus != null) {
            this.addWaypoint(desired, focus.getName() + "'s HQ", focus.getHQ(), Color.RED);
         }
      }

      if (this.enabled("waypoints.koth")) {
         for (Event event : this.plugin.getEventHandler().getEvents()) {
            if (event instanceof KOTH && event.isActive()) {
               KOTH koth = (KOTH)event;
               this.addWaypoint(desired, koth.getName(), this.getKothWaypointLocation(koth), this.getKothColor(koth));
            }
         }
      }

      WaypointModule module = Apollo.getModuleManager().getModule(WaypointModule.class);
      Map<String, ApolloSyncService.Mark> shown = session.waypoints == null ? new HashMap<>() : session.waypoints;
      if (session.waypoints == null) {
         // First sync for this client: clear anything a one-shot send or Apollo's config defaults left behind.
         module.resetWaypoints(apolloPlayer);
      }

      Iterator<Entry<String, ApolloSyncService.Mark>> old = shown.entrySet().iterator();

      while (old.hasNext()) {
         Entry<String, ApolloSyncService.Mark> entry = old.next();
         if (!entry.getValue().equals(desired.get(entry.getKey()))) {
            module.removeWaypoint(apolloPlayer, entry.getKey());
            old.remove();
         }
      }

      for (Entry<String, ApolloSyncService.Mark> entry : desired.entrySet()) {
         if (!shown.containsKey(entry.getKey())) {
            ApolloSyncService.Mark mark = entry.getValue();
            module.displayWaypoint(
               apolloPlayer,
               Waypoint.builder()
                  .name(entry.getKey())
                  .location(ApolloBlockLocation.builder().world(mark.world).x(mark.x).y(mark.y).z(mark.z).build())
                  .color(mark.color)
                  .preventRemoval(false)
                  .hidden(false)
                  .showBeam(true)
                  .highlightBlock(false)
                  .build()
            );
            shown.put(entry.getKey(), mark);
         }
      }

      session.waypoints = shown;
   }

   private void addWaypoint(Map<String, ApolloSyncService.Mark> desired, String name, Location location, Color color) {
      if (location != null && location.getWorld() != null) {
         desired.put(name, new ApolloSyncService.Mark(location, color == null ? Color.RED : color));
      }
   }

   private Location getSpawnWaypointLocation() {
      Location saved = this.getSavedWaypointLocation("Spawn");
      if (saved != null) {
         return saved;
      }

      World world = this.plugin.getServerHandler().getMapWorld();
      return world == null ? null : world.getSpawnLocation();
   }

   private Location getKothWaypointLocation(KOTH koth) {
      World world = this.plugin.getServer().getWorld(koth.getWorld());
      BlockVector cap = koth.getCapLocation();
      return world != null && cap != null ? new Location(world, cap.getBlockX(), cap.getBlockY() + 2, cap.getBlockZ()) : null;
   }

   private Location getSavedWaypointLocation(String name) {
      Team landmark = this.plugin.getTeamHandler().getTeam(name);
      Location location = landmark == null ? null : landmark.getHQ();
      return location != null && location.getWorld() != null ? location.clone() : null;
   }

   private Color getKothColor(KOTH koth) {
      String configured = this.plugin.getConfig().getString("koth.colors." + koth.getName(), "");
      if (configured != null && configured.length() >= 2 && configured.startsWith("&")) {
         org.bukkit.ChatColor chatColor = org.bukkit.ChatColor.getByChar(configured.charAt(1));
         if (chatColor != null) {
            return chatColorToColor(chatColor);
         }
      }

      return koth.getWaypointColor();
   }

   private static Color chatColorToColor(org.bukkit.ChatColor chatColor) {
      switch (chatColor) {
         case BLACK:
            return Color.BLACK;
         case DARK_BLUE:
            return Color.BLUE.darker();
         case DARK_GREEN:
            return Color.GREEN.darker();
         case DARK_AQUA:
            return Color.CYAN.darker();
         case DARK_RED:
            return Color.RED.darker();
         case DARK_PURPLE:
            return Color.MAGENTA.darker();
         case GOLD:
            return Color.ORANGE;
         case GRAY:
            return Color.GRAY;
         case DARK_GRAY:
            return Color.DARK_GRAY;
         case BLUE:
            return Color.BLUE;
         case GREEN:
            return Color.GREEN;
         case AQUA:
            return Color.CYAN;
         case LIGHT_PURPLE:
            return Color.PINK;
         case YELLOW:
            return Color.YELLOW;
         case WHITE:
            return Color.WHITE;
         default:
            return Color.RED;
      }
   }

   private boolean enabled(String key) {
      return this.plugin.getConfig().getBoolean("lunar." + key, true);
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onQuit(PlayerQuitEvent event) {
      this.sessions.remove(event.getPlayer().getUniqueId());
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onWorldChange(PlayerChangedWorldEvent event) {
      ApolloSyncService.Session session = this.sessions.get(event.getPlayer().getUniqueId());
      if (session != null) {
         // Resend everything on the next tick; the client may drop waypoints when its world changes.
         session.waypoints = null;
      }
   }

   private void logFailure(Throwable error) {
      long now = System.currentTimeMillis();
      if (now - this.lastErrorLog > 60000L) {
         this.lastErrorLog = now;
         this.plugin.getLogger().log(Level.WARNING, "[Apollo] Failed to synchronize Lunar Client state.", error);
      }
   }

   private static final class Session {
      private boolean teamShown;
      private Map<String, ApolloSyncService.Mark> waypoints;

      private Session() {
      }
   }

   private static final class Mark {
      private final String world;
      private final int x;
      private final int y;
      private final int z;
      private final Color color;

      private Mark(Location location, Color color) {
         this.world = location.getWorld().getName();
         this.x = location.getBlockX();
         this.y = location.getBlockY();
         this.z = location.getBlockZ();
         this.color = color;
      }

      @Override
      public boolean equals(Object other) {
         if (!(other instanceof ApolloSyncService.Mark)) {
            return false;
         }

         ApolloSyncService.Mark mark = (ApolloSyncService.Mark)other;
         return this.world.equals(mark.world) && this.x == mark.x && this.y == mark.y && this.z == mark.z && this.color.equals(mark.color);
      }

      @Override
      public int hashCode() {
         return ((this.world.hashCode() * 31 + this.x) * 31 + this.y) * 31 + this.z;
      }
   }
}
