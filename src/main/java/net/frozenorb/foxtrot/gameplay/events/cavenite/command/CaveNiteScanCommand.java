package net.frozenorb.foxtrot.gameplay.events.cavenite.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CaveNiteScanCommand {
   public static void see(Player player) {
      for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
         if (!onlinePlayer.getWorld().getName().equalsIgnoreCase("sg")) {
            player.sendMessage(onlinePlayer.getName() + " is not IN!");
         }
      }
   }

   public static void execute(CommandSender sender) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam("CaveNite");
      if (team == null) {
         sender.sendMessage(ChatColor.RED + "The 'SquadsNite' faction doesn't exist!");
      } else {
         sender.sendMessage(ChatColor.GREEN + "Scanning Squads Nite chests...");
         Foxtrot.getInstance().getCaveNiteHandler().scanLoot();
         sender.sendMessage(ChatColor.GREEN + "Done! Scanned " + Foxtrot.getInstance().getCaveNiteHandler().getChestLocations().size() + " chests!");
      }
   }

   public static void asd(CommandSender sender) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam("CaveNite");
      if (team == null) {
         sender.sendMessage(ChatColor.RED + "The 'SquadsNite' faction doesn't exist!");
      } else {
         sender.sendMessage(ChatColor.GREEN + "Resettng Squads Nite chests...");
         Foxtrot.getInstance().getCaveNiteHandler().respawnChests();
      }
   }
}
