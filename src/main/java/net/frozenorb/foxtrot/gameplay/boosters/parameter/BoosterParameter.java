package net.frozenorb.foxtrot.gameplay.boosters.parameter;

import cc.fyre.proton.command.param.ParameterType;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.boosters.Booster;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class BoosterParameter implements ParameterType<Booster> {
   public Booster transform(CommandSender commandSender, String s) {
      Booster toReturn = Foxtrot.getInstance().getNetworkBoosterHandler().findBooster(s);
      if (toReturn == null) {
         commandSender.sendMessage(ChatColor.YELLOW + "Booster " + ChatColor.YELLOW + s + ChatColor.YELLOW + " not found.");
         return null;
      } else {
         return toReturn;
      }
   }
}
