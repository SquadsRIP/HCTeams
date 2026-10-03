package net.frozenorb.foxtrot.gameplay.content.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.content.ContentHandler;
import net.frozenorb.foxtrot.gameplay.content.ContentType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class ContentService extends BukkitRunnable {
   private Foxtrot instance;
   private ContentHandler contentHandler;
   private Map<UUID, Integer> seconds = new HashMap<>();

   @Override
   public void run() {
      for (Player onlinePlayer : this.instance.getServer().getOnlinePlayers()) {
         this.seconds.putIfAbsent(onlinePlayer.getUniqueId(), 30);
         int seconds = this.seconds.getOrDefault(onlinePlayer.getUniqueId(), 30);
         if (this.contentHandler.isContentType(onlinePlayer.getUniqueId(), ContentType.RANDOM_EFFECT)) {
            if (--seconds == 0) {
               List<PotionEffectType> potionEffects = Arrays.asList(PotionEffectType.values());
               PotionEffectType potionEffectType = potionEffects.get(ThreadLocalRandom.current().nextInt(potionEffects.size() - 1));
               onlinePlayer.addPotionEffect(new PotionEffect(potionEffectType, 600, 1));
            }
         }

         if (this.contentHandler.isContentType(onlinePlayer.getUniqueId(), ContentType.WATER_DAMAGE)
            && onlinePlayer.getLocation().getBlock().getType().name().contains("WATER")) {
            onlinePlayer.damage(0.0);
            onlinePlayer.setHealth(onlinePlayer.getHealth() - 4.0);
         }
      }
   }
}
