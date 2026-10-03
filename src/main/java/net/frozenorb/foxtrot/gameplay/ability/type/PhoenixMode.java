package net.frozenorb.foxtrot.gameplay.ability.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.ParticleEffect;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class PhoenixMode extends Ability {
   private Map<UUID, Location> cache = new HashMap<>();

   public PhoenixMode() {
      (new BukkitRunnable() {
         @Override
         public void run() {
            for (Entry<UUID, Location> entry : PhoenixMode.this.cache.entrySet()) {
               Player player = Foxtrot.getInstance().getServer().getPlayer(entry.getKey());
               if (player == null || player.isDead() || !player.isOnline()) {
                  this.cancel();
                  return;
               }

               Location location = entry.getValue();
               Location cornerOne = new Location(location.getWorld(), location.getX() - 5.0, location.getY() - 5.0, location.getZ() - 5.0);
               Location cornerTwo = new Location(location.getWorld(), location.getX() + 5.0, location.getY() + 5.0, location.getZ() + 5.0);

               for (Vector vector : PhoenixMode.getHollowCube(cornerOne, cornerTwo)) {
                  ParticleEffect.CLOUD.display(0.0F, 0.0F, 0.0F, 0.01F, 1, vector.toLocation(player.getWorld()), 50.0);
               }
            }
         }
      }).runTaskTimer(Foxtrot.getInstance(), 10L, 10L);
   }

   @Override
   public Category getCategory() {
      return Category.SEASONAL_CRATE;
   }

   @Override
   public String getDescription() {
      return "Created a cloud of particles! Fight with an advantage!";
   }

   @Override
   public String getName() {
      return this.getClass().getSimpleName();
   }

   @Override
   public Material getMaterial() {
      return Material.EMERALD;
   }

   @Override
   public String getDisplayName() {
      return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Phoenix Mode";
   }

   @Override
   public boolean inPartnerPackage() {
      return true;
   }

   @Override
   public List<String> getLore() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("");
      toReturn.add(ChatColor.translate("&6❙ &fClick to create a cloud of particles."));
      toReturn.add(ChatColor.translate("&6❙ &fInside the cloud, enemies deal &c&l-15% &fdamage"));
      toReturn.add(ChatColor.translate("&6❙ &fto you and you deal &c&l+15% &fdamage to enemies."));
      toReturn.add("");
      toReturn.add(ChatColor.translate("&fCan be found in the &4&lSquads Crate&f!"));
      return toReturn;
   }

   @Override
   public Boolean isAllowedAtLocation(Location location) {
      return Foxtrot.getInstance().getMapHandler().isKitMap()
         ? !DTRBitmask.KOTH.appliesAt(location)
            && !DTRBitmask.CONQUEST.appliesAt(location)
            && !DTRBitmask.CITADEL.appliesAt(location)
            && !DTRBitmask.DTC.appliesAt(location)
            && !DTRBitmask.SAFE_ZONE.appliesAt(location)
         : !Foxtrot.getInstance().getServerHandler().isWarzone(location)
            && location.getWorld().getEnvironment() == Environment.NORMAL
            && !DTRBitmask.KOTH.appliesAt(location)
            && !DTRBitmask.CONQUEST.appliesAt(location)
            && !DTRBitmask.CITADEL.appliesAt(location)
            && !DTRBitmask.DTC.appliesAt(location)
            && !DTRBitmask.SAFE_ZONE.appliesAt(location);
   }

   @Override
   public long getCooldown() {
      return 120000L;
   }

   @EventHandler(priority = EventPriority.NORMAL)
   public void onInteract(PlayerInteractEvent event) {
      if (event.getAction().name().contains("RIGHT")) {
         Player player = event.getPlayer();
         Location blockAt = player.getLocation();
         if (this.isSimilar(event.getItem())) {
            AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
            Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);
            if (!abilityUseEvent.isCancelled()) {
               event.setCancelled(true);
               if (player.getItemInHand().getAmount() == 1) {
                  player.setItemInHand(null);
               } else {
                  player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
               }

               this.cache.put(player.getUniqueId(), player.getLocation().clone().add(0.0, 1.0, 0.0));
               Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                  this.cache.remove(player.getUniqueId());
                  if (player.isOnline()) {
                     player.sendMessage("");
                     player.sendMessage(ChatColor.RED + "Your " + this.getDisplayName() + ChatColor.RED + " has expired!");
                     player.sendMessage("");
                  }
               }, 300L);
               this.applyCooldown(player);
            }
         }
      }
   }

   @EventHandler
   public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
         Player damager = (Player)event.getDamager();
         if (this.cache.containsKey(damager.getUniqueId())) {
            Location location = this.cache.get(damager.getUniqueId());
            if (!(location.distance(damager.getLocation()) > 5.0)) {
               event.setDamage(event.getDamage() * 1.15);
            }
         }
      }
   }

   @EventHandler
   public void onDamage(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
         Player player = (Player)event.getEntity();
         if (this.cache.containsKey(player.getUniqueId())) {
            Location location = this.cache.get(player.getUniqueId());
            if (!(location.distance(player.getLocation()) > 5.0)) {
               event.setDamage(event.getDamage() * 0.85);
            }
         }
      }
   }

   public static List<Vector> getHollowCube(Location corner1, Location corner2) {
      List<Vector> result = new ArrayList<>();
      int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
      int minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
      int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
      int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
      int maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
      int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

      for (int x = minX; x <= maxX; x++) {
         for (int z = minZ; z <= maxZ; z++) {
            result.add(new Vector(x, minY, z));
            result.add(new Vector(x, maxY, z));
         }
      }

      for (int x = minX; x <= maxX; x++) {
         for (int y = minY; y <= maxY; y++) {
            result.add(new Vector(x, y, minZ));
            result.add(new Vector(x, y, maxZ));
         }
      }

      for (int z = minZ; z <= maxZ; z++) {
         for (int y = minY; y <= maxY; y++) {
            result.add(new Vector(minX, y, z));
            result.add(new Vector(maxX, y, z));
         }
      }

      return result;
   }
}
