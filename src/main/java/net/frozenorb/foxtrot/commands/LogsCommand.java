package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.TimeUtils;
import cc.fyre.proton.util.UUIDUtils;
import java.util.Date;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LogsCommand {
   @Command(names = {"logs"}, permission = "foxtrot.command.logs")
   public static void logs(CommandSender sender, @Parameter(name = "player") UUID uuid) {
      Foxtrot foxtrot = Foxtrot.getInstance();
      long firstJoin = foxtrot.getFirstJoinMap().getFirstJoin(uuid);
      long lastJoin = foxtrot.getLastJoinMap().getLastJoin(uuid);
      if (firstJoin == 0L && lastJoin == 0L && !foxtrot.getPlaytimeMap().hasPlayed(uuid)) {
         sender.sendMessage(ChatColor.RED + UUIDUtils.name(uuid) + " has never played on this server.");
         return;
      }

      Player target = foxtrot.getServer().getPlayer(uuid);
      boolean online = target != null && (!(sender instanceof Player) || ((Player)sender).canSee(target));
      long session = online ? foxtrot.getPlaytimeMap().getCurrentSession(uuid) / 1000L : 0L;
      long playtime = foxtrot.getPlaytimeMap().getPlaytime(uuid) + session;
      Team team = foxtrot.getTeamHandler().getTeam(uuid);

      sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-----------------------------------");
      sender.sendMessage(ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + UUIDUtils.name(uuid) + ChatColor.YELLOW + "'s logs");
      sender.sendMessage(
         ChatColor.YELLOW + "Status: " + (online ? ChatColor.GREEN + "Online" + ChatColor.GRAY + " (session " + TimeUtils.formatIntoDetailedString((int)session) + ")" : ChatColor.RED + "Offline")
      );
      sender.sendMessage(ChatColor.YELLOW + "First joined: " + formatDate(firstJoin));
      sender.sendMessage(ChatColor.YELLOW + "Last joined: " + formatDate(lastJoin));
      sender.sendMessage(ChatColor.YELLOW + "Playtime: " + ChatColor.WHITE + TimeUtils.formatIntoDetailedString((int)playtime));
      sender.sendMessage(ChatColor.YELLOW + "Messages sent: " + ChatColor.WHITE + foxtrot.getChatMessagesMap().getMessages(uuid));
      sender.sendMessage(ChatColor.YELLOW + "Team: " + ChatColor.WHITE + (team == null ? "None" : team.getName()));
      sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-----------------------------------");
   }

   private static String formatDate(long time) {
      if (time == 0L) {
         return ChatColor.GRAY + "Unknown";
      }

      return ChatColor.WHITE
         + TimeUtils.formatIntoCalendarString(new Date(time))
         + ChatColor.GRAY
         + " ("
         + TimeUtils.formatIntoDetailedString((int)((System.currentTimeMillis() - time) / 1000L))
         + " ago)";
   }
}
