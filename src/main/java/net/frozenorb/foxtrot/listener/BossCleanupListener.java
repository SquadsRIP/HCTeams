package net.frozenorb.foxtrot.listener;

import java.util.HashMap;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

// The CavePVP bosses (Magnum, Giant, Surtur) were removed, but the ones spawned on the old map are
// saved in its chunks and never despawn because they have a custom name. Remove them as they load.
public class BossCleanupListener implements Listener {
   private static final Map<String, EntityType> BOSSES = new HashMap<>();

   public BossCleanupListener() {
      Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
         for (World world : Foxtrot.getInstance().getServer().getWorlds()) {
            for (Entity entity : world.getEntities()) {
               removeIfBoss(entity);
            }
         }
      }, 20L);
   }

   @EventHandler
   public void onChunkLoad(ChunkLoadEvent event) {
      for (Entity entity : event.getChunk().getEntities()) {
         removeIfBoss(entity);
      }
   }

   private static void removeIfBoss(Entity entity) {
      if (entity instanceof LivingEntity && !entity.isDead()) {
         String name = ((LivingEntity)entity).getCustomName();
         if (name != null && BOSSES.get(ChatColor.stripColor(name).trim().toLowerCase()) == entity.getType()) {
            Location location = entity.getLocation();
            entity.remove();
            Foxtrot.getInstance()
               .getLogger()
               .info(
                  "Removed leftover boss "
                     + ChatColor.stripColor(name)
                     + " at "
                     + location.getWorld().getName()
                     + " "
                     + location.getBlockX()
                     + ", "
                     + location.getBlockY()
                     + ", "
                     + location.getBlockZ()
               );
         }
      }
   }

   static {
      BOSSES.put("magnum", EntityType.WITHER);
      BOSSES.put("giant", EntityType.GIANT);
      BOSSES.put("surtur", EntityType.BLAZE);
   }
}
