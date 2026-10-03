package net.frozenorb.foxtrot.gameplay.loot.crate.command.parameterType;

import cc.fyre.proton.command.param.ParameterType;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.crate.Crate;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CrateParameterProvider implements ParameterType<Crate> {
   @Override
   public List<String> tabComplete(Player player, Set<String> set, String s) {
      return null;
   }

   public Crate transform(CommandSender commandSender, String s) {
      Optional<Crate> optionalCrate = Foxtrot.getInstance().getCrateHandler().findById(s);
      if (!optionalCrate.isPresent()) {
         commandSender.sendMessage(ChatColor.translate("&cCrate named &e'" + s + "' &cdoesn't exist."));
         return null;
      } else {
         return optionalCrate.get();
      }
   }
}
