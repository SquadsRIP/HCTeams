package net.frozenorb.foxtrot.team.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.VisualClaim;
import net.frozenorb.foxtrot.team.claims.VisualClaimType;
import net.frozenorb.foxtrot.team.commands.team.TeamBaseCommand;
import net.frozenorb.foxtrot.team.commands.team.TeamFallTrapCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.scheduler.BukkitRunnable;

public class BlueprintMenu extends Menu {
   @Override
   public String getTitle(Player player) {
      return "Blueprints";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      Map<Integer, Button> toReturn = new HashMap<>();
      toReturn.put(11, new Button() {
         @Override
         public String getName(Player player) {
            return ChatColor.translate("&4&lBase");
         }

         @Override
         public List<String> getDescription(Player player) {
            List<String> toReturnx = new ArrayList<>();
            toReturnx.add(ChatColor.GRAY + "Get a base wand, select two positions");
            toReturnx.add(ChatColor.GRAY + "then shift left click to confirm the");
            toReturnx.add(ChatColor.GRAY + "two ends of the base. Then select the glass");
            toReturnx.add(ChatColor.GRAY + "color you want for your base and watch it go!");
            toReturnx.add("");
            toReturnx.add(ChatColor.translate("&4&l┃ &fCost: &c1 Base Token (&6⛃&e100 Coins&c)"));
            toReturnx.add("");
            toReturnx.add(ChatColor.translate("&aClick here to get a base claim wand"));
            return toReturnx;
         }

         @Override
         public Material getMaterial(Player player) {
            return Material.STAINED_GLASS;
         }

         @Override
         public void clicked(final Player sender, int slot, ClickType clickType) {
            Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
            if (team == null) {
               sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            } else {
               if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId()) && !team.isCaptain(sender.getUniqueId())) {
                  sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
               } else {
                  sender.getInventory().remove(TeamBaseCommand.SELECTION_WAND);
                  (new BukkitRunnable() {
                     @Override
                     public void run() {
                        sender.getInventory().addItem(TeamBaseCommand.SELECTION_WAND.clone());
                     }
                  }).runTaskLater(Foxtrot.getInstance(), 1L);
                  new VisualClaim(sender, VisualClaimType.BASE, false).draw(false);
                  if (!VisualClaim.getCurrentMaps().containsKey(sender.getName())) {
                     new VisualClaim(sender, VisualClaimType.MAP, false).draw(true);
                  }
               }
            }
         }
      });
      toReturn.put(15, new Button() {
         @Override
         public String getName(Player player) {
            return ChatColor.translate("&4&lFaller");
         }

         @Override
         public List<String> getDescription(Player player) {
            List<String> toReturn = new ArrayList<>();
            toReturn.add(ChatColor.GRAY + "Get a faller wand, select two positions");
            toReturn.add(ChatColor.GRAY + "then shift left click to confirm the");
            toReturn.add(ChatColor.GRAY + "two ends of the faller. Then select the outline");
            toReturn.add(ChatColor.GRAY + "you want for your fall trap and watch it generate!");
            toReturn.add("");
            toReturn.add(ChatColor.translate("&4&l┃ &fCost: &c1 Faller Token (&6⛃&e200 Coins&c)"));
            toReturn.add("");
            toReturn.add(ChatColor.translate("&aClick here to get a base claim wand"));
            return toReturn;
         }

         @Override
         public Material getMaterial(Player player) {
            return Material.HOPPER;
         }

         @Override
         public void clicked(final Player sender, int slot, ClickType clickType) {
            Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
            if (team == null) {
               sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            } else {
               if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId()) && !team.isCaptain(sender.getUniqueId())) {
                  sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
               } else {
                  sender.getInventory().remove(TeamFallTrapCommand.SELECTION_WAND);
                  (new BukkitRunnable() {
                     @Override
                     public void run() {
                        sender.getInventory().addItem(TeamFallTrapCommand.SELECTION_WAND.clone());
                     }
                  }).runTaskLater(Foxtrot.getInstance(), 1L);
                  new VisualClaim(sender, VisualClaimType.FALL_TRAP, false).draw(false);
                  if (!VisualClaim.getCurrentMaps().containsKey(sender.getName())) {
                     new VisualClaim(sender, VisualClaimType.MAP, false).draw(true);
                  }
               }
            }
         }
      });
      return toReturn;
   }

   @Override
   public int size(Player player) {
      return 27;
   }

   @Override
   public boolean isPlaceholder() {
      return true;
   }
}
