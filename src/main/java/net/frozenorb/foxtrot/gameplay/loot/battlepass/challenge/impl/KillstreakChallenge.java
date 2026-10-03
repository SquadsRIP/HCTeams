package net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl;

import java.lang.reflect.Type;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.Challenge;
import net.frozenorb.foxtrot.util.Formats;
import org.bukkit.entity.Player;

public class KillstreakChallenge extends Challenge {
   private int streak;

   public KillstreakChallenge(String id, String name, int experience, boolean daily, int streak) {
      super(id, name, experience, daily);
      this.streak = streak;
   }

   @Override
   public Type getAbstractType() {
      return KillstreakChallenge.class;
   }

   @Override
   public String getText() {
      return "Reach a " + this.streak + " killstreak";
   }

   @Override
   public boolean hasStarted(Player player) {
      return Foxtrot.getInstance().getKillstreakMap().getKillstreak(player.getUniqueId()) > 0;
   }

   @Override
   public boolean meetsCompletionCriteria(Player player) {
      return Foxtrot.getInstance().getKillstreakMap().getKillstreak(player.getUniqueId()) >= this.streak;
   }

   @Override
   public String getProgressText(Player player) {
      int amount = Foxtrot.getInstance().getKillstreakMap().getKillstreak(player.getUniqueId());
      int remaining = this.streak - amount;
      return "You need to kill " + Formats.formatNumber(remaining) + " more players to reach a killstreak of " + this.streak + ".";
   }

   public int getStreak() {
      return this.streak;
   }
}
