package net.frozenorb.foxtrot.cheatbreaker;

import cc.fyre.modsuite.mod.ModHandler;
import cc.fyre.modsuite.mod.event.PlayerModModeEvent;
import com.cheatbreaker.api.CheatBreakerAPI;
import com.cheatbreaker.api.event.PlayerRegisterCBEvent;
import com.cheatbreaker.api.event.PlayerUnregisterCBEvent;
import com.cheatbreaker.api.object.CBWaypoint;
import com.cheatbreaker.api.object.MinimapStatus;
import com.cheatbreaker.api.object.StaffModule;
import com.cheatbreaker.nethandler.server.CBPacketTeammates;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.logging.Level;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.commands.DisableCommand;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.nametag.FactionNametagFormatter;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockVector;

public final class CheatBreakerService implements Listener {
   private static final long UPDATE_TICKS = 10L;
   private final Foxtrot plugin;
   private final CheatBreakerAPI api;
   private final Map<UUID, CheatBreakerService.Session> sessions = new HashMap<>();
   private final Map<UUID, Player> pendingWorldUpdates = new HashMap<>();
   private BukkitTask task;
   private boolean closed;
   private long lastErrorLog;

   CheatBreakerService(Foxtrot plugin) {
      this.plugin = plugin;
      this.api = CheatBreakerAPI.getInstance();
   }

