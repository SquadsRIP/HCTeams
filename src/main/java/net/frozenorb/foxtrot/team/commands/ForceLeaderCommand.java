package net.frozenorb.foxtrot.team.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ForceLeaderCommand {
   @Command(names = "ForceLeader", permission = "foxtrot.forceleader")
   public static void forceLeader(Player sender, @Parameter(name = "player", defaultValue = "self") UUID player) {
      Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player);
      if (playerTeam == null) {
         sender.sendMessage(ChatColor.GRAY + "That player is not on a team.");
      } else {
         Player bukkitPlayer = Bukkit.getPlayer(player);
         if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
            bukkitPlayer.sendMessage(ChatColor.YELLOW + "A staff member has made you leader of §b" + playerTeam.getName() + "§e.");
         }

         playerTeam.setOwner(player);
         sender.sendMessage(
            ChatColor.LIGHT_PURPLE
               + UUIDUtils.name(player)
               + ChatColor.YELLOW
               + " is now the owner of "
               + ChatColor.LIGHT_PURPLE
               + playerTeam.getName()
               + ChatColor.YELLOW
               + "."
         );
      }
   }
}
