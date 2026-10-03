package net.frozenorb.foxtrot.gameplay.clickitem.parameter;

import cc.fyre.proton.command.param.ParameterType;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.clickitem.ClickItem;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ClickItemParameter implements ParameterType<ClickItem> {
   public ClickItem transform(CommandSender commandSender, String s) {
      ClickItem toReturn = Foxtrot.getInstance().getClickItemHandler().findClickItem(s);
      if (toReturn == null) {
         commandSender.sendMessage(ChatColor.YELLOW + "Click Item " + ChatColor.YELLOW + s + ChatColor.YELLOW + " not found.");
         return null;
      } else {
         return toReturn;
      }
   }
}
