package net.frozenorb.foxtrot.gameplay.loot.shop;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.ItemUtils;
import java.beans.ConstructorProperties;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.SellAllListener;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ShopMenu extends Menu {
   private boolean buyShop;

   @Override
   public String getTitle(Player player) {
      return this.buyShop ? "Buy Shop" : "Sell Shop";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      Map<Integer, Button> toReturn = new HashMap<>();
      if (this.buyShop) {
         for (final Entry<ItemStack, Double> entry : Foxtrot.getInstance().getShopHandler().getSpecificBuyShop().entrySet()) {
            toReturn.put(
               toReturn.size(),
               new Button() {
                  @Override
                  public String getName(Player player) {
                     return null;
                  }

                  @Override
                  public List<String> getDescription(Player player) {
                     return null;
                  }

                  @Override
                  public Material getMaterial(Player player) {
                     return null;
                  }

                  @Override
                  public ItemStack getButtonItem(Player player) {
                     return ItemBuilder.copyOf(entry.getKey().clone())
                        .addToLore(
                           "",
                           "&4&l┃ &fCost: &2$&a" + NumberFormat.getNumberInstance(Locale.US).format(entry.getValue()),
                           "",
                           ChatColor.GREEN + "Click to purchase this item."
                        )
                        .build();
                  }

                  @Override
                  public void clicked(Player player, int slot, ClickType clickType) {
                     double balance = Foxtrot.getInstance().getEconomyHandler().getBalance(player.getUniqueId());
                     double cost = entry.getValue();
                     if (cost > balance) {
                        player.sendMessage(ChatColor.RED + "Insufficient funds!");
                     } else {
                        Foxtrot.getInstance().getEconomyHandler().setBalance(player.getUniqueId(), balance - cost);
                        player.getInventory().addItem(entry.getKey().clone());
                     }
                  }
               }
            );
         }

         return toReturn;
      } else {
         toReturn.put(
            8,
            new Button() {
               @Override
               public String getName(Player player) {
                  return CC.translate("&4&lSell All");
               }

               @Override
               public List<String> getDescription(Player player) {
                  List<String> toReturn = new ArrayList<>();
                  toReturn.add("");
                  toReturn.add(ChatColor.GREEN + "Click to sell all valuables in your inventory!");
                  return toReturn;
               }

               @Override
               public Material getMaterial(Player player) {
                  return Material.NETHER_STAR;
               }

               @Override
               public void clicked(Player player, int slot, ClickType clickType) {
                  player.closeInventory();
                  List<ItemStack> soldItems = new ArrayList<>();
                  int itemsSold = 0;
                  double amountPaid = 0.0;

                  for (ItemStack content : player.getInventory().getContents()) {
                     if (content != null
                        && content.getType() != Material.AIR
                        && content.getAmount() > 0
                        && (content.getItemMeta() == null || !content.getItemMeta().hasLore())
                        && SellAllListener.sellableItems.containsKey(content.getType())) {
                        double cost = SellAllListener.sellableItems.get(content.getType());
                        soldItems.add(content);
                        itemsSold += content.getAmount();
                        amountPaid += content.getAmount() * cost;
                     }
                  }

                  if (soldItems.size() == 0) {
                     player.sendMessage(ChatColor.RED + "You do not have any valuables to sell on you!");
                  } else {
                     soldItems.forEach(it -> player.getInventory().remove(it));
                     Foxtrot.getInstance().getEconomyHandler().deposit(player.getUniqueId(), Math.floor(amountPaid));
                     player.sendMessage(
                        ChatColor.translate(
                           "&6You sold &f" + itemsSold + " blocks &6for &2$&a" + NumberFormat.getNumberInstance(Locale.US).format(Math.floor(amountPaid))
                        )
                     );
                  }
               }
            }
         );

         for (final Entry<Material, Double> entry : Foxtrot.getInstance().getShopHandler().getSellShop().entrySet()) {
            toReturn.put(
               toReturn.size(),
               new Button() {
                  @Override
                  public String getName(Player player) {
                     return null;
                  }

                  @Override
                  public List<String> getDescription(Player player) {
                     return null;
                  }

                  @Override
                  public Material getMaterial(Player player) {
                     return null;
                  }

                  @Override
                  public ItemStack getButtonItem(Player player) {
                     return ItemBuilder.of(entry.getKey())
                        .addToLore(
                           "",
                           "&4&l┃ &fCost: &2$&a" + NumberFormat.getNumberInstance(Locale.US).format(entry.getValue()),
                           "",
                           ChatColor.GREEN
                              + "Click to sell "
                              + ItemUtils.getName(new ItemStack(entry.getKey(), 1))
                              + " for $"
                              + NumberFormat.getNumberInstance(Locale.US).format(Math.round(entry.getValue())),
                           ChatColor.GREEN
                              + "Shift Click to sell "
                              + ItemUtils.getName(new ItemStack(entry.getKey(), 16))
                              + " for $"
                              + NumberFormat.getNumberInstance(Locale.US).format(Math.round(entry.getValue() * 16.0)),
                           ChatColor.GREEN + "Middle Click to sell all"
                        )
                        .build();
                  }

                  @Override
                  public void clicked(Player player, int slot, ClickType clickType) {
                     List<ItemStack> soldItems = new ArrayList<>();
                     int itemCount = 0;

                     for (ItemStack content : player.getInventory().getContents()) {
                        if (content != null
                           && content.getType() != Material.AIR
                           && content.getAmount() > 0
                           && (content.getItemMeta() == null || !content.getItemMeta().hasLore())
                           && content.getType().equals(entry.getKey())) {
                           itemCount += content.getAmount();
                        }
                     }

                     if (itemCount == 0) {
                        player.sendMessage(ChatColor.RED + "You don't have enough items!");
                     } else if (clickType.isShiftClick()) {
                        double total = entry.getValue() * 16.0;
                        if (itemCount < 16) {
                           player.sendMessage(ChatColor.RED + "You don't have enough items!");
                        } else {
                           Foxtrot.getInstance().getEconomyHandler().deposit(player.getUniqueId(), total);
                           ShopMenu.this.removeItem(player, new ItemStack(entry.getKey(), 16), 16);
                           if (Foxtrot.getInstance().getBattlePassHandler() != null) {
                              Foxtrot.getInstance().getBattlePassHandler().useProgress(player.getUniqueId(), progress -> {
                                 progress.setValuablesSold(progress.getValuablesSold() + (int)Math.round(total));
                                 progress.requiresSave();
                                 Foxtrot.getInstance().getBattlePassHandler().checkCompletionsAsync(player);
                              });
                           }
                        }
                     } else if (!clickType.equals(ClickType.MIDDLE)) {
                        double total = entry.getValue();
                        Foxtrot.getInstance().getEconomyHandler().deposit(player.getUniqueId(), total);
                        ShopMenu.this.removeItem(player, new ItemStack(entry.getKey(), 16), 1);
                     } else {
                        int itemsSold = 0;
                        double amountPaid = 0.0;

                        for (ItemStack content : player.getInventory().getContents()) {
                           if (content != null
                              && content.getType() != Material.AIR
                              && content.getAmount() > 0
                              && (content.getItemMeta() == null || !content.getItemMeta().hasLore())
                              && entry.getKey().equals(content.getType())) {
                              double cost = entry.getValue();
                              soldItems.add(content);
                              itemsSold += content.getAmount();
                              amountPaid += content.getAmount() * cost;
                           }
                        }

                        if (soldItems.size() == 0) {
                           player.sendMessage(ChatColor.RED + "You do not have any valuables to sell on you!");
                        } else {
                           soldItems.forEach(it -> player.getInventory().remove(it));
                           Foxtrot.getInstance().getEconomyHandler().deposit(player.getUniqueId(), Math.floor(amountPaid));
                           if (Foxtrot.getInstance().getBattlePassHandler() != null) {
                              double finalAmountPaid = amountPaid;
                              Foxtrot.getInstance().getBattlePassHandler().useProgress(player.getUniqueId(), progress -> {
                                 progress.setValuablesSold(progress.getValuablesSold() + (int)Math.round(finalAmountPaid));
                                 progress.requiresSave();
                                 Foxtrot.getInstance().getBattlePassHandler().checkCompletionsAsync(player);
                              });
                           }

                           player.sendMessage(
                              ChatColor.translate(
                                 "&6You sold &f" + itemsSold + " blocks &6for &2$&a" + NumberFormat.getNumberInstance(Locale.US).format(Math.floor(amountPaid))
                              )
                           );
                        }
                     }
                  }
               }
            );
         }

         return toReturn;
      }
   }

   public int countItems(Player player, Material material, int damageValue) {
      PlayerInventory inventory = player.getInventory();
      ItemStack[] items = inventory.getContents();
      int amount = 0;

      for (ItemStack item : items) {
         if (item != null) {
            boolean specialDamage = material.getMaxDurability() == 0;
            if (item.getType() != null && item.getType() == material && (!specialDamage || item.getDurability() == (short)damageValue)) {
               amount += item.getAmount();
            }
         }
      }

      return amount;
   }

   public void removeItem(Player p, ItemStack it, int amount) {
      boolean specialDamage = it.getType().getMaxDurability() == 0;

      for (int a = 0; a < amount; a++) {
         for (ItemStack i : p.getInventory()) {
            if (i != null && i.getType() == it.getType() && (!specialDamage || it.getDurability() == i.getDurability())) {
               if (i.getAmount() == 1) {
                  p.getInventory().clear(p.getInventory().first(i));
               } else {
                  i.setAmount(i.getAmount() - 1);
               }
               break;
            }
         }
      }
   }

   @Override
   public int getSlot(int x, int y) {
      return this.buyShop ? 36 : 27;
   }

   @Override
   public boolean isPlaceholder() {
      return true;
   }

   @ConstructorProperties("buyShop")
   public ShopMenu(boolean buyShop) {
      this.buyShop = buyShop;
   }
}
