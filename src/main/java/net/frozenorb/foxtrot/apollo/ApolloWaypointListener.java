package net.frozenorb.foxtrot.apollo;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.common.location.ApolloBlockLocation;
import com.lunarclient.apollo.module.waypoint.Waypoint;
import com.lunarclient.apollo.module.waypoint.WaypointModule;
import com.lunarclient.apollo.recipients.Recipients;
import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import net.frozenorb.foxtrot.gameplay.events.events.EventActivatedEvent;
import net.frozenorb.foxtrot.gameplay.events.events.EventCapturedEvent;
import net.frozenorb.foxtrot.gameplay.events.events.EventDeactivatedEvent;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;

public class ApolloWaypointListener implements Listener {
   private static final Set<String> activeWaypoints = new HashSet<>();

   public static void startTask(Plugin plugin) {
      (new BukkitRunnable() {
         @Override
         public void run() {
            try {
               for (Event event : Foxtrot.getInstance().getEventHandler().getEvents()) {
                  if (event.getType() == EventType.KOTH && event instanceof KOTH) {
                     KOTH kOTH = (KOTH)event;
                     if (kOTH.isActive() && !ApolloWaypointListener.activeWaypoints.contains(kOTH.getName())) {
                        ApolloWaypointListener.sendKothWaypoint(kOTH);
                     } else if (!kOTH.isActive() && ApolloWaypointListener.activeWaypoints.contains(kOTH.getName())) {
                        ApolloWaypointListener.removeKothWaypoint(kOTH);
                     }
                  }
               }
            } catch (Throwable var4) {
            }
         }
      }).runTaskTimer(plugin, 200L, 200L);
   }

   private static Color chatColorToColor(String string) {
      if (string != null && string.length() >= 2) {
         char c = Character.toLowerCase(string.charAt(1));
         switch (c) {
            case '0':
               return Color.BLACK;
            case '1':
               return Color.BLUE.darker();
            case '2':
               return Color.GREEN.darker();
            case '3':
               return Color.CYAN.darker();
            case '4':
               return Color.RED.darker();
            case '5':
               return Color.MAGENTA.darker();
            case '6':
               return Color.ORANGE;
            case '7':
               return Color.GRAY;
            case '8':
               return Color.DARK_GRAY;
            case '9':
               return Color.BLUE;
            case ':':
            case ';':
            case '<':
            case '=':
            case '>':
            case '?':
            case '@':
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
            case 'G':
            case 'H':
            case 'I':
            case 'J':
            case 'K':
            case 'L':
            case 'M':
            case 'N':
            case 'O':
            case 'P':
            case 'Q':
            case 'R':
            case 'S':
            case 'T':
            case 'U':
            case 'V':
            case 'W':
            case 'X':
            case 'Y':
            case 'Z':
            case '[':
            case '\\':
            case ']':
            case '^':
            case '_':
            case '`':
            default:
               return Color.RED;
            case 'a':
               return Color.GREEN;
            case 'b':
               return Color.CYAN;
            case 'c':
               return Color.RED;
            case 'd':
               return Color.PINK;
            case 'e':
               return Color.YELLOW;
            case 'f':
               return Color.WHITE;
         }
      } else {
         return Color.RED;
      }
   }

   private static Color getKothColor(KOTH kOTH) {
      try {
         String string = Foxtrot.getInstance().getConfig().getString("koth.colors." + kOTH.getName(), "");
         if (string != null && string.length() >= 2 && string.startsWith("&")) {
            return chatColorToColor(string);
         }
      } catch (Throwable var2) {
      }

      return kOTH.getWaypointColor();
   }

   public static void sendKothWaypoint(KOTH kOTH) {
      if (ApolloService.isApolloEnabled()) {
         try {
            BlockVector blockVector = kOTH.getCapLocation();
            World world = Bukkit.getWorld(kOTH.getWorld());
            if (world == null) {
               return;
            }

            int n = blockVector.getBlockX();
            int n2 = blockVector.getBlockY() + 2;
            int n3 = blockVector.getBlockZ();
            Color color = getKothColor(kOTH);
            ApolloBlockLocation apolloBlockLocation = ApolloBlockLocation.builder().world(kOTH.getWorld()).x(n).y(n2).z(n3).build();
            Waypoint waypoint = Waypoint.builder()
               .name(kOTH.getName())
               .location(apolloBlockLocation)
               .color(color)
               .preventRemoval(false)
               .hidden(false)
               .showBeam(true)
               .highlightBlock(false)
               .build();
            ((WaypointModule)Apollo.getModuleManager().getModule(WaypointModule.class)).displayWaypoint(Recipients.ofEveryone(), waypoint);
            activeWaypoints.add(kOTH.getName());
         } catch (Throwable var9) {
         }
      }
   }

   public static void removeKothWaypoint(KOTH kOTH) {
      if (ApolloService.isApolloEnabled()) {
         try {
            ((WaypointModule)Apollo.getModuleManager().getModule(WaypointModule.class)).removeWaypoint(Recipients.ofEveryone(), kOTH.getName());
            activeWaypoints.remove(kOTH.getName());
         } catch (Throwable var2) {
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onKothActivate(EventActivatedEvent eventActivatedEvent) {
      Event event = eventActivatedEvent.getEvent();
      if (event.getType() == EventType.KOTH && event instanceof KOTH) {
         final KOTH kOTH = (KOTH)event;
         (new BukkitRunnable() {
            @Override
            public void run() {
               ApolloWaypointListener.sendKothWaypoint(kOTH);
            }
         }).runTaskLater(Foxtrot.getInstance(), 1L);
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onKothDeactivate(EventDeactivatedEvent eventDeactivatedEvent) {
      Event event = eventDeactivatedEvent.getEvent();
      if (event.getType() == EventType.KOTH && event instanceof KOTH) {
         removeKothWaypoint((KOTH)event);
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onKothCaptured(EventCapturedEvent eventCapturedEvent) {
      Event event = eventCapturedEvent.getEvent();
      if (event.getType() == EventType.KOTH && event instanceof KOTH) {
         removeKothWaypoint((KOTH)event);
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onJoin(PlayerJoinEvent playerJoinEvent) {
      final Player player = playerJoinEvent.getPlayer();
      (new BukkitRunnable() {
         @Override
         public void run() {
            if (player.isOnline()) {
               try {
                  for (Event event : Foxtrot.getInstance().getEventHandler().getEvents()) {
                     KOTH kOTH;
                     if (event.getType() == EventType.KOTH && event instanceof KOTH && (kOTH = (KOTH)event).isActive()) {
                        ApolloWaypointListener.sendKothWaypoint(kOTH);
                     }
                  }
               } catch (Throwable var4) {
               }
            }
         }
      }).runTaskLater(Foxtrot.getInstance(), 60L);
   }
}
