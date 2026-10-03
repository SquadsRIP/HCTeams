package net.frozenorb.foxtrot.team.upgrade.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.upgrade.UpgradeType;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class UpgradeListener implements Listener {
   @EventHandler(priority = EventPriority.MONITOR)
   private void onDrop(EntityDeathEvent event) {
      if (!(event.getEntity() instanceof Player) && (event.getEntity() instanceof Monster || event.getEntity() instanceof Animals)) {
         Player killer = event.getEntity().getKiller();
         if (killer != null) {
            Team team = Foxtrot.getInstance().getTeamHandler().getTeam(killer);
            if (team != null) {
               if (team.getPurchasedUpgrades().contains(UpgradeType.DOUBLE_XP)) {
                  event.setDroppedExp(event.getDroppedExp() * 2);
               }

               if (team.getPurchasedUpgrades().contains(UpgradeType.DOUBLE_DROPS)) {
                  event.getDrops().forEach(it -> it.setAmount(it.getAmount() * 2));
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   private void onBreak(BlockBreakEvent event) {
      Material type = event.getBlock().getType();
      if (type.name().contains("ORE")) {
         Player player = event.getPlayer();
         Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
         if (team != null) {
            if (team.getPurchasedUpgrades().contains(UpgradeType.DOUBLE_XP)) {
               event.setExpToDrop(event.getExpToDrop() * 2);
            }

            if (team.getPurchasedUpgrades().contains(UpgradeType.DOUBLE_DROPS)) {
               event.getBlock().getDrops().forEach(it -> it.setAmount(it.getAmount() * 2));
            }
         }
      }
   }
}
