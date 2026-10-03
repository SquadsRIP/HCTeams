package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.gameplay.killtags.KillTagMenu;
import org.bukkit.entity.Player;

public class KillTagsCommand {
   public static void execute(Player player) {
      new KillTagMenu().openMenu(player);
   }
}
