package net.frozenorb.foxtrot.gameplay.ability.type.kitmap.fall;

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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TrickOrTreat extends Ability {
   @Override
   public Category getCategory() {
      return Category.KIT_MAP;
   }

   @Override
   public String getDescription() {
      return "You have been given effects for 5 seconds!";
   }

   @Override
   public String getName() {
      return this.getClass().getSimpleName();
   }

   @Override
   public Material getMaterial() {
      return Material.APPLE;
   }

   @Override
   public String getDisplayName() {
      return ChatColor.RED.toString() + ChatColor.BOLD + "Trick or Treat";
   }

   @Override
   public List<String> getLore() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("");
      toReturn.add(ChatColor.translate("&6❙ &fRight Click for a 50/50 chance,"));
      toReturn.add(ChatColor.translate("&6❙ &fof getting Speed III and Strength II"));
      toReturn.add(ChatColor.translate("&6❙ &ffor 8 seconds or Slowness II, Weakness II"));
      toReturn.add(ChatColor.translate("&6❙ &fand Blindness III for 8 seconds."));
      toReturn.add("");
      toReturn.add(ChatColor.translate("&fCan be found in a &d&lPartner Package&f."));
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
      if (event.getAction().name().contains("RIGHT")) {
         Player player = event.getPlayer();
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

               if (ThreadLocalRandom.current().nextBoolean()) {
                  this.fullDescription = "You got the lucky effects!";
                  player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                  player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 180, 2), true);
                  player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 180, 1), true);
               } else {
                  this.fullDescription = "You got the unlucky effects!";
                  player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1.0F, 1.0F);
                  player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 180, 1), true);
                  player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 180, 1), true);
                  player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 180, 2), true);
               }

               this.applyCooldown(player);
            }
         }
      }
   }
}
