package net.frozenorb.foxtrot.listener;

import java.util.Iterator;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.event.DisallowedPotionDrinkEvent;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

public class PotionLimiterListener implements Listener {
   private static final short AWKWARD_POTION = 16;

   // Vanilla brewing stands ignore potions.yml (only FancyBrewer checks it), so stop any brew
   // that would turn a bottle into a potion type whose limit is 0 (Strength, Regen, Weakness, Harming).
   // BrewEvent is missing from the compile-only server stub, so it is registered by name.
   public void registerBrewGuard(Plugin plugin) {
      Class<? extends Event> brewEvent;
      try {
         brewEvent = Class.forName("org.bukkit.event.inventory.BrewEvent").asSubclass(Event.class);
      } catch (ClassNotFoundException exception) {
         plugin.getLogger().warning("BrewEvent not found; vanilla brewing stands are not potion-limited.");
         return;
      }

      plugin.getServer().getPluginManager().registerEvent(brewEvent, this, EventPriority.HIGH, (listener, event) -> {
         if (brewEvent.isInstance(event)) {
            this.onBrew((BlockEvent)event);
         }
      }, plugin, true);
   }

   private void onBrew(BlockEvent event) {
      BlockState state = event.getBlock().getState();
      if (state instanceof BrewingStand) {
         BrewerInventory contents = ((BrewingStand)state).getInventory();
         ItemStack ingredient = contents.getIngredient();
         if (ingredient != null) {
            for (int slot = 0; slot < 3; slot++) {
               ItemStack bottle = contents.getItem(slot);
               if (bottle != null && bottle.getType() == Material.POTION) {
                  PotionType result = this.getBrewResult(ingredient.getType(), bottle.getDurability());
                  Integer limit = result == null ? null : Foxtrot.getInstance().getServerHandler().getPotionLimits().get(result);
                  if (limit != null && limit == 0) {
                     ((Cancellable)event).setCancelled(true);
                     return;
                  }
               }
            }
         }
      }
   }

   private PotionType getBrewResult(Material ingredient, short data) {
      PotionType current = PotionType.getByDamageValue(data & 15);
      switch (ingredient) {
         case BLAZE_POWDER:
            return data == AWKWARD_POTION ? PotionType.STRENGTH : null;
         case GHAST_TEAR:
            return data == AWKWARD_POTION ? PotionType.REGEN : null;
         case GLOWSTONE_DUST:
         case REDSTONE:
         case SULPHUR:
            return current;
         case FERMENTED_SPIDER_EYE:
            if (current == null || current == PotionType.WATER || current == PotionType.STRENGTH || current == PotionType.REGEN) {
               return PotionType.WEAKNESS;
            } else if (current == PotionType.POISON || current == PotionType.INSTANT_HEAL) {
               return PotionType.INSTANT_DAMAGE;
            }

            return null;
         default:
            return null;
      }
   }

   @EventHandler(priority = EventPriority.HIGH)
   public void onPotionSplash(PotionSplashEvent event) {
      for (LivingEntity livingEntity : event.getAffectedEntities()) {
         if (DTRBitmask.SAFE_ZONE.appliesAt(livingEntity.getLocation())) {
            event.setIntensity(livingEntity, 0.0);
         }
      }

      Potion potion = Potion.fromItemStack(event.getPotion().getItem());
      if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
         if (!Foxtrot.getInstance().getServerHandler().isDrinkablePotionAllowed(potion.getType())
            && !Foxtrot.getInstance().getServerHandler().isPotionLevelAllowed(potion.getType(), potion.getLevel())) {
            if (!potion.hasExtendedDuration() || potion.getType() != PotionType.SLOWNESS && potion.getType() != PotionType.POISON) {
               if (potion.getType() == PotionType.POISON && 1 < potion.getLevel()) {
                  event.setCancelled(true);
               }
            } else {
               event.setCancelled(true);
            }
         } else {
            event.setCancelled(true);
         }
      }

      if (potion.getType() == PotionType.INSTANT_DAMAGE) {
         event.setCancelled(true);
      } else if (potion.getType() == PotionType.STRENGTH) {
         event.setCancelled(true);
      } else {
         Iterator iterator;
         if (event.getPotion().getShooter() != null
            && event.getPotion().getShooter() instanceof Player
            && !event.isCancelled()
            && (iterator = event.getPotion().getEffects().iterator()).hasNext()
            && FoxListener.DEBUFFS.contains(((PotionEffect)iterator.next()).getType())
            && (
               event.getAffectedEntities().size() > 1
                  || event.getAffectedEntities().size() == 1 && !event.getAffectedEntities().contains(event.getPotion().getShooter())
            )) {
            SpawnTagHandler.addOffensiveSeconds((Player)event.getPotion().getShooter(), SpawnTagHandler.getMaxTagTime((Player)event.getPotion().getShooter()));
         }
      }
   }

   @EventHandler
   public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
      if (event.getItem().getType() == Material.POTION && event.getItem().getDurability() != 0) {
         Potion potion = Potion.fromItemStack(event.getItem());
         if ((
               Foxtrot.getInstance().getServerHandler().isDrinkablePotionAllowed(potion.getType())
                  || Foxtrot.getInstance().getServerHandler().isPotionLevelAllowed(potion.getType(), potion.getLevel())
            )
            && !new DisallowedPotionDrinkEvent(event.getPlayer(), potion).isAllowed()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.YELLOW + "This potion is not usable!");
         }
      }
   }
}
