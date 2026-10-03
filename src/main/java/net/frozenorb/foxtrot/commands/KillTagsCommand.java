package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.gameplay.killtags.KillTagMenu;
import org.bukkit.entity.Player;

public class KillTagsCommand {
   @Command(names = {"killtags", "killtag", "kills tags"}, permission = "")
   public static void execute(Player player) {
      new KillTagMenu().openMenu(player);
   }
}
