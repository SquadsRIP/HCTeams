package net.frozenorb.foxtrot.gameplay.kitmap.kits.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.DefaultKit;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.editor.setup.KitEditorItemsMenu;
import org.bukkit.entity.Player;

public class KitEditorItemsCommand {
   @Command(names = "defaultkit editoritems", description = "Edit a kit's editor items", permission = "op")
   public static void execute(Player player, @Parameter(name = "kit") DefaultKit kit) {
      new KitEditorItemsMenu(kit).openMenu(player);
   }
}
