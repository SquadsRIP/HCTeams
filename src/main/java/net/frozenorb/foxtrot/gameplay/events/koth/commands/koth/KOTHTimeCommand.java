package net.frozenorb.foxtrot.gameplay.events.koth.commands.koth;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHTimeCommand {
   @Command(names = "KOTH Time", permission = "foxtrot.koth.admin")
   public static void kothTime(Player sender, @Parameter(name = "koth") Event koth, @Parameter(name = "time") float time) {
      if (time > 20.0F) {
         sender.sendMessage(
            ChatColor.YELLOW.toString()
               + ChatColor.BOLD
               + "This command was changed! The time parameter is now in minutes, not seconds. For example, to set a KOTH's capture time to 20 minutes 30 seconds, use /koth time 20.5"
         );
      }

      if (koth.getType() != EventType.KOTH) {
         sender.sendMessage(ChatColor.YELLOW + "Unable to modify cap time for a non-KOTH event.");
      } else {
         ((KOTH)koth).setCapTime((int)(time * 60.0F));
         sender.sendMessage(ChatColor.GRAY + "Set cap time for the " + koth.getName() + " KOTH.");
      }
   }
}
