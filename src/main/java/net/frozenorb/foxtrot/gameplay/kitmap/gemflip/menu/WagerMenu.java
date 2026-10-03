package net.frozenorb.foxtrot.gameplay.kitmap.gemflip.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.gemflip.GemFlipHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.gemflip.data.GemFlipEntry;
import net.frozenorb.foxtrot.gameplay.kitmap.gemflip.data.GemFlipSide;
import net.frozenorb.foxtrot.util.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class WagerMenu extends Menu {
   private Set<Pair<Integer, GemFlipSide>> entryInfo;
   private int gems;

   @Override
   public String getTitle(Player player) {
      return "Wager";
   }

   @Override
   public int size(Player player) {
      return 9;
   }

   @Override
   public boolean isPlaceholder() {
      return true;
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      Map<Integer, Button> toReturn = new HashMap<>();

      for (Pair<Integer, GemFlipSide> entryPair : this.entryInfo) {
         int slot = entryPair.first;
         final GemFlipSide side = entryPair.second;
         toReturn.put(slot, new Button() {
            @Override
            public String getName(Player player) {
               return ChatColor.translate("&2&l" + side.getFriendlyName());
            }

            @Override
            public List<String> getDescription(Player player) {
               return null;
            }

            @Override
            public Material getMaterial(Player player) {
               return side.getMaterial();
            }

            @Override
            public void clicked(Player player, int slotx, ClickType clickType) {
               GemFlipHandler gemFlipHandler = Foxtrot.getInstance().getGemFlipHandler();
               if (gemFlipHandler.getQueue().size() >= 8) {
                  player.sendMessage(ChatColor.RED + "The wager queue is currently full. Please try again later.");
               } else if (Foxtrot.getInstance().getGemMap().getGems(player.getUniqueId()) < WagerMenu.this.gems) {
                  player.sendMessage(ChatColor.RED + "Insufficient gems!");
               } else {
                  Foxtrot.getInstance().getGemMap().removeGems(player.getUniqueId(), WagerMenu.this.gems);
                  GemFlipEntry entry = new GemFlipEntry(player, WagerMenu.this.gems);
                  entry.setChosenSide(side);
                  player.sendMessage(ChatColor.GREEN + "You have entered the wager queue with the position of " + side.getFriendlyName());
                  player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                  player.closeInventory();
                  gemFlipHandler.getQueue().add(entry);
               }
            }
         });
      }

      return toReturn;
   }

   @ConstructorProperties({"entryInfo", "gems"})
   public WagerMenu(Set<Pair<Integer, GemFlipSide>> entryInfo, int gems) {
      this.entryInfo = entryInfo;
      this.gems = gems;
   }
}
