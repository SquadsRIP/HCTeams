package net.frozenorb.foxtrot.gameplay.pvpclasses.pvpclasses;

import cc.fyre.proton.util.TimeUtils;
import cc.fyre.proton.util.UUIDUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.deathmessage.DeathMessageHandler;
import net.frozenorb.foxtrot.deathmessage.objects.PlayerDamage;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClass;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClassHandler;
import net.frozenorb.foxtrot.listener.event.BackstabKillEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class RogueClass extends PvPClass {
   private static final Map<String, Long> lastSpeedUsage = new HashMap<>();
   private static final Map<String, Long> lastJumpUsage = new HashMap<>();
   private static final Map<String, Long> backstabCooldown = new HashMap<>();

   public RogueClass() {
      super("Rogue", Arrays.asList(Material.SUGAR, Material.FEATHER));
   }

   @Override
   public boolean canApply(Player player) {
      int limit = Foxtrot.getInstance().getMapHandler().isKitMap() ? 3 : 2;
      if (PvPClassHandler.countMembersWithKitOn(player, this) > limit) {
         this.sendLimitMessage(player, ChatColor.RED + "Your team has too many rogues!");
         return false;
      } else {
         return super.canApply(player);
      }
   }

   @Override
   public boolean qualifies(Player player, PlayerInventory armor) {
      return this.wearingAllArmor(armor)
         && armor.getHelmet().getType() == Material.CHAINMAIL_HELMET
         && armor.getChestplate().getType() == Material.CHAINMAIL_CHESTPLATE
         && armor.getLeggings().getType() == Material.CHAINMAIL_LEGGINGS
         && armor.getBoots().getType() == Material.CHAINMAIL_BOOTS;
   }

   @Override
   public void apply(Player player) {
      player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2), true);
      player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1), true);
      player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0), true);
   }

   @Override
   public void tick(Player player) {
      if (!player.hasPotionEffect(PotionEffectType.SPEED)
         || player.getActivePotionEffects().stream().anyMatch(it -> it.getType().equals(PotionEffectType.SPEED) && it.getAmplifier() < 2)) {
         player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2), true);
      }

      if (!player.hasPotionEffect(PotionEffectType.JUMP)
         || player.getActivePotionEffects().stream().anyMatch(it -> it.getType().equals(PotionEffectType.JUMP) && it.getAmplifier() < 1)) {
         player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1), true);
      }

      int resistanceValue = 0;
      if (!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)
         || player.getActivePotionEffects()
            .stream()
            .anyMatch(it -> it.getType().equals(PotionEffectType.DAMAGE_RESISTANCE) && it.getAmplifier() < resistanceValue)) {
         player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, resistanceValue), true);
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
            Long millisLeft = (lastSpeedUsage.get(player.getName()) - System.currentTimeMillis()) / 1000L * 1000L;
            String msg = TimeUtils.formatIntoDetailedString((int)(millisLeft / 1000L));
            player.sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
            return false;
         }

         lastSpeedUsage.put(player.getName(), System.currentTimeMillis() + 60000L);
         player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 4), true);
      } else {
         if (lastJumpUsage.containsKey(player.getName()) && lastJumpUsage.get(player.getName()) > System.currentTimeMillis()) {
            Long millisLeft = (lastJumpUsage.get(player.getName()) - System.currentTimeMillis()) / 1000L * 1000L;
            String msg = TimeUtils.formatIntoDetailedString((int)(millisLeft / 1000L));
            player.sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
            return false;
         }

         lastJumpUsage.put(player.getName(), System.currentTimeMillis() + 60000L);
         player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 200, 6), true);
      }

      return true;
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onEntityArrowHit(EntityDamageByEntityEvent event) {
      if (!event.isCancelled()) {
         if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player)event.getDamager();
            Player victim = (Player)event.getEntity();
            if (damager.getItemInHand() != null && damager.getItemInHand().getType() == Material.GOLD_SWORD && PvPClassHandler.hasKitOn(damager, this)) {
               if (backstabCooldown.containsKey(damager.getName()) && backstabCooldown.get(damager.getName()) > System.currentTimeMillis()) {
                  return;
               }

               backstabCooldown.put(damager.getName(), System.currentTimeMillis() + 3500L);
               Vector playerVector = damager.getLocation().getDirection();
               Vector entityVector = victim.getLocation().getDirection();
               playerVector.setY(0.0F);
               entityVector.setY(0.0F);
               double degrees = playerVector.angle(entityVector);
               if (Math.abs(degrees) < 1.4) {
                  damager.setItemInHand(new ItemStack(Material.AIR));
                  damager.playSound(damager.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
                  damager.getWorld().playEffect(victim.getEyeLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
                  if (victim.getHealth() - 6.0 <= 0.0) {
                     event.setCancelled(true);
                  } else {
                     event.setDamage(0.0);
                  }

                  DeathMessageHandler.addDamage(victim, new RogueClass.BackstabDamage(victim.getName(), 6.0, damager.getName()));
                  victim.setHealth(Math.max(0.0, victim.getHealth() - 6.0));
                  if (victim.getHealth() <= 0.0) {
                     Bukkit.getPluginManager().callEvent(new BackstabKillEvent(damager, victim));
                  }

                  if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
                     Foxtrot.getInstance().getMasteryUpgradeHandler().addUpgrade(damager, "Rogue", 1);
                  }

                  damager.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 2));
               } else {
                  damager.sendMessage(ChatColor.RED + "Backstab failed!");
               }
            }
         }
      }
   }

   public static class BackstabDamage extends PlayerDamage {
      public BackstabDamage(String damaged, double damage, String damager) {
         super(damaged, damage, damager);
      }

      public String getDescription() {
         return "Backstabbed by " + this.getDamager();
      }

      @Override
      public String getDeathMessage() {
         return ChatColor.RED
            + this.getDamaged()
            + ChatColor.DARK_RED
            + "["
            + Foxtrot.getInstance().getKillsMap().getKills(UUIDUtils.uuid(this.getDamaged()))
            + "]"
            + ChatColor.YELLOW
            + " was backstabbed by "
            + ChatColor.RED
            + this.getDamager()
            + ChatColor.DARK_RED
            + "["
            + Foxtrot.getInstance().getKillsMap().getKills(UUIDUtils.uuid(this.getDamager()))
            + "]"
            + ChatColor.YELLOW
            + ".";
      }
   }
}
