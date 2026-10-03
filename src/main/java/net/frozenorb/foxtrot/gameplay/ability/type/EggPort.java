package net.frozenorb.foxtrot.gameplay.ability.type;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class EggPort extends Ability {
   public static final int SWAP_RADIUS = 15;

   @Override
   public String getName() {
      return this.getClass().getSimpleName();
   }

   @Override
   public Material getMaterial() {
      return Material.EGG;
   }

   @Override
   public String getDisplayName() {
      return ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Eggport";
   }

   @Override
   public List<String> getLore() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("");
      toReturn.add(ChatColor.translate("&6❙ &fSwitch places with your"));
      toReturn.add(ChatColor.translate("&6❙ &fenemy that are within &c&l15 &fblocks!"));
      toReturn.add("");
      if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
         toReturn.add(ChatColor.translate("&fCan be found in the &d&lAbility Crate&f!"));
      } else {
         toReturn.add(ChatColor.translate("&fCan be found in the &e&ki&6&lHalloween Crate&e&ki&f!"));
      }

      return toReturn;
   }

   @Override
   public Category getCategory() {
      return Category.SEASONAL_CRATE;
   }

   @Override
   public String getDescription() {
      return "You will swap positions with whoever this egg hits!";
   }

   @Override
   public Boolean isAllowedAtLocation(Location location) {
      if (location.getWorld().getName().equalsIgnoreCase("sg")) {
         return true;
      } else {
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
   }

   @Override
   public long getCooldown() {
      return 10000L;
   }

   @EventHandler
   public void onLaunch(ProjectileLaunchEvent event) {
      if (event.getEntity() instanceof Egg && event.getEntity().getShooter() instanceof Player) {
         Player player = (Player)event.getEntity().getShooter();
         if (this.isSimilar(player.getItemInHand())) {
            event.getEntity().setMetadata("Eggport", new FixedMetadataValue(Foxtrot.getInstance(), player.getUniqueId().toString()));
            this.applyCooldown(player);
         }
      }
   }

   @EventHandler(priority = EventPriority.NORMAL)
   public void onInteract(PlayerInteractEvent event) {
      if (event.hasItem() && this.isSimilar(event.getPlayer().getItemInHand())) {
         if (event.getAction().name().contains("RIGHT")) {
            Player player = event.getPlayer();
            if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId())) {
               player.sendMessage(CC.translate("&cYou may not use &d&lEgg Ports &cwhile your &a&lPvP Timer &cis active!"));
               player.updateInventory();
               event.setCancelled(true);
            } else {
               AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
               Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);
               if (abilityUseEvent.isCancelled()) {
                  event.setCancelled(true);
                  player.updateInventory();
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onCreature(CreatureSpawnEvent event) {
      if (event.getSpawnReason() == SpawnReason.EGG) {
         event.setCancelled(true);
      }
   }

   @EventHandler(priority = EventPriority.NORMAL)
   private void onDamage(EntityDamageByEntityEvent event) {
      if (!event.isCancelled() && event.getEntity() instanceof Player && event.getDamager() instanceof Egg && event.getDamager().hasMetadata("Eggport")) {
         Player shooter = Foxtrot.getInstance().getServer().getPlayer(UUID.fromString(event.getDamager().getMetadata("Eggport").get(0).asString()));
         Player target = (Player)event.getEntity();
         if (shooter != null) {
            if (!this.isAllowedAtLocation(target.getLocation())) {
               Team ownerTeam = LandBoard.getInstance().getTeam(target.getLocation());
               String location;
               if (ownerTeam != null) {
                  location = ownerTeam.getName(shooter);
               } else if (!Foxtrot.getInstance().getServerHandler().isWarzone(target.getLocation())) {
                  location = ChatColor.GRAY + "The Wilderness";
               } else {
                  location = ChatColor.DARK_RED + "WarZone";
               }

               shooter.getInventory().addItem(this.hassanStack);
               shooter.updateInventory();
               shooter.sendMessage(
                  ChatColor.RED + "You may not swap locations with " + target.getDisplayName() + " who is in " + location + ChatColor.RED + "."
               );
            } else {
               Location shooterLocation = shooter.getLocation().clone();
               Location targetLocation = target.getLocation().clone();
               if (shooterLocation.distance(targetLocation) > 15.0) {
                  shooter.sendMessage(ChatColor.RED + "You need to be within 15 blocks of that player!");
               } else {
                  shooter.teleport(targetLocation);
                  target.teleport(shooterLocation);
                  shooter.sendMessage("");
                  shooter.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Ability Items");
                  shooter.sendMessage(ChatColor.GRAY + "You have hit a player with the " + this.getDisplayName() + ChatColor.GRAY + ".");
                  shooter.sendMessage(ChatColor.RED + "You have swapped positions with " + target.getName() + "!");
                  shooter.sendMessage("");
                  target.sendMessage("");
                  target.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Ability Items");
                  target.sendMessage(ChatColor.GRAY + "You have been hit with the " + this.getDisplayName() + ChatColor.GRAY + ".");
                  target.sendMessage(ChatColor.RED + "You have swapped positions with " + shooter.getName() + "!");
                  target.sendMessage("");
               }
            }
         }
      }
   }
}
