package net.frozenorb.foxtrot.gameplay.ability.type.kitmap.fall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class PiercingShot extends Ability {
   public static Map<UUID, UUID> cache = new HashMap<>();

   @Override
   public String getName() {
      return this.getClass().getSimpleName();
   }

   @Override
   public Material getMaterial() {
      return Material.ARROW;
   }

   @Override
   public String getDisplayName() {
      return ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Piercing Shot";
   }

   @Override
   public List<String> getLore() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("");
      toReturn.add(ChatColor.translate("&6❙ &fWhen you hit a player with this 3 times"));
      toReturn.add(ChatColor.translate("&6❙ &fyou will now deal 20% more damage towards them."));
      toReturn.add("");
      toReturn.add(ChatColor.translate("&fCan be found in the &d&lPartner Package &f!"));
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
      return 150000L;
   }

   @Override
   public Category getCategory() {
      return Category.KIT_MAP;
   }

   @Override
   public String getDescription() {
      return "";
   }

   @EventHandler(priority = EventPriority.MONITOR)
   private void onDamage(EntityDamageByEntityEvent event) {
      if (!event.isCancelled() && event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
         Player target = (Player)event.getEntity();
         Player damager = (Player)event.getDamager();
         if (damager.getItemInHand() != null && this.isSimilar(damager.getItemInHand())) {
            AbilityUseEvent abilityUseEvent = new AbilityUseEvent(damager, target, damager.getLocation(), this, false);
            Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);
            if (!abilityUseEvent.isCancelled()) {
               if (target.hasMetadata("UNDEAD_WARRIOR")) {
                  damager.sendMessage(ChatColor.RED + "You may not use this on an Undead Warrior!");
               } else {
                  int value = target.hasMetadata("PIERCING_SHOT") ? target.getMetadata("PIERCING_SHOT").get(0).asInt() + 1 : 1;
                  target.setMetadata("PIERCING_SHOT", new FixedMetadataValue(Foxtrot.getInstance(), value));
                  if (value != 3) {
                     damager.sendMessage(
                        CC.translate("&cYou have to hit &f" + target.getName() + " &c" + (3 - value) + " more time" + (3 - value == 1 ? "" : "s") + "!")
                     );
                  } else {
                     target.removeMetadata("PIERCING_SHOT", Foxtrot.getInstance());
                     cache.put(target.getUniqueId(), damager.getUniqueId());
                     if (damager.getItemInHand().getAmount() == 1) {
                        damager.setItemInHand(null);
                     } else {
                        damager.getItemInHand().setAmount(damager.getItemInHand().getAmount() - 1);
                     }

                     damager.playSound(damager.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                     target.playSound(target.getLocation(), Sound.ANVIL_BREAK, 1.0F, 1.0F);
                     target.sendMessage(ChatColor.RED + "You have been hit with the " + this.getDisplayName() + ChatColor.RED + "!");
                     this.fullDescription = "For the next 10 seconds you now deal 20% more damage towards them!";
                     Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                        UUID var10000 = cache.remove(target.getUniqueId());
                     }, 200L);
                     this.applyCooldown(damager);
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
         Player player = (Player)event.getEntity();
         if (cache.containsKey(player.getUniqueId())) {
            event.setDamage(event.getDamage() * 1.25);
         }
      }
   }

   public static Map<UUID, UUID> getCache() {
      return cache;
   }
}
