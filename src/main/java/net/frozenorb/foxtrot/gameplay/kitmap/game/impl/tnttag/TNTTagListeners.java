package net.frozenorb.foxtrot.gameplay.kitmap.game.impl.tnttag;

import cc.fyre.neutron.Neutron;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class TNTTagListeners implements Listener {
   private final GameHandler gameHandler = Foxtrot.getInstance().getMapHandler().getGameHandler();

   @EventHandler
   private void onDamage(EntityDamageByEntityEvent event) {
      if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
         Player damager = (Player)event.getDamager();
         Player target = (Player)event.getEntity();
         if (target.getLocation().getWorld().getName().equals("kits_events")) {
            if (this.gameHandler.isOngoingGame() && this.gameHandler.getOngoingGame() instanceof TNTTagGame) {
               TNTTagGame ongoingGame = (TNTTagGame)this.gameHandler.getOngoingGame();
               if (ongoingGame.getState() == GameState.RUNNING) {
                  if (ongoingGame.isPlaying(damager.getUniqueId()) && ongoingGame.isPlaying(target.getUniqueId())) {
                     if (ongoingGame.isStarted()) {
                        event.setDamage(0.0);
                        if (ongoingGame.getTagged().contains(damager.getUniqueId())) {
                           if (!ongoingGame.getTagged().contains(target.getUniqueId())) {
                              String targetName = Neutron.getInstance().getProfileHandler().findDisplayName(target.getUniqueId());
                              String damagerName = Neutron.getInstance().getProfileHandler().findDisplayName(damager.getUniqueId());
                              ongoingGame.sendMessages(targetName + " &7has been tagged by " + damagerName + "&7.");
                              ongoingGame.setTagged(target, true);
                              ongoingGame.setTagged(damager, false);
                              event.setDamage(0.0);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
