package net.frozenorb.foxtrot.gameplay.extra.stats.command;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.extra.stats.StatsEntry;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class StatsTopCommand {
   @Command(names = {"statstop", "leaderboards", "lb"}, permission = "")
   public static void statstop(CommandSender sender, @Parameter(name = "objective", defaultValue = "kills") StatsTopCommand.StatsObjective objective) {
      sender.sendMessage(Team.DARK_GRAY_LINE);
      sender.sendMessage(ChatColor.DARK_RED.toString() + ChatColor.BOLD + objective.getName() + " Leaderboard");
      sender.sendMessage("");
      int index = 0;

      for (Entry<StatsEntry, String> entry : Foxtrot.getInstance().getMapHandler().getStatsHandler().getLeaderboards(objective, 10).entrySet()) {
         sender.sendMessage(
            ChatColor.DARK_RED.toString()
               + ++index
               + ". "
               + ChatColor.WHITE
               + Proton.getInstance().getUuidCache().name(entry.getKey().getOwner())
               + ": "
               + ChatColor.DARK_RED
               + entry.getValue()
         );
      }

      sender.sendMessage(Team.DARK_GRAY_LINE);
   }

   public enum StatsObjective {
      KILLS("Kills", "k"),
      DEATHS("Deaths", "d"),
      KD("KD", "kdr"),
      HIGHEST_KILLSTREAK("Highest Killstreak", "killstreak", "highestkillstreak", "ks", "highestks", "hks"),
      CAVE_SAYS_COMPLETED("Squads Says Completed", "squadssays", "tasks", "squadssayscompleted", "csc"),
      KOTH_CAPTURES("KoTH Captures", "captures", "koth", "kothcaptures");

      private final String name;
      private final String[] aliases;

      StatsObjective(String name, String... aliases) {
         this.name = name;
         this.aliases = aliases;
      }

      public String getName() {
         return this.name;
      }

      public String[] getAliases() {
         return this.aliases;
      }
   }
}
