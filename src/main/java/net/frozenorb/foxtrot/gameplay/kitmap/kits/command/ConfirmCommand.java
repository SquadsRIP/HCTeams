package net.frozenorb.foxtrot.gameplay.kitmap.kits.command;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.DefaultKit;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.Kit;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

public class ConfirmCommand {
   @Command(names = "confirm", permission = "")
   public static void execute(Player player) {
      if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
         player.sendMessage(ChatColor.YELLOW + "You may only run this command on Kitmap!");
      } else if (!player.hasMetadata("CONFIRM")) {
         player.sendMessage(ChatColor.YELLOW + "You don't have anything to confirm...");
      } else if (!DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
         player.sendMessage(ChatColor.YELLOW + "You may not use this command outside of Spawn!");
      } else {
         MetadataValue first = player.getMetadata("CONFIRM").get(0);
         if (first == null) {
            player.sendMessage(ChatColor.YELLOW + "You have no kit to confirm! Contact an administrator!");
         } else {
            player.removeMetadata("CONFIRM", Foxtrot.getInstance());
            DefaultKit originalKit = Foxtrot.getInstance().getMapHandler().getKitManager().getDefaultKit(first.asString());
            if (originalKit != null) {
               Kit kit = Foxtrot.getInstance().getMapHandler().getKitManager().getUserKit(player.getUniqueId(), originalKit);
               if (kit != null) {
                  kit.apply(player);
               } else {
                  originalKit.apply(player);
               }
            }
         }
      }
   }
}
