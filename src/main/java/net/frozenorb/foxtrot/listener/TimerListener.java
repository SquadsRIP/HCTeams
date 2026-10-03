package net.frozenorb.foxtrot.listener;

import java.beans.ConstructorProperties;
import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.listener.event.TimerEndEvent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class TimerListener implements Listener {
   private Foxtrot instance;

   @EventHandler(priority = EventPriority.LOW)
   private void onSOTWTimerEnd(TimerEndEvent event) {
      if (event.getDisplayName().equals("&a&lSOTW")) {
         for (Player loopPlayer : this.instance.getServer().getOnlinePlayers()) {
            loopPlayer.playSound(loopPlayer.getLocation(), Sound.WITHER_SPAWN, 1.0F, 1.0F);
            loopPlayer.sendMessage(ChatColor.GRAY + "███████");
            loopPlayer.sendMessage(ChatColor.GRAY + "█" + ChatColor.DARK_GREEN + "█████" + ChatColor.GRAY + "█");
            loopPlayer.sendMessage(ChatColor.GRAY + "█" + ChatColor.DARK_GREEN + "█" + ChatColor.GRAY + "█████ " + ChatColor.DARK_GREEN + "[SOTW]");
            loopPlayer.sendMessage(
               ChatColor.GRAY + "█" + ChatColor.DARK_GREEN + "█████" + ChatColor.GRAY + "█ " + ChatColor.GREEN + ChatColor.BOLD + "SOTW Timer is now over!"
            );
            loopPlayer.sendMessage(ChatColor.GRAY + "█████" + ChatColor.DARK_GREEN + "█" + ChatColor.GRAY + "█ " + ChatColor.RED + "PvP has been enabled!");
            loopPlayer.sendMessage(ChatColor.GRAY + "█" + ChatColor.DARK_GREEN + "█████" + ChatColor.GRAY + "█");
            loopPlayer.sendMessage(ChatColor.GRAY + "███████");
            LunarClientListener.updateNametag(loopPlayer);
         }

         CustomTimerCreateCommand.getCustomTimers().put("dragon", System.currentTimeMillis() + TimeUnit.MINUTES.toSeconds(15L));
      }
   }

   @ConstructorProperties("instance")
   public TimerListener(Foxtrot instance) {
      this.instance = instance;
   }
}
