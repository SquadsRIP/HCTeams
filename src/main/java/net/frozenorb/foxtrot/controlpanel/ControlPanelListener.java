package net.frozenorb.foxtrot.controlpanel;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ControlPanelListener implements Listener {
   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onChat(AsyncPlayerChatEvent event) {
      ControlPanelService.logChat(event.getPlayer().getName(), event.getMessage());
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onCommand(PlayerCommandPreprocessEvent event) {
      ControlPanelService.logCommand(event.getPlayer().getName(), event.getMessage());
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onDeath(PlayerDeathEvent event) {
      Player victim = event.getEntity();
      Player killer = victim.getKiller();
      String killerName = killer != null ? killer.getName() : "Environment";
      String victimName = victim.getName();
      String weapon = killer != null && killer.getItemInHand() != null ? killer.getItemInHand().getType().name() : "NONE";
      ControlPanelService.logKill(
         killerName, victimName, weapon, victim.getLocation().getX(), victim.getLocation().getY(), victim.getLocation().getZ(), victim.getWorld().getName()
      );
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent event) {
      final Player player = event.getPlayer();
      (new BukkitRunnable() {
         @Override
         public void run() {
            try {
               long playtime = 0L;
               ControlPanelService.logPlayer(player.getName(), player.getUniqueId().toString(), System.currentTimeMillis(), playtime, 0, 0);
            } catch (Exception var3) {
            }
         }
      }).runTaskAsynchronously(Foxtrot.getInstance());
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      Player player = event.getPlayer();
      ControlPanelService.logPlayer(player.getName(), player.getUniqueId().toString(), System.currentTimeMillis(), 0L, 0, 0);
   }

   public static void startStatusTask() {
      (new BukkitRunnable() {
         private final long startTime = System.currentTimeMillis();

         @Override
         public void run() {
            int onlinePlayers = ControlPanelListener.getOnlinePlayersList().size();
            double tps = 20.0;
            long uptime = System.currentTimeMillis() - this.startTime;
            ControlPanelService.updateStatus(onlinePlayers, tps, uptime);
         }
      }).runTaskTimer(Foxtrot.getInstance(), 100L, 600L);
   }

   private static List<Player> getOnlinePlayersList() {
      try {
         Method m = Bukkit.class.getMethod("getOnlinePlayers");
         Object result = m.invoke(null);
         if (result instanceof Collection) {
            return new ArrayList<>((Collection<? extends Player>)result);
         }

         if (result instanceof Player[]) {
            return Arrays.asList((Player[])result);
         }
      } catch (Exception var2) {
      }

      return new ArrayList<>();
   }
}
