package net.frozenorb.foxtrot.server.keyalls.command.parameter;

import cc.fyre.proton.command.param.ParameterType;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.keyalls.KeyAll;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class KeyAllParameter implements ParameterType<KeyAll> {
   public KeyAll transform(CommandSender commandSender, String s) {
      KeyAll keyAll = Foxtrot.getInstance().getKeyAllHandler().findKeyAll(s);
      if (keyAll == null) {
         commandSender.sendMessage(ChatColor.YELLOW + "The key-all named " + ChatColor.YELLOW + "'" + s + "'" + ChatColor.YELLOW + " doesn't exist!");
         return null;
      } else {
         return keyAll;
      }
   }
}
