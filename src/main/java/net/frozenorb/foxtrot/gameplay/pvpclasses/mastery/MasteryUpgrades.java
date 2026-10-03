package net.frozenorb.foxtrot.gameplay.pvpclasses.mastery;

import java.beans.ConstructorProperties;

public enum MasteryUpgrades {
   LEVEL_ZERO("Level 0", 0, 0, 0, 0),
   LEVEL_ONE("Level 1", 250, 250, 0, 1),
   LEVEL_TWO("Level 2", 500, 500, 0, 2),
   LEVEL_THREE("Level 3", 750, 1000, 0, 3),
   LEVEL_FOUR("Level 4", 1000, 1500, 0, 4),
   LEVEL_FIVE("Level 5", 1500, 2500, 5, 5);

   String displayName;
   int scoreToLevelUp;
   int gemPrize;
   int treasureChests;
   int priority;

   public static MasteryUpgrades getUpgradeByScore(int score) {
      MasteryUpgrades chosen = null;

      for (MasteryUpgrades value : values()) {
         if (value.scoreToLevelUp <= score && (chosen == null || chosen.priority <= value.priority)) {
            chosen = value;
         }
      }

      return chosen;
   }

   @ConstructorProperties({"displayName", "scoreToLevelUp", "gemPrize", "treasureChests", "priority"})
   MasteryUpgrades(String displayName, int scoreToLevelUp, int gemPrize, int treasureChests, int priority) {
      this.displayName = displayName;
      this.scoreToLevelUp = scoreToLevelUp;
      this.gemPrize = gemPrize;
      this.treasureChests = treasureChests;
      this.priority = priority;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public int getScoreToLevelUp() {
      return this.scoreToLevelUp;
   }

   public int getGemPrize() {
      return this.gemPrize;
   }

   public int getTreasureChests() {
      return this.treasureChests;
   }

   public int getPriority() {
      return this.priority;
   }
}
