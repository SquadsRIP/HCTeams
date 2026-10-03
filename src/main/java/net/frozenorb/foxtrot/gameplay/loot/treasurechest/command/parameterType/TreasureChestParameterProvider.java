package net.frozenorb.foxtrot.gameplay.loot.treasurechest.command.parameterType;

import cc.fyre.proton.command.param.ParameterType;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.treasurechest.TreasureChest;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TreasureChestParameterProvider implements ParameterType<TreasureChest> {
   @Override
   public List<String> tabComplete(Player player, Set<String> set, String s) {
      return null;
   }

   public TreasureChest transform(CommandSender commandSender, String s) {
      Optional<TreasureChest> optionalCrate = Foxtrot.getInstance()
         .getTreasureChestHandler()
         .getTreasureChests()
         .stream()
         .filter(it -> it.getId().equalsIgnoreCase(s))
         .findFirst();
      if (!optionalCrate.isPresent()) {
         commandSender.sendMessage(ChatColor.translate("&cTreasure Chest named &e'" + s + "' &cdoesn't exist."));
         return null;
      } else {
         return optionalCrate.get();
      }
   }
}
