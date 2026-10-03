package net.frozenorb.foxtrot.gameplay.events.region.glowmtn.listeners;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.region.glowmtn.GlowHandler;
import net.frozenorb.foxtrot.gameplay.events.region.glowmtn.GlowMountain;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class GlowListener implements Listener {
   @EventHandler(priority = EventPriority.HIGHEST)
   public void onBlockBreak(BlockBreakEvent event) {
      if (event.getBlock().getType() == Material.GLOWSTONE) {
         Location location = event.getBlock().getLocation();
         GlowHandler glowHandler = Foxtrot.getInstance().getGlowHandler();
         if (glowHandler != null) {
            if (!glowHandler.hasGlowMountain()) {
               glowHandler.ensureGlowMountain();
            }

            Team teamAt = LandBoard.getInstance().getTeam(location);
            if (!Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getPlayer(), location)) {
               if (teamAt != null && teamAt.getName().equals(GlowHandler.getGlowTeamName())) {
                  event.setCancelled(false);
                  if (glowHandler.hasGlowMountain()) {
                     GlowMountain glowMountain = glowHandler.getGlowMountain();
                     if (glowMountain.getGlowstone().contains(location.toVector().toBlockVector())) {
                        glowMountain.setRemaining(glowMountain.getRemaining() - 1);
                        double total = glowMountain.getGlowstone().size();
                        double remaining = glowMountain.getRemaining();
                        if (total == remaining) {
                           Bukkit.broadcastMessage(ChatColor.GOLD + "[Glowstone Mountain]" + ChatColor.AQUA + " 50% of Glowstone has been mined!");
                        } else if (remaining == 0.0) {
                           Bukkit.broadcastMessage(ChatColor.GOLD + "[Glowstone Mountain]" + ChatColor.YELLOW + "  All Glowstone has been mined!");
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
