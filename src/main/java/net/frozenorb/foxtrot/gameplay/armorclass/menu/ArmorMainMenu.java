package net.frozenorb.foxtrot.gameplay.armorclass.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import net.frozenorb.foxtrot.gameplay.armorclass.Category;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class ArmorMainMenu extends Menu {
   @Override
   public String getTitle(Player player) {
      return "Armor Classes";
   }

   @Override
   public int size(Player player) {
      return 27;
   }

   @Override
   public boolean isPlaceholder() {
      return true;
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      Map<Integer, Button> toReturn = new HashMap<>();
      toReturn.put(4, new Button() {
         @Override
         public String getName(Player player) {
            return ChatColor.translate("&9&lArmor Classes");
         }

         @Override
         public List<String> getDescription(Player player) {
            List<String> toReturnx = new ArrayList<>();
            toReturnx.add("");
            toReturnx.add(ChatColor.translate("&bWhat are Armor Classes?"));
            toReturnx.add(ChatColor.translate("&9❙ &fEach armor classes provides unique traits."));
            toReturnx.add(ChatColor.translate("&9❙ &fSuit up to enhance your play-style."));
            toReturnx.add("");
            toReturnx.add(ChatColor.translate("&7Hover over each armor classes to view its use"));
            return toReturnx;
         }

         @Override
         public Material getMaterial(Player player) {
            return Material.BOOK;
         }
      });
      toReturn.put(11, new Button() {
         @Override
         public String getName(Player player) {
            return ChatColor.translate("&b&lDiamond Armor Classes");
         }

         @Override
         public List<String> getDescription(Player player) {
            List<String> toReturn = new ArrayList<>();
            toReturn.add("");

            for (ArmorClass armorClass : Foxtrot.getInstance().getArmorClassHandler().getArmorClasses()) {
               if (armorClass.getCategory().equals(Category.DIAMOND)) {
                  toReturn.add(ChatColor.translate("&b❙ &f" + armorClass.getDisplayName()));
               }
            }

            toReturn.add("");
            toReturn.add(ChatColor.GREEN + "Click to view all diamond armor classes.");
            return toReturn;
         }

         @Override
         public Material getMaterial(Player player) {
            return Material.DIAMOND_HELMET;
         }

         @Override
         public void clicked(Player player, int slot, ClickType clickType) {
            new ArmorClassesMenu(Category.DIAMOND).openMenu(player);
         }
      });
      toReturn.put(15, new Button() {
         @Override
         public String getName(Player player) {
            return ChatColor.translate("&d&lArcher Armor Classes");
         }

         @Override
         public List<String> getDescription(Player player) {
            List<String> toReturn = new ArrayList<>();
            toReturn.add("");

            for (ArmorClass armorClass : Foxtrot.getInstance().getArmorClassHandler().getArmorClasses()) {
               if (armorClass.getCategory().equals(Category.ARCHER)) {
                  toReturn.add(ChatColor.translate("&d❙ &f" + armorClass.getDisplayName()));
               }
            }

            toReturn.add("");
            toReturn.add(ChatColor.GREEN + "Click to view all archer armor classes.");
            return toReturn;
         }

         @Override
         public Material getMaterial(Player player) {
            return Material.LEATHER_HELMET;
         }

         @Override
         public void clicked(Player player, int slot, ClickType clickType) {
            new ArmorClassesMenu(Category.ARCHER).openMenu(player);
         }
      });
      return toReturn;
   }
}
