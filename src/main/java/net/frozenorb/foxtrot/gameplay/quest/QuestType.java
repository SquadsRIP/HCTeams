package net.frozenorb.foxtrot.gameplay.quest;

import java.beans.ConstructorProperties;

public enum QuestType {
   STARTER("Starter", "airdrops give {player} 5"),
   INTERMEDIATE("Intermediate", "mcrate give {player} Seasonal 2"),
   ADVANCED("Advanced", "mcrate give {player} Seasonal 10");

   String displayName;
   String winningCommand;

   @ConstructorProperties({"displayName", "winningCommand"})
   QuestType(String displayName, String winningCommand) {
      this.displayName = displayName;
      this.winningCommand = winningCommand;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public String getWinningCommand() {
      return this.winningCommand;
   }
}
