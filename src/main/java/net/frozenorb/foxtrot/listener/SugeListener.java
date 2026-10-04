package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.cavepvp.suge.kit.event.KitUseEvent;

public class SugeListener implements Listener {
   @EventHandler(priority = EventPriority.LOWEST)
   private void onKitUse(KitUseEvent event) {
      if (!canUseKit(event.getPlayer(), true)) {
         event.setCancelled(true);
      }
   }

   public static boolean canUseKit(Player player, boolean checkSpawnTag) {
      Location location = player.getLocation();
      if (player.getGameMode() == GameMode.CREATIVE && player.isOp()) {
         return true;
      } else if (CustomTimerCreateCommand.isSOTWTimer() && !CustomTimerCreateCommand.hasSOTWEnabled(player.getUniqueId())) {
         return true;
      } else if (location.getWorld().getEnvironment() == Environment.NETHER && !CustomTimerCreateCommand.isSOTWTimer()) {
         player.sendMessage(ChatColor.YELLOW + "You can't use a kit at all in Nether!");
         return false;
      } else {
         if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId()) || DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
            return true;
         } else if (Foxtrot.getInstance().getInDuelPredicate().test(player) || Foxtrot.getInstance().getInEventPredicate().test(player)) {
            player.sendMessage(ChatColor.YELLOW + "You may not use a kit whilst in a Duel or in an Event!");
            return false;
         } else if (location.getWorld().getEnvironment() != Environment.NORMAL) {
            player.sendMessage(ChatColor.YELLOW + "You can only use a kit in The Overworld!");
            return false;
         } else if (DTRBitmask.CONQUEST.appliesAt(location) || DTRBitmask.KOTH.appliesAt(location) || DTRBitmask.CITADEL.appliesAt(location)) {
            player.sendMessage(ChatColor.YELLOW + "You may not use a kit in " + LandBoard.getInstance().getClaim(location).getName() + ChatColor.YELLOW + ".");
            return false;
         } else if (Foxtrot.getInstance().getServerHandler().isWarzone(location)) {
            player.sendMessage(ChatColor.YELLOW + "You may not use a kit in WarZone! You must be 1000 blocks out of Spawn to use one!");
            return false;
         } else if (SpawnTagHandler.isTagged(player) && checkSpawnTag) {
            player.sendMessage(ChatColor.YELLOW + "You may not use a kit while you are spawn tagged!");
            return false;
         } else {
            return true;
         }
      }
   }
}
