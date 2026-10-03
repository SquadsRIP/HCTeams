package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class SpectatorCommand implements Listener {
   @Command(names = "gmsp", permission = "bukkit.command.gamemode", description = "Switch to spectator mode")
   public static void execute(Player player) {
      GameMode spectator;
      try {
         spectator = GameMode.valueOf("SPECTATOR");
      } catch (IllegalArgumentException exception) {
         LegacySpectatorMode.toggle(player);
         return;
      }

      player.setGameMode(spectator);
      if (player.getGameMode() == spectator) {
         player.sendMessage(ChatColor.GREEN + "Your gamemode is now spectator.");
      }
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void onCommand(PlayerCommandPreprocessEvent event) {
      if (event.getMessage().trim().matches("(?i)/gamemode\\s+spectator")) {
         event.setMessage("/gmsp");
      } else if (event.getMessage().trim().matches("(?i)/(?:gmc|gms|gma|(?:gamemode|gm)\\s+(?:creative|survival|adventure|[012csa]))")) {
         LegacySpectatorMode.exit(event.getPlayer());
      }
   }
}
