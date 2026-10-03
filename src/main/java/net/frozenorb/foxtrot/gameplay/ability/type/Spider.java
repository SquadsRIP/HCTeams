package net.frozenorb.foxtrot.gameplay.ability.type;

import cc.fyre.neutron.Neutron;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class Spider extends Ability {
   @Override
   public Category getCategory() {
      return Category.PARTNER_CRATE;
   }

   @Override
   public String getDescription() {
      return "For the next 15 all hits have a 10% chance of spawning cobweb.";
   }

   @Override
   public String getName() {
      return this.getClass().getSimpleName();
   }

   @Override
   public Material getMaterial() {
      return Material.SPIDER_EYE;
   }

   @Override
   public String getDisplayName() {
      return ChatColor.YELLOW.toString() + ChatColor.BOLD + "Spider Ability";
   }

   @Override
   public List<String> getLore() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("");
      toReturn.add(ChatColor.translate("&6❙ &fClick to activate and for the"));
      toReturn.add(ChatColor.translate("&6❙ &fnext &e&l15 seconds&f, all hits dealt"));
      toReturn.add(ChatColor.translate("&6❙ &fhave a &c&l25% &fchance of putting a"));
      toReturn.add(ChatColor.translate("&6❙ &fcobweb underneath the enemy."));
      toReturn.add("");
      toReturn.add(ChatColor.translate("&fCan be found in the &d&lPartner Crate&f!"));
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
   public boolean inPartnerPackage() {
      return true;
   }

   @Override
   public long getCooldown() {
      return 90000L;
   }

   @EventHandler(priority = EventPriority.NORMAL)
   public void onInteract(PlayerInteractEvent event) {
      if (event.getAction() == Action.RIGHT_CLICK_AIR
         || event.getAction() == Action.RIGHT_CLICK_BLOCK
         || event.getAction() == Action.LEFT_CLICK_AIR
         || event.getAction() == Action.LEFT_CLICK_BLOCK) {
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

               player.setMetadata("SPIDER", new FixedMetadataValue(Foxtrot.getInstance(), true));
               Foxtrot.getInstance()
                  .getServer()
                  .getScheduler()
                  .runTaskLater(Foxtrot.getInstance(), () -> player.removeMetadata("SPIDER", Foxtrot.getInstance()), 300L);
               this.applyCooldown(player);
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   private void onPlayerHit(EntityDamageByEntityEvent event) {
      if (!event.isCancelled() && event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
         Player target = (Player)event.getEntity();
         Player damager = (Player)event.getDamager();
         if (damager.hasMetadata("SPIDER")) {
            if (ThreadLocalRandom.current().nextInt(100) <= 25) {
               Block blockAt = target.getLocation().getBlock();
               if (blockAt.getType() != Material.WEB) {
                  Block block2 = blockAt.getRelative(BlockFace.SOUTH);
                  Block block3 = blockAt.getRelative(BlockFace.WEST);
                  Block block4 = block3.getRelative(BlockFace.SOUTH);
                  if (blockAt.getType() == Material.AIR) {
                     blockAt.setType(Material.WEB);
                  }

                  if (block2.getType() == Material.AIR) {
                     block2.setType(Material.WEB);
                  }

                  if (block3.getType() == Material.AIR) {
                     block3.setType(Material.WEB);
                  }

                  if (block4.getType() == Material.AIR) {
                     block4.setType(Material.WEB);
                  }

                  Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                     if (blockAt.getType() == Material.WEB) {
                        blockAt.setType(Material.AIR);
                     }

                     if (block2.getType() == Material.WEB) {
                        block2.setType(Material.AIR);
                     }

                     if (block3.getType() == Material.WEB) {
                        block3.setType(Material.AIR);
                     }

                     if (block4.getType() == Material.WEB) {
                        block4.setType(Material.AIR);
                     }
                  }, 300L);
                  damager.playSound(damager.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                  damager.sendMessage(
                     ChatColor.RED
                        + "You have put "
                        + Neutron.getInstance().getProfileHandler().findDisplayName(target.getUniqueId())
                        + ChatColor.RED
                        + " in a cobweb!"
                  );
                  damager.sendMessage(ChatColor.RED + target.getName() + " has been put in a cobweb due to the " + this.getDisplayName() + "!");
                  target.playSound(target.getLocation(), Sound.ZOMBIE_WOODBREAK, 1.0F, 1.0F);
                  target.sendMessage(ChatColor.RED + "You were placed in a cobweb due to the " + this.getDisplayName() + ChatColor.RED + "!");
               }
            }
         }
      }
   }
}
