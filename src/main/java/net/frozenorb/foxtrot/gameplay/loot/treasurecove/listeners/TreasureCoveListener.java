package net.frozenorb.foxtrot.gameplay.loot.treasurecove.listeners;

import cc.fyre.neutron.util.PlayerUtil;
import cc.fyre.proton.event.HourEvent;
import java.beans.ConstructorProperties;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.BattlePassProgress;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.event.TeamEnterClaimEvent;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TreasureCoveListener implements Listener {
   private Foxtrot instance;
   public static long SPAWN_IN = 0L;

   @EventHandler
   public void onHour(HourEvent event) {
   }

   @EventHandler
   private void onEnter(TeamEnterClaimEvent event) {
      Player player = event.getPlayer();
      Team teamTo = event.getToTeam();
      if (teamTo != null && player.getWorld().getEnvironment().equals(Environment.NETHER)) {
         if (teamTo.getName().startsWith("TreasureCove") && teamTo.getOwner() == null) {
            BattlePassProgress battlePassProgress = Foxtrot.getInstance().getBattlePassHandler().fetchProgress(player.getUniqueId());
            if (battlePassProgress != null) {
               if (!battlePassProgress.isVisitTreasureCove()) {
                  PlayerUtil.sendTitle(player, "&6&lTreasure Cove", "&eWelcome to Treasure Cove! Loot chests for free items!");
                  Foxtrot.getInstance()
                     .getServer()
                     .getScheduler()
                     .runTaskLater(
                        Foxtrot.getInstance(), () -> PlayerUtil.sendTitle(player, "&6&lTreasure Cove", "&eTreasure Cove is currently unavailable."), 120L
                     );
                  battlePassProgress.setVisitTreasureCove(true);
                  battlePassProgress.requiresSave();
                  Foxtrot.getInstance().getBattlePassHandler().checkCompletionsAsync(event.getPlayer());
               }
            }
         }
      }
   }

   @ConstructorProperties("instance")
   public TreasureCoveListener(Foxtrot instance) {
      this.instance = instance;
   }
}
