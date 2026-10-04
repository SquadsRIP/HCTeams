package net.frozenorb.foxtrot.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public final class EnchantmentRules {
   private static final Set<Enchantment> TABLE_ENCHANTMENTS = Collections.unmodifiableSet(
      new HashSet<>(
         Arrays.asList(
            Enchantment.PROTECTION_ENVIRONMENTAL,
            Enchantment.PROTECTION_FALL,
            Enchantment.DURABILITY,
            Enchantment.DIG_SPEED,
            Enchantment.LOOT_BONUS_BLOCKS,
            Enchantment.SILK_TOUCH,
            Enchantment.DAMAGE_UNDEAD,
            Enchantment.DAMAGE_ARTHROPODS,
            Enchantment.LOOT_BONUS_MOBS,
            Enchantment.DAMAGE_ALL
         )
      )
   );
   private static final Set<Enchantment> ANVIL_LIMITED = new HashSet<>(Arrays.asList(Enchantment.PROTECTION_ENVIRONMENTAL, Enchantment.DAMAGE_ALL, Enchantment.ARROW_DAMAGE));
   // The name SquadsCrates gives the KOTH crate bow; anvils can't rename a coloured name, so it can't be faked.
   private static final String KOTH_BOW_NAME = ChatColor.RED + "" + ChatColor.ITALIC + "KOTH Bow";
   private final Map<Enchantment, Integer> limits = new HashMap<>();
   private final int kothBowPower;
   private final double maxLevelChance;
   private final double secondEnchantChance;
   private final double thirdEnchantChance;

   public EnchantmentRules(ConfigurationSection config) {
      this.addLimit(config, Enchantment.PROTECTION_ENVIRONMENTAL, "protection", 2);
      this.addLimit(config, Enchantment.DAMAGE_ALL, "sharpness", 2);
      this.addLimit(config, Enchantment.DURABILITY, "unbreaking", 3);
      this.addLimit(config, Enchantment.PROTECTION_FIRE, "fireProtection", 2);
      this.addLimit(config, Enchantment.PROTECTION_PROJECTILE, "projectileProtection", 2);
      this.addLimit(config, Enchantment.PROTECTION_EXPLOSIONS, "blastProtection", 2);
      this.addLimit(config, Enchantment.PROTECTION_FALL, "featherFalling", 4);
      this.addLimit(config, Enchantment.DAMAGE_UNDEAD, "smite", 2);
      this.addLimit(config, Enchantment.DAMAGE_ARTHROPODS, "baneOfArthropods", 2);
      this.addLimit(config, Enchantment.ARROW_DAMAGE, "power", 3);
      this.kothBowPower = config.getInt("enchantmentLimits.kothPower", 4);
      this.addLimit(config, Enchantment.LOOT_BONUS_MOBS, "looting", 3);
      this.maxLevelChance = readChance(config, "max-level-chance-percent", 25.0);
      this.secondEnchantChance = readChance(config, "level-30-second-enchant-chance-percent", 70.0);
      this.thirdEnchantChance = readChance(config, "level-30-third-enchant-chance-percent", 20.0);
   }

   private static double readChance(ConfigurationSection config, String key, double fallback) {
      double chance = config.getDouble("enchantingTable." + key, fallback);
      return !Double.isNaN(chance) && !Double.isInfinite(chance) ? Math.max(0.0, Math.min(100.0, chance)) / 100.0 : fallback / 100.0;
   }

   private void addLimit(ConfigurationSection config, Enchantment enchantment, String key, int fallback) {
      this.limits.put(enchantment, config.getInt("enchantmentLimits." + key, fallback));
   }

   public int getLimit(Enchantment enchantment) {
      Integer limit = this.limits.get(enchantment);
      return limit == null ? 0 : limit;
   }

   // Anvils only cap Protection, Sharpness and Power; the other enchantmentLimits only apply to the enchanting table.
   public int getAnvilLimit(Enchantment enchantment) {
      return ANVIL_LIMITED.contains(enchantment) ? this.getLimit(enchantment) : 0;
   }

   // Anvil caps keyed by enchantment id (the same ids NMS uses).
   public Map<Integer, Integer> getAnvilLimitsById() {
      Map<Integer, Integer> caps = new HashMap<>();

      for (Enchantment enchantment : ANVIL_LIMITED) {
         int limit = this.getLimit(enchantment);
         if (limit > 0) {
            caps.put(enchantment.getId(), limit);
         }
      }

      return caps;
   }

   public void applyTableRules(ItemStack item, Map<Enchantment, Integer> enchants, int levelCost, Random random) {
      if (item != null && item.getType() != Material.AIR) {
         if (item.getType() == Material.BOW) {
            enchants.remove(Enchantment.ARROW_FIRE);
            enchants.remove(Enchantment.ARROW_KNOCKBACK);
            for (Entry<Enchantment, Integer> entry : enchants.entrySet()) {
               int maximum = this.getTableMaximum(entry.getKey());
               if (entry.getValue() > maximum || levelCost >= 30 && this.getLimit(entry.getKey()) > 0) {
                  entry.setValue(maximum);
               }
            }

            if (enchants.isEmpty() && Enchantment.DURABILITY.canEnchantItem(item)) {
               enchants.put(Enchantment.DURABILITY, 1);
            }

            return;
         }

         boolean book = item.getType() == Material.BOOK;
         enchants.entrySet()
            .removeIf(
               entryx -> !TABLE_ENCHANTMENTS.contains(entryx.getKey())
                  || entryx.getValue() == null
                  || (Integer)entryx.getValue() < 1
                  || !book && !((Enchantment)entryx.getKey()).canEnchantItem(item)
            );
         if (enchants.isEmpty() && (book || Enchantment.DURABILITY.canEnchantItem(item))) {
            enchants.put(Enchantment.DURABILITY, 1);
         }

         if (book && enchants.size() > 1) {
            ArrayList<Enchantment> choices = new ArrayList<>(enchants.keySet());
            Enchantment chosen = choices.get(random.nextInt(choices.size()));
            enchants.keySet().retainAll(Collections.singleton(chosen));
         }

         if (!book && levelCost >= 30) {
            if (enchants.size() == 1) {
               this.addBonusEnchantment(item, enchants, this.secondEnchantChance, random);
            }

            if (enchants.size() == 2) {
               this.addBonusEnchantment(item, enchants, this.thirdEnchantChance, random);
            }
         }

         double chance = this.maxLevelChance * Math.max(0.0, Math.min(1.0, levelCost / 30.0));

         for (Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int maximum = this.getTableMaximum(enchantment);
            int level = Math.min(entry.getValue(), maximum);
            if (levelCost >= 30 && this.getLimit(enchantment) > 0) {
               level = maximum;
            } else if (maximum > 1 && level == maximum && random.nextDouble() >= chance) {
               level = maximum - 1;
            }

            entry.setValue(level);
         }
      } else {
         enchants.clear();
      }
   }

   private int getTableMaximum(Enchantment enchantment) {
      int limit = this.getLimit(enchantment);
      return limit > 0 ? Math.min(limit, enchantment.getMaxLevel()) : enchantment.getMaxLevel();
   }

   private void addBonusEnchantment(ItemStack item, Map<Enchantment, Integer> enchants, double chance, Random random) {
      if (!(chance <= 0.0)) {
         ArrayList<Enchantment> candidates = new ArrayList<>();

         for (Enchantment candidate : TABLE_ENCHANTMENTS) {
            if (!candidate.equals(Enchantment.SILK_TOUCH) && !enchants.containsKey(candidate) && candidate.canEnchantItem(item)) {
               boolean conflict = false;

               for (Enchantment existing : enchants.keySet()) {
                  if (candidate.conflictsWith(existing) || existing.conflictsWith(candidate)) {
                     conflict = true;
                     break;
                  }
               }

               if (!conflict) {
                  candidates.add(candidate);
               }
            }
         }

         if (!candidates.isEmpty() && random.nextDouble() < chance) {
            Enchantment chosen = candidates.get(random.nextInt(candidates.size()));
            enchants.put(chosen, this.getTableMaximum(chosen));
         }
      }
   }

   public boolean exceedsLimits(ItemStack item) {
      if (item != null && item.getType() != Material.AIR) {
         ItemMeta meta = item.getItemMeta();
         return meta != null
            && (
               this.exceedsLimits(meta.getEnchants())
                  || meta instanceof EnchantmentStorageMeta && this.exceedsLimits(((EnchantmentStorageMeta)meta).getStoredEnchants())
            );
      } else {
         return false;
      }
   }

   public boolean exceedsLimits(ItemStack result, ItemStack... inputs) {
      if (result != null && result.getType() != Material.AIR && result.hasItemMeta()) {
         for (Entry<Enchantment, Integer> entry : getAllEnchants(result).entrySet()) {
            int limit = this.getAnvilLimit(entry.getKey());
            if (limit > 0 && entry.getValue() > limit) {
               int inputLevel = 0;

               for (ItemStack input : inputs) {
                  Integer level = getAllEnchants(input).get(entry.getKey());
                  if (level != null) {
                     inputLevel = Math.max(inputLevel, level);
                  }
               }

               if (entry.getValue() > inputLevel) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static Map<Enchantment, Integer> getAllEnchants(ItemStack item) {
      Map<Enchantment, Integer> enchants = new HashMap<>();
      if (item != null && item.getType() != Material.AIR && item.hasItemMeta()) {
         ItemMeta meta = item.getItemMeta();
         enchants.putAll(meta.getEnchants());
         if (meta instanceof EnchantmentStorageMeta) {
            for (Entry<Enchantment, Integer> entry : ((EnchantmentStorageMeta)meta).getStoredEnchants().entrySet()) {
               enchants.merge(entry.getKey(), entry.getValue(), Math::max);
            }
         }
      }

      return enchants;
   }

   private boolean exceedsLimits(Map<Enchantment, Integer> enchants) {
      for (Entry<Enchantment, Integer> entry : enchants.entrySet()) {
         int limit = this.getLimit(entry.getKey());
         if (limit > 0 && entry.getValue() > limit) {
            return true;
         }
      }

      return false;
   }

   public static boolean isKothBow(ItemStack item) {
      if (item != null && item.getType() == Material.BOW && item.hasItemMeta()) {
         ItemMeta meta = item.getItemMeta();
         return meta.hasDisplayName() && KOTH_BOW_NAME.equals(meta.getDisplayName());
      } else {
         return false;
      }
   }

   // Lowers Power on a bow above its cap (KOTH bows: kothPower, others: power). Other enchants are untouched.
   public boolean limitBowPower(ItemStack item) {
      if (item != null && item.getType() == Material.BOW && item.hasItemMeta()) {
         int limit = isKothBow(item) ? this.kothBowPower : this.getLimit(Enchantment.ARROW_DAMAGE);
         if (limit > 0 && item.getEnchantmentLevel(Enchantment.ARROW_DAMAGE) > limit) {
            ItemMeta meta = item.getItemMeta();
            meta.addEnchant(Enchantment.ARROW_DAMAGE, limit, true);
            item.setItemMeta(meta);
            return true;
         }
      }

      return false;
   }

   public boolean limitItem(ItemStack item) {
      if (item != null && item.getType() != Material.AIR) {
         ItemMeta meta = item.getItemMeta();
         if (meta == null) {
            return false;
         }

         boolean changed = false;

         for (Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
            int limit = this.getLimit(entry.getKey());
            if (limit > 0 && entry.getValue() > limit) {
               meta.addEnchant(entry.getKey(), limit, true);
               changed = true;
            }
         }

         if (meta instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta book = (EnchantmentStorageMeta)meta;

            for (Entry<Enchantment, Integer> entry : book.getStoredEnchants().entrySet()) {
               int limit = this.getLimit(entry.getKey());
               if (limit > 0 && entry.getValue() > limit) {
                  book.addStoredEnchant(entry.getKey(), limit, true);
                  changed = true;
               }
            }
         }

         if (changed) {
            item.setItemMeta(meta);
         }

         return changed;
      } else {
         return false;
      }
   }
}
