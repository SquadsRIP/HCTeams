package net.frozenorb.foxtrot.listener;

import cc.fyre.proton.util.PlayerUtils;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class PvPTimerListener implements Listener {
   @EventHandler
   public void onEntityShootBow(EntityShootBowEvent event) {
      if (event.getEntity() instanceof Player) {
         Player player = (Player)event.getEntity();
         if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
            player.sendMessage(ChatColor.YELLOW + "You cannot do this while your PVP Timer is active!");
            player.sendMessage(ChatColor.YELLOW + "Type '" + ChatColor.YELLOW + "/pvp enable" + ChatColor.YELLOW + "' to remove your timer.");
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Player) {
         Player damager = PlayerUtils.getDamageSource(event.getDamager());
         if (damager != null) {
            if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(damager.getUniqueId())) {
               damager.sendMessage(ChatColor.YELLOW + "You cannot do this while your PVP Timer is active!");
               damager.sendMessage(ChatColor.YELLOW + "Type '" + ChatColor.YELLOW + "/pvp enable" + ChatColor.YELLOW + "' to remove your timer.");
               event.setCancelled(true);
            } else {
               if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(event.getEntity().getUniqueId())) {
                  damager.sendMessage(ChatColor.YELLOW + "That player currently has their PVP Timer!");
                  event.setCancelled(true);
               }
            }
         }
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void onEntityDamage(EntityDamageEvent event) {
      if (event.getEntity() instanceof Player
         && (event.getCause() == DamageCause.LAVA || event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK)) {
         Player player = (Player)event.getEntity();
         if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
            event.setCancelled(true);
         }
      }
   }
}
