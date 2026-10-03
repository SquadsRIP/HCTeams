package net.frozenorb.foxtrot.gameplay.ability.type.portablebard;

import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.Team;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PortableStrength extends Ability {
   public static ItemStack itemStack;

   @Override
   public Category getCategory() {
      return Category.PORTABLE_BARD;
   }

   @Override
   public String getDescription() {
      return "Your team has been given Strength II for 5 seconds.";
   }

   public PortableStrength() {
      itemStack = this.hassanStack;
   }

   @Override
   public String getName() {
      return this.getClass().getSimpleName();
   }

   @Override
   public Material getMaterial() {
      return Material.BLAZE_POWDER;
   }

   @Override
   public String getDisplayName() {
      return ChatColor.GOLD.toString() + ChatColor.BOLD + "Strength";
   }

   @Override
   public List<String> getLore() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("");
      toReturn.add(ChatColor.translate("&6❙ &fGive you and your teammates"));
      toReturn.add(ChatColor.translate("&6❙ &e&l5 seconds &fof &c&lStrength"));
      toReturn.add("");
      toReturn.add(ChatColor.translate("&fCan be found in an &b&lAirdrop&f!"));
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
      if (event.getAction() == Action.RIGHT_CLICK_AIR
         || event.getAction() == Action.RIGHT_CLICK_BLOCK
         || event.getAction() == Action.LEFT_CLICK_AIR
         || event.getAction() == Action.LEFT_CLICK_BLOCK) {
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

               Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId());
               if (team != null) {
                  player.getNearbyEntities(25.0, 25.0, 25.0)
                     .stream()
                     .filter(it -> it instanceof Player && team.getOnlineMembers().contains(it))
                     .forEach(it -> ((Player)it).addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 1), true));
               }

               player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 1), true);
               this.applyCooldown(player);
            }
         }
      }
   }
}
