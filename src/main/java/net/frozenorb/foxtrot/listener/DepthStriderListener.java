package net.frozenorb.foxtrot.listener;

import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DepthStriderListener implements Listener {
   @EventHandler(priority = EventPriority.LOW)
   private void onClick(InventoryClickEvent event) {
      Player player = (Player)event.getWhoClicked();
      ItemStack bookItem = event.getCursor();
      ItemStack applyToItem = event.getCurrentItem();
      if (applyToItem != null && bookItem != null) {
         if (this.isSimilar(bookItem)) {
            Material material = applyToItem.getType();
            List bookLore = bookItem.getItemMeta().getLore();
            ItemMeta applyToMeta = applyToItem.getItemMeta();
            if (applyToMeta != null && material != null) {
               List applyToLore = applyToMeta.hasLore() ? applyToMeta.getLore() : new ArrayList();
               applyToMeta.setLore(applyToLore);
               applyToItem.setItemMeta(applyToMeta);
               event.setCurrentItem(applyToItem.clone());
               event.setCancelled(true);
               player.setItemOnCursor(null);
               player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
               player.sendMessage(ChatColor.GREEN + "Applied custom enchant!");
            } else {
               player.sendMessage(ChatColor.YELLOW + "Can't use a book on that item!");
            }
         }
      }
   }

   public boolean isSimilar(ItemStack itemStack) {
      if (itemStack != null && itemStack.getItemMeta() != null && itemStack.getItemMeta().getLore() != null && itemStack.getItemMeta().getDisplayName() != null
         )
       {
         if (itemStack.getType() != Material.BOOK) {
            return false;
         }

         if (!itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Depth Strider Book")) {
            return false;
         }

         List lore = itemStack.getItemMeta().getLore();
         return lore.contains(CC.translate("&eDouble Click this book on the boots you want to put it on!"));
      } else {
         return false;
      }
   }
}
