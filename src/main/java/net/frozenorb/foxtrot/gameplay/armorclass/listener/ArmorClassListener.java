package net.frozenorb.foxtrot.gameplay.armorclass.listener;

import java.beans.ConstructorProperties;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClassHandler;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorPiece;
import net.frozenorb.foxtrot.gameplay.armorclass.event.ArmorSetEquipEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.EquipmentSetEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class ArmorClassListener implements Listener {
   public final ArmorClassHandler armorClassHandler;

   @EventHandler(priority = EventPriority.MONITOR)
   public void onEquipmentSet(EquipmentSetEvent event) {
      ItemStack itemStack = event.getNewItem();
      if (itemStack != null && itemStack.getType() != Material.AIR) {
         Player player = (Player)event.getHumanEntity();
         ArmorClass armorClass = this.armorClassHandler.findWearing(player);
         if (armorClass != null) {
            if (this.armorClassHandler.getActiveSets().put(player.getUniqueId(), armorClass) == null) {
               ArmorSetEquipEvent equipEvent = new ArmorSetEquipEvent(player, armorClass);
               equipEvent.call();
               if (equipEvent.isCancelled()) {
                  this.armorClassHandler.getActiveSets().remove(player.getUniqueId());
                  return;
               }

               armorClass.apply(player);
               player.sendMessage(ChatColor.translate("&4&lArmor Classes &7┃ &fYou have equipped the " + armorClass.getDisplayName() + "&f armor set!"));
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onJoin(PlayerJoinEvent event) {
      Player player = event.getPlayer();
      ArmorClass armorClass = this.armorClassHandler.findWearing(player);
      if (armorClass != null) {
         if (this.armorClassHandler.getActiveSets().put(player.getUniqueId(), armorClass) == null) {
            armorClass.apply(player);
            player.sendMessage(ChatColor.translate("&4&lArmor Classes &7┃ &fYou have equipped the " + armorClass.getDisplayName() + "&f armor set!"));
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void onEquipmentUnset(EquipmentSetEvent event) {
      Player player = (Player)event.getHumanEntity();
      ArmorClass armorClass = this.armorClassHandler.getActiveSets().remove(player.getUniqueId());
      if (armorClass != null) {
         armorClass.unapply(player);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onQuit(PlayerQuitEvent event) {
      Player player = event.getPlayer();
      this.handleRemoval(player);
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onKick(PlayerKickEvent event) {
      Player player = event.getPlayer();
      this.handleRemoval(player);
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onDeath(PlayerDeathEvent event) {
      Player player = event.getEntity();
      this.handleRemoval(player);
   }

   private void handleRemoval(Player player) {
      ArmorClass armorClass = this.armorClassHandler.getActiveSets().remove(player.getUniqueId());
      if (armorClass != null) {
         armorClass.unapply(player);
      }
   }

   @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
   public void onInventoryClick(InventoryClickEvent event) {
      if (event.getView().getType() == InventoryType.ANVIL && event.getRawSlot() >= 0 && event.getRawSlot() <= 2) {
         ItemStack itemZero = event.getView().getItem(0);
         ItemStack itemOne = event.getView().getItem(1);
         if (itemZero != null && itemZero.getType() != Material.AIR && this.armorClassHandler.findByPiece(itemZero) != null
            || itemOne != null && itemOne.getType() != Material.AIR && this.armorClassHandler.findByPiece(itemOne) != null) {
            ((Player)event.getWhoClicked()).sendMessage(ChatColor.translate("&cYou can't rename armor sets in anvils."));
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   public void onInteract(PlayerInteractEvent event) {
      Action action = event.getAction();
      if (action != Action.PHYSICAL) {
         Player player = event.getPlayer();
         ItemStack itemStack = event.getItem();
         if (itemStack != null && itemStack.getType().equals(Material.CHEST)) {
            ArmorClass armorClass = this.armorClassHandler.findByRedeemItem(itemStack);
            if (armorClass != null) {
               event.setCancelled(true);
               if (itemStack.getAmount() == 1) {
                  player.setItemInHand(null);
               } else {
                  itemStack.setAmount(itemStack.getAmount() - 1);
               }

               player.getInventory().addItem(armorClass.createPiece(ArmorPiece.HELMET));
               player.getInventory().addItem(armorClass.createPiece(ArmorPiece.CHESTPLATE));
               player.getInventory().addItem(armorClass.createPiece(ArmorPiece.LEGGINGS));
               player.getInventory().addItem(armorClass.createPiece(ArmorPiece.BOOTS));
            }
         }
      }
   }

   @ConstructorProperties("armorClassHandler")
   public ArmorClassListener(ArmorClassHandler armorClassHandler) {
      this.armorClassHandler = armorClassHandler;
   }
}
