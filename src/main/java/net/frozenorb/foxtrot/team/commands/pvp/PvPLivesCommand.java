package net.frozenorb.foxtrot.team.commands.pvp;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class PvPLivesCommand {
   @Command(names = {"pvptimer lives", "timer lives", "pvp lives"}, permission = "")
   public static void pvpLives(CommandSender sender, @Parameter(name = "player", defaultValue = "self") UUID player) {
      String name = UUIDUtils.name(player);
      sender.sendMessage(ChatColor.DARK_RED + name + "'s Lives: " + ChatColor.WHITE + Foxtrot.getInstance().getFriendLivesMap().getLives(player));
   }
}
