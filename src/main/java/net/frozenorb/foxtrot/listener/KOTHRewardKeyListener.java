package net.frozenorb.foxtrot.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class KOTHRewardKeyListener implements Listener {
   @EventHandler
   public void onPlayerInteract(final PlayerInteractEvent event) {
      if (event.getClickedBlock() != null
         && event.getItem() != null
         && event.getClickedBlock().getType() == Material.ENDER_CHEST
         && DTRBitmask.SAFE_ZONE.appliesAt(event.getClickedBlock().getLocation())
         && InventoryUtils.isSimilar(event.getItem(), ChatColor.RED + "KOTH Reward Key")) {
         ItemStack key = event.getItem();
         if (key.getItemMeta() != null
            && key.getType() == Material.GOLD_NUGGET
            && key.getItemMeta().getLore().get(1) != null
            && ChatColor.stripColor(key.getItemMeta().getLore().get(1)).contains("Obtained from")) {
            event.setCancelled(true);
            int open = 0;

            for (ItemStack itemStack : event.getPlayer().getInventory().getContents()) {
               if (itemStack == null || itemStack.getType() == Material.AIR) {
                  open++;
               }
            }

            if (open < 4) {
               event.getPlayer().sendMessage(ChatColor.RED + "You must have at least 3 open inventory slots to use a KOTH reward key!");
            } else {
               Block block = event.getClickedBlock().getRelative(BlockFace.DOWN, 3);
               if (block.getType() == Material.CHEST) {
                  ItemStack stack = event.getPlayer().getItemInHand();
                  if (stack.getAmount() == 1) {
                     event.getPlayer().setItemInHand(null);
                  } else {
                     stack.setAmount(stack.getAmount() - 1);
                     event.getPlayer().setItemInHand(stack);
                  }

                  event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.FIREWORK_BLAST, 1.0F, 1.0F);
                  Chest chest = (Chest)block.getState();
                  ItemStack[] lootTables = chest.getBlockInventory().getContents();
                  final List<ItemStack> loot = new ArrayList<>();
                  int given = 0;
                  int tries = 0;

                  label93:
                  while (given < 3 && tries < 100) {
                     tries++;
                     ItemStack chosenItem = lootTables[ThreadLocalRandom.current().nextInt(lootTables.length)];
                     if (chosenItem != null && chosenItem.getType() != Material.AIR && chosenItem.getAmount() != 0) {
                        for (ItemStack givenLoot : loot) {
                           if (givenLoot.getType() == chosenItem.getType()) {
                              continue label93;
                           }
                        }

                        given++;
                        loot.add(chosenItem);
                     }
                  }

                  final StringBuilder builder = new StringBuilder();

                  for (ItemStack itemStack : loot) {
                     String displayName = itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()
                        ? ChatColor.RED.toString() + ChatColor.ITALIC + ChatColor.stripColor(itemStack.getItemMeta().getDisplayName())
                        : ChatColor.BLUE.toString()
                           + itemStack.getAmount()
                           + "x "
                           + ChatColor.YELLOW
                           + WordUtils.capitalize(itemStack.getType().name().replace("_", " ").toLowerCase());
                     builder.append(ChatColor.YELLOW).append(displayName).append(ChatColor.GOLD).append(", ");
                  }

                  if (builder.length() > 2) {
                     builder.setLength(builder.length() - 2);
                  }

                  Foxtrot.getInstance()
                     .getServer()
                     .broadcastMessage(
                        ChatColor.GOLD
                           + "[KingOfTheHill] "
                           + ChatColor.GOLD
                           + event.getPlayer().getName()
                           + ChatColor.YELLOW
                           + " is obtaining loot from a "
                           + ChatColor.BLUE
                           + ChatColor.ITALIC
                           + "Koth Key"
                           + ChatColor.YELLOW
                           + " obtained from "
                           + ChatColor.GOLD
                           + InventoryUtils.getLoreData(event.getItem(), 1)
                           + ChatColor.YELLOW
                           + " at "
                           + ChatColor.GOLD
                           + InventoryUtils.getLoreData(event.getItem(), 2)
                           + ChatColor.YELLOW
                           + "."
                     );
                  (new BukkitRunnable() {
                        @Override
                        public void run() {
                           (new BukkitRunnable() {
                                 @Override
                                 public void run() {
                                    Foxtrot.getInstance()
                                       .getServer()
                                       .broadcastMessage(
                                          ChatColor.GOLD
                                             + "[KingOfTheHill] "
                                             + ChatColor.GOLD
                                             + event.getPlayer().getName()
                                             + ChatColor.YELLOW
                                             + " obtained "
                                             + builder.toString()
                                             + ChatColor.GOLD
                                             + ","
                                             + ChatColor.YELLOW
                                             + " from a "
                                             + ChatColor.BLUE.toString()
                                             + ChatColor.ITALIC
                                             + "KOTH key"
                                             + ChatColor.YELLOW
                                             + "."
                                       );
                                 }
                              })
                              .runTaskAsynchronously(Foxtrot.getInstance());

                           for (ItemStack lootItem : loot) {
                              event.getPlayer().getInventory().addItem(lootItem);
                           }

                           event.getPlayer().updateInventory();
                        }
                     })
                     .runTaskLater(Foxtrot.getInstance(), 100L);
               }
            }
         } else {
            event.setCancelled(true);
            this.openKothLoot(event.getPlayer(), event.getClickedBlock());
         }
      } else {
         if (event.getClickedBlock() != null
            && event.getClickedBlock().getType() == Material.ENDER_CHEST
            && DTRBitmask.SAFE_ZONE.appliesAt(event.getClickedBlock().getLocation())) {
            event.setCancelled(true);
            this.openKothLoot(event.getPlayer(), event.getClickedBlock());
         }
      }
   }

   private void openKothLoot(Player player, Block clickedBlock) {
      Block block = clickedBlock.getRelative(BlockFace.DOWN, 3);
      if (block.getType() == Material.CHEST) {
         Chest chest = (Chest)block.getState();
         ItemStack[] lootTables = chest.getBlockInventory().getContents();
         Inventory inventory = Bukkit.createInventory(player, 27, "KOTH Loot");
         inventory.setContents(lootTables);
         player.openInventory(inventory);
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void onClick(InventoryClickEvent event) {
      if (event.getWhoClicked() instanceof Player) {
         Player player = (Player)event.getWhoClicked();
         if (player.getOpenInventory() != null && player.getOpenInventory().getTitle() != null && player.getOpenInventory().getTitle().equals("KOTH Loot")) {
            event.setCancelled(true);
         }
      }
   }
}
