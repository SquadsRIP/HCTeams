package net.frozenorb.foxtrot.apollo;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ApolloService {
   private static boolean initialized = false;
   private static boolean apolloEnabled = false;
   private static Class<?> apolloBukkitPlatformClass;
   private static Method getInstanceMethod;
   private static Method getOptionsMethod;
   private static Class<?> optionsClass;
   private static Method setOptionGlobalMethod;
   private static Method setOptionPlayerMethod;
   private static Class<?> modNickHiderClass;
   private static Object nickHiderEnabledOption;
   private static Class<?> apolloPlayerManagerClass;
   private static Method getPlayerManagerMethod;
   private static Method getPlayerMethod;

   public static void init() {
      if (!initialized) {
         initialized = true;

         try {
            Plugin apolloPlugin = Bukkit.getPluginManager().getPlugin("Apollo-Bukkit");
            if (apolloPlugin == null || !apolloPlugin.isEnabled()) {
               Foxtrot.getInstance().getLogger().info("[ApolloService] Apollo not found — nickhider/xray/waypoints disabled.");
               return;
            }

            apolloBukkitPlatformClass = Class.forName("com.lunarclient.apollo.ApolloBukkitPlatform");
            getInstanceMethod = apolloBukkitPlatformClass.getMethod("getInstance");
            getOptionsMethod = apolloBukkitPlatformClass.getMethod("getOptions");
            optionsClass = Class.forName("com.lunarclient.apollo.option.Options");

            for (Method m : optionsClass.getMethods()) {
               Class<?>[] params;
               if (m.getName().equals("set")
                  && m.getParameterCount() == 2
                  && (params = m.getParameterTypes())[0].getName().contains("Option")
                  && !params[0].getName().contains("ApolloPlayer")) {
                  setOptionGlobalMethod = m;
                  break;
               }
            }

            for (Method m : optionsClass.getMethods()) {
               if (m.getName().equals("set") && m.getParameterCount() == 3 && m.getParameterTypes()[0].getName().contains("ApolloPlayer")) {
                  setOptionPlayerMethod = m;
                  break;
               }
            }

            modNickHiderClass = Class.forName("com.lunarclient.apollo.mods.impl.ModNickHider");
            nickHiderEnabledOption = modNickHiderClass.getField("ENABLED").get(null);
            apolloPlayerManagerClass = Class.forName("com.lunarclient.apollo.player.ApolloPlayerManager");

            try {
               getPlayerManagerMethod = apolloBukkitPlatformClass.getMethod("getPlayerManager");
            } catch (NoSuchMethodException e) {
               Class<?> apolloManagerClass = Class.forName("com.lunarclient.apollo.ApolloManager");

               for (Method m : apolloManagerClass.getMethods()) {
                  if (m.getName().equals("getPlayerManager")) {
                     getPlayerManagerMethod = m;
                     break;
                  }
               }
            }

            if (getPlayerManagerMethod != null) {
               getPlayerMethod = apolloPlayerManagerClass.getMethod("getPlayer", UUID.class);
            }

            apolloEnabled = true;
            Foxtrot.getInstance().getLogger().info("[ApolloService] Apollo found — nickhider/xray/waypoints enabled.");
         } catch (Exception e) {
            Foxtrot.getInstance().getLogger().log(Level.WARNING, "[ApolloService] Failed to initialize Apollo integration", e);
            apolloEnabled = false;
         }
      }
   }

   public static boolean isApolloEnabled() {
      return apolloEnabled;
   }

   public static void disableNickHiderGlobal() {
      if (apolloEnabled && nickHiderEnabledOption != null && setOptionGlobalMethod != null) {
         try {
            Object optionsInstance = getOptionsMethod.invoke(getInstanceMethod.invoke(null));
            setOptionGlobalMethod.invoke(optionsInstance, nickHiderEnabledOption, false);
         } catch (Exception e) {
            Foxtrot.getInstance().getLogger().log(Level.WARNING, "[ApolloService] Failed to disable NickHider globally", e);
         }
      }
   }

   public static void disableNickHider(Player player) {
      if (apolloEnabled && nickHiderEnabledOption != null && setOptionPlayerMethod != null) {
         try {
            Object apolloPlayer = getApolloPlayer(player.getUniqueId());
            if (apolloPlayer == null) {
               return;
            }

            Object optionsInstance = getOptionsMethod.invoke(getInstanceMethod.invoke(null));
            setOptionPlayerMethod.invoke(optionsInstance, apolloPlayer, nickHiderEnabledOption, false);
         } catch (Exception var3) {
         }
      }
   }

   private static Object getApolloPlayer(UUID uuid) {
      if (apolloEnabled && getPlayerManagerMethod != null && getPlayerMethod != null) {
         try {
            Object platform = getInstanceMethod.invoke(null);
            Object playerManager = getPlayerManagerMethod.invoke(platform);
            Optional opt = (Optional)getPlayerMethod.invoke(playerManager, uuid);
            return opt.isPresent() ? opt.get() : null;
         } catch (Exception e) {
            return null;
         }
      } else {
         return null;
      }
   }

   public static void toggleXray(Player player, boolean enabled) {
      if (!apolloEnabled) {
         player.sendMessage("§cApollo is not installed — xray not available.");
      } else {
         player.sendMessage("§cXray is not yet implemented. Requires block update packet system.");
      }
   }

   public static void sendWaypoint(Player player, LCWaypoint waypoint) {
      if (waypoint != null) {
         LunarClientAPI.getInstance().sendWaypoint(player, waypoint);
         if (apolloEnabled) {
            try {
               ApolloWaypoints.display(player, waypoint);
            } catch (Throwable var3) {
            }
         }
      }
   }

   public static void removeWaypoint(Player player, LCWaypoint waypoint) {
      if (waypoint != null) {
         LunarClientAPI.getInstance().removeWaypoint(player, waypoint);
         if (apolloEnabled) {
            try {
               ApolloWaypoints.remove(player, waypoint);
            } catch (Throwable var3) {
            }
         }
      }
   }
}
