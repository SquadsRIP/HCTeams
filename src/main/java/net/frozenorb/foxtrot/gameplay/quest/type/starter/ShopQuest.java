package net.frozenorb.foxtrot.gameplay.quest.type.starter;

import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.gameplay.quest.Quest;
import net.frozenorb.foxtrot.gameplay.quest.QuestType;

public class ShopQuest extends Quest {
   @Override
   public String getQuestID() {
      return "Shop";
   }

   @Override
   public String getQuestDisplayName() {
      return "Shop";
   }

   @Override
   public List<String> getDescription() {
      ArrayList<String> toReturn = new ArrayList<>();
      toReturn.add("Open up the shop by typing &e/shop");
      toReturn.add("You should also open any keys you have");
      toReturn.add("and sell all the valuables you gain in the &e/shop");
      return toReturn;
   }

   @Override
   public QuestType getQuestType() {
      return QuestType.STARTER;
   }

   @Override
   public int getWeight() {
      return 2;
   }
}
