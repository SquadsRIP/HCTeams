package net.frozenorb.foxtrot.team.menu.manage.button;

import cc.fyre.proton.menu.Button;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class SettingsButton extends Button {
   private Team team;

   @Override
   public String getName(Player player) {
      return ChatColor.GOLD + ChatColor.BOLD.toString() + "Settings";
   }

   @Override
   public List<String> getDescription(Player player) {
      ArrayList<String> toReturn = new ArrayList<>();
      toReturn.add(ChatColor.GRAY + "View all current faction");
      toReturn.add(ChatColor.GRAY + "settings and modify them.");
      toReturn.add("");
      toReturn.add(ChatColor.GREEN + "Click to view & modify all settings");
      return toReturn;
   }

   @Override
   public Material getMaterial(Player player) {
      return Material.WORKBENCH;
   }

   @Override
   public void clicked(Player player, int slot, ClickType clickType) {
   }

   @ConstructorProperties("team")
   public SettingsButton(Team team) {
      this.team = team;
   }
}
