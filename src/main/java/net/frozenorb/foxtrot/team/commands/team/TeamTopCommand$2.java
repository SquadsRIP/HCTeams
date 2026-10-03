package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.Proton;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

final class TeamTopCommand$2 extends BukkitRunnable {
   // $VF: synthetic field
   final CommandSender val$sender;

   TeamTopCommand$2(CommandSender var1) {
      this.val$sender = var1;
   }

   @Override
   public void run() {
      LinkedHashMap<Team, Integer> sortedTeamPlayerCount = TeamTopCommand.getSortedRaidableTeams();
      int index = 0;
      this.val$sender.sendMessage(Team.DARK_GRAY_LINE);
      this.val$sender.sendMessage("§4§lTop Factions Trapping");
      this.val$sender.sendMessage(" ");

      for (Entry<Team, Integer> teamEntry : sortedTeamPlayerCount.entrySet()) {
         if (teamEntry.getKey().getOwner() != null) {
            if (11 <= ++index) {
               break;
            }

            FancyMessage teamMessage = new FancyMessage();
            Team team = teamEntry.getKey();
            teamMessage.text(index + ". ").color(ChatColor.RED).then();
            teamMessage.text(teamEntry.getKey().getName())
               .color(
                  this.val$sender instanceof Player && teamEntry.getKey().isMember(((Player)this.val$sender).getUniqueId()) ? ChatColor.GREEN : ChatColor.RED
               )
               .tooltip(
                  (this.val$sender instanceof Player && teamEntry.getKey().isMember(((Player)this.val$sender).getUniqueId()) ? ChatColor.GREEN : ChatColor.RED)
                        .toString()
                     + teamEntry.getKey().getName()
                     + "\n"
                     + ChatColor.RED
                     + "Leader: "
                     + ChatColor.GRAY
                     + Proton.getInstance().getUuidCache().name(teamEntry.getKey().getOwner())
                     + "\n\n"
                     + ChatColor.RED
                     + "Balance: "
                     + ChatColor.DARK_GREEN
                     + "$"
                     + ChatColor.GREEN
                     + (int)Math.round(team.getBalance())
                     + "\n"
                     + ChatColor.RED
                     + "Kills: "
                     + ChatColor.GRAY
                     + team.getKills()
                     + "\n"
                     + ChatColor.RED
                     + "Deaths: "
                     + ChatColor.GRAY
                     + team.getDeaths()
                     + "\n\n"
                     + ChatColor.RED
                     + "KOTH Captures: "
                     + ChatColor.GRAY
                     + team.getKothCaptures()
                     + "\n"
                     + ChatColor.RED
                     + "Citadel Captures: "
                     + ChatColor.GRAY
                     + team.getCitadelsCapped()
                     + "\n"
                     + ChatColor.RED
                     + "Conquest Captures: "
                     + ChatColor.GRAY
                     + team.getConquestsCapped()
                     + "\n\n"
                     + ChatColor.GREEN
                     + "Click to view faction info"
               )
               .command("/t who " + teamEntry.getKey().getName())
               .then();
            teamMessage.text(" - ").color(ChatColor.WHITE).then();
            teamMessage.text(teamEntry.getValue().toString()).color(ChatColor.WHITE);
            teamMessage.send(this.val$sender);
         }
      }

      this.val$sender.sendMessage(Team.DARK_GRAY_LINE);
   }
}
