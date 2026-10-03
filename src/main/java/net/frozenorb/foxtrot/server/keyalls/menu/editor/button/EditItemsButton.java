package net.frozenorb.foxtrot.server.keyalls.menu.editor.button;

import cc.fyre.proton.menu.Button;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.server.keyalls.KeyAll;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EditItemsButton extends Button {
   private KeyAll keyAll;

   @Override
   public String getName(Player player) {
      return ChatColor.translate("&6&lEdit Items");
   }

   @Override
   public List<String> getDescription(Player player) {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("");
      toReturn.add(ChatColor.translate("&6&lItems:"));

      for (ItemStack item : this.keyAll.getItems()) {
         String name = item.getType().name();
         if (item.getItemMeta() != null && item.getItemMeta().getDisplayName() != null) {
            name = ChatColor.translate(item.getItemMeta().getDisplayName());
         }

         toReturn.add(ChatColor.translate("&6❙ &f" + item.getAmount() + "x " + name));
      }

      toReturn.add("");
      toReturn.add(ChatColor.GREEN + "Type /keyall setitems to set the items");
      return toReturn;
   }

   @Override
   public Material getMaterial(Player player) {
      return this.keyAll.getItems().isEmpty() ? Material.PAPER : this.keyAll.getItems().get(0).getType();
   }

   @ConstructorProperties("keyAll")
   public EditItemsButton(KeyAll keyAll) {
      this.keyAll = keyAll;
   }
}
