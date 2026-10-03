package net.frozenorb.foxtrot.gameplay.events.cavenite;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import cc.fyre.proton.util.ItemBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class SpectateMenu extends PaginatedMenu {
   @Override
   public String getPrePaginatedTitle(Player player) {
      return "Spectate";
   }

   @Override
   public Map<Integer, Button> getAllPagesButtons(Player player) {
      Map<Integer, Button> toReturn = new HashMap<>();
      CaveNiteHandler caveNiteHandler = Foxtrot.getInstance().getCaveNiteHandler();

      for (final Player onlinePlayer : caveNiteHandler.getOnlinePlayers()) {
         toReturn.put(toReturn.size(), new Button() {
            @Override
            public String getName(Player player) {
               return ChatColor.WHITE + onlinePlayer.getDisplayName();
            }

            @Override
            public List<String> getDescription(Player player) {
               List<String> toReturnx = new ArrayList<>();
               toReturnx.add("");
               toReturnx.add(ChatColor.YELLOW + "Click to teleport to this player");
               return toReturnx;
            }

            @Override
            public Material getMaterial(Player player) {
               return Material.SKULL_ITEM;
            }

            @Override
            public byte getDamageValue(Player player) {
               return 3;
            }

            @Override
            public ItemStack getButtonItem(Player player) {
               return ItemBuilder.copyOf(super.getButtonItem(player)).skull(onlinePlayer.getName()).build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
               player.teleport(onlinePlayer.getLocation().clone());
               player.sendMessage(ChatColor.translate("&6Teleported to &f" + onlinePlayer.getDisplayName() + "&6!"));
            }
         });
      }

      return toReturn;
   }
}
