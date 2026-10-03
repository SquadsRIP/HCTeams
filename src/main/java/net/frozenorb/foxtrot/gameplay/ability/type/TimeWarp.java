package net.frozenorb.foxtrot.gameplay.ability.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.server.pearl.EnderpearlCooldownHandler;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World.Environment;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeWarp extends Ability {
   public static Map<UUID, Location> enderPearl = new HashMap<>();

   @Override
   public Category getCategory() {
      return Category.AIRDROPS;
   }

   @Override
   public String getDescription() {
      return "You will be warped back to your previous pearl in 3 seconds...";
   }

   @Override
   public String getName() {
      return this.getClass().getSimpleName();
   }

   @Override
   public Material getMaterial() {
      return Material.WATCH;
   }

   @Override
   public String getDisplayName() {
      return ChatColor.GOLD.toString() + ChatColor.BOLD + "Time Warp";
   }

   @Override
   public List<String> getLore() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("");
      toReturn.add(ChatColor.translate("&6❙ &fTravel back to where"));
      toReturn.add(ChatColor.translate("&6❙ &fyou last threw a pearl!"));
      toReturn.add("");
      toReturn.add(ChatColor.translate("&fCan be found in an &b&lAirdrop&f!"));
      return toReturn;
   }

   @Override
   public Boolean isAllowedAtLocation(Location location) {
      return !Foxtrot.getInstance().getServerHandler().isWarzone(location)
         && location.getWorld().getEnvironment() == Environment.NORMAL
         && !DTRBitmask.KOTH.appliesAt(location)
         && !DTRBitmask.CONQUEST.appliesAt(location)
         && !DTRBitmask.CITADEL.appliesAt(location)
         && !DTRBitmask.DTC.appliesAt(location)
         && !DTRBitmask.SAFE_ZONE.appliesAt(location);
   }

   @Override
   public long getCooldown() {
      return 90000L;
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onInteract(final PlayerInteractEvent event) {
      if (this.isSimilar(event.getItem()) && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
         final Player player = event.getPlayer();
         event.setCancelled(true);
         AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
         Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);
         if (!abilityUseEvent.isCancelled()) {
            if (!enderPearl.containsKey(player.getUniqueId())) {
               player.sendMessage(ChatColor.RED + "You have not thrown a pearl in the last 16 seconds...");
            } else if (player.hasMetadata("NINJASTAR")) {
               player.sendMessage(
                  ChatColor.RED + "You may not use a " + this.getDisplayName() + ChatColor.RED + " whilst someone is using a Ninja Star on you!"
               );
            } else {
               if (player.getItemInHand().getAmount() == 1) {
                  player.setItemInHand(null);
               } else {
                  player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
               }

               final Location location = enderPearl.remove(player.getUniqueId()).clone();
               (new BukkitRunnable() {
                     private int seconds = 4;

                     @Override
                     public void run() {
                        this.seconds--;
                        if (!event.getPlayer().isOnline()) {
                           this.cancel();
                        } else if (player.hasMetadata("NINJASTAR")) {
                           player.sendMessage(
                              ChatColor.RED
                                 + "You may not use a "
                                 + TimeWarp.this.getDisplayName()
                                 + ChatColor.RED
                                 + " whilst someone is using a Ninja Star on you!"
                           );
                           this.cancel();
                           TimeWarp.this.refund(player);
                        } else if (Foxtrot.getInstance().getDeathbanArenaHandler().isDeathbanArena(player)) {
                           this.cancel();
                           TimeWarp.this.refund(player);
                           player.sendMessage(ChatColor.translate("&cYou may not use &f" + TimeWarp.this.getDisplayName() + " &cwhile deathbanned!"));
                        } else if (this.seconds <= 0) {
                           player.teleport(location);
                           this.cancel();
                        } else {
                           event.getPlayer()
                              .sendMessage(
                                 ChatColor.GREEN
                                    + "Teleporting in "
                                    + ChatColor.WHITE
                                    + this.seconds
                                    + ChatColor.GREEN
                                    + " second"
                                    + (this.seconds == 1 ? "" : "s")
                                    + "..."
                              );
                           event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                        }
                     }
                  })
                  .runTaskTimer(Foxtrot.getInstance(), 0L, 20L);
               this.applyCooldown(player);
            }
         }
      }
   }

   public void refund(Player player) {
      player.getInventory().addItem(this.hassanStack.clone());
      this.removeCooldown(player);
   }

   @EventHandler(priority = EventPriority.HIGHEST)
   private void onLaunch(ProjectileLaunchEvent event) {
      if (!event.isCancelled() && event.getEntity() instanceof EnderPearl && event.getEntity().getShooter() instanceof Player) {
         Player shooter = (Player)event.getEntity().getShooter();
         Location location = shooter.getLocation();
         enderPearl.remove(shooter.getUniqueId());
         enderPearl.put(shooter.getUniqueId(), shooter.getLocation());
         Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            if (enderPearl.containsKey(shooter.getUniqueId())) {
               Location newLocation = enderPearl.get(shooter.getUniqueId());
               if (location.getX() == newLocation.getX() && location.getY() == newLocation.getY() && location.getZ() == newLocation.getZ()) {
                  enderPearl.remove(shooter.getUniqueId());
               }
            }
         }, 320L);
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onPearl(PlayerInteractEvent event) {
      Player player = event.getPlayer();
      if (event.getItem() != null && event.getAction().name().contains("RIGHT")) {
         if (event.getItem().getType() == Material.ENDER_PEARL) {
            if (!EnderpearlCooldownHandler.getEnderpearlCooldown().containsKey(player.getName())) {
               Location location = player.getLocation().clone();
               enderPearl.remove(player.getUniqueId());
               enderPearl.put(player.getUniqueId(), location);
               Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                  if (enderPearl.containsKey(player.getUniqueId())) {
                     Location newLocation = enderPearl.get(player.getUniqueId());
                     if (location.getX() == newLocation.getX() && location.getY() == newLocation.getY() && location.getZ() == newLocation.getZ()) {
                        enderPearl.remove(player.getUniqueId());
                     }
                  }
               }, 300L);
            }
         }
      }
   }
}
