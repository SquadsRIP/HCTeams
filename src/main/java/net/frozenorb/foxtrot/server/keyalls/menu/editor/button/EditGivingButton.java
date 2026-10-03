package net.frozenorb.foxtrot.server.keyalls.menu.editor.button;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.server.keyalls.KeyAll;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class EditGivingButton extends Button {
   private Menu parentMenu;
   private KeyAll keyAll;

   @Override
   public String getName(Player player) {
      return ChatColor.GOLD + ChatColor.BOLD.toString() + "Status";
   }

   @Override
   public List<String> getDescription(Player player) {
      return Arrays.asList(
         ChatColor.GRAY + "Modify if the key-all",
         ChatColor.GRAY + "is currently available to players.",
         "",
         ChatColor.translate("&6&l┃ &fCurrent: &e" + this.keyAll.isGiving()),
         "",
         ChatColor.GREEN + "Click to modify this key-all's status."
      );
   }

   @Override
   public Material getMaterial(Player player) {
      return this.keyAll.isGiving() ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK;
   }

   @Override
   public void clicked(Player player, int slot, ClickType clickType) {
      player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
      if (this.keyAll.isGiving()) {
         player.sendMessage(ChatColor.RED + "Key-All is no longer available to players.");
         this.keyAll.setGiving(false);
         this.keyAll.setGiveAllTime(0L);
         this.keyAll.setEnd(0L);
      } else {
         player.sendMessage(ChatColor.GREEN + "Key-All is now available to players.");
         this.keyAll.setEnd(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5L));
         this.keyAll.setGiveAllTime(0L);
         this.keyAll.setGiving(true);
      }
   }

   @ConstructorProperties({"parentMenu", "keyAll"})
   public EditGivingButton(Menu parentMenu, KeyAll keyAll) {
      this.parentMenu = parentMenu;
      this.keyAll = keyAll;
   }
}
