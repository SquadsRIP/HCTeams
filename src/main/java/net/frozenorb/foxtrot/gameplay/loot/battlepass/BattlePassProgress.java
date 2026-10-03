package net.frozenorb.foxtrot.gameplay.loot.battlepass;

import com.google.gson.annotations.JsonAdapter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.Challenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.KillEntityChallenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl.MineBlockChallenge;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.serializer.ChallengeSetSerializer;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.tier.Tier;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.tier.serialize.TierSetSerializer;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class BattlePassProgress {
   private final UUID uuid;
   private boolean requiresSave;
   private boolean premium;
   private int experience = 0;
   @JsonAdapter(ChallengeSetSerializer.class)
   protected Set<Challenge> completedChallenges = new HashSet<>();
   protected Map<Material, Integer> blocksMined = new HashMap<>();
   protected Map<EntityType, Integer> entityKills = new HashMap<>();
   private UUID dailyChallengesId;
   @JsonAdapter(ChallengeSetSerializer.class)
   protected Set<Challenge> completedDailyChallenges = new HashSet<>();
   private Map<Material, Integer> dailyBlocksMined = new HashMap<>();
   private Map<EntityType, Integer> dailyEntityKills = new HashMap<>();
   private int valuablesSold;
   private int archerTags;
   private boolean madeFactionRaidable;
   private int partnerItemsUsed;
   private boolean visitedNether;
   private boolean visitedEnd;
   private boolean visitActiveKoth;
   private boolean visitTreasureCove;
   private boolean attemptCaptureKoth;
   private boolean visitGlowstoneMountain;
   @JsonAdapter(TierSetSerializer.class)
   private Set<Tier> claimedRewardsPremium = new HashSet<>();
   @JsonAdapter(TierSetSerializer.class)
   private Set<Tier> claimedRewardsFree = new HashSet<>();

   public BattlePassProgress(UUID uuid, UUID dailyChallengesId) {
      this.uuid = uuid;
      this.dailyChallengesId = dailyChallengesId;
      this.fillDefaults();
   }

   public void fillDefaults() {
      for (Challenge challenge : Foxtrot.getInstance().getBattlePassHandler().getAllChallenges()) {
         if (challenge instanceof KillEntityChallenge) {
            this.entityKills.putIfAbsent(((KillEntityChallenge)challenge).getEntityType(), 0);
         } else if (challenge instanceof MineBlockChallenge) {
            this.blocksMined.putIfAbsent(((MineBlockChallenge)challenge).getMaterial(), 0);
         }
      }
   }

   public void setPremium(boolean premium) {
      this.premium = premium;
      this.requiresSave = true;
   }

   public void setExperience(int experience) {
      this.experience += experience;
   }

   public void addExperience(int experience) {
      this.experience += experience;
   }

   public boolean isTierUnlocked(Tier tier) {
      return this.experience >= tier.getRequiredExperience();
   }

   public int getBlocksMined(Material material, boolean daily) {
      return (daily ? this.dailyBlocksMined : this.blocksMined).getOrDefault(material, 0);
   }

   public void incrementBlocksMined(Material material) {
      this.blocksMined.put(material, this.blocksMined.getOrDefault(material, 0) + 1);
      this.dailyBlocksMined.put(material, this.dailyBlocksMined.getOrDefault(material, 0) + 1);
      this.requiresSave = true;
   }

   public boolean isTrackingBlock(Material material) {
      return this.blocksMined.containsKey(material) || this.dailyBlocksMined.containsKey(material);
   }

   public int getEntitiesKilled(EntityType entityType, boolean daily) {
      return (daily ? this.dailyEntityKills : this.entityKills).getOrDefault(entityType, 0);
   }

   public void incrementEntitiesKilled(EntityType entityType) {
      this.entityKills.put(entityType, this.entityKills.getOrDefault(entityType, 0) + 1);
      this.dailyEntityKills.put(entityType, this.dailyEntityKills.getOrDefault(entityType, 0) + 1);
      this.requiresSave = true;
   }

   public boolean isTrackingKillsForEntity(EntityType entityType) {
      return this.entityKills.containsKey(entityType) || this.dailyEntityKills.containsKey(entityType);
   }

   public Tier getCurrentTier() {
      return Foxtrot.getInstance().getBattlePassHandler().getFirstUnreachedTier(this.experience);
   }

   public boolean hasCompletedChallenge(Challenge challenge) {
      return challenge.isDaily() ? this.completedDailyChallenges.contains(challenge) : this.completedChallenges.contains(challenge);
   }

   public void completeChallenge(Challenge challenge) {
      if (challenge.isDaily()) {
         this.completedDailyChallenges.add(challenge);
      } else {
         this.completedChallenges.add(challenge);
      }

      this.experience = this.experience + challenge.getExperience();
      this.requiresSave = true;
   }

   public void resetDailyChallengeData(UUID uuid) {
      this.dailyChallengesId = uuid;
      this.dailyEntityKills.clear();
      this.dailyBlocksMined.clear();
      this.completedDailyChallenges.clear();
      this.requiresSave = true;
   }

   public void requiresSave() {
      this.requiresSave = true;
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public boolean isRequiresSave() {
      return this.requiresSave;
   }

   public boolean isPremium() {
      return this.premium;
   }

   public int getExperience() {
      return this.experience;
   }

   public Set<Challenge> getCompletedChallenges() {
      return this.completedChallenges;
   }

   public Map<Material, Integer> getBlocksMined() {
      return this.blocksMined;
   }

   public Map<EntityType, Integer> getEntityKills() {
      return this.entityKills;
   }

   public UUID getDailyChallengesId() {
      return this.dailyChallengesId;
   }

   public Set<Challenge> getCompletedDailyChallenges() {
      return this.completedDailyChallenges;
   }

   public Map<Material, Integer> getDailyBlocksMined() {
      return this.dailyBlocksMined;
   }

   public Map<EntityType, Integer> getDailyEntityKills() {
      return this.dailyEntityKills;
   }

   public int getValuablesSold() {
      return this.valuablesSold;
   }

   public int getArcherTags() {
      return this.archerTags;
   }

   public boolean isMadeFactionRaidable() {
      return this.madeFactionRaidable;
   }

   public int getPartnerItemsUsed() {
      return this.partnerItemsUsed;
   }

   public boolean isVisitedNether() {
      return this.visitedNether;
   }

   public boolean isVisitedEnd() {
      return this.visitedEnd;
   }

   public boolean isVisitActiveKoth() {
      return this.visitActiveKoth;
   }

   public boolean isVisitTreasureCove() {
      return this.visitTreasureCove;
   }

   public boolean isAttemptCaptureKoth() {
      return this.attemptCaptureKoth;
   }

   public boolean isVisitGlowstoneMountain() {
      return this.visitGlowstoneMountain;
   }

   public Set<Tier> getClaimedRewardsPremium() {
      return this.claimedRewardsPremium;
   }

   public Set<Tier> getClaimedRewardsFree() {
      return this.claimedRewardsFree;
   }

   public void setDailyChallengesId(UUID dailyChallengesId) {
      this.dailyChallengesId = dailyChallengesId;
   }

   public void setValuablesSold(int valuablesSold) {
      this.valuablesSold = valuablesSold;
   }

   public void setArcherTags(int archerTags) {
      this.archerTags = archerTags;
   }

   public void setMadeFactionRaidable(boolean madeFactionRaidable) {
      this.madeFactionRaidable = madeFactionRaidable;
   }

   public void setPartnerItemsUsed(int partnerItemsUsed) {
      this.partnerItemsUsed = partnerItemsUsed;
   }

   public void setVisitedNether(boolean visitedNether) {
      this.visitedNether = visitedNether;
   }

   public void setVisitedEnd(boolean visitedEnd) {
      this.visitedEnd = visitedEnd;
   }

   public void setVisitActiveKoth(boolean visitActiveKoth) {
      this.visitActiveKoth = visitActiveKoth;
   }

   public void setVisitTreasureCove(boolean visitTreasureCove) {
      this.visitTreasureCove = visitTreasureCove;
   }

   public void setAttemptCaptureKoth(boolean attemptCaptureKoth) {
      this.attemptCaptureKoth = attemptCaptureKoth;
   }

   public void setVisitGlowstoneMountain(boolean visitGlowstoneMountain) {
      this.visitGlowstoneMountain = visitGlowstoneMountain;
   }
}
