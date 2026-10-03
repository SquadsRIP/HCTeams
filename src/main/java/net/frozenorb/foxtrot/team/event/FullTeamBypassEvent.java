package net.frozenorb.foxtrot.team.event;

import java.beans.ConstructorProperties;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FullTeamBypassEvent extends Event {
   private static HandlerList handlerList = new HandlerList();
   private final Player player;
   private final Team team;
   private final boolean roster;
   private boolean allowBypass = false;
   private int extraSlots = 0;

   @Override
   public HandlerList getHandlers() {
      return handlerList;
   }

   @ConstructorProperties({"player", "team", "roster"})
   public FullTeamBypassEvent(Player player, Team team, boolean roster) {
      this.player = player;
      this.team = team;
      this.roster = roster;
   }

   public Player getPlayer() {
      return this.player;
   }

   public Team getTeam() {
      return this.team;
   }

   public boolean isAllowBypass() {
      return this.allowBypass;
   }

   public int getExtraSlots() {
      return this.extraSlots;
   }

   public static HandlerList getHandlerList() {
      return handlerList;
   }

   public boolean isRoster() {
      return this.roster;
   }

   public void setAllowBypass(boolean allowBypass) {
      this.allowBypass = allowBypass;
   }

   public void setExtraSlots(int extraSlots) {
      this.extraSlots = extraSlots;
   }
}
