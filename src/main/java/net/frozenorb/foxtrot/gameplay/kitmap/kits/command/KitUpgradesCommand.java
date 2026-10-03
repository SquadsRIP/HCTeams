package net.frozenorb.foxtrot.gameplay.kitmap.kits.command;

import net.frozenorb.foxtrot.gameplay.kitmap.gem.menu.upgrades.GemShopUpgradesMenu;
import org.bukkit.entity.Player;

public class KitUpgradesCommand {
   public static void execute(Player player) {
      new GemShopUpgradesMenu().openMenu(player);
   }
}
