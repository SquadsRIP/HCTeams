package net.frozenorb.foxtrot.listener;

import java.util.Random;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

public class ArmorDamageListener implements Listener {
   private static final Random random = new Random();

   @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
   public void onItemDamage(PlayerItemDamageEvent e) {
      if (30 < random.nextInt(100)) {
         e.setCancelled(true);
         return;
      }

      ItemStack item = e.getItem();
      int level = item.getEnchantmentLevel(Enchantment.DURABILITY);
      if (level <= 0 || !isArmor(item)) {
         return;
      }

      // Vanilla 1.7 ignores Unbreaking on armor 60% of the time (Unbreaking III = only 1.43x life).
      // Keep each remaining point with 1 / (0.6 * level + 1) so armor gets the tool rate: (level + 1)x life.
      double keepChance = 1.0 / (0.6 * level + 1.0);
      int damage = 0;

      for (int i = 0; i < e.getDamage(); i++) {
         if (random.nextDouble() < keepChance) {
            damage++;
         }
      }

      if (damage <= 0) {
         e.setCancelled(true);
      } else {
         e.setDamage(damage);
      }
   }

   private static boolean isArmor(ItemStack item) {
      String name = item.getType().name();
      return name.endsWith("_HELMET") || name.endsWith("_CHESTPLATE") || name.endsWith("_LEGGINGS") || name.endsWith("_BOOTS");
   }
}
