package net.frozenorb.foxtrot.gameplay.loot.itemboxes.parameter;

import cc.fyre.proton.command.param.ParameterType;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.itemboxes.ItemBox;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ItemBoxParameter implements ParameterType<ItemBox> {
   public ItemBox transform(CommandSender commandSender, String s) {
      ItemBox toReturn = Foxtrot.getInstance().getItemBoxesHandler().findItemBox(s);
      if (toReturn == null) {
         commandSender.sendMessage(ChatColor.YELLOW + "Item Box " + ChatColor.YELLOW + s + ChatColor.YELLOW + " not found.");
         return null;
      } else {
         return toReturn;
      }
   }
}
