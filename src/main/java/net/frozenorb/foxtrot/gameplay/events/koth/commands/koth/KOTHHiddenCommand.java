package net.frozenorb.foxtrot.gameplay.events.koth.commands.koth;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.gameplay.events.Event;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHHiddenCommand {
   @Command(names = {"KOTH Hidden", "events hidden", "event hidden"}, permission = "foxtrot.koth.admin")
   public static void kothHidden(Player sender, @Parameter(name = "koth") Event koth, @Parameter(name = "hidden") boolean hidden) {
      koth.setHidden(hidden);
      sender.sendMessage(ChatColor.GRAY + "Set visibility for the " + koth.getName() + " event.");
   }
}
