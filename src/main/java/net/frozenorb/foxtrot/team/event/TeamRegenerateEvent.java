package net.frozenorb.foxtrot.team.event;

import java.beans.ConstructorProperties;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeamRegenerateEvent extends Event {
   private static final HandlerList handlerList = new HandlerList();
   private final Team team;
   private final double oldDTR;
   private final double newDTR;
   private final boolean wasRaidable;

   @Override
   public HandlerList getHandlers() {
      return handlerList;
   }

   @ConstructorProperties({"team", "oldDTR", "newDTR", "wasRaidable"})
   public TeamRegenerateEvent(Team team, double oldDTR, double newDTR, boolean wasRaidable) {
      this.team = team;
      this.oldDTR = oldDTR;
      this.newDTR = newDTR;
      this.wasRaidable = wasRaidable;
   }

   public Team getTeam() {
      return this.team;
   }

   public double getOldDTR() {
      return this.oldDTR;
   }

   public double getNewDTR() {
      return this.newDTR;
   }

   public boolean isWasRaidable() {
      return this.wasRaidable;
   }

   public static HandlerList getHandlerList() {
      return handlerList;
   }
}
