package net.frozenorb.foxtrot.server.deathban.listener;

import cc.fyre.proton.util.TimeUtils;
import java.beans.ConstructorProperties;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathbanListener implements Listener {
   private static final String[] ALLOWED_COMMANDS = new String[]{
      "/lives", "/pvp lives", "/hub", "/request", "/report", "/helpop", "/msg ", "/r ", "/reply ", "/message ", "/m ", "/w ", "/tell "
   };
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

         // No Nether/End/event life cooldown: players with lives can use one as soon as they rejoin.
         this.instance.getDeathbanMap().deathban(player.getUniqueId(), seconds);
         player.removeMetadata("PVP_TIMER_BYPASS", this.instance);
         this.instance.getPvPTimerMap().removeTimer(player.getUniqueId());
         player.sendMessage(ChatColor.RED + "You have been deathbanned for " + TimeUtils.formatIntoDetailedString(seconds) + "!");
         this.sendToHub(player);
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

         // Joining Teams with a usable life spends it and lets the player play.
         if (this.instance.getDeathbanArenaHandler().canUseLife(player)) {
            this.instance.getDeathbanArenaHandler().useLife(player);
            return;
         }

         long seconds = (this.instance.getDeathbanMap().getDeathban(player.getUniqueId()) - System.currentTimeMillis()) / 1000L;
         player.sendMessage("");
         player.sendMessage(ChatColor.RED + "You are still deathbanned for " + TimeUtils.formatIntoDetailedString((int)seconds) + "!");
         this.sendToHub(player);
      }
   }

   public void sendToHub(Player player) {
      long hubDelay = 40L;
      int lives = this.instance.getFriendLivesMap().getLives(player.getUniqueId());
      if (lives > 0 && !this.instance.getServerHandler().isPreEOTW()) {
         long cooldown = this.instance.getDeathbanArenaHandler().getLifeCooldown().getOrDefault(player.getUniqueId(), 0L);
         String when = cooldown > System.currentTimeMillis()
            ? " in " + TimeUtils.formatIntoDetailedString((int)((cooldown - System.currentTimeMillis()) / 1000L)) + " (you died in either Nether, End or an Event)"
            : "";
         player.sendMessage(
            ChatColor.GREEN + "You have " + ChatColor.WHITE + lives + ChatColor.GREEN + (lives == 1 ? " life" : " lives") + ". Rejoin Teams" + when + " to use one and respawn."
         );
      } else if (lives <= 0) {
         player.sendMessage(ChatColor.RED + "You have no lives. A teammate can revive you with " + ChatColor.WHITE + "/lives revive " + player.getName() + ChatColor.RED + ".");
      }

      (new BukkitRunnable() {
         @Override
         public void run() {
            if (player.isOnline() && DeathbanListener.this.instance.getDeathbanMap().isDeathbanned(player.getUniqueId())) {
               player.performCommand("hub");
            }
         }
      }).runTaskLater(this.instance, hubDelay);
      (new BukkitRunnable() {
         @Override
         public void run() {
            if (player.isOnline() && DeathbanListener.this.instance.getDeathbanMap().isDeathbanned(player.getUniqueId())) {
               player.kickPlayer(ChatColor.RED + "You are deathbanned!");
            }
         }
      }).runTaskLater(this.instance, hubDelay + 80L);
   }

   // During the 2 s before being sent to the hub, a deathbanned player can't move, fight or run commands.
   private boolean isHeld(Player player) {
      return !this.instance.getMapHandler().isKitMap()
         && this.instance.getDeathbanMap().isDeathbanned(player.getUniqueId())
         && !player.hasPermission("foxtrot.staff")
         && !player.getWorld().getName().equalsIgnoreCase("Deathban");
   }

   @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
   public void onMove(PlayerMoveEvent event) {
      if (event.getFrom().getBlockX() != event.getTo().getBlockX()
         || event.getFrom().getBlockY() != event.getTo().getBlockY()
         || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
         if (this.isHeld(event.getPlayer())) {
            event.setTo(event.getFrom());
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
   public void onDamage(EntityDamageEvent event) {
      if (event.getEntity() instanceof Player && this.isHeld((Player)event.getEntity())) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
   public void onAttack(EntityDamageByEntityEvent event) {
      Object damager = event.getDamager();
      if (damager instanceof Projectile) {
         damager = ((Projectile)damager).getShooter();
      }

      if (damager instanceof Player && this.isHeld((Player)damager)) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
   public void onCommand(PlayerCommandPreprocessEvent event) {
      if (this.isHeld(event.getPlayer())) {
         String message = event.getMessage().toLowerCase();

         for (String allowed : ALLOWED_COMMANDS) {
            if (message.startsWith(allowed)) {
               return;
            }
         }

         event.getPlayer().sendMessage(ChatColor.RED + "You may not run that command whilst deathbanned!");
         event.setCancelled(true);
      }
   }

   @ConstructorProperties("instance")
   public DeathbanListener(Foxtrot instance) {
      this.instance = instance;
   }
}
