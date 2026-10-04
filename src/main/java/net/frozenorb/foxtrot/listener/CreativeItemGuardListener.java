package net.frozenorb.foxtrot.listener;

import cc.fyre.piston.Piston;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

// Creative players (staff in mod mode, /gamemode) can put any item into their inventory or drop it, and a hacked
// client can send items no creative menu offers, e.g. a Sharpness 20000 book. Non-OPs may only take items within
// vanilla limits. The server refuses a denied creative packet and resends the slot.
public class CreativeItemGuardListener implements Listener {
   private static final long ALERT_COOLDOWN_MILLIS = 3000L;
   private final Map<UUID, Long> lastAlert = new HashMap<>();

   @EventHandler(priority = EventPriority.HIGHEST)
   public void onCreativeItem(InventoryCreativeEvent event) {
      if (event.getWhoClicked() instanceof Player) {
         Player player = (Player)event.getWhoClicked();
         if (!player.isOp()) {
            String problem = findProblem(event.getCursor());
            if (problem != null) {
               event.setCancelled(true);
               this.alert(player, event.getCursor(), problem);
            }
         }
      }
   }

   private static String findProblem(ItemStack item) {
      if (item != null && item.getType() != Material.AIR) {
         if (item.getAmount() > item.getMaxStackSize()) {
            return "a stack of " + item.getAmount();
         } else if (!item.hasItemMeta()) {
            return null;
         } else {
            ItemMeta meta = item.getItemMeta();
            String problem = findEnchantProblem(meta.getEnchants());
            if (problem == null && meta instanceof EnchantmentStorageMeta) {
               problem = findEnchantProblem(((EnchantmentStorageMeta)meta).getStoredEnchants());
            }

            return problem;
         }
      } else {
         return null;
      }
   }

   private static String findEnchantProblem(Map<Enchantment, Integer> enchants) {
      for (Entry<Enchantment, Integer> entry : enchants.entrySet()) {
         int level = entry.getValue();
         if (level < 1 || level > entry.getKey().getMaxLevel()) {
            return entry.getKey().getName() + " " + level;
         }
      }

      return null;
   }

   private void alert(Player player, ItemStack item, String problem) {
      long now = System.currentTimeMillis();
      Long last = this.lastAlert.get(player.getUniqueId());
      if (last == null || now - last >= ALERT_COOLDOWN_MILLIS) {
         this.lastAlert.put(player.getUniqueId(), now);
         player.sendMessage(ChatColor.RED + "You cannot spawn that item.");
         String message = ChatColor.RED + "[Creative] " + player.getName() + " tried to spawn " + item.getType().name() + " with " + problem + " (blocked).";
         Foxtrot.getInstance()
            .getServer()
            .getOnlinePlayers()
            .stream()
            .filter(it -> it.hasPermission("neutron.staff") && !Piston.getInstance().getToggleStaff().contains(it.getUniqueId()))
            .forEach(it -> it.sendMessage(message));
         Foxtrot.getInstance().getLogger().warning(ChatColor.stripColor(message));
      }
   }
}
