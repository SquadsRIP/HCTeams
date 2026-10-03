package net.frozenorb.foxtrot.gameplay.events.koth.commands.koth;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.util.TimeUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import net.frozenorb.foxtrot.gameplay.events.dtc.DTC;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHListCommand {
   @Command(names = {"KOTH List", "events list", "event list"}, permission = "foxtrot.koth")
   public static void kothList(Player sender) {
      if (Foxtrot.getInstance().getEventHandler().getEvents().isEmpty()) {
         sender.sendMessage(ChatColor.YELLOW + "There aren't any events set.");
      } else {
         for (Event event : Foxtrot.getInstance().getEventHandler().getEvents()) {
            if (event.getType() == EventType.KOTH) {
               KOTH koth = (KOTH)event;
               sender.sendMessage(
                  (koth.isHidden() ? ChatColor.DARK_GRAY + "[H] " : "")
                     + (koth.isActive() ? ChatColor.GREEN : ChatColor.YELLOW)
                     + koth.getName()
                     + ChatColor.WHITE
                     + " - "
                     + ChatColor.GRAY
                     + TimeUtils.formatIntoMMSS(koth.getRemainingCapTime())
                     + ChatColor.DARK_GRAY
                     + "/"
                     + ChatColor.GRAY
                     + TimeUtils.formatIntoMMSS(koth.getCapTime())
                     + " "
                     + ChatColor.WHITE
                     + "- "
                     + ChatColor.GRAY
                     + (koth.getCurrentCapper() == null ? "None" : koth.getCurrentCapper())
               );
            } else if (event.getType() == EventType.DTC) {
               DTC dtc = (DTC)event;
               sender.sendMessage(
                  (dtc.isHidden() ? ChatColor.DARK_GRAY + "[H] " : "")
                     + (dtc.isActive() ? ChatColor.GREEN : ChatColor.YELLOW)
                     + dtc.getName()
                     + ChatColor.WHITE
                     + " - "
                     + ChatColor.GRAY
                     + "P: "
                     + dtc.getCurrentPoints()
               );
            }
         }
      }
   }
}
