package net.frozenorb.foxtrot.gameplay.events.koth.commands.koth;

import cc.fyre.proton.command.Command;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.EventScheduledTime;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHScheduleCommand {
   public static final DateFormat KOTH_DATE_FORMAT = new SimpleDateFormat("EEE h:mm a");

   @Command(names = "KOTH Schedule", permission = "")
   public static void kothSchedule(Player sender) {
      int sent = 0;
      Date now = new Date();

      for (Entry<EventScheduledTime, String> entry : Foxtrot.getInstance().getEventHandler().getEventSchedule().entrySet()) {
         Event resolved = Foxtrot.getInstance().getEventHandler().getEvent(entry.getValue());
         if (resolved != null && !resolved.isHidden() && entry.getKey().toDate().after(now) && resolved.getType() == EventType.KOTH) {
            if (sent > 5) {
               break;
            }

            sent++;
            sender.sendMessage(
               ChatColor.GOLD
                  + "[KingOfTheHill] "
                  + ChatColor.YELLOW
                  + entry.getValue()
                  + ChatColor.GOLD
                  + " can be captured at "
                  + ChatColor.BLUE
                  + KOTH_DATE_FORMAT.format(entry.getKey().toDate())
                  + ChatColor.GOLD
                  + "."
            );
         }
      }

      if (sent == 0) {
         sender.sendMessage(ChatColor.DARK_RED + "[KingOfTheHill] " + ChatColor.YELLOW + "KOTH Schedule: " + ChatColor.GRAY + "Every 2 hours");
         sender.sendMessage(
            ChatColor.GRAY
               + "Rotation: "
               + ChatColor.DARK_RED
               + "Spider"
               + ChatColor.GRAY
               + ", "
               + ChatColor.DARK_RED
               + "Mushroom"
               + ChatColor.GRAY
               + ", "
               + ChatColor.DARK_RED
               + "Hell"
               + ChatColor.GRAY
               + ", "
               + ChatColor.DARK_RED
               + "End"
         );
      } else {
         sender.sendMessage(
            ChatColor.GOLD
               + "[KingOfTheHill] "
               + ChatColor.YELLOW
               + "It is currently "
               + ChatColor.BLUE
               + KOTH_DATE_FORMAT.format(new Date())
               + ChatColor.GOLD
               + "."
         );
      }
   }
}
