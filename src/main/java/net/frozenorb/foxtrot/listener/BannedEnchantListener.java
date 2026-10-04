package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.util.EnchantmentRules;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

// Items made before the ban can still carry Thorns, Knockback, Punch, Fire Aspect, Flame, Respiration
// or Aqua Affinity. Strip them (KOTH crate gear excepted) from a player's own items: on join (inventory,
// armor, ender chest), when held or used, on pickup, after closing any inventory, and when fighting.
// Containers themselves are left alone, so KOTH loot chests and crate previews are never edited.
public class BannedEnchantListener implements Listener {
   private static boolean strip(Inventory inventory) {
      boolean changed = false;
      ItemStack[] contents = inventory.getContents();

      for (int i = 0; i < contents.length; i++) {
         if (EnchantmentRules.stripBanned(contents[i])) {
            inventory.setItem(i, contents[i]);
            changed = true;
         }
      }

      return changed;
   }

   private static boolean stripArmor(PlayerInventory inventory) {
      ItemStack[] armor = inventory.getArmorContents();
      boolean changed = false;

      for (ItemStack piece : armor) {
         changed |= EnchantmentRules.stripBanned(piece);
      }

      if (changed) {
         inventory.setArmorContents(armor);
      }

      return changed;
   }

   private static void stripPlayer(Player player) {
      boolean changed = strip(player.getInventory());
      changed |= stripArmor(player.getInventory());
      if (changed) {
         player.updateInventory();
      }
   }

   private static void stripHand(Player player) {
      ItemStack hand = player.getItemInHand();
      if (EnchantmentRules.stripBanned(hand)) {
         player.setItemInHand(hand);
         player.updateInventory();
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onJoin(PlayerJoinEvent event) {
      stripPlayer(event.getPlayer());
      strip(event.getPlayer().getEnderChest());
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onItemHeld(PlayerItemHeldEvent event) {
      Player player = event.getPlayer();
      ItemStack item = player.getInventory().getItem(event.getNewSlot());
      if (EnchantmentRules.stripBanned(item)) {
         player.getInventory().setItem(event.getNewSlot(), item);
         player.updateInventory();
      }
   }

   // Runs before a bow is drawn or a sword swung.
   @EventHandler(priority = EventPriority.LOWEST)
   public void onInteract(PlayerInteractEvent event) {
      stripHand(event.getPlayer());
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onPickup(PlayerPickupItemEvent event) {
      ItemStack stack = event.getItem().getItemStack();
      if (EnchantmentRules.stripBanned(stack)) {
         event.getItem().setItemStack(stack);
      }
   }

   // Covers items taken out of chests, crates and the ender chest.
   @EventHandler(priority = EventPriority.MONITOR)
   public void onInventoryClose(InventoryCloseEvent event) {
      if (event.getPlayer() instanceof Player) {
         Player player = (Player)event.getPlayer();
         stripPlayer(player);
         if (event.getInventory().equals(player.getEnderChest())) {
            strip(player.getEnderChest());
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void onDamage(EntityDamageByEntityEvent event) {
      if (event.getDamager() instanceof Player) {
         stripHand((Player)event.getDamager());
      }

      if (event.getEntity() instanceof Player) {
         Player victim = (Player)event.getEntity();
         if (stripArmor(victim.getInventory())) {
            victim.updateInventory();
         }
      }
   }
}
