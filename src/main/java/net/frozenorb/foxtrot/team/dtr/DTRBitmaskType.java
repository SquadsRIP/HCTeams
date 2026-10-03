package net.frozenorb.foxtrot.team.dtr;

import cc.fyre.proton.command.param.ParameterType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DTRBitmaskType implements ParameterType<DTRBitmask> {
   public DTRBitmask transform(CommandSender sender, String source) {
      for (DTRBitmask bitmaskType : DTRBitmask.values()) {
         if (source.equalsIgnoreCase(bitmaskType.getName())) {
            return bitmaskType;
         }
      }

      sender.sendMessage(ChatColor.YELLOW + "No bitmask type with the name " + source + " found.");
      return null;
   }

   @Override
   public List<String> tabComplete(Player sender, Set<String> flags, String source) {
      ArrayList<String> completions = new ArrayList<>();

      for (DTRBitmask bitmask : DTRBitmask.values()) {
         if (StringUtils.startsWithIgnoreCase(bitmask.getName(), source)) {
            completions.add(bitmask.getName());
         }
      }

      return completions;
   }
}
