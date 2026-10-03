package net.frozenorb.foxtrot.gameplay.ability.type.kitmap;

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
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FiftyFifty extends Ability {
   @Override
   public Category getCategory() {
      return Category.KIT_MAP;
   }

   @Override
   public String getDescription() {
      return "";
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
      return ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Fifty Fifty";
   }

   @Override
   public List<String> getLore() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("");
      toReturn.add(ChatColor.translate("&6❙ &f50% chance of getting Strength II"));
      toReturn.add(ChatColor.translate("&6❙ &f50% chance of getting Weakness II"));
      toReturn.add("");
      toReturn.add(ChatColor.translate("&fCan be found in the &d&l??? &fCrate!"));
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

   @EventHandler(priority = EventPriority.NORMAL)
   public void onInteract(PlayerInteractEvent event) {
      if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
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

               if (ThreadLocalRandom.current().nextBoolean()) {
                  player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 1));
                  this.fullDescription = "You have received Strength II!";
               } else {
                  player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 1));
                  this.fullDescription = "You have received Weakness II!";
               }

               this.applyCooldown(player);
            }
         }
      }
   }
}
