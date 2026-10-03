package net.frozenorb.foxtrot.gameplay.quest.type.starter;

import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.gameplay.quest.Quest;
import net.frozenorb.foxtrot.gameplay.quest.QuestType;

public class RedeemQuest extends Quest {
   @Override
   public String getQuestID() {
      return "Redeem";
   }

   @Override
   public String getQuestDisplayName() {
      return "Redeem Creator";
   }

   @Override
   public List<String> getDescription() {
      ArrayList<String> toReturn = new ArrayList<>();
      toReturn.add("Which YouTuber/Streamer/Tiktoker lead you to Squads?");
      toReturn.add("Type &e/redeem [their name] &fto receive free keys!");
      return toReturn;
   }

   @Override
   public QuestType getQuestType() {
      return QuestType.STARTER;
   }

   @Override
   public int getWeight() {
      return 3;
   }
}
