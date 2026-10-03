package net.frozenorb.foxtrot.gameplay.grounds.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.entity.Player;

public class GroundsCommand {
   private static final String UNKNOWN_COMMAND = "Unknown command. Type \"/help\" for help.";

   @Command(names = {"spawnergrounds", "grounds", "spawners", "ground"}, permission = "")
   public static void execute(Player player) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   @Command(names = {"grounds reset", "spawnergrounds reset"}, permission = "op")
   public static void start(Player player, @Parameter(name = "groundsType") String groundsType) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   @Command(names = {"grounds resetall", "spawnergrounds resetall"}, permission = "op")
   public static void resetAll(Player player, @Parameter(name = "groundsType", defaultValue = "R_A_N_D_O_M") String groundsType) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }

   @Command(names = {"grounds addspawn", "spawnergrounds addspawn"}, permission = "op")
   public static void addSpawn(Player player, @Parameter(name = "groundsType") String groundsType) {
      player.sendMessage("Unknown command. Type \"/help\" for help.");
   }
}
