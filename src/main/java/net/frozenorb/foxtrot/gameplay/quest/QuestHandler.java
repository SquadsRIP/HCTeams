package net.frozenorb.foxtrot.gameplay.quest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.entity.Player;

public class QuestHandler {
   private Foxtrot instance;
   private List<Quest> quests = new ArrayList<>();
   private Map<UUID, String> playerQuests = new HashMap<>();
   private Map<UUID, Integer> questStatus = new HashMap<>();

   public QuestHandler(Foxtrot instance) {
      this.instance = instance;
   }

   public Quest findCurrentQuest(Player player) {
      return this.playerQuests.containsKey(player.getUniqueId())
         ? this.findQuestByName(this.playerQuests.get(player.getUniqueId()))
         : this.findQuestByName("ClaimRankStarter");
   }

   public List<Quest> findQuestByWeight(int weight) {
      return this.quests.stream().filter(it -> it.getWeight() == weight).collect(Collectors.toList());
   }

   public void completeQuest(Player player, String questId) {
   }

   public Quest findNextQuest(Player player) {
      Quest currentQuest = this.findCurrentQuest(player);
      int nextWeight = currentQuest.getWeight() + 1;

      for (Quest loopQuest : this.findQuestByWeight(nextWeight)) {
         if (loopQuest.getWeight() == nextWeight && loopQuest.canDoQuest(player)) {
            return loopQuest;
         }
      }

      return null;
   }

   public Quest findQuestByName(String name) {
      return this.quests.stream().filter(it -> it.getQuestID().equalsIgnoreCase(name)).findFirst().orElse(null);
   }

   public List<Quest> getQuests() {
      return this.quests;
   }

   public Map<UUID, String> getPlayerQuests() {
      return this.playerQuests;
   }

   public Map<UUID, Integer> getQuestStatus() {
      return this.questStatus;
   }
}
