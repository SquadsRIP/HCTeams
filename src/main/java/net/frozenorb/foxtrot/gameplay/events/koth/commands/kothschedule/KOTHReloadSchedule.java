package net.frozenorb.foxtrot.gameplay.events.koth.commands.kothschedule;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.controlpanel.EventRewardsHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class KOTHReloadSchedule {
   @Command(names = "KOTHSchedule Reload", permission = "foxtrot.koth.admin")
   public static void kothScheduleReload(CommandSender sender) {
      EventRewardsHandler.loadConfig();
      Foxtrot.getInstance().getEventHandler().loadSchedules();
      sender.sendMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.YELLOW + "Reloaded the KOTH schedule.");
   }
}
