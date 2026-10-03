package net.frozenorb.foxtrot.team.menu.button;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.util.Callback;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class BooleanButton extends Button {
   private boolean accept;
   private Callback<Boolean> callback;

   @Override
   public void clicked(Player player, int i, ClickType clickType) {
      if (this.accept) {
         player.playSound(player.getLocation(), Sound.NOTE_PIANO, 20.0F, 0.1F);
      } else {
         player.playSound(player.getLocation(), Sound.DIG_GRAVEL, 20.0F, 0.1F);
      }

      player.closeInventory();
      this.callback.callback(this.accept);
   }

   @Override
   public String getName(Player player) {
      return this.accept ? "§aConfirm" : "§cCancel";
   }

   @Override
   public List<String> getDescription(Player player) {
      return new ArrayList<>();
   }

   @Override
   public byte getDamageValue(Player player) {
      return (byte)(this.accept ? 5 : 14);
   }

   @Override
   public Material getMaterial(Player player) {
      return Material.WOOL;
   }

   @ConstructorProperties({"accept", "callback"})
   public BooleanButton(boolean accept, Callback<Boolean> callback) {
      this.accept = accept;
      this.callback = callback;
   }
}
