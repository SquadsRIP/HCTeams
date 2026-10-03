package net.frozenorb.foxtrot.gameplay.kitmap.bounty.listener;

import java.beans.ConstructorProperties;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.bounty.Bounty;
import net.frozenorb.foxtrot.gameplay.kitmap.bounty.BountyManager;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class BountyListener implements Listener {
   private Foxtrot instance;
   private BountyManager bountyManager;

   @EventHandler(priority = EventPriority.LOW)
   private void onDeath(PlayerDeathEvent event) {
      Player player = event.getEntity();
      Player killer = player.getKiller();
      if (killer != null) {
         Bounty bounty = this.bountyManager.getBounty(player);
         if (bounty != null) {
            if (!this.instance.getInDuelPredicate().test(player) && !this.instance.getInEventPredicate().test(player)) {
               Bukkit.broadcastMessage(
                  CC.GRAY
                     + "["
                     + CC.GOLD
                     + "Bounty"
                     + CC.GRAY
                     + "] "
                     + killer.getDisplayName()
                     + CC.YELLOW
                     + " has claimed the bounty on "
                     + player.getDisplayName()
                     + CC.YELLOW
                     + " worth "
                     + CC.GREEN
                     + bounty.getGems()
                     + " gems"
                     + CC.YELLOW
                     + "!"
               );
               this.instance.getGemMap().addGems(killer.getUniqueId(), bounty.getGems(), true);
               this.bountyManager.removeBounty(player);
            }
         }
      }
   }

   @ConstructorProperties({"instance", "bountyManager"})
   public BountyListener(Foxtrot instance, BountyManager bountyManager) {
      this.instance = instance;
      this.bountyManager = bountyManager;
   }
}
