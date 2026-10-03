package net.frozenorb.foxtrot.gameplay.loot.battlepass.reward;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.BattlePassProgress;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.tier.Tier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Reward {
   private final UUID uuid = UUID.randomUUID();
   private final Tier tier;
   private List<String> text = new ArrayList<>();
   private List<String> commands = new ArrayList<>();

   public Reward addText(String... lines) {
      this.text.addAll(Arrays.asList(lines));
      return this;
   }

   public Reward addCommand(String command) {
      this.commands.add(command);
      return this;
   }

   public boolean isFreeReward() {
      return this.tier.getFreeReward() == this;
   }

   public boolean hasClaimed(BattlePassProgress progress) {
      return this.isFreeReward() && progress.getClaimedRewardsFree().contains(this.tier)
         || !this.isFreeReward() && progress.getClaimedRewardsPremium().contains(this.tier);
   }

   public void execute(Player player) {
      for (String command : this.commands) {
         String processedCommand = command.replace("{playerName}", player.getName())
            .replace("{playerUuid}", player.getUniqueId().toString())
            .replace("{tier}", String.valueOf(this.tier.getNumber()));
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand);
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Reward reward = (Reward)o;
         return this.uuid == reward.uuid && this.tier.getNumber() == reward.tier.getNumber();
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.tier, this.text, this.commands);
   }

   @ConstructorProperties("tier")
   public Reward(Tier tier) {
      this.tier = tier;
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public Tier getTier() {
      return this.tier;
   }

   public List<String> getText() {
      return this.text;
   }

   public List<String> getCommands() {
      return this.commands;
   }
}
