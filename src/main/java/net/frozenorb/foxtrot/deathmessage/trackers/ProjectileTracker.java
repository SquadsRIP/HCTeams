package net.frozenorb.foxtrot.deathmessage.trackers;

import net.frozenorb.foxtrot.deathmessage.event.CustomPlayerDamageEvent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ProjectileTracker implements Listener {
   @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
   public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
      Player damaged;
      Player shooter;
      Projectile projectile;
      EntityDamageByEntityEvent entityDamageByEntityEvent;
      if (event.getCause() instanceof EntityDamageByEntityEvent
         && (entityDamageByEntityEvent = (EntityDamageByEntityEvent)event.getCause()).getDamager() instanceof Projectile
         && !(entityDamageByEntityEvent.getDamager() instanceof Arrow)
         && (projectile = (Projectile)entityDamageByEntityEvent.getDamager()).getShooter() instanceof Player
         && (shooter = (Player)projectile.getShooter()) != (damaged = event.getPlayer())) {
         event.setTrackerDamage(new PVPTracker.PVPDamage(damaged.getName(), event.getDamage(), shooter.getName(), shooter.getItemInHand()));
      }
   }
}
