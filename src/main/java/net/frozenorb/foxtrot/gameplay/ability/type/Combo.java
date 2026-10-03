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
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Combo extends Ability {
   public static Map<UUID, Integer> cache = new HashMap<>();

   @Override
   public String getName() {
      return this.getClass().getSimpleName();
   }

   @Override
   public Material getMaterial() {
      return Material.RED_ROSE;
   }

   @Override
   public String getDisplayName() {
      return ChatColor.GREEN.toString() + ChatColor.BOLD + "Combo Ability";
   }

   @Override
   public List<String> getLore() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("");
      toReturn.add(ChatColor.translate("&6❙ &fGet a second of strength II for the"));
      toReturn.add(ChatColor.translate("&6❙ &famount of hits dealt within &e&l10 seconds&f."));
      toReturn.add("");
      toReturn.add(ChatColor.translate("&fCan be found in the &4&lSquads Crate&f!"));
      return toReturn;
   }

   @Override
   public Category getCategory() {
      return Category.PARTNER_CRATE;
   }

   @Override
   public String getDescription() {
      return "Each hit will grant you a second of Strength II!";
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

   @EventHandler(priority = EventPriority.LOWEST)
   private void onInteract(PlayerInteractEvent event) {
      Player player = event.getPlayer();
      if (this.isSimilar(player.getItemInHand()) && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
         event.setCancelled(true);
         AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
         Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);
         if (!abilityUseEvent.isCancelled()) {
            event.setCancelled(true);
            if (player.getItemInHand().getAmount() == 1) {
               player.setItemInHand(null);
            } else {
               player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
            }

            player.updateInventory();
            cache.put(player.getUniqueId(), 0);
            this.applyCooldown(event.getPlayer());
            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
               int seconds = cache.remove(player.getUniqueId());
               if (player.isOnline()) {
                  player.playSound(player.getLocation(), Sound.EXPLODE, 1.0F, 1.0F);
                  player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * seconds, 1), true);
                  player.sendMessage("");
                  player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Ability Items");
                  player.sendMessage(ChatColor.GRAY + "Your " + this.getDisplayName() + ChatColor.GRAY + " has been activated!");
                  player.sendMessage(ChatColor.RED + "You have been given a total of " + seconds + " seconds of Strength II!");
                  player.sendMessage("");
               }
            }, 200L);
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   private void onDamage(EntityDamageByEntityEvent event) {
      if (!event.isCancelled() && event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
         Player damager = (Player)event.getDamager();
         if (cache.containsKey(damager.getUniqueId())) {
            int amount = cache.getOrDefault(damager.getUniqueId(), 0) + 1;
            if (amount <= 10) {
               if (!Foxtrot.getInstance().getAbilityCooldownsScoreboardMap().isScoreboard(damager.getUniqueId())) {
                  damager.sendMessage(ChatColor.RED + "Found another hit, you will get " + amount + " seconds of Strength II.");
               }

               cache.put(damager.getUniqueId(), amount);
            }
         }
      }
   }
}
