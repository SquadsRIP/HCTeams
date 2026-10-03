package net.frozenorb.foxtrot.team.event;

import net.frozenorb.foxtrot.team.Team;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerBuildInOthersClaimEvent extends PlayerEvent {
   private static HandlerList handlerList = new HandlerList();
   private boolean willIgnore;
   private final Block block;
   private final Team team;

   public PlayerBuildInOthersClaimEvent(Player who, Block block, Team team) {
      super(who);
      this.block = block;
      this.team = team;
   }

   @Override
   public HandlerList getHandlers() {
      return handlerList;
   }

   public static HandlerList getHandlerList() {
      return handlerList;
   }

   public boolean isWillIgnore() {
      return this.willIgnore;
   }

   public void setWillIgnore(boolean willIgnore) {
      this.willIgnore = willIgnore;
   }

   public Block getBlock() {
      return this.block;
   }

   public Team getTeam() {
      return this.team;
   }
}
