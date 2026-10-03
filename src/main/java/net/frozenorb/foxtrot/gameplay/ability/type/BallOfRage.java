package net.frozenorb.foxtrot.gameplay.ability.type;

import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BallOfRage extends Ability {
   @Override
   public String getName() {
      return this.getClass().getSimpleName();
   }

   @Override
   public Material getMaterial() {
      return Material.SNOW_BALL;
   }

   @Override
   public String getDisplayName() {
      return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Ball Of Rage";
   }

   @Override
   public List<String> getLore() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("");
      toReturn.add(ChatColor.translate("&6❙ &fThrow to create a cloud of effects"));
      toReturn.add(ChatColor.translate("&6❙ &fwhere all teammates within 5 block"));
      toReturn.add(ChatColor.translate("&6❙ &fradius will be given positive effects."));
      toReturn.add("");
      toReturn.add(ChatColor.translate("&fCan be found in the &4&lSimplyTrash Crate&f!"));
      return toReturn;
   }

   @Override
   public boolean inPartnerPackage() {
      return true;
   }

   @Override
   public Category getCategory() {
      return Category.TREASURE_CHEST;
   }

   @Override
   public String getDescription() {
      return "Once this ball lands teammates effected will be given effects!";
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
      return 120000L;
   }

   @EventHandler
   public void onLaunch(ProjectileLaunchEvent event) {
      if (event.getEntity() instanceof Snowball && event.getEntity().getShooter() instanceof Player) {
         Player player = (Player)event.getEntity().getShooter();
         if (this.isSimilar(player.getItemInHand())) {
            event.getEntity().setMetadata("BALL_OF_RAGE", new FixedMetadataValue(Foxtrot.getInstance(), player.getUniqueId().toString()));
            this.applyCooldown(player);
         }
      }
   }

   @EventHandler(priority = EventPriority.NORMAL)
   public void onInteract(PlayerInteractEvent event) {
      if (event.hasItem() && this.isSimilar(event.getPlayer().getItemInHand())) {
         if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(CC.translate("&cYou may not use " + this.getDisplayName() + " &cwhilst your &a&lPvP Timer &cis active!"));
            event.getPlayer().updateInventory();
            event.setCancelled(true);
         } else if (this.hasCooldown(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
         } else if (!this.isAllowedAtLocation(event.getPlayer().getLocation())) {
            Team ownerTeam = LandBoard.getInstance().getTeam(event.getPlayer().getLocation());
            String teamName;
            if (ownerTeam != null) {
               teamName = ownerTeam.getName(event.getPlayer());
            } else if (!Foxtrot.getInstance().getServerHandler().isWarzone(event.getPlayer().getLocation())) {
               teamName = ChatColor.GRAY + "The Wilderness";
            } else {
               teamName = ChatColor.DARK_RED + "WarZone";
            }

            event.getPlayer()
               .sendMessage(ChatColor.RED + "You cannot use a " + this.getDisplayName() + ChatColor.RED + " in " + teamName + ChatColor.RED + ".");
            event.getPlayer().updateInventory();
            event.setCancelled(true);
         }
      }
   }

   @EventHandler
   private void onLand(ProjectileHitEvent event) {
      if (event.getEntity() instanceof Snowball && event.getEntity().getShooter() instanceof Player && event.getEntity().hasMetadata("BALL_OF_RAGE")) {
         Projectile snowBall = event.getEntity();
         Player player = (Player)snowBall.getShooter();
         snowBall.getWorld().createExplosion(snowBall.getLocation(), 0.0F);
         snowBall.getWorld().spigot().playEffect(snowBall.getLocation().clone().add(0.0, 1.0, 0.0), Effect.EXPLOSION_HUGE);
         Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
         snowBall.getNearbyEntities(6.0, 6.0, 6.0)
            .stream()
            .filter(it -> it instanceof Player && this.isAllowedAtLocation(it.getLocation()))
            .map(it -> (Player)it)
            .forEach(it -> {
               if (team == null || team.isMember(it.getUniqueId())) {
                  if (team != null || it.getName().equalsIgnoreCase(player.getName())) {
                     it.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 1), true);
                     it.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 120, 2), true);
                     it.sendMessage("");
                     it.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Ability Items");
                     it.sendMessage(ChatColor.GRAY + "You have been hit by " + this.getDisplayName() + ChatColor.GRAY + ".");
                     it.sendMessage(ChatColor.RED + "You were given Strength II and Resistance III for 5 seconds!");
                     it.sendMessage("");
                  }
               }
            });
      }
   }
}
