package net.frozenorb.foxtrot.server.keyalls.menu.redeem;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenorb.foxtrot.server.keyalls.KeyAll;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class MultipleKeyAllMenu extends PaginatedMenu {
   private List<KeyAll> keyAlls;

   @Override
   public String getPrePaginatedTitle(Player player) {
      return "Key-All";
   }

   @Override
   public Map<Integer, Button> getAllPagesButtons(Player player) {
      Map<Integer, Button> toReturn = new HashMap<>();

      for (final KeyAll keyAll : this.keyAlls) {
         toReturn.put(toReturn.size(), new Button() {
            @Override
            public String getName(Player player) {
               return ChatColor.translate(keyAll.getDisplayName());
            }

            @Override
            public List<String> getDescription(Player player) {
               List<String> toReturnx = new ArrayList<>();
               toReturnx.add(ChatColor.translate("&4&l❙ &fItems: &c" + keyAll.getItems().size()));
               toReturnx.add(ChatColor.translate("&4&l❙ &fRedeemed: &c" + keyAll.getRedeemed().size()));
               toReturnx.add("");
               toReturnx.add(ChatColor.GREEN + "Click to redeem the key-all items.");
               return toReturnx;
            }

            @Override
            public Material getMaterial(Player player) {
               return keyAll.getItems().isEmpty() ? Material.TRIPWIRE_HOOK : keyAll.getItems().get(0).getType();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
               player.closeInventory();
               if (player.getInventory().firstEmpty() == -1) {
                  player.sendMessage(ChatColor.RED + "Your inventory is full!");
               } else {
                  for (ItemStack itemStack : keyAll.getItems()) {
                     if (itemStack != null) {
                        if (player.getInventory().firstEmpty() == -1) {
                           player.getWorld().dropItemNaturally(player.getLocation(), itemStack.clone());
                        } else {
                           player.getInventory().addItem(itemStack.clone());
                        }
                     }
                  }

                  keyAll.getRedeemed().add(player.getUniqueId());
                  player.updateInventory();
                  player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                  player.sendMessage(ChatColor.translate("&aYou have redeemed the " + keyAll.getDisplayName() + "&a!"));
               }
            }
         });
      }

      return toReturn;
   }

   @ConstructorProperties("keyAlls")
   public MultipleKeyAllMenu(List<KeyAll> keyAlls) {
      this.keyAlls = keyAlls;
   }
}
