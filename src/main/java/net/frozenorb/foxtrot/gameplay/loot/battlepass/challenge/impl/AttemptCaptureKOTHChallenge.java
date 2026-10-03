package net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl;

import java.lang.reflect.Type;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.Challenge;
import org.bukkit.entity.Player;

public class AttemptCaptureKOTHChallenge extends Challenge {
   public AttemptCaptureKOTHChallenge(String id, String name, int experience, boolean daily) {
      super(id, name, experience, daily);
   }

   @Override
   public Type getAbstractType() {
      return AttemptCaptureKOTHChallenge.class;
   }

   @Override
   public String getText() {
      return "Attempt to capture a KOTH";
   }

   @Override
   public boolean meetsCompletionCriteria(Player player) {
      return Foxtrot.getInstance().getBattlePassHandler().getProgress(player.getUniqueId()).isAttemptCaptureKoth();
   }
}
