package net.frozenorb.foxtrot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

class EnchantmentLimiterListener$1 extends BukkitRunnable {
   // $VF: synthetic field
   final InventoryClickEvent val$event;
   // $VF: synthetic field
   final EnchantmentLimiterListener this$0;

   EnchantmentLimiterListener$1(EnchantmentLimiterListener this$0, InventoryClickEvent var2) {
      this.this$0 = this$0;
      this.val$event = var2;
   }

   @Override
   public void run() {
      ((Player)this.val$event.getWhoClicked()).giveExp(5);
   }
}
