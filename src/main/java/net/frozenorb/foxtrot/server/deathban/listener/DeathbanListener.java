package net.frozenorb.foxtrot.server.deathban.listener;

import cc.fyre.proton.util.TimeUtils;
import cc.fyre.universe.util.BungeeUtil;
import java.beans.ConstructorProperties;
import java.util.concurrent.ThreadLocalRandom;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.kit.data.Kit;

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
         if ((!this.instance.getServerHandler().isEOTW() || !this.instance.getServerHandler().isPreEOTW())
            && this.instance.getServer().getWorld("Deathban") != null) {
            String time = TimeUtils.formatIntoDetailedString(seconds);
            player.sendMessage(ChatColor.RED + "You have been deathbanned for " + time);
            this.instance.getDeathbanArenaHandler().deathban(player);
         } else {
            this.kickPlayer(player);
         }
      }
   }

   @EventHandler
   private void onRespawn(PlayerRespawnEvent event) {
      Player player = event.getPlayer();
      if (this.instance.getDeathbanArenaHandler().isDeathbanArena(player)) {
         Kit kit = Suge.getInstance().getKitHandler().findKit("UltimateDiamond").orElse(null);
         InventoryUtils.resetInventoryNow(player);
         if (kit != null) {
            kit.apply(player);
         }
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
      }
   }

   public void kickPlayer(final Player player) {
      final String randomHub = "Hub-0" + ThreadLocalRandom.current().nextInt(1, 3);
      (new BukkitRunnable() {
         @Override
         public void run() {
            if (player.isOnline()) {
               player.sendMessage(ChatColor.RED + "You have been deathbanned!");
               BungeeUtil.sendToServer(player, randomHub);
            }
         }
      }).runTaskLaterAsynchronously(this.instance, 40L);
      (new BukkitRunnable() {
         @Override
         public void run() {
            if (player.isOnline()) {
               player.kickPlayer(ChatColor.RED + "You have been deathbanned!");
            }
         }
      }).runTaskLater(this.instance, 120L);
   }

   @ConstructorProperties("instance")
   public DeathbanListener(Foxtrot instance) {
      this.instance = instance;
   }
}
