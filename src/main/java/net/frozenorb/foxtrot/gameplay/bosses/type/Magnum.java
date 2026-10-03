package net.frozenorb.foxtrot.gameplay.bosses.type;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.bosses.Boss;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Magnum extends Boss {
   private int seconds;

   @Override
   public String getBossID() {
      return "Magnum";
   }

   @Override
   public String getBossDisplayName() {
      return ChatColor.translate("&5&lMagnum");
   }

   @Override
   public int getMaxHealth() {
      return 1500;
   }

   @Override
   public double getDamageMultiplier() {
      return 12.5;
   }

   @Override
   public EntityType getEntityType() {
      return EntityType.WITHER;
   }

   @Override
   public String getWorldName() {
      return "world";
   }

   @Override
   public void activate(Location location) {
      super.activate(location);
      (new BukkitRunnable() {
         @Override
         public void run() {
            if (Magnum.this.getEntity().isDead()) {
               this.cancel();
            } else {
               if (Magnum.this.seconds % 15 == 0) {
                  Magnum.this.getEntity().getNearbyEntities(10.0, 10.0, 10.0).stream().filter(it -> it instanceof Player).forEach(it -> {
                     it.setVelocity(it.getLocation().getDirection().multiply(-2.5));
                     ((Player)it).playSound(it.getLocation(), Sound.WITHER_SHOOT, 1.0F, 1.0F);
                  });
               }

               Magnum.this.seconds++;
               if (Magnum.this.seconds % 25 == 0) {
                  if (!Magnum.this.getEntity().getNearbyEntities(30.0, 30.0, 30.0).isEmpty()) {
                     for (int i = 0; i < 5; i++) {
                        Magnum.this.spawnMinion(BlockFace.WEST);
                     }

                     for (int i = 0; i < 5; i++) {
                        Magnum.this.spawnMinion(BlockFace.EAST);
                     }

                     for (int i = 0; i < 5; i++) {
                        Magnum.this.spawnMinion(BlockFace.SOUTH);
                     }

                     for (int i = 0; i < 5; i++) {
                        Magnum.this.spawnMinion(BlockFace.NORTH);
                     }
                  }
               }
            }
         }
      }).runTaskTimer(Foxtrot.getInstance(), 20L, 20L);
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onDeath(EntityDeathEvent event) {
      if (event.getEntity() instanceof Monster) {
         if (event.getEntity().hasMetadata("BOSS")) {
            this.deactivate(event.getEntity().getKiller());
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onDamage(EntityDamageByEntityEvent event) {
      if (event.getDamager() instanceof Player) {
         Monster monster = null;
         if (event.getDamager() instanceof Monster) {
            monster = (Monster)event.getDamager();
         }

         if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile)event.getDamager();
            if (projectile.getShooter() instanceof Monster) {
               monster = (Monster)projectile.getShooter();
            }
         }

         if (monster != null) {
            if (monster.hasMetadata("BOSS")) {
               event.setDamage(event.getDamage() * this.getDamageMultiplier());
            }
         }
      }
   }

   public void spawnMinion(BlockFace blockFace) {
      Location entityLocation = this.getEntity().getLocation().clone();
      Block block = entityLocation.getBlock().getRelative(blockFace, 4);
      Skeleton skeleton = (Skeleton)this.getEntity()
         .getWorld()
         .spawnEntity(block.getWorld().getHighestBlockAt(block.getLocation()).getLocation().clone(), EntityType.SKELETON);
      skeleton.setSkeletonType(SkeletonType.WITHER);
   }
}
