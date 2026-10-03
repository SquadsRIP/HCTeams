package net.frozenorb.foxtrot.gameplay.bosses.type;

import java.util.stream.Collectors;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.bosses.Boss;
import net.frozenorb.foxtrot.util.RandomUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Giant extends Boss {
   private int seconds;

   @Override
   public String getBossID() {
      return "Giant";
   }

   @Override
   public String getBossDisplayName() {
      return ChatColor.translate("&2&lGiant");
   }

   @Override
   public int getMaxHealth() {
      return 1400;
   }

   @Override
   public double getDamageMultiplier() {
      return 10.0;
   }

   @Override
   public EntityType getEntityType() {
      return EntityType.GIANT;
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
            if (Giant.this.getEntity().isDead()) {
               this.cancel();
            } else {
               if (Giant.this.seconds % 15 == 0) {
                  Giant.this.getEntity().getNearbyEntities(10.0, 10.0, 10.0).stream().filter(it -> it instanceof Player).forEach(it -> {
                     it.setVelocity(it.getLocation().getDirection().multiply(-2.5));
                     ((Player)it).playSound(it.getLocation(), Sound.ZOMBIE_WOODBREAK, 1.0F, 1.0F);
                  });
               }

               Giant.this.seconds++;
               if (Giant.this.seconds % 25 == 0) {
                  if (!Giant.this.getEntity().getNearbyEntities(30.0, 30.0, 30.0).isEmpty()) {
                     for (int i = 0; i < 5; i++) {
                        Giant.this.spawnMinion(BlockFace.UP);
                     }

                     for (int i = 0; i < 5; i++) {
                        Giant.this.spawnMinion(BlockFace.WEST);
                     }

                     for (int i = 0; i < 5; i++) {
                        Giant.this.spawnMinion(BlockFace.EAST);
                     }

                     for (int i = 0; i < 5; i++) {
                        Giant.this.spawnMinion(BlockFace.SOUTH);
                     }

                     for (int i = 0; i < 5; i++) {
                        Giant.this.spawnMinion(BlockFace.NORTH);
                     }
                  }
               }
            }
         }
      }).runTaskTimer(Foxtrot.getInstance(), 20L, 20L);
   }

   public void stomp() {
      if (this.getEntity().isOnGround()) {
         this.getEntity().setVelocity(new Vector(0.0, RandomUtil.getRandDouble(1.5, 2.25), 0.0));
         this.getEntity().setMetadata("stomp", new FixedMetadataValue(Foxtrot.getInstance(), true));
         (new BukkitRunnable() {
               @Override
               public void run() {
                  if (!Giant.this.getEntity().isDead() && Giant.this.getEntity().isValid()) {
                     if (Giant.this.getEntity().isOnGround()) {
                        for (Player player : Giant.this.getEntity()
                           .getNearbyEntities(10.0, 10.0, 10.0)
                           .stream()
                           .filter(Player.class::isInstance)
                           .map(Player.class::cast)
                           .collect(Collectors.toList())) {
                           player.damage(20.0, Giant.this.getEntity());
                        }

                        Giant.this.getEntity().removeMetadata("stomp", Foxtrot.getInstance());
                        this.cancel();
                     }
                  } else {
                     this.cancel();
                  }
               }
            })
            .runTaskTimer(Foxtrot.getInstance(), 20L, 4L);
      }
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
      Block block = entityLocation.getBlock().getRelative(blockFace, 2);
      this.getEntity().getWorld().spawnEntity(block.getLocation().clone(), EntityType.ZOMBIE);
      this.getEntity().getWorld().playSound(block.getLocation(), Sound.ZOMBIE_INFECT, 1.0F, 1.0F);
   }
}
