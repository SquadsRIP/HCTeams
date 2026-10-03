package net.frozenorb.foxtrot.team.menu.manage.button;

import cc.fyre.proton.menu.Button;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.menu.manage.ManageMembersMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class MemberButton extends Button {
   private Team team;

   @Override
   public String getName(Player player) {
      return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Members";
   }

   @Override
   public List<String> getDescription(Player player) {
      List<String> toReturn = new ArrayList<>();
      toReturn.add(ChatColor.GRAY + "View all current faction");
      toReturn.add(ChatColor.GRAY + "members and manage them.");
      toReturn.add("");
      toReturn.add(ChatColor.translate("&4&l┃ &fOnline Members: &c" + this.team.getOnlineMemberAmount() + "/" + this.team.getMembers().size()));
      toReturn.add("");
      toReturn.add(ChatColor.GREEN + "Click to manage all members");
      return toReturn;
   }

   @Override
   public Material getMaterial(Player player) {
      return Material.SKULL_ITEM;
   }

   @Override
   public byte getDamageValue(Player player) {
      return 3;
   }

   @Override
   public void clicked(Player player, int slot, ClickType clickType) {
      new ManageMembersMenu(this.team).openMenu(player);
   }

   @ConstructorProperties("team")
   public MemberButton(Team team) {
      this.team = team;
   }
}
