package net.frozenorb.foxtrot.gameplay.ability.type;

import cc.fyre.proton.util.ItemBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MindStone extends Ability {
   public MindStone() {
      this.hassanStack = ItemBuilder.copyOf(this.hassanStack.clone()).data((short)14).build();
   }

   @Override
   public Category getCategory() {
      return Category.AIRDROPS;
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
      return Material.INK_SACK;
   }

   @Override
   public String getDisplayName() {
      return ChatColor.YELLOW.toString() + ChatColor.BOLD + "Mind Stone";
   }

   @Override
   public List<String> getLore() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("");
      toReturn.add(ChatColor.translate("&6❙ &fHit a player to rotate their"));
      toReturn.add(ChatColor.translate("&6❙ &fhead 180 degrees and give them"));
      toReturn.add(ChatColor.translate("&6❙ &fBlindness X, Slowness III and"));
      toReturn.add(ChatColor.translate("&6❙ &fNausea III for 8 seconds."));
      toReturn.add("");
      toReturn.add(ChatColor.translate("&fCan be found in the &6&lOctober Mystery Box&f!"));
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
      return TimeUnit.MINUTES.toMillis(2L) + TimeUnit.SECONDS.toMillis(15L);
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
               if (PvPClassHandler.getPvPClass(target) != null) {
                  damager.sendMessage(
                     ChatColor.RED
                        + "You may not use a "
                        + this.getDisplayName()
                        + ChatColor.RED
                        + " on a "
                        + ChatColor.WHITE
                        + Objects.requireNonNull(PvPClassHandler.getPvPClass(target)).getName()
                        + ChatColor.RED
                        + "."
                  );
               } else {
                  Location location = target.getLocation();
                  location.setYaw(location.getYaw() + 180.0F);
                  target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 160, 2));
                  target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 160, 2));
                  target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 160, 9));
                  target.teleport(location);
                  target.sendMessage("");
                  target.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Ability Items");
                  target.sendMessage(ChatColor.GRAY + "You have been hit with the " + this.getDisplayName() + ChatColor.RED + ".");
                  target.sendMessage(ChatColor.RED + "Your head has been rotated and you were given negative effects!");
                  target.sendMessage("");
                  this.fullDescription = "Rotated " + target.getName() + "'s head and gave them negative effects.";
                  ItemStack itemStack = damager.getItemInHand();
                  if (itemStack.getAmount() == 1) {
                     damager.setItemInHand(null);
                  } else {
                     itemStack.setAmount(itemStack.getAmount() - 1);
                  }

                  this.applyCooldown(damager);
               }
            }
         }
      }
   }
}
