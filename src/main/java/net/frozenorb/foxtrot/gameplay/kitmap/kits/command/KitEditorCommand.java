package net.frozenorb.foxtrot.gameplay.kitmap.kits.command;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.editor.menu.KitsMenu;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitEditorCommand {
   @Command(names = "kit editor", description = "Opens the Kit Editor", permission = "")
   public static void execute(Player player) {
      if (!DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
         player.sendMessage(ChatColor.YELLOW + "You can only open the Kit Editor while in Spawn!");
      } else if (SpawnTagHandler.isTagged(player)) {
         player.sendMessage(ChatColor.YELLOW + "You can't open the Kit Editor while spawn-tagged!");
      } else {
         new KitsMenu().openMenu(player);
      }
   }
}
