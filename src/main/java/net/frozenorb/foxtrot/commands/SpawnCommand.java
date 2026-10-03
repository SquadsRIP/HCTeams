package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SpawnCommand {
   @Command(names = "spawn", permission = "command.spawn")
   public static void spawn(Player sender, @Parameter(name = "world", defaultValue = "world") String worldName) {
      if (!sender.hasPermission("command.spawn.advanced")) {
         sender.sendMessage(ChatColor.RED + "This command is disabled. Walk to spawn!");
      } else {
         if (worldName.equalsIgnoreCase("s")) {
            worldName = sender.getWorld().getName();
         }

         World world;
         if ((world = Foxtrot.getInstance().getServer().getWorld(worldName)) == null) {
            Player target = Foxtrot.getInstance().getServer().getPlayer(worldName);
            if (target == null) {
               sender.sendMessage(ChatColor.YELLOW + "No world or player with that name exists!");
            } else {
               target.teleport(Foxtrot.getInstance().getServerHandler().getSpawnLocation());
            }
         } else {
            sender.teleport(world.getSpawnLocation().add(0.5, 0.5, 0.5));
         }
      }
   }

   public static boolean canSpawn(Player player) {
      if (CustomTimerCreateCommand.isSOTWTimer() && !CustomTimerCreateCommand.hasSOTWEnabled(player.getUniqueId())) {
         return true;
      } else {
         return DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation()) ? true : Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId());
      }
   }
}
