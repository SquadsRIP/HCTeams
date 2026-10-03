package net.frozenorb.foxtrot.gameplay.quest.type.starter;

import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.gameplay.quest.Quest;
import net.frozenorb.foxtrot.gameplay.quest.QuestType;

public class VoteQuest extends Quest {
   @Override
   public String getQuestID() {
      return "Vote";
   }

   @Override
   public String getQuestDisplayName() {
      return "Vote for the Server";
   }

   @Override
   public List<String> getDescription() {
      ArrayList<String> toReturn = new ArrayList<>();
      toReturn.add("Type &e/vote &fand read the instructions for free keys!");
      return toReturn;
   }

   @Override
   public QuestType getQuestType() {
      return QuestType.STARTER;
   }

   @Override
   public int getWeight() {
      return 4;
   }
}
