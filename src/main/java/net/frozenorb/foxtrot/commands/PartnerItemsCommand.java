package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PartnerItemsCommand {
   @Command(names = {"partneritems", "abilityitems"}, permission = "")
   public static void execute(Player player) {
      (new Menu() {
            @Override
            public String getTitle(Player player) {
               return "Partner Items";
            }

            @Override
            public Map<Integer, Button> getButtons(Player player) {
               Map<Integer, Button> toReturn = new HashMap<>();

               for (final Ability value : Foxtrot.getInstance().getMapHandler().getAbilityHandler().getAbilities().values()) {
                  if (value.getCategory() != Category.PORTABLE_BARD
                     && (Foxtrot.getInstance().getMapHandler().isKitMap() || value.getCategory() != Category.KIT_MAP)) {
                     toReturn.put(toReturn.size(), new Button() {
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
                           return value.hassanStack.clone();
                        }

                        @Override
                        public void clicked(Player player, int slot, ClickType clickType) {
                           if (player.isOp()) {
                              player.getInventory().addItem(value.hassanStack.clone());
                           }
                        }
                     });
                  }
               }

               return toReturn;
            }
         })
         .openMenu(player);
   }

   @Command(names = {"ability statistics", "ability stats"}, permission = "op")
   public static void statistics(Player player) {
      (new Menu() {
            @Override
            public String getTitle(Player player) {
               return "Ability Statistics";
            }

            @Override
            public Map<Integer, Button> getButtons(Player player) {
               Map<Integer, Button> toReturn = new HashMap<>();
               List<Ability> abilities = new ArrayList<>();

               for (Entry<String, Ability> stringAbilityEntry : Foxtrot.getInstance().getMapHandler().getAbilityHandler().getAbilities().entrySet()) {
                  if (!stringAbilityEntry.getKey().contains("Launcher")
                     && !stringAbilityEntry.getKey().contains("BanStick")
                     && (Foxtrot.getInstance().getMapHandler().isKitMap() || stringAbilityEntry.getValue().getCategory() != Category.KIT_MAP)) {
                     abilities.add(stringAbilityEntry.getValue());
                  }
               }

               abilities.sort(Comparator.comparingInt(it -> Foxtrot.getInstance().getMapHandler().getAbilityHandler().getUsedItems().getOrDefault(it, 0)));

               for (final Ability ability : abilities) {
                  toReturn.put(toReturn.size(), new Button() {
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
                        int amountUsed = Foxtrot.getInstance().getMapHandler().getAbilityHandler().getUsedItems().getOrDefault(ability, 0);
                        ItemStack itemStack = ability.hassanStack.clone();
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        List<String> lore = itemMeta.getLore();
                        lore.add("");
                        lore.add(ChatColor.GOLD + "Amount Used: " + ChatColor.WHITE + amountUsed);
                        itemMeta.setLore(lore);
                        itemStack.setAmount(Math.min(amountUsed == 0 ? 1 : amountUsed, 64));
                        itemStack.setItemMeta(itemMeta);
                        return itemStack;
                     }
                  });
               }

               return toReturn;
            }

            @Override
            public boolean isAutoUpdate() {
               return true;
            }
         })
         .openMenu(player);
   }
}
