package net.frozenorb.foxtrot.gameplay.pvpclasses;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.minecraft.util.com.google.common.collect.HashBasedTable;
import net.minecraft.util.com.google.common.collect.Table;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionEffectExpireEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class PvPClass implements Listener {
   private final String name;
   private final List<Material> consumables;
   private boolean energyBased;
   private boolean warmup;
   private final List<PotionEffectType> permanentEffects = new ArrayList<>();
   private static final Table<UUID, PotionEffectType, PotionEffect> restores = HashBasedTable.create();
   private static final long LIMIT_MESSAGE_COOLDOWN = 5000L;
   private static final Map<String, Long> limitMessageTimes = new HashMap<>();

   public PvPClass(String name, List<Material> consumables) {
      this.name = name;
      this.consumables = consumables;
   }

   public PvPClass(String name, List<Material> consumables, boolean energyBased) {
      this.name = name;
      this.consumables = consumables;
      this.energyBased = energyBased;
   }

   public void apply(Player player) {
      if (this.energyBased) {
         Foxtrot.getInstance().getPvpClassHandler().getEnergyService().getCache().put(player.getUniqueId(), 0.0F);
      }
   }

   public void tick(Player player) {
   }

   public void remove(Player player) {
      if (this.energyBased) {
         Foxtrot.getInstance().getPvpClassHandler().getEnergyService().getCache().remove(player.getUniqueId());
         Foxtrot.getInstance().getPvpClassHandler().getEnergyService().getLastEffectUsage().remove(player.getUniqueId());
      }
   }

   public boolean canApply(Player player) {
      return true;
   }

   protected void sendLimitMessage(Player player, String message) {
      String key = player.getUniqueId() + ":" + this.name;
      long now = System.currentTimeMillis();
      Long last = limitMessageTimes.get(key);
      if (last == null || now - last >= LIMIT_MESSAGE_COOLDOWN) {
         limitMessageTimes.put(key, now);
         player.sendMessage(message);
      }
   }

   public static void removeInfiniteEffects(Player player) {
      for (PotionEffect potionEffect : player.getActivePotionEffects()) {
         if (potionEffect.getDuration() > 1000000) {
            player.removePotionEffect(potionEffect.getType());
         }
      }
   }

   public boolean itemConsumed(Player player, Material type) {
      return true;
   }

   public abstract boolean qualifies(Player var1, PlayerInventory var2);

   protected boolean wearingAllArmor(PlayerInventory armor) {
      return armor.getHelmet() != null && armor.getChestplate() != null && armor.getLeggings() != null && armor.getBoots() != null;
   }

   public static void smartAddPotion(Player player, PotionEffect potionEffect, boolean persistOldValues, PvPClass pvpClass) {
      setRestoreEffect(player, potionEffect);
   }

   public static void setRestoreEffect(Player player, PotionEffect effect) {
      boolean shouldCancel = true;

      for (PotionEffect active : player.getActivePotionEffects()) {
         if (active.getType().equals(effect.getType())) {
            if (effect.getAmplifier() < active.getAmplifier()) {
               return;
            }

            if (effect.getAmplifier() == active.getAmplifier()
               && 0 < active.getDuration()
               && (effect.getDuration() <= active.getDuration() || effect.getDuration() - active.getDuration() < 10)) {
               return;
            }

            restores.put(player.getUniqueId(), active.getType(), active);
            shouldCancel = false;
            break;
         }
      }

      player.addPotionEffect(effect, true);
      if (shouldCancel && effect.getDuration() > 120 && effect.getDuration() < 9600) {
         restores.remove(player.getUniqueId(), effect.getType());
      }
   }

   @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
   public void onPotionEffectExpire(PotionEffectExpireEvent event) {
      LivingEntity livingEntity = event.getEntity();
      if (livingEntity instanceof Player) {
         Player player = (Player)livingEntity;
         if (PvPClassHandler.getPvPClass(player) == null
            && event.getEffect().getType().getName().equalsIgnoreCase("Speed")
            && player.getInventory().getBoots() != null
            && player.getInventory().getBoots().getItemMeta().hasLore()
            && player.getInventory().getBoots().getItemMeta().getLore().contains(ChatColor.RED + "Speed II")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1), true);
         }

         PotionEffect previous = (PotionEffect)restores.remove(player.getUniqueId(), event.getEffect().getType());
         if (previous != null && previous.getDuration() < 1000000) {
            event.setCancelled(true);
            player.addPotionEffect(previous, true);
            Bukkit.getLogger()
               .info(
                  "Restored "
                     + previous.getType().toString()
                     + " for "
                     + player.getName()
                     + ". duration: "
                     + previous.getDuration()
                     + ". amp: "
                     + previous.getAmplifier()
               );
         }
      }
   }

   public String getName() {
      return this.name;
   }

   public List<Material> getConsumables() {
      return this.consumables;
   }

   public boolean isEnergyBased() {
      return this.energyBased;
   }

   public boolean isWarmup() {
      return this.warmup;
   }

   public List<PotionEffectType> getPermanentEffects() {
      return this.permanentEffects;
   }

   public static class SavedPotion {
      private PotionEffect potionEffect;
      private long time;
      private boolean perm;

      public PotionEffect getPotionEffect() {
         return this.potionEffect;
      }

      public long getTime() {
         return this.time;
      }

      public boolean isPerm() {
         return this.perm;
      }

      @ConstructorProperties({"potionEffect", "time", "perm"})
      public SavedPotion(PotionEffect potionEffect, long time, boolean perm) {
         this.potionEffect = potionEffect;
         this.time = time;
         this.perm = perm;
      }
   }
}
