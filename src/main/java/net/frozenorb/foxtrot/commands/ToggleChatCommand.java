package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ToggleChatCommand {
   @Command(names = {"ToggleChat", "ToggleGlobalChat", "TGC"}, permission = "")
   public static void toggleChat(Player sender) {
      boolean val = !Foxtrot.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(sender.getUniqueId());
      sender.sendMessage(
         ChatColor.YELLOW + "You are now " + (!val ? ChatColor.YELLOW + "unable" : ChatColor.GREEN + "able") + ChatColor.YELLOW + " to see global chat!"
      );
      Foxtrot.getInstance().getToggleGlobalChatMap().setGlobalChatToggled(sender.getUniqueId(), val);
   }
}
