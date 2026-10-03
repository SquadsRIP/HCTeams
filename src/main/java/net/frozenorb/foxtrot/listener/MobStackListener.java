package net.frozenorb.foxtrot.listener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class MobStackListener implements Listener {
   private static final double STACK_RADIUS = 25.0;
   private static final int MAX_STACK = 500;

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onCreatureSpawn(CreatureSpawnEvent event) {
      SpawnReason reason = event.getSpawnReason();
      if (reason != SpawnReason.CUSTOM) {
         if (event.getEntityType() != EntityType.ENDERMAN) {
            LivingEntity entity = event.getEntity();
            String stripped;
            if (entity.getCustomName() != null && (stripped = ChatColor.stripColor(entity.getCustomName()).trim()).endsWith("x") && stripped.length() > 1) {
               String numPart = stripped.substring(0, stripped.length() - 1);

               try {
                  Integer.parseInt(numPart);
                  return;
               } catch (NumberFormatException var13) {
               }
            }

            Location loc;
            World world;
            if ((world = (loc = entity.getLocation()).getWorld()) != null) {
               ArrayList<Entity> nearby = new ArrayList<>();

               for (Entity e : world.getEntities()) {
                  if (!e.getUniqueId().equals(entity.getUniqueId()) && e.getLocation().distanceSquared(loc) <= 625.0) {
                     nearby.add(e);
                  }
               }

               for (Entity nearbyEntity : nearby) {
                  LivingEntity nearbyMob;
                  int nearbyCount;
                  if (!nearbyEntity.getUniqueId().equals(entity.getUniqueId())
                     && nearbyEntity.getType() == entity.getType()
                     && nearbyEntity instanceof LivingEntity
                     && !nearbyEntity.isDead()
                     && (nearbyCount = this.getStackCount(nearbyMob = (LivingEntity)nearbyEntity)) < 500) {
                     int newCount = nearbyCount + 1;
                     nearbyMob.setCustomName(ChatColor.RED + String.valueOf(newCount) + "x");
                     nearbyMob.setCustomNameVisible(true);
                     event.setCancelled(true);
                     return;
                  }
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onEntityDeath(EntityDeathEvent event) {
      LivingEntity entity = event.getEntity();
      int count = this.getStackCount(entity);
      if (count > 1) {
         int newCount = count - 1;
         Location loc = entity.getLocation().clone();
         EntityType type = entity.getType();
         int finalNewCount = newCount;
         Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            if (loc.getWorld() != null) {
               LivingEntity newMob = (LivingEntity)loc.getWorld().spawnEntity(loc, type);
               newMob.setCustomName(ChatColor.RED + String.valueOf(finalNewCount) + "x");
               newMob.setCustomNameVisible(true);
               newMob.setHealth(20.0);
            }
         }, 1L);
      }
   }

   private int getStackCount(LivingEntity entity) {
      String name = entity.getCustomName();
      if (name == null) {
         return 1;
      }

      String stripped = ChatColor.stripColor(name).trim();
      if (stripped.endsWith("x") && stripped.length() > 1) {
         try {
            return Integer.parseInt(stripped.substring(0, stripped.length() - 1));
         } catch (NumberFormatException e) {
            return 1;
         }
      } else {
         return 1;
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onEndermanDamage(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Player && event.getDamager() instanceof Enderman) {
         try {
            Method m = EntityDamageEvent.class.getMethod("getDamage");
            Object result = m.invoke(event);
            double damage = result instanceof Number ? ((Number)result).doubleValue() : 0.0;
            Method setM = EntityDamageEvent.class.getMethod("setDamage", double.class);
            setM.invoke(event, damage * 0.5);
         } catch (Exception var7) {
         }
      }
   }
}
