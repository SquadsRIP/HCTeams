package net.frozenorb.foxtrot.team.event;

import java.beans.ConstructorProperties;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeamEnterClaimEvent extends Event implements Cancellable {
   private static final HandlerList handlerList = new HandlerList();
   private final Player player;
   private final Location from;
   private final Location to;
   private final Team fromTeam;
   private final Team toTeam;
   private boolean cancelled;

   @Override
   public HandlerList getHandlers() {
      return handlerList;
   }

   @ConstructorProperties({"player", "from", "to", "fromTeam", "toTeam", "cancelled"})
   public TeamEnterClaimEvent(Player player, Location from, Location to, Team fromTeam, Team toTeam, boolean cancelled) {
      this.player = player;
      this.from = from;
      this.to = to;
      this.fromTeam = fromTeam;
      this.toTeam = toTeam;
      this.cancelled = cancelled;
   }

   public Player getPlayer() {
      return this.player;
   }

   public Location getFrom() {
      return this.from;
   }

   public Location getTo() {
      return this.to;
   }

   public Team getFromTeam() {
      return this.fromTeam;
   }

   public Team getToTeam() {
      return this.toTeam;
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }

   public static HandlerList getHandlerList() {
      return handlerList;
   }
}
