package net.frozenorb.foxtrot.listener;

import cc.fyre.proton.menu.event.MenuOpenEvent;
import cc.fyre.proton.util.PlayerUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class SpawnTagListener implements Listener {
   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Player) {
         Player damager = PlayerUtils.getDamageSource(event.getDamager());
         if ((damager == null || !Foxtrot.getInstance().getDeathbanArenaHandler().isDeathbanArena(damager))
            && (!(event.getEntity() instanceof Player) || !Foxtrot.getInstance().getDeathbanArenaHandler().isDeathbanArena((Player)event.getEntity()))) {
            if (damager != null && damager != event.getEntity()) {
               SpawnTagHandler.addOffensiveSeconds(damager, SpawnTagHandler.getMaxTagTime(damager));
               SpawnTagHandler.addPassiveSeconds((Player)event.getEntity(), SpawnTagHandler.getMaxTagTime((Player)event.getEntity()));
            }
         }
      }
   }

   @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
   public void onBlockPlace(BlockPlaceEvent event) {
      Player player = event.getPlayer();
      if (player.getGameMode() != GameMode.CREATIVE && SpawnTagHandler.isTagged(player) && !Foxtrot.getInstance().getServerHandler().isPlaceBlocksInCombat()) {
         player.sendMessage(ChatColor.YELLOW + "You can't place blocks whilst in combat.");
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
      String message = event.getMessage().toLowerCase();
      if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
         Player player = event.getPlayer();
         if ((
               message.startsWith("/rename")
                  || message.startsWith("/hub")
                  || message.startsWith("/universe:hub")
                  || message.startsWith("/buy")
                  || message.startsWith("/lobby")
            )
            && SpawnTagHandler.isTagged(player)) {
            player.sendMessage(ChatColor.YELLOW + "You may not use '" + message + "' while you are spawn tagged!");
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   private void onOpen(MenuOpenEvent event) {
      Player player = event.getPlayer();
      if (!player.isOp() && player.getGameMode() != GameMode.CREATIVE && !DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
         if (SpawnTagHandler.isTagged(player)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.YELLOW + "You may not open this menu as you are Spawn Tagged!");
         }
      }
   }
}
