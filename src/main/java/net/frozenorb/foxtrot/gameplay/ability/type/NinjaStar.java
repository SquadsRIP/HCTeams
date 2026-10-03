package net.frozenorb.foxtrot.gameplay.ability.type;

import cc.fyre.neutron.Neutron;
import cc.fyre.proton.Proton;
import cc.fyre.proton.util.TimeUtils;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

public class NinjaStar extends Ability {
   public static Map<UUID, Long> teleportCooldowns = new HashMap<>();
   public static Map<UUID, NinjaStar.LastDamageEntry> cache = new HashMap<>();

   @Override
   public Category getCategory() {
      return Category.AIRDROPS;
   }

   @Override
   public String getDescription() {
      return "";
   }

   @Override
   public String getName() {
      return this.getClass().getSimpleName();
   }

   @Override
   public Material getMaterial() {
      return Material.NETHER_STAR;
   }

   @Override
   public String getDisplayName() {
      return ChatColor.AQUA.toString() + ChatColor.BOLD + "Ninja Star";
   }

   @Override
   public List<String> getLore() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("");
      toReturn.add(ChatColor.translate("&6❙ &fTeleport to the last person that"));
      toReturn.add(ChatColor.translate("&6❙ &fhit you within the last &e&l30 seconds&f!"));
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
      return 180000L;
   }

   @EventHandler
   private void onInteract(final PlayerInteractEvent event) {
      final Player player = event.getPlayer();
      long difference = TimeUnit.SECONDS.toMillis(30L);
      if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
         if (player.getItemInHand() != null && this.isSimilar(player.getItemInHand())) {
            AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
            Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);
            if (!abilityUseEvent.isCancelled()) {
               if (!cache.containsKey(player.getUniqueId()) || System.currentTimeMillis() - cache.get(player.getUniqueId()).getTime() > difference) {
                  player.sendMessage(ChatColor.RED + "No player has hit you within the last 30 seconds.");
               } else if (!player.hasMetadata("NO_COOLDOWN")
                  && teleportCooldowns.containsKey(player.getUniqueId())
                  && teleportCooldowns.get(player.getUniqueId()) > System.currentTimeMillis()) {
                  long remaining = teleportCooldowns.get(player.getUniqueId()) - System.currentTimeMillis();
                  player.sendMessage(
                     ChatColor.translate(
                        "&cYou cannot use a &b&lTeleportation Item &cfor another &l" + TimeUtils.formatIntoDetailedString((int)(remaining / 1000L)) + "&c."
                     )
                  );
               } else {
                  final NinjaStar.LastDamageEntry entry = cache.get(player.getUniqueId());
                  Player target = Foxtrot.getInstance().getServer().getPlayer(entry.getUuid());
                  if (target.isOnline()) {
                     target.setMetadata("NINJASTAR", new FixedMetadataValue(Foxtrot.getInstance(), true));
                     target.sendMessage(ChatColor.RED + "You may not use an enderpearl or timewarp for the next 5 seconds...");
                     Foxtrot.getInstance()
                        .getServer()
                        .getScheduler()
                        .runTaskLater(Foxtrot.getInstance(), () -> target.removeMetadata("NINJASTAR", Foxtrot.getInstance()), 100L);
                  }

                  this.fullDescription = "Teleporting to &f" + target.getName() + " &cin 3 seconds...";
                  (new BukkitRunnable() {
                        private int seconds = 3;

                        @Override
                        public void run() {
                           if (!event.getPlayer().isOnline()) {
                              this.cancel();
                           } else {
                              Player targetx = Foxtrot.getInstance().getServer().getPlayer(entry.getUuid());
                              if (Foxtrot.getInstance().getDeathbanArenaHandler().isDeathbanArena(targetx)) {
                                 NinjaStar.this.refund(player);
                                 player.sendMessage(ChatColor.RED + "Cancelled the process as that player is in the Deathban Arena!");
                                 this.cancel();
                              } else if (this.seconds >= 1) {
                                 if (Foxtrot.getInstance().getDeathbanArenaHandler().isDeathbanArena(player)) {
                                    this.cancel();
                                    player.sendMessage(ChatColor.translate("&cYou may not use &f" + NinjaStar.this.getDisplayName() + " &cwhile deathbanned!"));
                                 } else {
                                    this.seconds--;
                                    event.getPlayer()
                                       .sendMessage(
                                          ChatColor.YELLOW
                                             + "Teleporting to "
                                             + ChatColor.WHITE
                                             + Proton.getInstance().getUuidCache().name(entry.getUuid())
                                             + ChatColor.YELLOW
                                             + " in "
                                             + ChatColor.RED
                                             + (this.seconds + 1)
                                             + ChatColor.YELLOW
                                             + " second"
                                             + (this.seconds == 1 ? "" : "s")
                                             + "..."
                                       );
                                    if (targetx != null && targetx.isOnline()) {
                                       targetx.sendMessage(
                                          Neutron.getInstance().getProfileHandler().findDisplayName(player.getUniqueId())
                                             + ChatColor.RED
                                             + " will teleport to you in "
                                             + ChatColor.WHITE
                                             + (this.seconds + 1)
                                             + ChatColor.RED
                                             + " second"
                                             + (this.seconds == 1 ? "" : "s")
                                             + "."
                                       );
                                    }
                                 }
                              } else {
                                 Location location = targetx != null && targetx.isOnline() ? targetx.getLocation() : entry.getLocation();
                                 event.getPlayer().teleport(location);
                                 this.cancel();
                              }
                           }
                        }
                     })
                     .runTaskTimer(Foxtrot.getInstance(), 0L, 20L);
                  if (player.getItemInHand().getAmount() == 1) {
                     player.setItemInHand(null);
                  } else {
                     player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                  }

                  teleportCooldowns.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1L));
                  this.applyCooldown(player);
               }
            }
         }
      }
   }

   public void refund(Player player) {
      player.getInventory().addItem(this.hassanStack.clone());
      this.removeCooldown(player);
   }

   @EventHandler(priority = EventPriority.MONITOR)
   private void onPearl(ProjectileLaunchEvent event) {
      if (event.getEntity() instanceof EnderPearl) {
         if (event.getEntity().getShooter() instanceof Player) {
            Player shooter = (Player)event.getEntity().getShooter();
            if (shooter.hasMetadata("NINJASTAR")) {
               event.setCancelled(true);
               shooter.updateInventory();
               shooter.sendMessage(
                  ChatColor.RED + "You may not throw enderpearls whilst someone is using a " + this.getDisplayName() + ChatColor.RED + " on you!"
               );
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
      if (!event.isCancelled()) {
         if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            cache.put(
               event.getEntity().getUniqueId(),
               new NinjaStar.LastDamageEntry(System.currentTimeMillis(), event.getDamager().getUniqueId(), event.getDamager().getLocation())
            );
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   private void onEntityDamageByProjectile(EntityDamageByEntityEvent event) {
      if (!event.isCancelled() && !(event.getDamager() instanceof EnderPearl)) {
         if (event.getEntity() instanceof Player && event.getDamager() instanceof Projectile) {
            if (((Projectile)event.getDamager()).getShooter() instanceof Player) {
               Player damager = (Player)((Projectile)event.getDamager()).getShooter();
               cache.put(
                  event.getEntity().getUniqueId(), new NinjaStar.LastDamageEntry(System.currentTimeMillis(), damager.getUniqueId(), damager.getLocation())
               );
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onDeath(PlayerDeathEvent event) {
      cache.remove(event.getEntity().getUniqueId());
      Optional<Entry<UUID, NinjaStar.LastDamageEntry>> optionalLastDamageEntry = cache.entrySet()
         .stream()
         .filter(it -> it.getValue().getUuid().toString().equalsIgnoreCase(event.getEntity().getUniqueId().toString()))
         .findFirst();
      if (optionalLastDamageEntry.isPresent()) {
         cache.remove(optionalLastDamageEntry.get().getKey(), optionalLastDamageEntry.get().getValue());
      }
   }

   public static class LastDamageEntry {
      private long time;
      private UUID uuid;
      private Location location;

      @ConstructorProperties({"time", "uuid", "location"})
      public LastDamageEntry(long time, UUID uuid, Location location) {
         this.time = time;
         this.uuid = uuid;
         this.location = location;
      }

      public long getTime() {
         return this.time;
      }

      public UUID getUuid() {
         return this.uuid;
      }

      public Location getLocation() {
         return this.location;
      }
   }
}
