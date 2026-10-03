package net.frozenorb.foxtrot.gameplay.events.koth.commands.koth;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHLocCommand {
   @Command(names = {"KOTH loc", "koth center"}, permission = "foxtrot.command.koth.location")
   public static void kothLoc(Player sender, @Parameter(name = "koth") Event koth) {
      if (koth.getType() != EventType.KOTH) {
         sender.sendMessage(ChatColor.YELLOW + "Unable to set location for a non-KOTH event.");
      } else {
         ((KOTH)koth).setLocation(sender.getLocation());
         sender.sendMessage(ChatColor.GREEN + "Successfully set the cap location for the " + koth.getName() + " KOTH.");
      }
   }
}
