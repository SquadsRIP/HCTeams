package net.frozenorb.foxtrot.gameplay.events.koth.commands.koth;

import cc.fyre.proton.command.Command;
import java.util.Date;
import java.util.Map.Entry;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.EventScheduledTime;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHCommand {
   @Command(names = {"Event", "Event Next", "Event Info", "koth", "koth next", "koth info"}, permission = "")
   public static void koth(Player sender) {
      for (Event koth : Foxtrot.getInstance().getEventHandler().getEvents()) {
         if (!koth.isHidden() && koth.isActive()) {
            FancyMessage fm = new FancyMessage("[Events] ").color(ChatColor.GOLD).then(koth.getName()).color(ChatColor.YELLOW).style(ChatColor.UNDERLINE);
            if (koth instanceof KOTH) {
               fm.tooltip(ChatColor.YELLOW.toString() + ((KOTH)koth).getCapLocation().getBlockX() + ", " + ((KOTH)koth).getCapLocation().getBlockZ());
            }

            fm.color(ChatColor.YELLOW).then(" can be contested now.").color(ChatColor.GOLD);
            fm.send(sender);
            return;
         }
      }

      Date now = new Date();

      for (Entry<EventScheduledTime, String> entry : Foxtrot.getInstance().getEventHandler().getEventSchedule().entrySet()) {
         if (entry.getKey().toDate().after(now)) {
            sender.sendMessage(
               ChatColor.GOLD
                  + "[KingOfTheHill] "
                  + ChatColor.YELLOW
                  + entry.getValue()
                  + ChatColor.GOLD
                  + " can be captured at "
                  + ChatColor.BLUE
                  + KOTHScheduleCommand.KOTH_DATE_FORMAT.format(entry.getKey().toDate())
                  + ChatColor.GOLD
                  + "."
            );
            sender.sendMessage(
               ChatColor.GOLD
                  + "[KingOfTheHill] "
                  + ChatColor.YELLOW
                  + "It is currently "
                  + ChatColor.BLUE
                  + KOTHScheduleCommand.KOTH_DATE_FORMAT.format(now)
                  + ChatColor.GOLD
                  + "."
            );
            sender.sendMessage(ChatColor.YELLOW + "Type '/koth schedule' to see more upcoming Events.");
            return;
         }
      }

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
   }
}
