package net.frozenorb.foxtrot.gameplay.blockshop.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

@FunctionalInterface
public interface ClickHandler {
   void onClick(Player var1, int var2, ClickType var3);
}
