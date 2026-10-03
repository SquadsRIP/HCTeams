package net.frozenorb.foxtrot.listener;

import cc.fyre.piston.Piston;
import java.beans.ConstructorProperties;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DupeGlitchListener implements Listener {
   private Foxtrot instance;

   @EventHandler(priority = EventPriority.LOW)
   private void onDrop(PlayerDropItemEvent event) {
      Player player = event.getPlayer();
      ItemStack itemStack = event.getItemDrop().getItemStack();
      if (itemStack != null) {
         if (!player.isOp()
            && itemStack.getType() == Material.ENDER_CHEST
            && itemStack.getItemMeta() != null
            && itemStack.getItemMeta().getLore() != null
            && itemStack.getItemMeta().getDisplayName() != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (ChatColor.stripColor(itemMeta.getDisplayName()).equalsIgnoreCase("--- Bunny Mystery Box ---")) {
               event.setCancelled(true);
               player.sendMessage(ChatColor.RED + "You may not drop this item!");
               player.updateInventory();
            }
         }

         if (this.isOPLoot(itemStack)) {
            if (itemStack.getAmount() > 10 && itemStack.getItemMeta().getDisplayName().contains("Perk")) {
               Foxtrot.getInstance()
                  .getServer()
                  .getOnlinePlayers()
                  .stream()
                  .filter(it -> it.hasPermission("neutron.staff") && !Piston.getInstance().getToggleStaff().contains(player.getUniqueId()))
                  .forEach(it -> {
                     it.sendMessage("");
                     it.sendMessage(ChatColor.RED + player.getName() + " just dropped " + itemStack.getAmount() + " perk keys! Could be duping!");
                     it.sendMessage("");
                  });
            }

            ItemMeta itemMeta = itemStack.getItemMeta();
            Location location = event.getItemDrop().getLocation();
            System.out.println("-----------------");
            System.out.println(player.getName() + " could be duping! Breakdown below");
            System.out.println("Display Name - " + itemMeta.getDisplayName());
            System.out.println("Location - " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
            System.out.println("Material - " + itemStack.getType().name());
            System.out.println("Amount - " + itemStack.getAmount());
            System.out.println("Lore - ");
            int i = 0;

            for (String s : itemMeta.getLore()) {
               System.out.println("Line " + ++i + " - " + s);
            }

            System.out.println("-----------------");
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onPickUp(PlayerPickupItemEvent event) {
      Player player = event.getPlayer();
      ItemStack itemStack = event.getItem().getItemStack();
      if (itemStack != null) {
         if (this.isOPLoot(itemStack)) {
            if (itemStack.getAmount() > 3 && itemStack.getItemMeta().getDisplayName().contains("Perk")) {
               Foxtrot.getInstance()
                  .getServer()
                  .getOnlinePlayers()
                  .stream()
                  .filter(it -> it.hasPermission("neutron.staff") && !Piston.getInstance().getToggleStaff().contains(player.getUniqueId()))
                  .forEach(it -> {
                     player.sendMessage("");
                     player.sendMessage(ChatColor.RED + player.getName() + " just picked up " + itemStack.getAmount() + " perk keys! Could be duping!");
                     player.sendMessage("");
                  });
            }

            ItemMeta itemMeta = itemStack.getItemMeta();
            Location location = event.getItem().getLocation();
            System.out.println("-----------------");
            System.out.println(player.getName() + " could be duping! Breakdown below! Picked up:");
            System.out.println("Display Name - " + itemMeta.getDisplayName());
            System.out.println("Material - " + itemStack.getType().name());
            System.out.println("Location - " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
            System.out.println("Amount - " + itemStack.getAmount());
            System.out.println("Lore - ");
            int i = 0;

            for (String s : itemMeta.getLore()) {
               System.out.println("Line " + ++i + " - " + s);
            }

            System.out.println("-----------------");
         }
      }
   }

   public boolean isOPLoot(ItemStack itemStack) {
      if (itemStack != null
         && itemStack.getItemMeta() != null
         && itemStack.getType() != Material.AIR
         && itemStack.getItemMeta().getDisplayName() != null
         && itemStack.getItemMeta().getLore() != null) {
         ItemMeta itemMeta = itemStack.getItemMeta();
         if (this.instance.getAirDropHandler() != null
            && this.instance.getAirDropHandler().getItemStack() != null
            && this.instance.getAirDropHandler().getItemStack().isSimilar(itemStack)) {
            return true;
         }

         String displayName = itemMeta.getDisplayName();
         return displayName.contains("Perk Key") && itemStack.getType() == Material.NETHER_STAR;
      } else {
         return false;
      }
   }

   @ConstructorProperties("instance")
   public DupeGlitchListener(Foxtrot instance) {
      this.instance = instance;
   }
}
