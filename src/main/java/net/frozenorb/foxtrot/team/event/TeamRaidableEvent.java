package net.frozenorb.foxtrot.team.event;

import java.beans.ConstructorProperties;
import java.util.UUID;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeamRaidableEvent extends Event {
   private static final HandlerList handlerList = new HandlerList();
   private final Player player;
   private final Player killer;
   private final Team team;
   private final double oldDTR;
   private final double newDTR;
   private final UUID playerUUID;

   @Override
   public HandlerList getHandlers() {
      return handlerList;
   }

   @ConstructorProperties({"player", "killer", "team", "oldDTR", "newDTR", "playerUUID"})
   public TeamRaidableEvent(Player player, Player killer, Team team, double oldDTR, double newDTR, UUID playerUUID) {
      this.player = player;
      this.killer = killer;
      this.team = team;
      this.oldDTR = oldDTR;
      this.newDTR = newDTR;
      this.playerUUID = playerUUID;
   }

   public Player getPlayer() {
      return this.player;
   }

   public Player getKiller() {
      return this.killer;
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

   public UUID getPlayerUUID() {
      return this.playerUUID;
   }

   public static HandlerList getHandlerList() {
      return handlerList;
   }
}
