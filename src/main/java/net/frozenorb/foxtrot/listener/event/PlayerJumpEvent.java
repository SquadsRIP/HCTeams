package net.frozenorb.foxtrot.listener.event;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class PlayerJumpEvent extends PlayerEvent implements Cancellable {
   private static final HandlerList handlers = new HandlerList();
   private static final Listener listener = new PlayerJumpEvent.PlayerJumpEventListener();
   private boolean isCancelled = false;

   public PlayerJumpEvent(Player player) {
      super(player);
   }

   @Override
   public boolean isCancelled() {
      return this.isCancelled;
   }

   @Override
   public void setCancelled(boolean cancel) {
      this.isCancelled = cancel;
      if (cancel) {
         this.player.setVelocity(new Vector());
      }
   }

   @Override
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   static {
      Bukkit.getServer().getPluginManager().registerEvents(listener, Foxtrot.getInstance());
   }

   private static class PlayerJumpEventListener implements Listener {
      private PlayerJumpEventListener() {
      }

      @EventHandler
      public void onPlayerMove(PlayerMoveEvent event) {
         Player player = event.getPlayer();
         Vector velocity = player.getVelocity();
         if (velocity.getY() > 0.0) {
            double jumpVelocity = 0.42F;
            PotionEffect jumpPotion = player.getActivePotionEffects().stream().filter(it -> it.getType() == PotionEffectType.JUMP).findFirst().orElse(null);
            if (jumpPotion != null) {
               jumpVelocity += (jumpPotion.getAmplifier() + 1.0F) * 0.1F;
            }

            if (player.getLocation().getBlock().getType() != Material.LADDER && Double.compare(velocity.getY(), jumpVelocity) == 0) {
               Bukkit.getServer().getPluginManager().callEvent(new PlayerJumpEvent(event.getPlayer()));
            }
         }
      }
   }
}
