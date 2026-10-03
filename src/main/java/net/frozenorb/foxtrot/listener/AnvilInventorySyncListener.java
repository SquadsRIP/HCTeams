package net.frozenorb.foxtrot.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;

public final class AnvilInventorySyncListener implements Listener {
   private final Plugin plugin;
   private final Map<UUID, InventoryView> pending = new HashMap<>();

   public AnvilInventorySyncListener(Plugin plugin) {
      this.plugin = plugin;
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onClick(InventoryClickEvent event) {
      this.queueRefresh(event.getView());
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onDrag(InventoryDragEvent event) {
      this.queueRefresh(event.getView());
   }

   private void queueRefresh(InventoryView view) {
      if (view.getTopInventory() instanceof AnvilInventory && view.getPlayer() instanceof Player) {
         Player player = (Player)view.getPlayer();
         UUID id = player.getUniqueId();
         if (this.pending.put(id, view) == null) {
            this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
               InventoryView expected = this.pending.remove(id);
               if (player.isOnline() && player.getOpenInventory() == expected) {
                  player.updateInventory();
               }
            });
         }
      }
   }
}
