package net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.impl;

import cc.fyre.proton.util.TimeUtils;
import java.lang.reflect.Type;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.Challenge;
import org.bukkit.entity.Player;

public class PlayTimeChallenge extends Challenge {
   private long playTime;

   public PlayTimeChallenge(String id, String name, int experience, boolean daily, long playTime) {
      super(id, name, experience, daily);
      this.playTime = playTime;
   }

   @Override
   public Type getAbstractType() {
      return PlayTimeChallenge.class;
   }

   @Override
   public String getText() {
      return "Reach " + TimeUtils.formatIntoDetailedString((int)(this.playTime / 1000L)) + " of play time";
   }

   @Override
   public boolean hasStarted(Player player) {
      return true;
   }

   @Override
   public boolean meetsCompletionCriteria(Player player) {
      return Foxtrot.getInstance().getPlaytimeMap().getPlaytime(player.getUniqueId())
            + Foxtrot.getInstance().getPlaytimeMap().getCurrentSession(player.getUniqueId())
         >= this.playTime;
   }

   @Override
   public String getProgressText(Player player) {
      long playerTime = Foxtrot.getInstance().getPlaytimeMap().getPlaytime(player.getUniqueId())
         + Foxtrot.getInstance().getPlaytimeMap().getCurrentSession(player.getUniqueId());
      long remaining = this.playTime - playerTime;
      return "You need to play for another " + TimeUtils.formatIntoDetailedString((int)(remaining / 1000L)) + ".";
   }

   public long getPlayTime() {
      return this.playTime;
   }
}
