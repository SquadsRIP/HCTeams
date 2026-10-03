package net.frozenorb.foxtrot.team.menu.roster;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.UUIDUtils;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.team.Role;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class TeamRosterMenu extends PaginatedMenu {
   private Team team;

   @Override
   public String getPrePaginatedTitle(Player player) {
      return "Roster";
   }

   @Override
   public Map<Integer, Button> getAllPagesButtons(Player player) {
      Map<Integer, Button> toReturn = new HashMap<>();

      for (final Entry<UUID, Role> entry : this.team.getRoster().entrySet()) {
         final String theName = UUIDUtils.name(entry.getKey());
         toReturn.put(
            toReturn.size(),
            new Button() {
               @Override
               public String getName(Player player) {
                  return ChatColor.DARK_RED + ChatColor.BOLD.toString() + theName;
               }

               @Override
               public List<String> getDescription(Player player) {
                  List<String> toReturnx = new ArrayList<>();
                  toReturnx.add("");
                  toReturnx.add(ChatColor.translate("&4&l┃ &fRole: &c" + entry.getValue().getRoleName()));
                  toReturnx.add("");
                  toReturnx.add(ChatColor.GREEN + "Click to manage this player's roster data");
                  return toReturnx;
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
               public ItemStack getButtonItem(Player player) {
                  return ItemBuilder.of(this.getMaterial(player))
                     .name(this.getName(player))
                     .data(this.getDamageValue(player))
                     .setLore(this.getDescription(player))
                     .skull(theName)
                     .build();
               }

               @Override
               public void clicked(Player player, int slot, ClickType clickType) {
                  new TeamManageRosterMenu(TeamRosterMenu.this.team, entry.getKey()).openMenu(player);
               }
            }
         );
      }

      return toReturn;
   }

   @Override
   public boolean isAutoUpdate() {
      return true;
   }

   @ConstructorProperties("team")
   public TeamRosterMenu(Team team) {
      this.team = team;
   }
}
