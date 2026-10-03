package net.frozenorb.foxtrot.gameplay.events.cavenite.command;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.cavenite.CaveNiteHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class CaveNiteAddItemCommand {
   public static void execute(Player player) {
      ItemStack itemStack = player.getItemInHand().clone();
      if (itemStack != null && itemStack.getType() != Material.AIR) {
         player.sendMessage(ChatColor.GREEN + "yes added");
         Foxtrot.getInstance().getCaveNiteHandler().getChestLoot().add(itemStack.clone());
      } else {
         player.sendMessage("no");
      }
   }

   public static void listSpawn(Player player) {
      final CaveNiteHandler caveNiteHandler = Foxtrot.getInstance().getCaveNiteHandler();
      (new Menu() {
         @Override
         public String getTitle(Player player) {
            return null;
         }

         @Override
         public Map<Integer, Button> getButtons(Player player) {
            Map<Integer, Button> toReturn = new HashMap<>();

            for (final ItemStack itemStack : caveNiteHandler.getChestLoot()) {
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
                     return itemStack.clone();
                  }

                  @Override
                  public void clicked(Player player, int slot, ClickType clickType) {
                     if (clickType == ClickType.SHIFT_RIGHT) {
                        caveNiteHandler.getChestLoot().remove(itemStack);
                        player.sendMessage(ChatColor.RED + "You have removed that item from the loot table!");
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                     } else {
                        player.getInventory().addItem(itemStack.clone());
                     }
                  }
               });
            }

            return toReturn;
         }
      }).openMenu(player);
   }
}
