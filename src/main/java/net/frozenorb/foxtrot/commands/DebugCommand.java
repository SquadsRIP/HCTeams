package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.menu.Menu;
import java.util.UUID;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class DebugCommand {
   @Command(names = {"debug", "pearldebug", "debugpearl", "toggledebug"}, permission = "op", hidden = true)
   public static void execute(Player player) {
      if (player.hasMetadata("DEBUG")) {
         player.removeMetadata("DEBUG", Foxtrot.getInstance());
         player.sendMessage(ChatColor.YELLOW + "Toggled off Debug Mode.");
      } else {
         player.setMetadata("DEBUG", new FixedMetadataValue(Foxtrot.getInstance(), true));
         player.sendMessage(ChatColor.GREEN + "Toggled on Debug Mode.");
      }
   }

   @Command(names = "showmenus", permission = "op", hidden = true)
   public static void sender(Player sender) {
      for (Entry uuidMenuEntry : Menu.getCurrentlyOpenedMenus().entrySet()) {
         Player player = Foxtrot.getInstance().getServer().getPlayer((UUID)uuidMenuEntry.getKey());
         if (player == null) {
            sender.sendMessage(ChatColor.YELLOW + ((UUID)uuidMenuEntry.getKey()).toString() + " --> " + ((Menu)uuidMenuEntry.getValue()).getTitle(sender));
         } else {
            sender.sendMessage(ChatColor.GREEN + player.getName() + " --> " + ((Menu)uuidMenuEntry.getValue()).getTitle(sender));
         }
      }
   }
}
