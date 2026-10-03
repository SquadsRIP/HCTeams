package net.frozenorb.foxtrot.team.menu.button;

import cc.fyre.proton.menu.Button;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class DTRButton extends Button {
   Team team;
   boolean increase;

   @Override
   public void clicked(Player player, int i, ClickType clickType) {
      if (!this.increase && this.team.getDTR() - 1.0 <= 0.0 && !player.hasPermission("foxtrot.dtr.setraidable")) {
         player.sendMessage(ChatColor.YELLOW + "You don't have permission to set teams as raidable. This has been logged.");
      } else if (this.increase && this.team.getMaxDTR() < this.team.getDTR() + 1.0) {
         player.sendMessage(ChatColor.YELLOW + "This would put the team above their maximum DTR. This has been logged.");
      } else {
         if (this.increase) {
            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 20.0F, 0.1F);
            this.team.setDTR(this.team.getDTR() + 1.0, player);
         } else {
            this.team.setDTR(this.team.getDTR() - 1.0, player);
            player.playSound(player.getLocation(), Sound.DIG_GRAVEL, 20.0F, 0.1F);
         }

         player.closeInventory();
      }
   }

   @Override
   public String getName(Player player) {
      return this.increase ? "§aIncrease by 1.0" : "§cDecrease by 1.0";
   }

   @Override
   public List<String> getDescription(Player player) {
      return new ArrayList<>();
   }

   @Override
   public byte getDamageValue(Player player) {
      return (byte)(this.increase ? 5 : 14);
   }

   @Override
   public Material getMaterial(Player player) {
      return Material.WOOL;
   }

   @ConstructorProperties({"team", "increase"})
   public DTRButton(Team team, boolean increase) {
      this.team = team;
      this.increase = increase;
   }
}
