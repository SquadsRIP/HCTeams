package net.frozenorb.foxtrot.gameplay.events.koth.minikoth.listener;

import cc.fyre.piston.command.admin.ScreenShareCommand;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class MiniKOTHListener implements Listener {
   @EventHandler(priority = EventPriority.MONITOR)
   private void onMove(PlayerMoveEvent event) {
      if (!event.isCancelled()) {
         Player player = event.getPlayer();
         Location from = event.getFrom();
         Location to = event.getTo();
         if (to.getBlockX() != from.getBlockX() || to.getBlockZ() != from.getBlockZ() || to.getBlockY() != from.getBlockY()) {
            if (DTRBitmask.SAFE_ZONE.appliesAt(to)) {
               if (to.getBlock().getType().name().contains("WATER")) {
                  Block downBlock = to.getBlock().getRelative(BlockFace.DOWN);
                  if (!downBlock.getType().equals(Material.GLOWSTONE)) {
                     Block downBlockTwo = downBlock.getRelative(BlockFace.DOWN);
                     if (!downBlockTwo.getType().equals(Material.GLOWSTONE)) {
                        return;
                     }
                  }

                  KOTH koth = Foxtrot.getInstance().getEventHandler().getActiveKOTH();
                  if (koth != null && koth.isMini()) {
                     Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
                     if (team == null) {
                        player.sendMessage(ChatColor.RED + "You must be in a team to warp to a Mini KOTH!");
                     } else if (team.getOnlineMemberAmount() > 4) {
                        player.sendMessage(ChatColor.RED + "You must have less than 4 faction members online!");
                     } else if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
                        player.sendMessage(ChatColor.RED + "You must enable your PvP Timer to warp!");
                     } else {
                        World world = Foxtrot.getInstance().getServer().getWorld(koth.getWorld());
                        if (world == null) {
                           player.sendMessage(ChatColor.RED + "Invalid world! Contact an administrator!");
                        } else {
                           player.playSound(player.getLocation(), Sound.PORTAL_TRAVEL, 1.0F, 1.0F);
                           player.sendMessage(ChatColor.GREEN + "Warping...");
                           player.teleport(world.getSpawnLocation().clone());
                        }
                     }
                  } else {
                     player.sendMessage(ChatColor.RED + "There is no active Mini KOTH! Type /minikoth schedule to view a schedule!");
                  }
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onDamage(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Player) {
         Player player = (Player)event.getEntity();
         if (player.getWorld().getName().contains("MiniKOTH")) {
            Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
            if (team == null) {
               player.sendMessage(ChatColor.RED + "You must be in a team to play in the Mini KOTH!");
            } else {
               if (team.getOnlineMemberAmount() > 4) {
                  ScreenShareCommand.sendDangerSign(
                     player,
                     "",
                     ChatColor.DARK_RED + ChatColor.BOLD.toString() + "WARNING!!",
                     ChatColor.YELLOW + "You can only have a maximum",
                     ChatColor.YELLOW + "of 3 members online to play MiniKOTH!",
                     ChatColor.RED + "Enemies will now deal double the damage to your faction!",
                     ""
                  );
                  event.setDamage(event.getDamage() * 2.0);
               }
            }
         }
      }
   }
}
