package net.frozenorb.foxtrot.gameplay.ability.type.kitmap.fall;

import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.gameplay.armorclass.type.kitmap.GrimReaperClass;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ReapersRevenge extends Ability {
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
      return Material.FERMENTED_SPIDER_EYE;
   }

   @Override
   public String getDisplayName() {
      return ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Reaper's Revenge";
   }

   @Override
   public List<String> getLore() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("");
      toReturn.add(ChatColor.translate("&6❙ &fRight Click for 1 level of"));
      toReturn.add(ChatColor.translate("&6❙ &fResistance, Regeneration and Speed"));
      toReturn.add(ChatColor.translate("&6❙ &ffor every player within a 25 block range."));
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

               int players = GrimReaperClass.getNearByEnemies(player, 30).size();
               player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 180, Math.min(players, 4)), true);
               player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 180, Math.min(players, 2)), true);
               player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 180, Math.min(players, 4)), true);
               this.applyCooldown(player);
            }
         }
      }
   }
}
