package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class AdminBuildCommand {
   @Command(names = {"adminbuild", "abuild"}, permission = "op")
   public static void adminBuild(Player player) {
      if (player.hasMetadata("ADMIN_BUILD")) {
         player.removeMetadata("ADMIN_BUILD", Foxtrot.getInstance());
         player.sendMessage(ChatColor.RED + "Admin build mode disabled.");
      } else {
         player.setMetadata("ADMIN_BUILD", new FixedMetadataValue(Foxtrot.getInstance(), true));
         player.sendMessage(ChatColor.GREEN + "Admin build mode enabled — you can break/place anywhere.");
      }
   }
}
