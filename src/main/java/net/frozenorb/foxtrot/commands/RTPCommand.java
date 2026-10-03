package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class RTPCommand {
   public static boolean STATUS = false;

   @Command(names = {"rtp", "wild", "wildtp", "randomtp", "randomteleport"}, permission = "")
   public static void execute(Player player) {
      if (!canRtp(player)) {
         player.sendMessage(ChatColor.YELLOW + "You can't use this command outside of Spawn!");
      } else {
         player.sendMessage("");
         player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aYou have been teleported 1000 down South Road!"));
         player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Type &f/f claim &7and find an open area to claim your base!"));
         player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Once you've gotten a claim, type /f sethome to set your claim's home!"));
         player.sendMessage("");
         player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
         player.teleport(Foxtrot.getInstance().getServerHandler().getMapWorld().getHighestBlockAt(0, 1000).getLocation().add(0.0, 1.0, 0.0));
      }
   }

   @Command(names = "rtp enable", permission = "op")
   public static void enable(Player player) {
      if (STATUS) {
         STATUS = false;
         player.sendMessage(ChatColor.YELLOW + "RTP is now disabled during SOTW Timer");
      } else {
         STATUS = true;
         player.sendMessage(ChatColor.GREEN + "RTP is now enabled during SOTW Timer");
      }
   }

   public static boolean canRtp(Player player) {
      if (CustomTimerCreateCommand.isSOTWTimer() && !STATUS) {
         return false;
      } else {
         return DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation()) ? true : Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId());
      }
   }
}
