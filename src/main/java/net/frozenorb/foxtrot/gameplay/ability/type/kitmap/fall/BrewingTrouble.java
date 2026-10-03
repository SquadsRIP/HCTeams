package net.frozenorb.foxtrot.gameplay.ability.type.kitmap.fall;

import cc.fyre.proton.util.TimeUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PotionEffectAddEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;

public class BrewingTrouble extends Ability {
   public static Map<UUID, Long> cache = new HashMap<>();

   public BrewingTrouble() {
      ItemMeta itemMeta = this.hassanStack.getItemMeta();
      itemMeta.setDisplayName(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Witch's Brewing Trouble");
      this.hassanStack.setItemMeta(itemMeta);
   }

   @Override
   public String getName() {
      return this.getClass().getSimpleName();
   }

   @Override
   public Material getMaterial() {
      return Material.CAULDRON_ITEM;
   }

   @Override
   public String getDisplayName() {
      return ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "Witch Trouble";
   }

   @Override
   public List<String> getLore() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("");
      toReturn.add(ChatColor.translate("&6❙ &fHit a player &c&l3 times"));
      toReturn.add(ChatColor.translate("&6❙ &fto remove and block all current and"));
      toReturn.add(ChatColor.translate("&6❙ &ffuture potion effects for &e&l12 seconds&f."));
      toReturn.add("");
      toReturn.add(ChatColor.translate("&fCan be found in the &4&lOP Crate&f!"));
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
      return 90000L;
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
            if (PvPClassHandler.getPvPClass(target) != null) {
               damager.sendMessage(CC.translate(this.getDisplayName() + " &ccan only be used on Diamonds!"));
            } else if (cache.containsKey(target.getUniqueId())) {
               damager.sendMessage(
                  ChatColor.translate(
                     "&c"
                        + target.getName()
                        + " already has their effects disabled for &l"
                        + TimeUtils.formatIntoMMSS((int)(cache.get(target.getUniqueId()) - System.currentTimeMillis()) / 1000)
                        + "&c."
                  )
               );
            } else {
               int value = target.hasMetadata("EFFECT_DISABLER") ? target.getMetadata("EFFECT_DISABLER").get(0).asInt() + 1 : 1;
               AbilityUseEvent abilityUseEvent = new AbilityUseEvent(damager, target, damager.getLocation(), this, false);
               if (value != 3) {
                  abilityUseEvent.setOneHit(true);
               }

               Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);
               if (!abilityUseEvent.isCancelled()) {
                  target.setMetadata("EFFECT_DISABLER", new FixedMetadataValue(Foxtrot.getInstance(), value));
                  if (value != 3) {
                     damager.sendMessage(
                        CC.translate("&cYou have to hit &f" + target.getName() + " &c" + (3 - value) + " more time" + (3 - value == 1 ? "" : "s") + "!")
                     );
                  } else {
                     target.removeMetadata("EFFECT_DISABLER", Foxtrot.getInstance());
                     cache.put(target.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15L));
                     if (damager.getItemInHand().getAmount() == 1) {
                        damager.setItemInHand(null);
                     } else {
                        damager.getItemInHand().setAmount(damager.getItemInHand().getAmount() - 1);
                     }

                     this.fullDescription = "Took and blocked all current and future effects for 15 seconds!";
                     target.sendMessage("");
                     target.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Ability Items");
                     target.sendMessage(ChatColor.GRAY + "You have been hit with the " + this.getDisplayName() + ChatColor.GRAY + ".");
                     target.sendMessage(ChatColor.RED + "All effects have been taken, and for 12 seconds all future effects will be blocked.");
                     target.sendMessage("");

                     for (PotionEffect activePotionEffect : new ArrayList<>(target.getActivePotionEffects())) {
                        target.removePotionEffect(activePotionEffect.getType());
                     }

                     Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                        cache.remove(target.getUniqueId());
                        if (damager.isOnline()) {
                           damager.sendMessage("");
                           damager.sendMessage(ChatColor.RED + "Your " + this.getDisplayName() + ChatColor.RED + " has expired!");
                           damager.sendMessage("");
                        }

                        if (target.isOnline()) {
                           target.sendMessage("");
                           target.sendMessage(ChatColor.GREEN + "The " + this.getDisplayName() + ChatColor.GREEN + " on you has expired!");
                           target.sendMessage("");
                        }
                     }, 300L);
                     this.applyCooldown(damager);
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void onPlace(PotionEffectAddEvent event) {
      if (!event.isCancelled() && event.getEntity() instanceof Player) {
         Player player = (Player)event.getEntity();
         if (cache.containsKey(player.getUniqueId())) {
            event.setCancelled(true);
         }
      }
   }
}
