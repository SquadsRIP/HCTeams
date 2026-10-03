package net.frozenorb.foxtrot.util;

import org.bukkit.entity.Player;

public class XPUtil {
   public static void setTotalExperience(Player player, int exp) {
      if (exp < 0) {
         throw new IllegalArgumentException("Experience is negative!");
      }

      player.setExp(0.0F);
      player.setLevel(0);
      player.setTotalExperience(0);
      int amount = exp;

      while (amount > 0) {
         int expToLevel = getExpAtLevel(player);
         if ((amount -= expToLevel) >= 0) {
            player.giveExp(expToLevel);
         } else {
            int var4;
            player.giveExp(var4 = amount + expToLevel);
            amount = 0;
         }
      }
   }

   private static int getExpAtLevel(Player player) {
      return getExpAtLevel(player.getLevel());
   }

   public static int getExpAtLevel(int level) {
      if (level <= 15) {
         return 2 * level + 7;
      } else {
         return level >= 16 && level <= 30 ? 5 * level - 38 : 9 * level - 158;
      }
   }

   public static int getLevelToExp(int level) {
      int exp = 0;

      for (int currentLevel = 0; currentLevel < level; currentLevel++) {
         exp += getExpAtLevel(currentLevel);
      }

      if (exp < 0) {
         exp = Integer.MAX_VALUE;
      }

      return exp;
   }

   public static int getTotalExperience(Player player) {
      int exp = Math.round(getExpAtLevel(player) * player.getExp());
      int currentLevel = player.getLevel();

      while (currentLevel > 0) {
         exp += getExpAtLevel(--currentLevel);
      }

      if (exp < 0) {
         exp = Integer.MAX_VALUE;
      }

      return exp;
   }

   public static int getExpUntilNextLevel(Player player) {
      int exp = Math.round(getExpAtLevel(player) * player.getExp());
      int nextLevel = player.getLevel();
      return getExpAtLevel(nextLevel) - exp;
   }
}
