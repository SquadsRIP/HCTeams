package net.frozenorb.foxtrot.server.pearl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import net.frozenorb.foxtrot.listener.event.EnderpearlCooldownAppliedEvent;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class EnderpearlCooldownHandler implements Listener {
   private static Map<String, Long> enderpearlCooldown = new ConcurrentHashMap<>();

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onProjectileLaunch(ProjectileLaunchEvent event) {
      if (event.getEntity().getShooter() instanceof Player) {
         Player shooter = (Player)event.getEntity().getShooter();
         if (event.getEntity() instanceof EnderPearl) {
            shooter.setMetadata("LastEnderPearl", new FixedMetadataValue(Foxtrot.getInstance(), event.getEntity()));
            long timeToApply = DTRBitmask.THIRTY_SECOND_ENDERPEARL_COOLDOWN.appliesAt(event.getEntity().getLocation())
               ? 30000L
               : (shooter.hasMetadata("DEBUG") ? 1000L : 16000L);
            if (shooter.getWorld().getEnvironment() == Environment.NORMAL
               && !Foxtrot.getInstance().getServerHandler().isWarzone(shooter.getLocation())
               && Foxtrot.getInstance().getNetworkBoosterHandler() != null
               && Foxtrot.getInstance().getNetworkBoosterHandler().isReducedEnderpearl()) {
               timeToApply = 10000L;
            }

            if (!shooter.getWorld().getEnvironment().equals(Environment.NETHER) && shooter.hasMetadata("ENDER_MAN")) {
               timeToApply = 10000L;
            }

            if (shooter.getWorld().getEnvironment().equals(Environment.THE_END) && shooter.hasMetadata("ENDERLOCK")) {
               timeToApply = 10000L;
            }

            if (ThreadLocalRandom.current().nextInt(0, 200) <= 20 && shooter.hasMetadata("ENDER_MAN")) {
               timeToApply = 1000L;
            }

            EnderpearlCooldownAppliedEvent appliedEvent = new EnderpearlCooldownAppliedEvent(shooter, timeToApply);
            Foxtrot.getInstance().getServer().getPluginManager().callEvent(appliedEvent);
            long finalTime = appliedEvent.getTimeToApply();
            enderpearlCooldown.put(shooter.getName(), System.currentTimeMillis() + finalTime);
            if (shooter.getWorld().getName().equalsIgnoreCase("sg")) {
               Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                  if (enderpearlCooldown.containsKey(shooter.getName()) && enderpearlCooldown.get(shooter.getName()) > System.currentTimeMillis()) {
                     shooter.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
                  }
               }, 160L);
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onPlayerInteract(ProjectileLaunchEvent event) {
      if (event.getEntity() instanceof EnderPearl) {
         if (event.getEntity().getShooter() instanceof Player) {
            Player thrower = (Player)event.getEntity().getShooter();
            if (enderpearlCooldown.containsKey(thrower.getName()) && enderpearlCooldown.get(thrower.getName()) > System.currentTimeMillis()) {
               long millisLeft = enderpearlCooldown.get(thrower.getName()) - System.currentTimeMillis();
               double value = millisLeft / 1000.0;
               double sec = value > 0.1 ? Math.round(10.0 * value) / 10.0 : 0.1;
               event.setCancelled(true);
               thrower.sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD + sec + ChatColor.RED + " seconds!");
               thrower.updateInventory();
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void onPlayerTeleport(PlayerTeleportEvent event) {
      if (event.getCause() == TeleportCause.ENDER_PEARL) {
         if (!enderpearlCooldown.containsKey(event.getPlayer().getName())) {
            event.setCancelled(true);
         } else {
            Location landing = PearlLandingFixer.findLanding(event.getPlayer(), event.getFrom(), event.getTo());
            if (landing == null) {
               event.setCancelled(true);
               removeCooldown(event.getPlayer());
               event.getPlayer()
                  .sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Invalid Pearl! " + ChatColor.YELLOW + "There is no room to land there!");
               return;
            }

            event.setTo(landing);
            Location target = event.getTo();
            Location from = event.getFrom();
            if (DTRBitmask.SAFE_ZONE.appliesAt(target) && !DTRBitmask.SAFE_ZONE.appliesAt(from)) {
               event.setCancelled(true);
               removeCooldown(event.getPlayer());
               event.getPlayer()
                  .sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Invalid Pearl! " + ChatColor.YELLOW + "You cannot Enderpearl into spawn!");
            } else if (!DTRBitmask.NO_ENDERPEARL.appliesAt(target) && !DTRBitmask.NO_ENDERPEARL.appliesAt(from)) {
               Team ownerTo = LandBoard.getInstance().getTeam(event.getTo());
               if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId()) && ownerTo != null) {
                  if (ownerTo.isMember(event.getPlayer().getUniqueId())) {
                     event.getPlayer().removeMetadata("PVP_TIMER_BYPASS", Foxtrot.getInstance());
                     Foxtrot.getInstance().getPvPTimerMap().removeTimer(event.getPlayer().getUniqueId());
                     LunarClientListener.updateNametag(event.getPlayer());
                  } else if (ownerTo.getOwner() != null || DTRBitmask.KOTH.appliesAt(event.getTo()) || DTRBitmask.CITADEL.appliesAt(event.getTo())) {
                     event.setCancelled(true);
                     removeCooldown(event.getPlayer());
                     event.getPlayer()
                        .sendMessage(
                           ChatColor.RED.toString()
                              + ChatColor.BOLD
                              + "Invalid Pearl! "
                              + ChatColor.YELLOW
                              + "You cannot Enderpearl into claims while having a PvP Timer!"
                        );
                  }
               }
            } else {
               event.setCancelled(true);
               removeCooldown(event.getPlayer());
               event.getPlayer()
                  .sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Invalid Pearl! " + ChatColor.YELLOW + "You cannot Enderpearl into this region!");
            }
         }
      }
   }

   public boolean clippingThrough(Location target, Location from, double thickness) {
      return from.getX() > target.getX() && from.getX() - target.getX() < thickness
         || target.getX() > from.getX() && target.getX() - from.getX() < thickness
         || from.getZ() > target.getZ() && from.getZ() - target.getZ() < thickness
         || target.getZ() > from.getZ() && target.getZ() - from.getZ() < thickness;
   }

   public static void clearEnderpearlTimer(Player player) {
      enderpearlCooldown.remove(player.getName());
   }

   public static void resetEnderpearlTimer(Player player) {
      if (DTRBitmask.THIRTY_SECOND_ENDERPEARL_COOLDOWN.appliesAt(player.getLocation())) {
         enderpearlCooldown.put(player.getName(), System.currentTimeMillis() + 30000L);
      } else {
         enderpearlCooldown.put(
            player.getName(), System.currentTimeMillis() + (Foxtrot.getInstance().getMapHandler().getScoreboardTitle().contains("Staging") ? 1000L : 16000L)
         );
      }
   }

   public static void removeCooldown(Player player) {
      enderpearlCooldown.remove(player.getName());
   }

   public static Map<String, Long> getEnderpearlCooldown() {
      return enderpearlCooldown;
   }
}
