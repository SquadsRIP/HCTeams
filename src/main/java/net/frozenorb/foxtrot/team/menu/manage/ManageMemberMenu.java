package net.frozenorb.foxtrot.team.menu.manage;

import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
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

public class ManageMemberMenu extends Menu {
   private Team team;
   private UUID target;
   private Profile targetProfile;

   @Override
   public String getTitle(Player player) {
      return "Manage Members";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      HashMap<Integer, Button> toReturn = new HashMap<>();
      toReturn.put(toReturn.size(), new Button() {
         @Override
         public String getName(Player player) {
            return ChatColor.GOLD + ChatColor.BOLD.toString() + "Kick Member";
         }

         @Override
         public List<String> getDescription(Player player) {
            ArrayList<String> toReturnx = new ArrayList<>();
            toReturnx.add("");
            toReturnx.add(ChatColor.YELLOW + "Click here to kick this member");
            return toReturnx;
         }

         @Override
         public Material getMaterial(Player player) {
            return Material.REDSTONE_BLOCK;
         }
      });
      toReturn.put(toReturn.size(), new Button() {
         @Override
         public String getName(Player player) {
            return ChatColor.GOLD + ChatColor.BOLD.toString() + "Promote Member";
         }

         @Override
         public List<String> getDescription(Player player) {
            ArrayList<String> toReturn = new ArrayList<>();
            toReturn.add("");
            toReturn.add(ChatColor.YELLOW + "Click here to promote this member");
            return toReturn;
         }

         @Override
         public Material getMaterial(Player player) {
            return Material.EMERALD_BLOCK;
         }
      });
      toReturn.put(toReturn.size(), new Button() {
         @Override
         public String getName(Player player) {
            return ChatColor.GOLD + ChatColor.BOLD.toString() + "Demote Member";
         }

         @Override
         public List<String> getDescription(Player player) {
            ArrayList<String> toReturn = new ArrayList<>();
            toReturn.add("");
            toReturn.add(ChatColor.YELLOW + "Click here to demote to");
            return toReturn;
         }

         @Override
         public Material getMaterial(Player player) {
            return Material.REDSTONE_BLOCK;
         }
      });
      return toReturn;
   }

   public String getRoleBelow(UUID member) {
      if (this.team.getCaptains().contains(member)) {
         return "Member";
      } else {
         return this.team.getColeaders().contains(member) ? "Co-Leader" : "N/A";
      }
   }

   @ConstructorProperties({"team", "target", "targetProfile"})
   public ManageMemberMenu(Team team, UUID target, Profile targetProfile) {
      this.team = team;
      this.target = target;
      this.targetProfile = targetProfile;
   }
}
