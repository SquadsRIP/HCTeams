package net.frozenorb.foxtrot.gameplay.kitmap.kits.listener;

import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.editor.button.EditKitMenu;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.editor.menu.KitsMenu;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.editor.setup.KitEditorItemsMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public final class KitEditorListener implements Listener {
   @EventHandler
   public void onPlayerQuitEvent(PlayerQuitEvent event) {
      Foxtrot.getInstance().getMapHandler().getKitManager().restoreState(event.getPlayer());
   }

   @EventHandler
   public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
      Menu openedMenu = Menu.getCurrentlyOpenedMenus().getOrDefault(event.getPlayer().getUniqueId(), null);
      if (openedMenu instanceof KitsMenu || openedMenu instanceof EditKitMenu || openedMenu instanceof KitEditorItemsMenu) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
      Menu openedMenu = Menu.getCurrentlyOpenedMenus().getOrDefault(event.getPlayer().getUniqueId(), null);
      if (openedMenu instanceof KitsMenu || openedMenu instanceof EditKitMenu || openedMenu instanceof KitEditorItemsMenu) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onPlayerInteractEvent(PlayerInteractEvent event) {
      if (event.getAction() == Action.RIGHT_CLICK_BLOCK
         && event.getClickedBlock().getState() instanceof Sign
         && ((Sign)event.getClickedBlock().getState()).getLine(0).equals(ChatColor.GOLD + "- Edit Kits -")) {
         new KitsMenu().openMenu(event.getPlayer());
      }
   }

   @EventHandler
   public void onInventoryClick(InventoryClickEvent event) {
      Player player = (Player)event.getWhoClicked();
      if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
         if (event.getClickedInventory() == event.getView().getTopInventory()) {
            if (Menu.getCurrentlyOpenedMenus().get(player.getUniqueId()) instanceof EditKitMenu) {
               event.setCancelled(true);
               if (event.getAction() == InventoryAction.DROP_ALL_CURSOR || event.getAction() == InventoryAction.DROP_ONE_CURSOR) {
                  event.setCursor(null);
                  player.updateInventory();
               }
            }
         }
      }
   }

   @EventHandler
   public void onInventoryDrag(InventoryDragEvent event) {
      Player player = (Player)event.getWhoClicked();
      if (Menu.getCurrentlyOpenedMenus().get(player.getUniqueId()) instanceof EditKitMenu) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onInsertEditorItem(InventoryClickEvent event) {
      Player player = (Player)event.getWhoClicked();
      if (event.getClickedInventory() == event.getView().getTopInventory()) {
         if (event.getCursor() != null) {
            if (Menu.getCurrentlyOpenedMenus().get(player.getUniqueId()) instanceof KitEditorItemsMenu) {
               ItemStack cursor = event.getCursor().clone();
               event.setCancelled(true);
               event.setCursor(null);
               KitEditorItemsMenu openedMenu = (KitEditorItemsMenu)Menu.getCurrentlyOpenedMenus().get(player.getUniqueId());
               openedMenu.getKit().getEditorItems().add(cursor);
               openedMenu.openMenu(player);
            }
         }
      }
   }
}
