package net.frozenorb.foxtrot.gameplay.loot.treasurecove.commands;

import org.bukkit.entity.Player;

public class TreasureLootCommand {
   private static final String UNKNOWN_COMMAND = "Unknown command. Type \"/help\" for help.";

   public static void execute(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }
}
