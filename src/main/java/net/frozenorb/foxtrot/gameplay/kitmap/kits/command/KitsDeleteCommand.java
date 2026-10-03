package net.frozenorb.foxtrot.gameplay.kitmap.kits.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.DefaultKit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitsDeleteCommand {
   @Command(names = "defaultkit delete", permission = "op")
   public static void execute(Player sender, @Parameter(name = "kit", wildcard = true) DefaultKit kit) {
      Foxtrot.getInstance().getMapHandler().getKitManager().deleteDefaultKit(kit);
      Foxtrot.getInstance().getMapHandler().getKitManager().saveDefaultKits();
      sender.sendMessage(ChatColor.YELLOW + "Kit " + ChatColor.BLUE + kit.getName() + ChatColor.YELLOW + " has been deleted.");
   }
}
