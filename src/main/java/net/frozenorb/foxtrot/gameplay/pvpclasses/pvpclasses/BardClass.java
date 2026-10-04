package net.frozenorb.foxtrot.gameplay.pvpclasses.pvpclasses;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.type.portablebard.PortableResistance;
import net.frozenorb.foxtrot.gameplay.ability.type.portablebard.PortableStrength;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClass;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.gameplay.pvpclasses.energy.EnergyEffect;
import net.frozenorb.foxtrot.gameplay.pvpclasses.event.BardEffectUseEvent;
import net.frozenorb.foxtrot.listener.FoxListener;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BardClass extends PvPClass implements Listener {
   public final Map<Material, EnergyEffect> BARD_CLICK_EFFECTS = new HashMap<>();
   public final Map<Material, EnergyEffect> BARD_PASSIVE_EFFECTS = new HashMap<>();
   public static int BARD_RANGE = 30;
   public static final int EFFECT_COOLDOWN = 10000;
   public static final float ULTIMATE_PER_BARD_EFFECT = 10.0F;

   public BardClass() {
      super("Bard", null, true);
      this.getPermanentEffects().add(PotionEffectType.SPEED);
      this.getPermanentEffects().add(PotionEffectType.REGENERATION);
      this.getPermanentEffects().add(PotionEffectType.DAMAGE_RESISTANCE);
      if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
         BARD_RANGE = 35;
      }

      this.BARD_CLICK_EFFECTS.put(Material.BLAZE_POWDER, EnergyEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 1), 45));
      this.BARD_CLICK_EFFECTS.put(Material.SUGAR, EnergyEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.SPEED, 120, 2), 20));
      this.BARD_CLICK_EFFECTS.put(Material.FEATHER, EnergyEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.JUMP, 100, 6), 25));
      this.BARD_CLICK_EFFECTS.put(Material.IRON_INGOT, EnergyEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 2), 40));
      this.BARD_CLICK_EFFECTS.put(Material.GHAST_TEAR, EnergyEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.REGENERATION, 100, 2), 45));
      this.BARD_CLICK_EFFECTS.put(Material.MAGMA_CREAM, EnergyEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 920, 0), 40));
      this.BARD_CLICK_EFFECTS.put(Material.SPIDER_EYE, EnergyEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.WITHER, 100, 1), 35));
      this.BARD_PASSIVE_EFFECTS.put(Material.BLAZE_POWDER, EnergyEffect.fromPotion(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 0)));
      this.BARD_PASSIVE_EFFECTS.put(Material.SUGAR, EnergyEffect.fromPotion(new PotionEffect(PotionEffectType.SPEED, 120, 1)));
      this.BARD_PASSIVE_EFFECTS.put(Material.FEATHER, EnergyEffect.fromPotion(new PotionEffect(PotionEffectType.JUMP, 120, 1)));
      this.BARD_PASSIVE_EFFECTS.put(Material.IRON_INGOT, EnergyEffect.fromPotion(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 120, 0)));
      this.BARD_PASSIVE_EFFECTS.put(Material.GHAST_TEAR, EnergyEffect.fromPotion(new PotionEffect(PotionEffectType.REGENERATION, 120, 0)));
      this.BARD_PASSIVE_EFFECTS.put(Material.MAGMA_CREAM, EnergyEffect.fromPotion(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 120, 0)));
      if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
         BARD_RANGE = 40;
         this.BARD_PASSIVE_EFFECTS.put(Material.FERMENTED_SPIDER_EYE, EnergyEffect.fromPotion(new PotionEffect(PotionEffectType.INVISIBILITY, 120, 0)));
      } else {
         this.BARD_CLICK_EFFECTS
            .put(Material.FERMENTED_SPIDER_EYE, EnergyEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.INVISIBILITY, 920, 0), 40));
      }
   }

   @Override
   public boolean canApply(Player player) {
      return !this.canUseEffect(player) ? false : super.canApply(player);
   }

   @Override
   public boolean qualifies(Player player, PlayerInventory armor) {
      return this.wearingAllArmor(armor)
         && armor.getHelmet().getType() == Material.GOLD_HELMET
         && armor.getChestplate().getType() == Material.GOLD_CHESTPLATE
         && armor.getLeggings().getType() == Material.GOLD_LEGGINGS
         && armor.getBoots().getType() == Material.GOLD_BOOTS;
   }

   @Override
   public void apply(Player player) {
      player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
      player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), true);
      player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0), true);
      if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
         player.sendMessage(ChatColor.YELLOW + "You are in PvP Protection and cannot use Bard effects. Type '/pvp enable' to remove your protection.");
      }
   }

   @Override
   public void tick(Player player) {
      if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
         player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
      }

      if (!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
         player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
      }

      if (!player.hasPotionEffect(PotionEffectType.REGENERATION)) {
         player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
      }

      if (player.getItemInHand() != null
         && this.BARD_PASSIVE_EFFECTS.containsKey(player.getItemInHand().getType())
         && !DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
         if (!this.canUseEffect(player)) {
            return;
         }

         this.giveBardEffect(player, this.BARD_PASSIVE_EFFECTS.get(player.getItemInHand().getType()), true, false, false);
      }

      super.tick(player);
   }

   @EventHandler
   private void onDamage(EntityDamageEvent event) {
      if (event.getEntity() instanceof Player) {
         if (event.getCause().equals(DamageCause.ENTITY_ATTACK)) {
            Player player = (Player)event.getEntity();
            if (PvPClassHandler.hasKitOn(player, this)) {
               event.setDamage(getDamageValue(event) * 0.8);
            }
         }
      }
   }

   @EventHandler
   public void onPlayerInteract(PlayerInteractEvent event) {
      if (event.getAction().name().contains("RIGHT_")
         && event.hasItem()
         && this.BARD_CLICK_EFFECTS.containsKey(event.getItem().getType())
         && PvPClassHandler.hasKitOn(event.getPlayer(), this)
         && Foxtrot.getInstance().getPvpClassHandler().getEnergyService().getCache().containsKey(event.getPlayer().getUniqueId())) {
         if (this.canUseEffect(event.getPlayer())) {
            if (DTRBitmask.SAFE_ZONE.appliesAt(event.getPlayer().getLocation())) {
               event.getPlayer().sendMessage(ChatColor.YELLOW + "Bard effects cannot be used while in spawn.");
            } else if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId())) {
               event.getPlayer()
                  .sendMessage(ChatColor.YELLOW + "You are in PvP Protection and cannot use Bard effects. Type '/pvp enable' to remove your protection.");
            } else {
               long cooldown = !Foxtrot.getInstance().getPvpClassHandler().getEnergyService().getLastEffectUsage().containsKey(event.getPlayer().getUniqueId())
                  ? 0L
                  : Foxtrot.getInstance().getPvpClassHandler().getEnergyService().getLastEffectUsage().get(event.getPlayer().getUniqueId())
                     - System.currentTimeMillis();
               if (cooldown > 0L && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                  double value = cooldown / 1000.0;
                  double sec = Math.round(10.0 * value) / 10.0;
                  event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot use this for another " + ChatColor.BOLD + sec + ChatColor.YELLOW + " seconds!");
               } else {
                  Float energy = Foxtrot.getInstance().getPvpClassHandler().getEnergyService().getCache().get(event.getPlayer().getUniqueId());
                  EnergyEffect bardEffect = this.BARD_CLICK_EFFECTS.get(event.getItem().getType());
                  if (bardEffect.getEnergy() > energy) {
                     event.getPlayer()
                        .sendMessage(
                           ChatColor.YELLOW
                              + "You do not have enough energy for this! You need "
                              + bardEffect.getEnergy()
                              + " energy, but you only have "
                              + energy.intValue()
                        );
                  } else {
                     if (!Foxtrot.getInstance().getServerHandler().isTeams()) {
                        ItemStack itemStack = event.getItem();
                        if (itemStack != null && (PortableStrength.itemStack.isSimilar(itemStack) || PortableResistance.itemStack.isSimilar(itemStack))) {
                           return;
                        }

                        if (itemStack != null && PortableResistance.itemStack.isSimilar(itemStack)) {
                           return;
                        }
                     }

                     boolean negative = bardEffect.getPotionEffect() != null && FoxListener.DEBUFFS.contains(bardEffect.getPotionEffect().getType());
                     BardEffectUseEvent bardEffectUseEvent = new BardEffectUseEvent(
                        event.getPlayer(),
                        bardEffect,
                        this.getNearbyPlayers(event.getPlayer(), !negative)
                           .stream()
                           .filter(nearbyPlayer -> !nearbyPlayer.getUniqueId().equals(event.getPlayer().getUniqueId()))
                           .collect(Collectors.toList())
                     );
                     Foxtrot.getInstance().getServer().getPluginManager().callEvent(bardEffectUseEvent);
                     if (!bardEffectUseEvent.isCancelled()) {
                        this.giveBardEffect(event.getPlayer(), bardEffect, !negative, true, true);
                        PvPClassHandler.getUltimate()
                           .put(event.getPlayer().getUniqueId(), Math.min(PvPClassHandler.getUltimate().get(event.getPlayer().getUniqueId()) + 10.0F, 100.0F));
                        if (event.getPlayer().isSneaking() && PvPClassHandler.getUltimate().get(event.getPlayer().getUniqueId()) >= 100.0F) {
                           PvPClassHandler.getUltimate().put(event.getPlayer().getUniqueId(), 0.0F);
                           this.giveEffect(event.getPlayer(), new PotionEffect(PotionEffectType.SPEED, 100, 2), true, true);
                           this.giveEffect(event.getPlayer(), new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 2), true, true);
                           this.giveEffect(event.getPlayer(), new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 1), true, true);
                           event.getPlayer().sendMessage(ChatColor.GREEN + "You have used your " + ChatColor.WHITE + "Bard Ultimate" + ChatColor.GREEN + ".");
                        } else {
                           Foxtrot.getInstance()
                              .getPvpClassHandler()
                              .getEnergyService()
                              .getCache()
                              .put(event.getPlayer().getUniqueId(), energy - bardEffect.getEnergy());
                           Foxtrot.getInstance()
                              .getPvpClassHandler()
                              .getEnergyService()
                              .getLastEffectUsage()
                              .put(event.getPlayer().getUniqueId(), System.currentTimeMillis() + 10000L);
                        }

                        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
                           Foxtrot.getInstance().getMasteryUpgradeHandler().addUpgrade(event.getPlayer(), "Bard", 1);
                        }

                        SpawnTagHandler.addOffensiveSeconds(event.getPlayer(), SpawnTagHandler.getMaxTagTime(event.getPlayer()));
                        if (event.getPlayer().getItemInHand().getAmount() == 1) {
                           event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                           event.getPlayer().updateInventory();
                        } else {
                           event.getPlayer().getItemInHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void giveBardEffect(Player source, EnergyEffect bardEffect, boolean friendly, boolean persistOldValues, boolean announce) {
      for (Player player : this.getNearbyPlayers(source, friendly)) {
         if (!DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())
            && (
               !PvPClassHandler.hasKitOn(player, this)
                  || bardEffect.getPotionEffect() == null
                  || !bardEffect.getPotionEffect().getType().equals(PotionEffectType.INCREASE_DAMAGE)
            )
            && bardEffect.getPotionEffect() != null) {
            smartAddPotion(player, bardEffect.getPotionEffect(), persistOldValues, this);
            if (announce && friendly && !player.getUniqueId().equals(source.getUniqueId())) {
               String effectName = bardEffect.getFancyName();
               String msg = Foxtrot.getInstance().getConfig().getString("bardEffectReceivedMessage", "&fYou have received &6<effect> &ffrom your &6Bard");
               msg = msg.replace("<effect>", effectName);
               player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
            }
         }
      }
   }

   public void giveEffect(Player source, PotionEffect potionEffect, boolean friendly, boolean persistOldValues) {
      for (Player player : this.getNearbyPlayers(source, friendly)) {
         if (!DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation()) && potionEffect != null) {
            smartAddPotion(player, potionEffect, persistOldValues, this);
         }
      }
   }

   public boolean canUseEffect(Player player) {
      int limit = Foxtrot.getInstance().getMapHandler().isKitMap() ? 3 : 1;
      if (PvPClassHandler.countMembersWithKitOn(player, this) > limit) {
         player.sendMessage(ChatColor.YELLOW + "Your team has too many bards!");
         return false;
      } else {
         return true;
      }
   }

   public List<Player> getNearbyPlayers(Player player, boolean friendly) {
      ArrayList<Player> valid = new ArrayList<>();
      Team sourceTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player);

      for (Entity entity : player.getNearbyEntities(BARD_RANGE, 15.0, BARD_RANGE)) {
         if (entity instanceof Player) {
            Player nearbyPlayer = (Player)entity;
            if (!Foxtrot.getInstance().getPvPTimerMap().hasTimer(nearbyPlayer.getUniqueId())) {
               if (sourceTeam == null) {
                  if (!friendly) {
                     valid.add(nearbyPlayer);
                  }
               } else {
                  boolean isFriendly = sourceTeam.isMember(nearbyPlayer.getUniqueId());
                  boolean isAlly = sourceTeam.isAlly(nearbyPlayer.getUniqueId());
                  if (friendly && isFriendly) {
                     valid.add(nearbyPlayer);
                  } else if (!friendly && !isFriendly && !isAlly) {
                     valid.add(nearbyPlayer);
                  }
               }
            }
         }
      }

      valid.add(player);
      return valid;
   }

   @EventHandler(priority = EventPriority.HIGHEST)
   public void onBardEffectUse(BardEffectUseEvent event) {
      if (event.getBardEffect().getPotionEffect() != null) {
         if (event.getAffectedPlayers().isEmpty()) {
            event.getSource().sendMessage(ChatColor.YELLOW + "You gave " + event.getBardEffect().getFancyName() + ChatColor.YELLOW + " to yourself.");
         } else {
            int amount = event.getAffectedPlayers().size();
            event.getSource()
               .sendMessage(
                  ChatColor.YELLOW
                     + "You have given "
                     + event.getBardEffect().getFancyName()
                     + ChatColor.YELLOW
                     + " to "
                     + (
                        FoxListener.DEBUFFS.contains(event.getBardEffect().getPotionEffect().getType())
                           ? ChatColor.YELLOW.toString() + amount + ChatColor.YELLOW + " " + (amount == 1 ? "enemy" : "enemies")
                           : ChatColor.GREEN.toString() + amount + ChatColor.YELLOW + " " + (amount == 1 ? "teammate" : "teammates")
                     )
                     + ChatColor.YELLOW
                     + "."
               );
         }
      }
   }

   private static double getDamageValue(EntityDamageEvent event) {
      try {
         Method m = EntityDamageEvent.class.getMethod("getDamage");
         Object result = m.invoke(event);
         return result instanceof Number ? ((Number)result).doubleValue() : 0.0;
      } catch (Exception e) {
         return 0.0;
      }
   }
}
