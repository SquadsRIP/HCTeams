package net.frozenorb.foxtrot.team;

import cc.fyre.proton.command.param.ParameterType;
import cc.fyre.proton.util.UUIDUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.frozenorb.foxtrot.Foxtrot;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamType implements ParameterType<Team> {
   public Team transform(CommandSender sender, String source) {
      if (!(sender instanceof Player) || !source.equalsIgnoreCase("self") && !source.equals("")) {
         Team byName = Foxtrot.getInstance().getTeamHandler().getTeam(source);
         if (byName != null) {
            return byName;
         }

         Player bukkitPlayer = Foxtrot.getInstance().getServer().getPlayer(source);
         if (bukkitPlayer != null) {
            Team byMemberBukkitPlayer = Foxtrot.getInstance().getTeamHandler().getTeam(bukkitPlayer.getUniqueId());
            if (byMemberBukkitPlayer != null) {
               return byMemberBukkitPlayer;
            }
         }

         Team byMemberUUID = Foxtrot.getInstance().getTeamHandler().getTeam(UUIDUtils.uuid(source));
         if (byMemberUUID != null) {
            return byMemberUUID;
         }

         sender.sendMessage(ChatColor.RED + "No team or member with the name " + source + " found.");
         return null;
      } else {
         Team team = Foxtrot.getInstance().getTeamHandler().getTeam(((Player)sender).getUniqueId());
         if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You're not on a team!");
            return null;
         } else {
            return team;
         }
      }
   }

   @Override
   public List<String> tabComplete(Player sender, Set<String> flags, String source) {
      List<String> completions = new ArrayList<>();
      if (source.isEmpty()) {
         return new ArrayList<>();
      }

      if (!flags.contains("noteams")) {
         for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
            if (StringUtils.startsWithIgnoreCase(team.getName(), source)) {
               completions.add(team.getName());
            }
         }
      }

      if (flags.contains("players")) {
         for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (StringUtils.startsWithIgnoreCase(player.getName(), source)) {
               completions.add(player.getName());
            }
         }
      }

      return completions;
   }
}
