package net.frozenorb.foxtrot.team.menu.button;

import cc.fyre.proton.menu.Button;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.commands.team.TeamShadowMuteCommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MuteButton extends Button {
   private int minutes;
   private Team team;

   @Override
   public void clicked(Player player, int i, ClickType clickType) {
      TeamShadowMuteCommand.teamShadowMute(player, this.team, this.minutes);
   }

   @Override
   public String getName(Player player) {
      return "§e" + this.minutes + "m mute";
   }

   @Override
   public List<String> getDescription(Player player) {
      return new ArrayList<>();
   }

   @Override
   public byte getDamageValue(Player player) {
      return 0;
   }

   @Override
   public ItemStack getButtonItem(Player player) {
      ItemStack it = new ItemStack(this.getMaterial(player));
      ItemMeta im = it.getItemMeta();
      im.setLore(this.getDescription(player));
      im.setDisplayName(this.getName(player));
      it.setItemMeta(im);
      it.setAmount(this.minutes);
      return it;
   }

   @Override
   public Material getMaterial(Player player) {
      return Material.CHEST;
   }

   @ConstructorProperties({"minutes", "team"})
   public MuteButton(int minutes, Team team) {
      this.minutes = minutes;
      this.team = team;
   }
}
