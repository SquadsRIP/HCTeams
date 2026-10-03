package net.frozenorb.foxtrot.gameplay.events.koth.commands.koth;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHDistCommand {
   @Command(names = {"KOTH Dist", "koth radius", "koth distance"}, permission = "foxtrot.command.koth.distance")
   public static void kothDist(Player sender, @Parameter(name = "koth") Event koth, @Parameter(name = "distance") int distance) {
      if (koth.getType() != EventType.KOTH) {
         sender.sendMessage(ChatColor.YELLOW + "Can only set distance for KOTHs");
      } else {
         ((KOTH)koth).setCapDistance(distance);
         sender.sendMessage(ChatColor.GREEN + "Successfully set the radius for the " + koth.getName() + " KOTH.");
      }
   }
}
