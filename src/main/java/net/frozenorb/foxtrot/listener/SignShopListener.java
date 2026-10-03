package net.frozenorb.foxtrot.listener;

import java.util.Locale;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.config.ConfigMessages;
import net.frozenorb.foxtrot.economy.EconomyHandler;
import net.frozenorb.foxtrot.util.SignShopFeedback;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SignShopListener implements Listener {
   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onSignChange(SignChangeEvent event) {
      String line0 = event.getLine(0);
      if (line0 != null) {
         line0 = line0.trim();
         boolean isBuy = line0.equalsIgnoreCase("[Buy]") || line0.equalsIgnoreCase("[BuySign]");
         boolean isSell = line0.equalsIgnoreCase("[Sell]") || line0.equalsIgnoreCase("[SellSign]");
         if (isBuy || isSell) {
            Player player = event.getPlayer();
            if (!player.isOp() && !player.hasPermission("foxtrot.command.signshop")) {
               player.sendMessage(ChatColor.RED + "You don't have permission to create shop signs.");
               event.setCancelled(true);
            } else {
               int legacyAmount = parseAmountFromSign(event.getLine(1));
               if (legacyAmount <= 0 || !line0.equalsIgnoreCase("[Buy]") && !line0.equalsIgnoreCase("[Sell]")) {
                  String materialName = event.getLine(1);
                  if (materialName != null && !materialName.isEmpty()) {
                     Material material = Material.matchMaterial(materialName);
                     if (material == null) {
                        player.sendMessage(ChatColor.RED + "Unknown material: " + materialName);
                        event.setCancelled(true);
                     } else {
                        int amount;
                        try {
                           amount = Integer.parseInt(event.getLine(2));
                           if (amount <= 0) {
                              throw new NumberFormatException();
                           }
                        } catch (NumberFormatException ex) {
                           player.sendMessage(ChatColor.RED + "Line 3 must be a positive number (amount).");
                           event.setCancelled(true);
                           return;
                        }

                        double price;
                        try {
                           price = Double.parseDouble(event.getLine(3));
                           if (!Double.isFinite(price) || price < 0.0) {
                              throw new NumberFormatException();
                           }
                        } catch (NumberFormatException ex) {
                           player.sendMessage(ChatColor.RED + "Line 4 must be a non-negative number (price).");
                           event.setCancelled(true);
                           return;
                        }

                        String tag = isBuy ? "&a[Buy]" : "&c[Sell]";
                        event.setLine(0, ChatColor.translateAlternateColorCodes('&', tag));
                        event.setLine(1, ChatColor.BLACK + prettyMaterialName(material));
                        event.setLine(2, ChatColor.BLACK + String.valueOf(amount));
                        event.setLine(3, ChatColor.BLACK + "$" + formatPrice(price));
                        player.sendMessage(ChatColor.GREEN + "Shop sign created successfully.");
                     }
                  } else {
                     player.sendMessage(ChatColor.RED + "Line 2 must be a Material name (e.g. DIAMOND_BLOCK).");
                     event.setCancelled(true);
                  }
               } else {
                  double legacyPrice = parsePriceFromSign(event.getLine(3));
                  if (!Double.isFinite(legacyPrice) || legacyPrice < 0.0 || legacyPrice > 2.147483647E9 || legacyPrice != Math.floor(legacyPrice)) {
                     player.sendMessage(ChatColor.RED + "Line 4 must be a non-negative whole number (price).");
                     event.setCancelled(true);
                  } else if (event.getLine(2) != null && !event.getLine(2).trim().isEmpty()) {
                     event.setLine(0, isBuy ? "[Buy]" : "[Sell]");
                     event.setLine(1, String.valueOf(legacyAmount));
                     event.setLine(3, "$" + formatPrice(legacyPrice));
                     player.sendMessage(ChatColor.GREEN + "Shop sign created successfully.");
                  } else {
                     player.sendMessage(ChatColor.RED + "Line 3 must be an item name.");
                     event.setCancelled(true);
                  }
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onPlayerInteract(PlayerInteractEvent event) {
      if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
         Block block = event.getClickedBlock();
         if (block != null) {
            if (block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
               Sign sign = (Sign)block.getState();
               String line0 = sign.getLine(0);
               if (line0 != null) {
                  boolean isBuy = line0.equals(ChatColor.translateAlternateColorCodes('&', "&a[Buy]"));
                  boolean isSell = line0.equals(ChatColor.translateAlternateColorCodes('&', "&c[Sell]"));
                  if (isBuy || isSell) {
                     if (parseAmountFromSign(sign.getLine(1)) <= 0) {
                        Player player = event.getPlayer();
                        event.setCancelled(true);
                        Material material = parseMaterialFromSign(sign.getLine(1));
                        int amount = parseAmountFromSign(sign.getLine(2));
                        double price = parsePriceFromSign(sign.getLine(3));
                        if (material != null && amount > 0 && Double.isFinite(price) && !(price < 0.0)) {
                           EconomyHandler economy = Foxtrot.getInstance().getEconomyHandler();
                           if (isBuy) {
                              this.handleBuy(player, economy, material, amount, price, sign);
                           } else {
                              this.handleSell(player, economy, material, amount, price, sign);
                           }
                        } else {
                           player.sendMessage(ChatColor.RED + "This shop sign is misconfigured.");
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static boolean isFormattedShopSign(Sign sign) {
      String header = sign.getLine(0);
      return (header.equals(ChatColor.GREEN + "[Buy]") || header.equals(ChatColor.RED + "[Sell]"))
         && parseAmountFromSign(sign.getLine(1)) <= 0
         && parseMaterialFromSign(sign.getLine(1)) != null;
   }

   @EventHandler
   public void onPlayerQuit(PlayerQuitEvent event) {
      SignShopFeedback.clear(event.getPlayer().getUniqueId());
   }

   private void handleBuy(Player player, EconomyHandler economy, Material material, int amount, double price, Sign sign) {
      double balance = economy.getBalance(player.getUniqueId());
      if (balance < price) {
         player.sendMessage(ChatColor.RED + "You don't have enough money! Need $" + formatPrice(price) + ", you have $" + formatPrice(balance) + ".");
         SignShopFeedback.show(player, sign, 3, ConfigMessages.message("messages.sign-shop.cannot-afford", "&cCan't afford"));
      } else {
         PlayerInventory inv = player.getInventory();
         int freeSpace = countFreeSpace(inv, material);
         if (freeSpace < amount) {
            player.sendMessage(
               ChatColor.RED
                  + "Your inventory is full! Need space for "
                  + amount
                  + " "
                  + prettyMaterialName(material)
                  + ", only have room for "
                  + freeSpace
                  + "."
            );
         } else {
            economy.withdraw(player.getUniqueId(), price);
            inv.addItem(new ItemStack(material, amount));
            player.sendMessage(
               ChatColor.GREEN
                  + "Bought "
                  + amount
                  + " "
                  + prettyMaterialName(material)
                  + " for "
                  + ChatColor.GREEN
                  + "$"
                  + formatPrice(price)
                  + ChatColor.GREEN
                  + "."
            );
            SignShopFeedback.show(player, sign, 2, ConfigMessages.message("messages.sign-shop.bought", "&aBought %amount%", "%amount%", String.valueOf(amount)));
         }
      }
   }

   private void handleSell(Player player, EconomyHandler economy, Material material, int amount, double price, Sign sign) {
      PlayerInventory inv = player.getInventory();
      int has = countItems(inv, material);
      if (has <= 0) {
         player.sendMessage(ChatColor.RED + "You don't have any " + prettyMaterialName(material) + " to sell!");
         SignShopFeedback.show(player, sign, 2, ConfigMessages.message("messages.sign-shop.not-carrying", "&cNot carrying"));
      } else {
         int sellAmount = Math.min(amount, has);
         double perItem = price / amount;
         double pay = perItem * sellAmount;
         removeItems(inv, material, sellAmount);
         player.updateInventory();
         economy.deposit(player.getUniqueId(), pay);
         player.sendMessage(
            ChatColor.GREEN
               + "Sold "
               + sellAmount
               + " "
               + prettyMaterialName(material)
               + " for "
               + ChatColor.GREEN
               + "$"
               + formatPrice(pay)
               + ChatColor.GREEN
               + "."
         );
         if (sellAmount < amount) {
            player.sendMessage(ChatColor.YELLOW + "Sold partial amount — sign offered " + amount + " but you only had " + sellAmount + ".");
         }

         SignShopFeedback.show(player, sign, 2, ConfigMessages.message("messages.sign-shop.sold", "&cSold %amount%", "%amount%", String.valueOf(sellAmount)));
      }
   }

   private static String prettyMaterialName(Material material) {
      String[] words = material.name().toLowerCase(Locale.ROOT).split("_");
      StringBuilder sb = new StringBuilder();

      for (String word : words) {
         if (sb.length() > 0) {
            sb.append(" ");
         }

         sb.append(Character.toUpperCase(word.charAt(0)));
         if (word.length() > 1) {
            sb.append(word.substring(1));
         }
      }

      return sb.toString();
   }

   private static String formatPrice(double price) {
      return price == Math.floor(price) ? String.valueOf((long)price) : String.format(Locale.US, "%.2f", price);
   }

   private static Material parseMaterialFromSign(String line) {
      return line == null ? null : Material.matchMaterial(ChatColor.stripColor(line).replace(' ', '_').toUpperCase(Locale.ROOT));
   }

   private static int parseAmountFromSign(String line) {
      if (line == null) {
         return 0;
      }

      try {
         return Integer.parseInt(ChatColor.stripColor(line).trim());
      } catch (NumberFormatException e) {
         return 0;
      }
   }

   private static double parsePriceFromSign(String line) {
      if (line == null) {
         return Double.NaN;
      }

      try {
         return Double.parseDouble(ChatColor.stripColor(line).replace("$", "").replace(",", "").trim());
      } catch (NumberFormatException e) {
         return Double.NaN;
      }
   }

   private static int countItems(PlayerInventory inv, Material material) {
      int total = 0;

      for (ItemStack stack : inv.getContents()) {
         if (stack != null && stack.getType() == material) {
            total += stack.getAmount();
         }
      }

      return total;
   }

   private static void removeItems(PlayerInventory inv, Material material, int toRemove) {
      int remaining = toRemove;
      ItemStack[] contents = inv.getContents();

      for (int i = 0; i < contents.length && remaining > 0; i++) {
         ItemStack stack = contents[i];
         if (stack != null && stack.getType() == material) {
            int take = Math.min(stack.getAmount(), remaining);
            if (take >= stack.getAmount()) {
               contents[i] = null;
            } else {
               stack.setAmount(stack.getAmount() - take);
            }

            remaining -= take;
         }
      }

      inv.setContents(contents);
   }

   private static int countFreeSpace(PlayerInventory inv, Material material) {
      int maxPerStack = material.getMaxStackSize();
      int free = 0;

      for (ItemStack stack : inv.getContents()) {
         if (stack == null || stack.getType() == Material.AIR) {
            free += maxPerStack;
         } else if (stack.getType() == material) {
            free += maxPerStack - stack.getAmount();
         }
      }

      return free;
   }
}
