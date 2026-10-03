package net.frozenorb.foxtrot.gameplay.events.dtc.commands;

import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.gameplay.events.dtc.DTC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DTCCreateCommand {
   public static void kothCreate(Player sender, @Parameter(name = "dtc") String koth) {
      new DTC(koth, sender.getLocation());
      sender.sendMessage(ChatColor.GRAY + "Created a DTC named " + koth + ".");
   }
}
