package net.frozenorb.foxtrot.server.deathban.listener;

import cc.fyre.proton.util.TimeUtils;
import java.beans.ConstructorProperties;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathbanListener implements Listener {
   private Foxtrot instance;

   @EventHandler(priority = EventPriority.HIGHEST)
   public void onPlayerDeath(PlayerDeathEvent event) {
      Player player = event.getEntity();
      if (!this.instance.getMapHandler().isKitMap()
         && !player.hasPermission("foxtrot.staff")
         && !this.instance.getInDuelPredicate().test(event.getEntity())
         && !player.getWorld().getName().equalsIgnoreCase("Deathban")) {
         int seconds = (int)this.instance.getServerHandler().getDeathban(event.getEntity());
         if (player.getWorld().getEnvironment() == Environment.NETHER) {
            seconds += 300;
         } else if (player.getWorld().getEnvironment() == Environment.THE_END
            || DTRBitmask.CITADEL.appliesAt(player.getLocation())
            || DTRBitmask.KOTH.appliesAt(player.getLocation())) {
            seconds += 180;
         }

         this.instance.getDeathbanMap().deathban(player.getUniqueId(), seconds);
         player.removeMetadata("PVP_TIMER_BYPASS", this.instance);
         this.instance.getPvPTimerMap().removeTimer(player.getUniqueId());
         player.sendMessage(ChatColor.RED + "You have been deathbanned for " + TimeUtils.formatIntoDetailedString(seconds) + "!");
         this.kickPlayer(player);
      }
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent event) {
      Player player = event.getPlayer();
      if (!this.instance.getMapHandler().isKitMap() && this.instance.getDeathbanMap().isDeathbanned(player.getUniqueId())) {
         boolean deathBanned = this.instance.getDeathbanMap().isDeathbanned(player.getUniqueId());
         if (deathBanned) {
            if (player.hasPermission("foxtrot.staff")) {
               Foxtrot.getInstance().getDeathbanArenaHandler().revive(player.getUniqueId());
               return;
            }

            if (this.instance.getServerHandler().isEOTW()) {
               player.kickPlayer(ChatColor.RED + "You are deathbanned for the rest of the map!\nCheck out SOTW information at:\nComing Soon!");
               return;
            }
         }

         long seconds = (this.instance.getDeathbanMap().getDeathban(player.getUniqueId()) - System.currentTimeMillis()) / 1000L;
         player.sendMessage("");
         player.sendMessage(ChatColor.RED + "You are still deathbanned for " + TimeUtils.formatIntoDetailedString((int)seconds) + "!");
         this.kickPlayer(player);
      }
   }

   public void kickPlayer(final Player player) {
      (new BukkitRunnable() {
         @Override
         public void run() {
            if (player.isOnline()) {
               player.performCommand("hub");
            }
         }
      }).runTaskLater(this.instance, 40L);
      (new BukkitRunnable() {
         @Override
         public void run() {
            if (player.isOnline()) {
               player.kickPlayer(ChatColor.RED + "You are deathbanned!");
            }
         }
      }).runTaskLater(this.instance, 120L);
   }

   @ConstructorProperties("instance")
   public DeathbanListener(Foxtrot instance) {
      this.instance = instance;
   }
}
