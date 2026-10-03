package net.frozenorb.foxtrot.gameplay.ability.type.kitmap;

import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerFishEvent.State;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class Grapple extends Ability {
   public static short DURABILITY_LOSS = 6;
   public static double VELOCITY_CHANGE = 0.25;

   @Override
   public String getName() {
      return this.getClass().getSimpleName();
   }

   @Override
   public Material getMaterial() {
      return Material.FISHING_ROD;
   }

   @Override
   public String getDisplayName() {
      return ChatColor.GOLD.toString() + ChatColor.BOLD + this.getName();
   }

   @Override
   public List<String> getLore() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add(ChatColor.GRAY + "Hook the block you'd like to");
      toReturn.add(ChatColor.GRAY + "move towards and then release to get sent!");
      toReturn.add("");
      if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
         toReturn.add(ChatColor.translate("&fCan be found in an &c&lMario Crate&f!"));
      } else {
         toReturn.add(ChatColor.translate("&fCan be found in an &b&lAir Drop&f!"));
      }

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
      return 30000L;
   }

   @Override
   public Category getCategory() {
      return !Foxtrot.getInstance().getMapHandler().isKitMap() ? Category.AIRDROPS : Category.KIT_MAP;
   }

   @Override
   public String getDescription() {
      return "You have been boosted towards that block!";
   }

   @EventHandler(priority = EventPriority.NORMAL)
   public void onPlayerInteract(PlayerInteractEvent event) {
      Player player = event.getPlayer();
      if (this.isSimilar(player.getItemInHand())) {
         if (this.hasCooldown(player)) {
            event.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.NORMAL)
   private void onPlayerFish(PlayerFishEvent event) {
      if (!event.isCancelled()) {
         Player player = event.getPlayer();
         if (this.isSimilar(player.getItemInHand())) {
            if (event.getState() != State.FISHING) {
               Entity hooked = event.getHook();
               if (hooked != null) {
                  if (event.getCaught() != null) {
                     event.getHook().remove();
                  } else {
                     Location location = player.getLocation();
                     Location hookedLocation = hooked.getLocation();
                     if (!this.isAllowedAtLocation(hookedLocation)) {
                        Team ownerTeam = LandBoard.getInstance().getTeam(hookedLocation);
                        String teamName;
                        if (ownerTeam != null) {
                           teamName = ownerTeam.getName(event.getPlayer());
                        } else if (!Foxtrot.getInstance().getServerHandler().isWarzone(hookedLocation)) {
                           teamName = ChatColor.GRAY + "The Wilderness";
                        } else {
                           teamName = ChatColor.DARK_RED + "WarZone";
                        }

                        event.getPlayer()
                           .sendMessage(ChatColor.RED + "You cannot use a " + this.getDisplayName() + ChatColor.RED + " in " + teamName + ChatColor.RED + ".");
                     } else if (this.canGrapple(hookedLocation.getBlock())
                        || this.canGrapple(hookedLocation.getBlock().getRelative(BlockFace.UP))
                        || this.canGrapple(hookedLocation.getBlock().getRelative(BlockFace.DOWN))) {
                        player.getWorld().playSound(player.getPlayer().getLocation(), Sound.ZOMBIE_INFECT, 0.5F, 1.8F);
                        Vector velocity = new Vector(
                              hookedLocation.getX() - location.getX(), hookedLocation.getY() - location.getY(), hookedLocation.getZ() - location.getZ()
                           )
                           .multiply(VELOCITY_CHANGE);
                        player.setVelocity(velocity);
                        player.setMetadata("noflag", new FixedMetadataValue(Foxtrot.getInstance(), true));
                        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                           if (player.isOnline()) {
                              player.removeMetadata("noflag", Foxtrot.getInstance());
                           }
                        }, 60L);
                        event.setExpToDrop(0);
                        hooked.remove();
                        if (event.getCaught() != null && !(event.getCaught() instanceof LivingEntity)) {
                           event.getCaught().remove();
                        }

                        if (player.getItemInHand().getDurability() + DURABILITY_LOSS >= player.getItemInHand().getType().getMaxDurability()) {
                           player.setItemInHand(null);
                           player.getWorld().playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
                        } else {
                           player.getItemInHand().setDurability((short)(player.getItemInHand().getDurability() + DURABILITY_LOSS));
                        }

                        this.applyCooldown(player);
                     }
                  }
               }
            }
         }
      }
   }

   private boolean canGrapple(Block block) {
      return block.getTypeId() != 0 && block.getType() != Material.STATIONARY_WATER && block.getType() != Material.WATER;
   }
}
