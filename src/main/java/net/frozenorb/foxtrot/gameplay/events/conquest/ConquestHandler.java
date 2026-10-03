package net.frozenorb.foxtrot.gameplay.events.conquest;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.conquest.game.ConquestGame;
import org.bukkit.ChatColor;

public class ConquestHandler {
   public static final String PREFIX = ChatColor.YELLOW + "[Conquest]";
   public static final int POINTS_DEATH_PENALTY = 20;
   public static final String KOTH_NAME_PREFIX = "Conquest-";
   public static final int TIME_TO_CAP = 30;
   private ConquestGame game;

   public static int getPointsToWin() {
      return Foxtrot.getInstance().getConfiguration("gameplay.yml").getInt("conquestWinPoints", 250);
   }

   public ConquestGame getGame() {
      return this.game;
   }

   public void setGame(ConquestGame game) {
      this.game = game;
   }
}
