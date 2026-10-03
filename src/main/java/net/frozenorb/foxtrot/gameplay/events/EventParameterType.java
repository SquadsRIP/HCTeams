package net.frozenorb.foxtrot.gameplay.events;

import cc.fyre.proton.command.param.ParameterType;
import com.mysql.jdbc.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EventParameterType implements ParameterType<Event> {
   public Event transform(CommandSender sender, String source) {
      if (source.equals("active")) {
         for (Event event : Foxtrot.getInstance().getEventHandler().getEvents()) {
            if (event.isActive() && !event.isHidden()) {
               return event;
            }
         }

         sender.sendMessage(ChatColor.YELLOW + "There is no active Event at the moment.");
         return null;
      } else {
         Event event = Foxtrot.getInstance().getEventHandler().getEvent(source);
         if (event == null) {
            sender.sendMessage(ChatColor.YELLOW + "No Event with the name " + source + " found.");
            return null;
         } else {
            return event;
         }
      }
   }

   @Override
   public List<String> tabComplete(Player sender, Set<String> flags, String source) {
      ArrayList<String> completions = new ArrayList<>();

      for (Event event : Foxtrot.getInstance().getEventHandler().getEvents()) {
         if (StringUtils.startsWithIgnoreCase(event.getName(), source)) {
            completions.add(event.getName());
         }
      }

      return completions;
   }
}
