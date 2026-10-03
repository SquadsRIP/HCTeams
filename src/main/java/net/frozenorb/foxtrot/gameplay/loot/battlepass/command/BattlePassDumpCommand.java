package net.frozenorb.foxtrot.gameplay.loot.battlepass.command;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.BattlePassProgress;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class BattlePassDumpCommand {
   @Command(names = {"battlepass dump", "bp dump"}, description = "Dump a player's BattlePass progress", permission = "battlepass.dump", async = true)
   public static void execute(CommandSender sender, @Parameter(name = "target") UUID targetUuid) {
      if (Foxtrot.getInstance().getBattlePassHandler() == null) {
         sender.sendMessage(ChatColor.YELLOW + "BattlePass is not enabled on this server!");
      } else {
         BattlePassProgress progress = Foxtrot.getInstance().getBattlePassHandler().getOrLoadProgress(targetUuid);
         sender.sendMessage(Proton.getInstance().getUuidCache().name(progress.getUuid()) + "'s BattlePass Dump");
         sender.sendMessage("XP: " + progress.getExperience());
         sender.sendMessage("Tier: " + progress.getCurrentTier().getNumber());
         sender.sendMessage("Premium: " + progress.isPremium());
         sender.sendMessage("Challenges Completed: " + progress.getCompletedChallenges().size());
         sender.sendMessage("Daily Challenges Completed: " + progress.getCompletedDailyChallenges().size());
      }
   }
}