   void start() {
      this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
      this.task = this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, this::refresh, 1L, 10L);
   }

   void refresh() {
      if (!this.closed && this.api.isEnabled()) {
         for (Player viewer : this.plugin.getServer().getOnlinePlayers()) {
            if (this.api.isRunningCheatBreaker(viewer)) {
               this.update(viewer);
            } else {
               this.sessions.remove(viewer.getUniqueId());
            }
         }
      }
   }

   private void update(Player viewer) {
      if (!this.closed && !this.pendingWorldUpdates.containsKey(viewer.getUniqueId()) && viewer.isOnline() && this.api.isRunningCheatBreaker(viewer)) {
         try {
            CheatBreakerService.Session session = this.sessions.get(viewer.getUniqueId());
            if (session == null) {
               session = new CheatBreakerService.Session();
               if (this.enabled("allow-minimap")) {
                  this.api.setMinimapStatus(viewer, MinimapStatus.NEUTRAL);
               }

               this.sessions.put(viewer.getUniqueId(), session);
            }

            this.updateStaff(viewer, session);
            Team team = this.plugin.getTeamHandler().getTeam(viewer.getUniqueId());
            this.updateTeammates(viewer, team);
            if (session.refreshCount++ % 4 == 0) {
               this.updateWaypoints(viewer, team, session);
               this.updateNametags(viewer, session);
            }
         } catch (RuntimeException | LinkageError error) {
            this.logFailure(error);
         }
      }
   }

   private void updateStaff(Player viewer, CheatBreakerService.Session session) {
      String permission = this.plugin.getConfig().getString("cheatbreaker.staff.permission", "modsuite.command.modmode");
      boolean allowed = this.enabled("staff.enabled")
         && permission != null
         && !permission.trim().isEmpty()
         && viewer.hasPermission(permission)
         && (!this.plugin.getConfig().getBoolean("cheatbreaker.staff.require-mod-mode", false) || ModHandler.INSTANCE.isInModMode(viewer.getUniqueId()));
      if (session.staffEnabled == null || session.staffEnabled != allowed) {
         this.setStaff(viewer, allowed);
         session.staffEnabled = allowed;
      }
   }

   private void setStaff(Player viewer, boolean allowed) {
      for (StaffModule module : StaffModule.values()) {
         this.api.setStaffModuleState(viewer, module, allowed);
      }
   }

   private void updateTeammates(Player viewer, Team team) {
      Map<UUID, Map<String, Double>> members = new HashMap<>();
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
               Map<String, Double> coordinates = new HashMap<>();
               coordinates.put("x", location.getX());
               coordinates.put("y", location.getY() + 4.0);
               coordinates.put("z", location.getZ());
               members.put(member.getUniqueId(), coordinates);
            }
         }
      }

      this.api.sendTeammates(viewer, new CBPacketTeammates(viewer.getUniqueId(), 500L, members));
   }

   private void updateWaypoints(Player viewer, Team team, CheatBreakerService.Session session) {
      Map<String, CBWaypoint> desired = new HashMap<>();
      if (this.enabled("waypoints.spawn")) {
         this.addWaypoint(viewer, desired, "spawn", ChatColor.GREEN + "Spawn", this.getSpawnWaypointLocation(), Color.GREEN);
      }

      if (this.enabled("waypoints.faction") && team != null) {
         this.addWaypoint(viewer, desired, "hq", ChatColor.BLUE + "HQ", team.getHQ(), Color.BLUE);
         this.addWaypoint(viewer, desired, "rally", ChatColor.GOLD + "Rally", team.getRallyPoint(), Color.ORANGE);
         Team focus = team.getFocusedTeam();
         if (focus != null) {
            this.addWaypoint(viewer, desired, "focus", ChatColor.YELLOW + focus.getName() + "'s HQ", focus.getHQ(), Color.RED);
         }
      }

      if (this.enabled("waypoints.koth")) {
         for (Event event : this.plugin.getEventHandler().getEvents()) {
            if (event instanceof KOTH && event.isActive()) {
               KOTH koth = (KOTH)event;
               this.addWaypoint(
                  viewer, desired, "koth:" + koth.getName(), "KOTH: " + koth.getName(), this.getKothWaypointLocation(koth), koth.getWaypointColor()
               );
            }
         }
      }

      Iterator<Entry<String, CBWaypoint>> old = session.waypoints.entrySet().iterator();

      while (old.hasNext()) {
         Entry<String, CBWaypoint> entry = old.next();
         if (!entry.getValue().equals(desired.get(entry.getKey()))) {
            this.api.removeWaypoint(viewer, entry.getValue());
            old.remove();
         }
      }

      for (Entry<String, CBWaypoint> entry : desired.entrySet()) {
         if (!session.waypoints.containsKey(entry.getKey())) {
            this.api.sendWaypoint(viewer, entry.getValue());
            session.waypoints.put(entry.getKey(), entry.getValue());
         }
      }
   }

   private Location getSpawnWaypointLocation() {
      Location saved = this.getSavedWaypointLocation("Spawn");
      if (saved != null) {
         return saved;
      }

      World world = this.plugin.getServer().getWorld("world");
      if (world == null) {
         world = this.plugin.getServer().getWorld("Spawn");
      }

      return world == null ? null : world.getSpawnLocation();
   }

   private Location getKothWaypointLocation(KOTH koth) {
      Location saved = this.getSavedWaypointLocation(koth.getName());
      if (saved != null) {
         return saved;
      }

      World world = this.plugin.getServer().getWorld(koth.getWorld());
      BlockVector cap = koth.getCapLocation();
      return world != null && cap != null ? new Location(world, cap.getBlockX(), cap.getBlockY() + 2, cap.getBlockZ()) : null;
   }

   private Location getSavedWaypointLocation(String name) {
      Team landmark = this.plugin.getTeamHandler().getTeam(name);
      Location location = landmark == null ? null : landmark.getHQ();
      return location != null && location.getWorld() != null ? location.clone() : null;
   }

   private void addWaypoint(Player viewer, Map<String, CBWaypoint> desired, String key, String name, Location location, Color color) {
      if (location != null && location.getWorld() != null && location.getWorld().getUID().equals(viewer.getWorld().getUID())) {
         desired.put(key, new CBWaypoint(name + ChatColor.RESET, location, (color == null ? Color.RED : color).getRGB(), true));
      }
   }

   private void updateNametags(Player viewer, CheatBreakerService.Session session) {
      Map<UUID, List<String>> desired = new HashMap<>();
      if (this.enabled("nametags")) {
         for (Player target : this.plugin.getServer().getOnlinePlayers()) {
            if (target.isOnline() && viewer.getWorld().equals(target.getWorld()) && viewer.canSee(target)) {
               List<String> lines = FactionNametagFormatter.format(target, viewer);
               desired.put(target.getUniqueId(), lines);
               if (!lines.equals(session.nametags.get(target.getUniqueId()))) {
                  this.api.overrideNametag(target, lines, viewer);
               }
            }
         }
      }

      for (UUID targetId : session.nametags.keySet()) {
         if (!desired.containsKey(targetId)) {
            Player target = this.plugin.getServer().getPlayer(targetId);
            if (target != null) {
               this.api.resetNametag(target, viewer);
            }
         }
      }

      session.nametags.clear();
      session.nametags.putAll(desired);
   }

   private boolean enabled(String key) {
      return this.plugin.getConfig().getBoolean("cheatbreaker." + key, true);
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onRegister(PlayerRegisterCBEvent event) {
      this.scheduleWorldUpdate(event.getPlayer());
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onUnregister(PlayerUnregisterCBEvent event) {
      this.pendingWorldUpdates.remove(event.getPlayer().getUniqueId());
      this.sessions.remove(event.getPlayer().getUniqueId());
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onQuit(PlayerQuitEvent event) {
      UUID id = event.getPlayer().getUniqueId();
      this.pendingWorldUpdates.remove(id);
      this.sessions.remove(id);

      for (CheatBreakerService.Session session : this.sessions.values()) {
         session.nametags.remove(id);
      }

      this.plugin.getServer().getScheduler().runTask(this.plugin, this::refresh);
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onWorldChange(PlayerChangedWorldEvent event) {
      this.scheduleWorldUpdate(event.getPlayer());
   }

   private void scheduleWorldUpdate(Player player) {
      UUID id = player.getUniqueId();
      this.pendingWorldUpdates.put(id, player);
      this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
         if (!this.closed && this.pendingWorldUpdates.get(id) == player) {
            this.pendingWorldUpdates.remove(id);
            this.cleanup(player);
            this.update(player);
         }
      });
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onModMode(PlayerModModeEvent event) {
      this.plugin.getServer().getScheduler().runTask(this.plugin, () -> this.update(event.getPlayer()));
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onPluginDisable(PluginDisableEvent event) {
      if (event.getPlugin() == this.api || event.getPlugin() == this.plugin) {
         this.close();
      }
   }

   private void cleanup(Player viewer) {
      CheatBreakerService.Session session = this.sessions.remove(viewer.getUniqueId());
      if (session != null && viewer.isOnline() && this.api.isRunningCheatBreaker(viewer)) {
         this.setStaff(viewer, false);
         this.api.sendTeammates(viewer, new CBPacketTeammates(viewer.getUniqueId(), 0L, new HashMap()));

         for (CBWaypoint waypoint : session.waypoints.values()) {
            this.api.removeWaypoint(viewer, waypoint);
         }

         for (UUID targetId : session.nametags.keySet()) {
            Player target = this.plugin.getServer().getPlayer(targetId);
            if (target != null) {
               this.api.resetNametag(target, viewer);
            }
         }
      }
   }

   void close() {
      this.closed = true;
      this.pendingWorldUpdates.clear();
      if (this.task != null) {
         this.task.cancel();
         this.task = null;
      }

      HandlerList.unregisterAll(this);

      for (UUID viewerId : new ArrayList<>(this.sessions.keySet())) {
         Player viewer = this.plugin.getServer().getPlayer(viewerId);
         if (viewer != null) {
            try {
               this.cleanup(viewer);
            } catch (RuntimeException | LinkageError error) {
               this.logFailure(error);
            }
         }
      }

      this.sessions.clear();
   }

   private void logFailure(Throwable error) {
      long now = System.currentTimeMillis();
      if (now - this.lastErrorLog > 60000L) {
         this.lastErrorLog = now;
         this.plugin.getLogger().log(Level.WARNING, "[CheatBreaker] Failed to synchronize client state.", error);
      }
   }

   private static final class Session {
      private int refreshCount;
      private Boolean staffEnabled;
      private final Map<String, CBWaypoint> waypoints = new HashMap<>();
      private final Map<UUID, List<String>> nametags = new HashMap<>();

      private Session() {
      }
   }
}
