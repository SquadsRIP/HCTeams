package net.frozenorb.foxtrot.team.menu.manage;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.UUIDUtils;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ManageMembersMenu extends Menu {
   private Team team;

   @Override
   public String getTitle(Player player) {
      return "Manage Members";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      Map<Integer, Button> toReturn = new HashMap<>();
      int highest = (int)Math.ceil(this.team.getMembers().size() / 9);

      for (int i = 0; i < highest * 9; i++) {
         toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)15, ""));
      }

      int i = 9;

      for (final UUID member : this.team.getMembers()) {
         final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(member, true);
         if (i % 9 == 0) {
            i += 2;
         }

         toReturn.put(i++, new Button() {
            @Override
            public String getName(Player player) {
               return Neutron.getInstance().getProfileHandler().findDisplayName(member);
            }

            @Override
            public List<String> getDescription(Player player) {
               List<String> toReturnx = new ArrayList<>();
               toReturnx.add("");
               toReturnx.add(ChatColor.translate("&4&l┃ &fRole: &c" + ManageMembersMenu.this.getRole(member)));
               toReturnx.add(ChatColor.translate("&4&l┃ &fStatus: &c" + (profile.getServerProfile().isOnline() ? "&aOnline" : "&cOffline")));
               if (!profile.getServerProfile().isOnline()) {
                  toReturnx.add(ChatColor.translate("&4&l┃ &fLast Online: &c" + profile.getServerProfile().getLastSeenString()));
               }

               toReturnx.add("");
               toReturnx.add(ChatColor.RED + "Click to manage this player");
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
               return ItemBuilder.copyOf(super.getButtonItem(player)).skull(UUIDUtils.name(member)).build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
               new ManageMemberMenu(ManageMembersMenu.this.team, member, profile).openMenu(player);
            }
         });
      }

      return toReturn;
   }

   public String getRole(UUID member) {
      if (this.team.getCaptains().contains(member)) {
         return "&eCaptain";
      } else if (this.team.getColeaders().contains(member)) {
         return "&6Co-Leader";
      } else {
         return this.team.getOwner().equals(member) ? "&c&lLeader" : "&aMember";
      }
   }

   @ConstructorProperties("team")
   public ManageMembersMenu(Team team) {
      this.team = team;
   }
}
