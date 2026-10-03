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

public class StatisticsButton extends Button {
   private Team team;

   @Override
   public String getName(Player player) {
      return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Statistics";
   }

   @Override
   public List<String> getDescription(Player player) {
      List<String> toReturn = new ArrayList<>();
      toReturn.add(ChatColor.GRAY + "View all current faction");
      toReturn.add(ChatColor.GRAY + "statistics and rankings.");
      toReturn.add("");
      toReturn.add(ChatColor.translate("&4&l┃ &fKills: &c" + this.team.getKills()));
      toReturn.add(ChatColor.translate("&4&l┃ &fDeaths: &c" + this.team.getDeaths()));
      toReturn.add(ChatColor.translate("&4&l┃ &fKOTH Captures: &c" + this.team.getKothCaptures()));
      toReturn.add("");
      toReturn.add(ChatColor.GREEN + "Click to view all statistics and rankings");
      return toReturn;
   }

   @Override
   public Material getMaterial(Player player) {
      return Material.PAPER;
   }

   @Override
   public void clicked(Player player, int slot, ClickType clickType) {
   }

   @ConstructorProperties("team")
   public StatisticsButton(Team team) {
      this.team = team;
   }
}
