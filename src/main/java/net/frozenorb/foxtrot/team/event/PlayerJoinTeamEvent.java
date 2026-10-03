package net.frozenorb.foxtrot.team.event;

import java.beans.ConstructorProperties;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerJoinTeamEvent extends Event {
   private static HandlerList handlerList = new HandlerList();
   private final Player player;
   private final Team team;

   @Override
   public HandlerList getHandlers() {
      return handlerList;
   }

   @ConstructorProperties({"player", "team"})
   public PlayerJoinTeamEvent(Player player, Team team) {
      this.player = player;
      this.team = team;
   }

   public Player getPlayer() {
      return this.player;
   }

   public Team getTeam() {
      return this.team;
   }

   public static HandlerList getHandlerList() {
      return handlerList;
   }
}
