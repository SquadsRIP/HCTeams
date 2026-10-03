package net.frozenorb.foxtrot.gameplay.content.listener;

import java.beans.ConstructorProperties;
import java.util.concurrent.ThreadLocalRandom;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.content.ContentHandler;
import net.frozenorb.foxtrot.listener.FoxListener;
import net.frozenorb.foxtrot.listener.event.PlayerJumpEvent;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.foxtrot.util.PotionUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.Potion;

public class ContentListener implements Listener {
   private Foxtrot instance;
   private ContentHandler contentHandler;

   @EventHandler
   private void onJump(PlayerJumpEvent event) {
      Player player = event.getPlayer();
      if (player.hasMetadata("JUMP")) {
         player.damage(0.0);
         int damage = ThreadLocalRandom.current().nextInt(1, 19);
         player.setHealth(player.getHealth() - damage);
         double half = damage / 2;
         player.sendMessage(ChatColor.translate("&aYou took &4&l❤" + half + " &aof damage!"));
      }
   }

   @EventHandler
   public void onPlace(BlockPlaceEvent event) {
      if (!event.isCancelled()) {
         Player player = event.getPlayer();
         if (player.hasMetadata("RESTRICTED")) {
            player.sendMessage(ChatColor.RED + "You may not place blocks as you are currently restricted from doing so!");
            event.setCancelled(true);
         }
      }
   }

   @EventHandler
   public void onBreak(BlockBreakEvent event) {
      if (!event.isCancelled()) {
         Player player = event.getPlayer();
         if (player.hasMetadata("RESTRICTED")) {
            player.sendMessage(ChatColor.RED + "You may not break blocks as you are currently restricted from doing so!");
            event.setCancelled(true);
         }
      }
   }

   @EventHandler
   private void onPlate(PlayerInteractEvent event) {
      if (event.getAction() == Action.PHYSICAL && event.getClickedBlock() != null && event.getClickedBlock().getType().name().contains("PLATE")) {
         Player player = event.getPlayer();
         if (player.hasMetadata("RESTRICTED")) {
            if (event.getItem() != null && event.getItem().getType() == Material.POTION && event.getItem().getDurability() != 0) {
               Potion potion = Potion.fromItemStack(event.getItem());
               if (potion.isSplash()) {
                  PotionUtil.splashPotion(player, event.getItem());
                  if (player.getItemInHand() != null && player.getItemInHand().isSimilar(event.getItem())) {
                     player.setItemInHand(null);
                     player.updateInventory();
                  } else {
                     InventoryUtils.removeAmountFromInventory(player.getInventory(), event.getItem(), 1);
                  }
               }
            }

            event.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   private void onInteract(PlayerInteractEvent event) {
      if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null) {
         Block clickedBlock = event.getClickedBlock();
         if (FoxListener.NO_INTERACT.contains(clickedBlock.getType())) {
            Player player = event.getPlayer();
            if (player.hasMetadata("RESTRICTED")) {
               if (event.getItem() != null && event.getItem().getType() == Material.POTION && event.getItem().getDurability() != 0) {
                  Potion potion = Potion.fromItemStack(event.getItem());
                  if (potion.isSplash()) {
                     PotionUtil.splashPotion(player, event.getItem());
                     if (player.getItemInHand() != null && player.getItemInHand().isSimilar(event.getItem())) {
                        player.setItemInHand(null);
                        player.updateInventory();
                     } else {
                        InventoryUtils.removeAmountFromInventory(player.getInventory(), event.getItem(), 1);
                     }
                  }
               }

               event.setCancelled(true);
               player.sendMessage(ChatColor.RED + "You may not interact with blocks as you are currently restricted from doing so!");
            }
         }
      }
   }

   @ConstructorProperties({"instance", "contentHandler"})
   public ContentListener(Foxtrot instance, ContentHandler contentHandler) {
      this.instance = instance;
      this.contentHandler = contentHandler;
   }
}
