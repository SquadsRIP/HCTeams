package net.frozenorb.foxtrot.gameplay.lettingIn.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.TimeUtils;
import java.util.ArrayList;
import java.util.List;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.lettingIn.menu.LettingInMenu;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class LettingInCommand {
   @Command(names = {"lettingin", "iamlettingin"}, permission = "")
   public static void execute(Player player, @Parameter(name = "base height") int baseHeight) {
      if (Foxtrot.getInstance().getServerHandler().isTeams()) {
         player.sendMessage(ChatColor.RED + "This feature is disabled on " + Foxtrot.getInstance().getServerHandler().getServerName() + ".");
      } else {
         Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
         if (team == null) {
            player.sendMessage(ChatColor.GRAY + "You are not in a faction!");
         } else if (team.getHQ() == null) {
            player.sendMessage(ChatColor.GRAY + "You have no HQ set!");
         } else if (team.isRaidable()) {
            player.sendMessage(ChatColor.RED + "Your team is raidable!");
         } else if (baseHeight <= 1) {
            player.sendMessage(ChatColor.RED + "You must chose a base height over 1!");
         } else if (baseHeight > 256) {
            player.sendMessage(ChatColor.RED + "Nice lie!");
         } else if (Foxtrot.getInstance().getLettingInHandler().getCache().containsKey(team.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are already on the letting in list!");
         } else {
            team.sendMessage(ChatColor.GREEN + "Your team has been put on the letting in list.");
            team.playSound(Sound.LEVEL_UP);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.translate("&4&l" + team.getName() + ":"));
            lore.add(ChatColor.translate("&4&l┃ &fHQ: " + ChatColor.RED + team.getHQ().getBlockX() + ", " + team.getHQ().getBlockZ()));
            lore.add(ChatColor.translate("&4&l┃ &fBase Height: " + ChatColor.RED + baseHeight + " blocks"));
            lore.add(ChatColor.translate("&4&l┃ &fMembers Online: " + ChatColor.RED + team.getOnlineMembers().size() + "/" + team.getMembers().size()));
            lore.add(ChatColor.translate("&4&l┃ &fDeaths Until Raidable: " + team.formatDTR()));
            if (DTRHandler.isOnCooldown(team)) {
               lore.add(
                  ChatColor.translate(
                     "&4&l┃ &fRegen: &c" + TimeUtils.formatIntoDetailedString((int)(team.getDTRCooldown() - System.currentTimeMillis()) / 1000)
                  )
               );
            }

            lore.add("");
            lore.add(ChatColor.RED + "Click to focus this faction");

            for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
               FancyMessage fancyMessage = new FancyMessage(
                  ChatColor.translate(
                     "&4&lLetting In &8┃ &f"
                        + team.getName(onlinePlayer)
                        + " &7is letting in! &f"
                        + team.getOnlineMemberAmount()
                        + " &7on &f"
                        + team.formatDTR()
                        + " &7DTR! &a[Hover]"
                  )
               );
               fancyMessage.tooltip(lore);
               fancyMessage.command("/f focus " + team.getName());
               fancyMessage.send(onlinePlayer);
            }

            Foxtrot.getInstance().getLettingInHandler().getCache().put(team.getUniqueId(), baseHeight);
         }
      }
   }

   @Command(names = {"lettingin list", "lettingin gui", "whoslettingin", "lettingin menu", "lettingin who", "lettingin show"}, permission = "")
   public static void list(Player player) {
      if (Foxtrot.getInstance().getServerHandler().isTeams()) {
         player.sendMessage(ChatColor.RED + "This feature is disabled on " + Foxtrot.getInstance().getServerHandler().getServerName() + ".");
      } else {
         new LettingInMenu().openMenu(player);
      }
   }
}
