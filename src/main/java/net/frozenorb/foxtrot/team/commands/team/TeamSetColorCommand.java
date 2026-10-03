package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamSetColorCommand {
   @Command(names = {"team setcolor", "t setcolor", "f setcolor", "faction setcolor", "fac setcolor"}, permission = "op")
   public static void setColor(Player sender, @Parameter(name = "faction") String factionName, @Parameter(name = "color") String colorCode) {
      if (factionName == null || factionName.trim().isEmpty()) {
         sender.sendMessage(ChatColor.RED + "Usage: /f setcolor <faction name> <color code>");
      } else if (colorCode != null && !colorCode.trim().isEmpty()) {
         if (!(colorCode = colorCode.trim()).startsWith("&") && colorCode.length() == 1) {
            colorCode = "&" + colorCode;
         }

         if (colorCode.length() >= 2 && colorCode.charAt(0) == '&') {
            char colorChar = Character.toLowerCase(colorCode.charAt(1));
            if ("0123456789abcdeflnokr".indexOf(colorChar) == -1) {
               sender.sendMessage(ChatColor.RED + "Invalid color code. Valid codes: &0-&9, &a-&f, &l, &n, &o, &k, &r");
            } else {
               Team team = Foxtrot.getInstance().getTeamHandler().getTeam(factionName);
               if (team == null) {
                  sender.sendMessage(ChatColor.RED + "No faction found with name '" + factionName + "'.");
               } else if (team.getOwner() != null) {
                  sender.sendMessage(ChatColor.RED + "You can only set colors on system factions (owner = null).");
                  sender.sendMessage(ChatColor.GRAY + "Use /flag add to convert a player faction to a system faction first.");
               } else {
                  String strippedName = ChatColor.stripColor(team.getName());
                  String newName = colorCode + strippedName;
                  if (team.getName().startsWith("&") || team.getName().startsWith("§")) {
                     newName = colorCode + strippedName;
                  }

                  team.setName(newName);
                  team.flagForSave();
                  sender.sendMessage(
                     ChatColor.GREEN
                        + "Set color of '"
                        + ChatColor.WHITE
                        + strippedName
                        + ChatColor.GREEN
                        + "' to "
                        + ChatColor.translateAlternateColorCodes('&', colorCode)
                        + colorCode
                        + ChatColor.GREEN
                        + "."
                  );
               }
            }
         } else {
            sender.sendMessage(ChatColor.RED + "Invalid color code. Use format like &6, &c, &4, &5, &b, etc.");
         }
      } else {
         sender.sendMessage(ChatColor.RED + "Usage: /f setcolor <faction name> <color code>");
      }
   }
}
