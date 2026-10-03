package net.frozenorb.foxtrot.gameplay.armorclass.parameter;

import cc.fyre.proton.command.param.ParameterType;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ArmorClassParameter implements ParameterType<ArmorClass> {
   public ArmorClass transform(CommandSender commandSender, String s) {
      ArmorClass toReturn = Foxtrot.getInstance().getArmorClassHandler().findArmorClass(s);
      if (toReturn == null) {
         commandSender.sendMessage(ChatColor.YELLOW + "Armor class " + ChatColor.YELLOW + s + ChatColor.YELLOW + " not found.");
         return null;
      } else {
         return toReturn;
      }
   }
}
