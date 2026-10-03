package net.frozenorb.foxtrot.team.menu.roster;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.foxtrot.team.Role;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class TeamManageRosterMenu extends Menu {
   private Team team;
   private UUID uuid;

   @Override
   public String getTitle(Player player) {
      return "Manage";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      Map<Integer, Button> toReturn = new HashMap<>();
      if (!this.team.getRoster().containsKey(this.uuid)) {
         player.closeInventory();
         return new HashMap<>();
      }

      final Role role = this.team.getRoster().get(this.uuid);
      final Role nextRole = Arrays.stream(Role.values()).filter(it -> it.getNumber() == role.getNumber() + 1).findFirst().orElse(Role.MEMBER);

      for (int i = 0; i < 27; i++) {
         toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)14, ""));
      }

      toReturn.put(11, new Button() {
         @Override
         public String getName(Player player) {
            return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Manage Role";
         }

         @Override
         public List<String> getDescription(Player player) {
            List<String> toReturnx = new ArrayList<>();
            toReturnx.add(ChatColor.GRAY + "Manage this player's role");
            toReturnx.add("");
            toReturnx.add(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Roles");

            for (Role value : Role.values()) {
               if (value == role) {
                  toReturnx.add(ChatColor.translate("&4&l┃ &c" + value.getRoleName()));
               } else {
                  toReturnx.add(ChatColor.translate("&4&l┃ &f" + value.getRoleName()));
               }
            }

            toReturnx.add("");
            toReturnx.add(ChatColor.GREEN + "Click to switch their role");
            return toReturnx;
         }

         @Override
         public Material getMaterial(Player player) {
            return Material.GOLD_INGOT;
         }

         @Override
         public void clicked(Player player, int slot, ClickType clickType) {
            TeamManageRosterMenu.this.team.getRoster().put(TeamManageRosterMenu.this.uuid, nextRole);
         }
      });
      toReturn.put(13, new Button() {
         @Override
         public String getName(Player player) {
            return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Go Back";
         }

         @Override
         public List<String> getDescription(Player player) {
            return new ArrayList<>();
         }

         @Override
         public Material getMaterial(Player player) {
            return Material.ARROW;
         }

         @Override
         public void clicked(Player player, int slot, ClickType clickType) {
            player.closeInventory();
            new TeamRosterMenu(TeamManageRosterMenu.this.team).openMenu(player);
         }
      });
      toReturn.put(15, new Button() {
         @Override
         public String getName(Player player) {
            return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Remove From Roster";
         }

         @Override
         public List<String> getDescription(Player player) {
            List<String> toReturn = new ArrayList<>();
            toReturn.add(ChatColor.GRAY + "Remove player from Roster");
            toReturn.add("");
            toReturn.add(ChatColor.GREEN + "Click to remove this player from the roster");
            return toReturn;
         }

         @Override
         public Material getMaterial(Player player) {
            return Material.TNT;
         }

         @Override
         public void clicked(Player player, int slot, ClickType clickType) {
            TeamManageRosterMenu.this.team.getRoster().remove(TeamManageRosterMenu.this.uuid);
            player.closeInventory();
            new TeamRosterMenu(TeamManageRosterMenu.this.team).openMenu(player);
         }
      });
      return toReturn;
   }

   @Override
   public boolean isUpdateAfterClick() {
      return true;
   }

   @ConstructorProperties({"team", "uuid"})
   public TeamManageRosterMenu(Team team, UUID uuid) {
      this.team = team;
      this.uuid = uuid;
   }
}
