package net.frozenorb.foxtrot.gameplay.quest.type.starter;

import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.quest.Quest;
import net.frozenorb.foxtrot.gameplay.quest.QuestType;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class WarpHomeQuest extends Quest {
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
   public boolean canDoQuest(Player player) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
      return team == null ? true : !team.isOwner(player.getUniqueId());
   }

   @Override
   public QuestType getQuestType() {
      return QuestType.STARTER;
   }

   @Override
   public int getWeight() {
      return 7;
   }
}
