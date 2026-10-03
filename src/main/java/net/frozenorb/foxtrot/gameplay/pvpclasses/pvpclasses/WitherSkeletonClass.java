package net.frozenorb.foxtrot.gameplay.pvpclasses.pvpclasses;

import cc.fyre.modsuite.mod.ModHandler;
import cc.fyre.proton.util.TimeUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClass;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class WitherSkeletonClass extends PvPClass {
   private static final Map<String, Long> lastSpeedUsage = new HashMap<>();
   private static final Map<String, Long> lastJumpUsage = new HashMap<>();
   private static final Map<String, Long> lastBiteUse = new HashMap<>();

   public WitherSkeletonClass() {
      super("Reaper", Arrays.asList(Material.SUGAR, Material.FEATHER));
   }

   @Override
   public boolean qualifies(Player player, PlayerInventory armor) {
      return this.wearingAllArmor(armor)
         && armor.getHelmet().getType() == Material.LEATHER_HELMET
         && armor.getChestplate().getType() == Material.DIAMOND_CHESTPLATE
         && armor.getLeggings().getType() == Material.CHAINMAIL_LEGGINGS
         && armor.getBoots().getType() == Material.CHAINMAIL_BOOTS;
   }

   @Override
   public void apply(Player player) {
      player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2), true);
      player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), true);
      player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0), true);
   }

   @Override
   public void tick(Player player) {
      if (!player.hasPotionEffect(PotionEffectType.SPEED)
         || player.getActivePotionEffects().stream().anyMatch(it -> it.getType().equals(PotionEffectType.SPEED) && it.getAmplifier() < 2)) {
         player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2), true);
      }

      if (!player.hasPotionEffect(PotionEffectType.REGENERATION)
         || player.getActivePotionEffects().stream().anyMatch(it -> it.getType().equals(PotionEffectType.REGENERATION) && it.getAmplifier() < 0)) {
         player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0), true);
      }

      if (!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)
         || player.getActivePotionEffects().stream().anyMatch(it -> it.getType().equals(PotionEffectType.DAMAGE_RESISTANCE) && it.getAmplifier() < 1)) {
         player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), true);
      }

      super.tick(player);
   }

   @Override
   public void remove(Player player) {
      removeInfiniteEffects(player);
   }

   @Override
   public boolean itemConsumed(Player player, Material material) {
      if (material == Material.SUGAR) {
         if (lastSpeedUsage.containsKey(player.getName()) && lastSpeedUsage.get(player.getName()) > System.currentTimeMillis()) {
            long millisLeft = (lastSpeedUsage.get(player.getName()) - System.currentTimeMillis()) / 1000L * 1000L;
            String msg = TimeUtils.formatIntoDetailedString((int)(millisLeft / 1000L));
            player.sendMessage(ChatColor.YELLOW + "You cannot use this for another §c§l" + msg + "§c.");
            return false;
         }

         lastSpeedUsage.put(player.getName(), System.currentTimeMillis() + 60000L);
         player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 180, 3), true);
      } else if (material == Material.FEATHER) {
         if (lastJumpUsage.containsKey(player.getName()) && lastJumpUsage.get(player.getName()) > System.currentTimeMillis()) {
            long millisLeft = (lastJumpUsage.get(player.getName()) - System.currentTimeMillis()) / 1000L * 1000L;
            String msg = TimeUtils.formatIntoDetailedString((int)(millisLeft / 1000L));
            player.sendMessage(ChatColor.YELLOW + "You cannot use this for another §c§l" + msg + "§c.");
            return false;
         }

         lastJumpUsage.put(player.getName(), System.currentTimeMillis() + 60000L);
         player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 180, 6), true);
      }

      return true;
   }

   @EventHandler
   private void onInteract(PlayerInteractEvent event) {
      Player player = event.getPlayer();
      ItemStack itemStack = event.getItem();
      if (itemStack != null) {
         if (event.getAction().name().contains("RIGHT")) {
            if (PvPClassHandler.hasKitOn(player, this)) {
               if (itemStack.getType().equals(Material.NETHER_BRICK_ITEM)) {
                  if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
                     player.sendMessage(ChatColor.YELLOW + "Cannot in spawn!");
                  } else if (lastBiteUse.containsKey(player.getName()) && lastBiteUse.get(player.getName()) > System.currentTimeMillis()) {
                     long millisLeft = (lastBiteUse.get(player.getName()) - System.currentTimeMillis()) / 1000L * 1000L;
                     String msg = TimeUtils.formatIntoDetailedString((int)(millisLeft / 1000L));
                     player.sendMessage(ChatColor.YELLOW + "You cannot use this for another §c§l" + msg + "§c.");
                  } else {
                     lastBiteUse.put(player.getName(), System.currentTimeMillis() + 180000L);
                     Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
                     int effected = 0;

                     for (Entity entity : player.getNearbyEntities(10.0, 10.0, 10.0).stream().filter(it -> it instanceof Player).collect(Collectors.toList())) {
                        Player target = (Player)entity;
                        if ((team == null || !team.isMember(target.getUniqueId()))
                           && !DTRBitmask.SAFE_ZONE.appliesAt(target.getLocation())
                           && (!CustomTimerCreateCommand.isSOTWTimer() || CustomTimerCreateCommand.hasSOTWEnabled(player.getUniqueId()))
                           && !ModHandler.INSTANCE.isInModMode(player.getUniqueId())) {
                           effected++;
                           this.giveEffects(target);
                        }
                     }

                     player.sendMessage(ChatColor.GREEN + "Successfully used the Wither Skeleton ability on " + effected + " enemies!");
                     player.sendMessage(ChatColor.WHITE + "Gave negative effects to enemies and gave you Strength II!");
                     player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 140, 1));
                  }
               }
            }
         }
      }
   }

   public void giveEffects(Player target) {
      target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 140, 1));
      target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 140, 1));
   }
}
