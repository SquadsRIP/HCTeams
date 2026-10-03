package net.frozenorb.foxtrot.gameplay.quest.type.starter;

import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.gameplay.quest.Quest;
import net.frozenorb.foxtrot.gameplay.quest.QuestType;
import org.bukkit.ChatColor;

public class CreateOrJoinFactionQuest extends Quest {
   @Override
   public String getQuestID() {
      return "CreateOrJoin";
   }

   @Override
   public String getQuestDisplayName() {
      return "Create or Join a Faction";
   }

   @Override
   public List<String> getDescription() {
      ArrayList<String> toReturn = new ArrayList<>();
      toReturn.add("Create a faction by typing " + ChatColor.YELLOW + "/f create");
      toReturn.add("Or if you already have an");
      toReturn.add("invite, type &e/f join [name]&7!");
      return toReturn;
   }

   @Override
   public QuestType getQuestType() {
      return QuestType.STARTER;
   }

   @Override
   public int getWeight() {
      return 5;
   }
}
