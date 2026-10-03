package net.frozenorb.foxtrot.team.commands.pvp;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.util.TimeUtils;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PvPTimeCommand {
   @Command(names = {"pvptimer time", "timer time", "pvp time"}, permission = "")
   public static void pvpTime(Player sender) {
      if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(sender.getUniqueId())) {
         sender.sendMessage(
            ChatColor.YELLOW
               + "You have "
               + TimeUtils.formatIntoMMSS(Foxtrot.getInstance().getPvPTimerMap().getSecondsRemaining(sender.getUniqueId()))
               + " left on your PVP Timer."
         );
      } else {
         sender.sendMessage(ChatColor.YELLOW + "You do not have a PVP Timer on!");
      }
   }
}
