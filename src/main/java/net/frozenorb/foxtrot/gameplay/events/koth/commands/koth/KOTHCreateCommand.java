package net.frozenorb.foxtrot.gameplay.events.koth.commands.koth;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KOTHCreateCommand {
   @Command(names = "KOTH Create", permission = "foxtrot.koth.admin")
   public static void kothCreate(Player sender, @Parameter(name = "koth") String koth) {
      new KOTH(koth, sender.getLocation());
      sender.sendMessage(ChatColor.GRAY + "Created a KOTH named " + koth + ".");
   }
}
