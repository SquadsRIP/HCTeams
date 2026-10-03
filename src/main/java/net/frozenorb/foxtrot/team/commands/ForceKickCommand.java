package net.frozenorb.foxtrot.team.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ForceKickCommand {
   @Command(names = "forcekick", permission = "foxtrot.forcekick")
   public static void forceKick(Player sender, @Parameter(name = "player") UUID player) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
      if (team == null) {
         sender.sendMessage(ChatColor.YELLOW + UUIDUtils.name(player) + " is not on a team!");
      } else if (team.getMembers().size() == 1) {
         sender.sendMessage(ChatColor.YELLOW + UUIDUtils.name(player) + "'s team has one member. Please use /forcedisband to perform this action.");
      } else {
         team.removeMember(player);
         Foxtrot.getInstance().getTeamHandler().setTeam(player, null);
         Player bukkitPlayer = Bukkit.getPlayer(player);
         if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
            LunarClientListener.updateNametag(bukkitPlayer);
            bukkitPlayer.sendMessage(ChatColor.YELLOW + "You were kicked from your team by a staff member.");
         }

         sender.sendMessage(
            ChatColor.YELLOW
               + "Force kicked "
               + ChatColor.LIGHT_PURPLE
               + UUIDUtils.name(player)
               + ChatColor.YELLOW
               + " from their team, "
               + ChatColor.LIGHT_PURPLE
               + team.getName()
               + ChatColor.YELLOW
               + "."
         );
      }
   }
}
