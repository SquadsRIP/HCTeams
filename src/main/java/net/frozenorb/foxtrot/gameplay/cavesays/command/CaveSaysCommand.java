package net.frozenorb.foxtrot.gameplay.cavesays.command;

import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.cavesays.CaveSaysHandler;
import net.frozenorb.foxtrot.gameplay.cavesays.Task;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CaveSaysCommand {
   public static void execute(Player player, @Parameter(name = "taskName", defaultValue = "R_A_N_D_O_M") String taskName) {
      CaveSaysHandler caveSaysHandler = Foxtrot.getInstance().getCaveSaysHandler();
      if (caveSaysHandler.getActiveTask() != null) {
         player.sendMessage(ChatColor.RED + "A Squads Says task is already active.");
         return;
      }

      if ("R_A_N_D_O_M".equalsIgnoreCase(taskName)) {
         caveSaysHandler.activateRandom();
      } else {
         Task task = caveSaysHandler.findTask(taskName);
         if (task == null) {
            player.sendMessage(ChatColor.RED + "Unknown Squads Says task: " + taskName);
            return;
         }

         caveSaysHandler.setActiveTask(task);
         task.activate();
      }
   }

   public static void execute(Player player) {
      CaveSaysHandler caveSaysHandler = Foxtrot.getInstance().getCaveSaysHandler();
      Task task = caveSaysHandler.getActiveTask();
      if (task == null) {
         player.sendMessage(ChatColor.RED + "There is no active Squads Says task.");
         return;
      }

      task.deactivate(null);
   }
}
