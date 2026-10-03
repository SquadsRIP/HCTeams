package net.frozenorb.foxtrot.server;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class SpawnerShopMenu extends Menu {
   @Override
   public String getTitle(Player player) {
      return "Spawner Shop";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      Map<Integer, Button> toReturn = new HashMap<>();

      for (int i = 0; i < 27; i++) {
         toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)15, ""));
      }

      int i = 0;

      for (final SpawnerType value : SpawnerType.values()) {
         toReturn.put(10 + i * 2, new Button() {
            @Override
            public String getName(Player player) {
               return ChatColor.DARK_RED + ChatColor.BOLD.toString() + value.getDisplayName() + " Spawner";
            }

            @Override
            public List<String> getDescription(Player player) {
               List<String> toReturnx = new ArrayList<>();
               toReturnx.add(ChatColor.GRAY + "Purchase various spawners for");
               toReturnx.add(ChatColor.GRAY + "your base off the network here!");
               toReturnx.add("");
               toReturnx.add(ChatColor.translate("&4&l┃ &fCost: &2$&a40,000"));
               toReturnx.add("");
               toReturnx.add(ChatColor.GREEN + "Click to purchase this Spawner");
               return toReturnx;
            }

            @Override
            public Material getMaterial(Player player) {
               return Material.MOB_SPAWNER;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
               if (Foxtrot.getInstance().getEconomyHandler().getBalance(player.getUniqueId()) < 25000.0) {
                  player.sendMessage(ChatColor.RED + "Insufficient funds!");
               } else {
                  Foxtrot.getInstance().getEconomyHandler().withdraw(player.getUniqueId(), 25000.0);
                  if (value == SpawnerType.ZOMBIE) {
                     player.getInventory().addItem(InventoryUtils.ZOMBIE_SPAWNER.clone());
                  }

                  if (value == SpawnerType.SKELETON) {
                     player.getInventory().addItem(InventoryUtils.SKELETON_SPAWNER.clone());
                  }

                  if (value == SpawnerType.SPIDER) {
                     player.getInventory().addItem(InventoryUtils.SPIDER_SPAWNER.clone());
                  }

                  if (value == SpawnerType.CAVE_SPIDER) {
                     player.getInventory().addItem(InventoryUtils.CAVE_SPIDER_SPAWNER.clone());
                  }
               }
            }
         });
         i++;
      }

      return toReturn;
   }
}
