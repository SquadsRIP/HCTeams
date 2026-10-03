package net.frozenorb.foxtrot.gameplay.bosses.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BossesCommand {
   @Command(names = {"boss spawn", "boss summon"}, permission = "op")
   public static void execute(Player player, @Parameter(name = "taskName", defaultValue = "R_A_N_D_O_M") String taskName) {
      player.sendMessage(ChatColor.RED + "Bosses are disabled on this server.");
   }

   @Command(names = {"boss stop", "boss deactivate"}, permission = "op")
   public static void execute(Player player) {
      player.sendMessage(ChatColor.RED + "Bosses are disabled on this server.");
   }
}
