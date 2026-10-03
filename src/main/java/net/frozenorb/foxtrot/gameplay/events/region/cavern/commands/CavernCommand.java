package net.frozenorb.foxtrot.gameplay.events.region.cavern.commands;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.region.cavern.Cavern;
import net.frozenorb.foxtrot.gameplay.events.region.cavern.CavernHandler;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CavernCommand {
   @Command(names = "cavern scan", permission = "op")
   public static void cavernScan(Player sender) {
      if (!Foxtrot.getInstance().getConfig().getBoolean("cavern", false)) {
         sender.sendMessage(ChatColor.YELLOW + "Cavern is currently disabled. Check config.yml to toggle.");
      } else {
         Team team = Foxtrot.getInstance().getTeamHandler().getTeam(CavernHandler.getCavernTeamName());
         if (team == null) {
            sender.sendMessage(ChatColor.YELLOW + "You must first create the team (" + CavernHandler.getCavernTeamName() + ") and claim it!");
         } else if (team.getClaims().isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "You must claim land for '" + CavernHandler.getCavernTeamName() + "' before scanning it!");
         } else {
            if (!Foxtrot.getInstance().getCavernHandler().hasCavern()) {
               Foxtrot.getInstance().getCavernHandler().setCavern(new Cavern());
            }

            Foxtrot.getInstance().getCavernHandler().getCavern().scan();
            Foxtrot.getInstance().getCavernHandler().save();
            sender.sendMessage(ChatColor.GREEN + "[Cavern] Scanned all ores and saved Cavern to file!");
         }
      }
   }

   @Command(names = "cavern reset", permission = "op")
   public static void cavernReset(Player sender) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(CavernHandler.getCavernTeamName());
      if (team != null && !team.getClaims().isEmpty() && Foxtrot.getInstance().getCavernHandler().hasCavern()) {
         Foxtrot.getInstance().getCavernHandler().getCavern().reset();
         Bukkit.broadcastMessage(ChatColor.AQUA + "[Cavern]" + ChatColor.GREEN + " All ores have been reset!");
      } else {
         sender.sendMessage(
            ChatColor.YELLOW + "Create the team '" + CavernHandler.getCavernTeamName() + "', then make a claim for it, finally scan it! (/cavern scan)"
         );
      }
   }
}
