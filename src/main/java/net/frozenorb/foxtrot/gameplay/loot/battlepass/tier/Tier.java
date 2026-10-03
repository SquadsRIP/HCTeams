package net.frozenorb.foxtrot.gameplay.loot.battlepass.tier;

import java.beans.ConstructorProperties;
import java.util.function.Consumer;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.reward.Reward;

public class Tier {
   private final int number;
   private final int requiredExperience;
   private Reward premiumReward;
   private Reward freeReward;

   public Tier newReward(boolean premium, Consumer<Reward> consumer) {
      if (premium) {
         this.premiumReward = new Reward(this);
         consumer.accept(this.premiumReward);
      } else {
         this.freeReward = new Reward(this);
         consumer.accept(this.freeReward);
      }

      return this;
   }

   @ConstructorProperties({"number", "requiredExperience"})
   public Tier(int number, int requiredExperience) {
      this.number = number;
      this.requiredExperience = requiredExperience;
   }

   public int getNumber() {
      return this.number;
   }

   public int getRequiredExperience() {
      return this.requiredExperience;
   }

   public Reward getPremiumReward() {
      return this.premiumReward;
   }

   public Reward getFreeReward() {
      return this.freeReward;
   }
}
