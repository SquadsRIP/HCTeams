package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.EnchantmentRules;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

// Bows made before the Power cap existed can be above it. Lower them (Power only, other enchants
// stay) wherever they turn up: on join, when drawn, when picked up, and in any opened container.
public class BowPowerListener implements Listener {
   private static EnchantmentRules rules() {
      return new EnchantmentRules(Foxtrot.getInstance().getConfig());
   }

   private static boolean limit(EnchantmentRules rules, Inventory inventory) {
      boolean changed = false;
      ItemStack[] contents = inventory.getContents();

      for (int i = 0; i < contents.length; i++) {
         if (rules.limitBowPower(contents[i])) {
            inventory.setItem(i, contents[i]);
            changed = true;
         }
      }

      return changed;
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onJoin(PlayerJoinEvent event) {
      Player player = event.getPlayer();
      EnchantmentRules rules = rules();
      boolean changed = limit(rules, player.getInventory());
      limit(rules, player.getEnderChest());
      if (changed) {
         player.updateInventory();
      }
   }

   // Runs before the bow is drawn, so the arrow already uses the lowered Power.
   @EventHandler(priority = EventPriority.LOWEST)
   public void onInteract(PlayerInteractEvent event) {
      if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
         ItemStack hand = event.getPlayer().getItemInHand();
         if (hand != null && hand.getType() == Material.BOW && rules().limitBowPower(hand)) {
            event.getPlayer().setItemInHand(hand);
            event.getPlayer().updateInventory();
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onPickup(PlayerPickupItemEvent event) {
      ItemStack stack = event.getItem().getItemStack();
      if (rules().limitBowPower(stack)) {
         event.getItem().setItemStack(stack);
      }
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onInventoryOpen(InventoryOpenEvent event) {
      limit(rules(), event.getInventory());
   }
}
