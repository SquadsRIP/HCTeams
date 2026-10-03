package net.frozenorb.foxtrot.deathmessage.trackers;

import cc.fyre.proton.util.EntityUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.deathmessage.event.CustomPlayerDamageEvent;
import net.frozenorb.foxtrot.deathmessage.objects.Damage;
import net.frozenorb.foxtrot.deathmessage.objects.MobDamage;
import net.frozenorb.foxtrot.deathmessage.objects.PlayerDamage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class ArrowTracker implements Listener {
   @EventHandler
   public void onEntityShootBow(EntityShootBowEvent event) {
      if (event.getEntity() instanceof Player) {
         event.getProjectile().setMetadata("ShotFromDistance", new FixedMetadataValue(Foxtrot.getInstance(), event.getProjectile().getLocation()));
      }
   }

   @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
   public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
      EntityDamageByEntityEvent entityDamageByEntityEvent;
      if (event.getCause() instanceof EntityDamageByEntityEvent
         && (entityDamageByEntityEvent = (EntityDamageByEntityEvent)event.getCause()).getDamager() instanceof Arrow) {
         Arrow arrow = (Arrow)entityDamageByEntityEvent.getDamager();
         if (arrow.getShooter() instanceof Player) {
            Player shooter = (Player)arrow.getShooter();

            for (MetadataValue value : arrow.getMetadata("ShotFromDistance")) {
               Location shotFrom = (Location)value.value();
               double distance = shotFrom.distance(event.getPlayer().getLocation());
               event.setTrackerDamage(
                  new ArrowTracker.ArrowDamageByPlayer(event.getPlayer().getName(), event.getDamage(), shooter.getName(), shotFrom, distance)
               );
            }
         } else if (arrow.getShooter() instanceof Entity) {
            event.setTrackerDamage(new ArrowTracker.ArrowDamageByMob(event.getPlayer().getName(), event.getDamage(), (Entity)arrow.getShooter()));
         } else {
            event.setTrackerDamage(new ArrowTracker.ArrowDamage(event.getPlayer().getName(), event.getDamage()));
         }
      }
   }

   public static class ArrowDamage extends Damage {
      public ArrowDamage(String damaged, double damage) {
         super(damaged, damage);
      }

      @Override
      public String getDeathMessage() {
         return this.wrapName(this.getDamaged()) + " was shot.";
      }
   }

   public static class ArrowDamageByMob extends MobDamage {
      public ArrowDamageByMob(String damaged, double damage, Entity damager) {
         super(damaged, damage, damager.getType());
      }

      @Override
      public String getDeathMessage() {
         return this.wrapName(this.getDamaged()) + " was shot by a " + ChatColor.DARK_RED + EntityUtils.getName(this.getMobType()) + ChatColor.DARK_RED + ".";
      }
   }

   public static class ArrowDamageByPlayer extends PlayerDamage {
      private Location shotFrom;
      private double distance;

      public ArrowDamageByPlayer(String damaged, double damage, String damager, Location shotFrom, double distance) {
         super(damaged, damage, damager);
         this.shotFrom = shotFrom;
         this.distance = distance;
      }

      @Override
      public String getDeathMessage() {
         return this.wrapName(this.getDamaged())
            + " was shot by "
            + this.wrapName(this.getDamager())
            + " from "
            + ChatColor.BLUE
            + (int)this.distance
            + " blocks"
            + ChatColor.DARK_RED
            + ".";
      }

      public Location getShotFrom() {
         return this.shotFrom;
      }

      public double getDistance() {
         return this.distance;
      }
   }
}
