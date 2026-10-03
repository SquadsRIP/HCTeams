package net.frozenorb.foxtrot.listener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.AbilityHandler;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClass;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClassHandler;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class RodCooldownListener implements Listener {
   public static Map<UUID, Long> cache = new HashMap<>();
   private static final List<String> TWO_SECOND_CLASSES = Arrays.asList("Bard", "Archer", "Miner", "Rogue");

   @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
   private void onFish(PlayerFishEvent event) {
      if (event.getState() == State.FISHING && !isAbilityItem(event.getPlayer().getItemInHand())) {
         Player player = event.getPlayer();
         Long expiry = cache.get(player.getUniqueId());
         if (expiry != null && System.currentTimeMillis() < expiry) {
            double value = (expiry - System.currentTimeMillis()) / 1000.0;
            double sec = value > 0.1 ? Math.round(10.0 * value) / 10.0 : 0.1;
            event.setCancelled(true);
            player.sendMessage(ChatColor.YELLOW + "You may not use a fishing rod for another " + ChatColor.BOLD + sec + ChatColor.YELLOW + " seconds!");
         } else {
            long millis = getCooldownMillis(player);
            if (millis > 0L) {
               cache.put(player.getUniqueId(), System.currentTimeMillis() + millis);
            }
         }
      }
   }

   @EventHandler
   private void onQuit(PlayerQuitEvent event) {
      cache.remove(event.getPlayer().getUniqueId());
   }

   private static boolean isAbilityItem(ItemStack item) {
      AbilityHandler handler = Foxtrot.getInstance().getMapHandler().getAbilityHandler();
      if (handler != null) {
         for (Ability ability : handler.getAbilities().values()) {
            if (ability.isSimilar(item)) {
               return true;
            }
         }
      }

      return false;
   }

   private static long getCooldownMillis(Player player) {
      FileConfiguration config = Foxtrot.getInstance().getConfig();
      double seconds = config.getDouble("rodCooldown.default", 3.0);
      PvPClass pvpClass = PvPClassHandler.getPvPClass(player);
      if (pvpClass != null) {
         String name = pvpClass.getName();
         seconds = config.getDouble("rodCooldown.classes." + name, TWO_SECOND_CLASSES.contains(name) ? 2.0 : seconds);
      }

      return (long)(seconds * 1000.0);
   }
}
