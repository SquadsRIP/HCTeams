package net.frozenorb.foxtrot.gameplay.loot.partnercrate.menu.element;

import cc.fyre.proton.menu.Button;
import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.config.ConfigMessages;
import net.frozenorb.foxtrot.gameplay.loot.partnercrate.PartnerCrateCommand;
import net.frozenorb.foxtrot.gameplay.loot.partnercrate.PartnerType;
import net.frozenorb.foxtrot.gameplay.loot.partnercrate.menu.PreviewMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class PartnerCrateElement extends Button {
   private PartnerType partnerType;

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
   public void clicked(Player player, int i, ClickType clickType) {
      if (clickType != ClickType.LEFT && clickType != ClickType.SHIFT_LEFT) {
         ItemStack itemStack = Arrays.stream(player.getInventory().getContents())
            .filter(it -> PartnerCrateCommand.isSimilar(this.partnerType, it))
            .findFirst()
            .orElse(null);
         if (itemStack == null) {
            player.sendMessage(
               ChatColor.YELLOW
                  + "You do not have a "
                  + this.partnerType.getCrateName()
                  + " key in your inventory, purchase one at "
                  + ConfigMessages.link("store", "Coming Soon!")
            );
         } else {
            if (itemStack.getAmount() == 1) {
               player.getInventory().remove(itemStack);
            } else {
               itemStack.setAmount(itemStack.getAmount() - 1);
            }

            Foxtrot.getInstance()
               .getServer()
               .dispatchCommand(
                  Foxtrot.getInstance().getServer().getConsoleSender(), "cr forceopen " + player.getName() + " " + this.partnerType.getCrateName()
               );
         }
      } else {
         new PreviewMenu(this.partnerType).openMenu(player);
      }
   }

   @Override
   public ItemStack getButtonItem(Player player) {
      return this.partnerType.getItemStack(false);
   }

   @ConstructorProperties("partnerType")
   public PartnerCrateElement(PartnerType partnerType) {
      this.partnerType = partnerType;
   }
}
