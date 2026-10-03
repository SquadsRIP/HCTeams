package net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge;

import java.beans.ConstructorProperties;
import java.lang.reflect.Type;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.BattlePassHandler;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.BattlePassProgress;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.tier.Tier;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Formats;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public abstract class Challenge {
   private String id;
   private String name;
   private int experience;
   private boolean daily;

   public abstract Type getAbstractType();

   public abstract String getText();

   public boolean hasStarted(Player player) {
      return false;
   }

   public abstract boolean meetsCompletionCriteria(Player var1);

   public String getProgressText(Player player) {
      return null;
   }

   public BattlePassProgress getProgress(Player player) {
      return Foxtrot.getInstance().getBattlePassHandler().getProgress(player.getUniqueId());
   }

   public void completed(Player player, BattlePassProgress progress) {
      String formattedExperience = Formats.formatNumber(this.experience);
      player.sendMessage(
         BattlePassHandler.CHAT_PREFIX
            + CC.YELLOW
            + "You've completed the "
            + CC.GOLD
            + this.name
            + " "
            + CC.YELLOW
            + (this.daily ? "daily" : "premium")
            + " challenge! "
            + CC.GRAY
            + "("
            + CC.GREEN
            + "+"
            + formattedExperience
            + " XP"
            + CC.GRAY
            + ")"
      );
      Tier currentTier = progress.getCurrentTier();
      progress.completeChallenge(this);
      Tier newTier = progress.getCurrentTier();
      if (!currentTier.equals(newTier)) {
         boolean newRewards = newTier.getFreeReward() != null;
         if (newTier.getPremiumReward() != null && progress.isPremium()) {
            newRewards = true;
         }

         player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
         if (newRewards) {
            player.sendMessage(
               BattlePassHandler.CHAT_PREFIX
                  + "Congratulations! You've reached Tier "
                  + newTier.getNumber()
                  + "! You have new rewards waiting to be collected."
            );
         } else {
            player.sendMessage(BattlePassHandler.CHAT_PREFIX + "Congratulations! You've reached Tier " + newTier.getNumber() + "!");
         }
      }
   }

   @ConstructorProperties({"id", "name", "experience", "daily"})
   public Challenge(String id, String name, int experience, boolean daily) {
      this.id = id;
      this.name = name;
      this.experience = experience;
      this.daily = daily;
   }

   public String getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public int getExperience() {
      return this.experience;
   }

   public boolean isDaily() {
      return this.daily;
   }
}
