package net.frozenorb.foxtrot.gameplay.ability.listener;

import cc.fyre.proton.util.TimeUtils;
import java.beans.ConstructorProperties;
import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.AbilityHandler;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class AbilityListener implements Listener {
   private Foxtrot instance;
   private AbilityHandler abilityHandler;

   @EventHandler(priority = EventPriority.LOW)
   private void onUse(AbilityUseEvent event) {
      Player player = event.getPlayer();
      Ability ability = event.getAbility();
      if (ability.hasCooldown(player)) {
         event.setCancelled(true);
      } else {
         Location blockAt = event.getChosenLocation();
         if (!blockAt.getWorld().getName().equalsIgnoreCase("sg")) {
            if (!ability.isAllowedAtLocation(blockAt)) {
               Team ownerTeam = LandBoard.getInstance().getTeam(blockAt);
               String teamName;
               if (ownerTeam != null) {
                  teamName = ownerTeam.getName(player);
               } else if (!Foxtrot.getInstance().getServerHandler().isWarzone(blockAt)) {
                  teamName = ChatColor.GRAY + "The Wilderness";
               } else {
                  teamName = ChatColor.DARK_RED + "WarZone";
               }

               player.sendMessage(ChatColor.RED + "You cannot use a " + ability.getDisplayName() + ChatColor.RED + " in " + teamName + ChatColor.RED + ".");
               event.setCancelled(true);
            } else if (!event.isOneHit()) {
               if (!Foxtrot.getInstance().getNetworkBoosterHandler().isFrenzy()
                  && (LandBoard.getInstance().getTeam(player.getLocation()) == null || !LandBoard.getInstance().getTeam(player.getLocation()).isRaidable())) {
                  if (this.abilityHandler.getGlobalCooldowns().containsKey(player.getUniqueId())
                     && this.abilityHandler.getGlobalCooldowns().get(player.getUniqueId()) > System.currentTimeMillis()) {
                     long difference = this.abilityHandler.getGlobalCooldowns().get(player.getUniqueId()) - System.currentTimeMillis();
                     player.sendMessage(
                        ChatColor.translate(
                           "&cYou may not use any ability items for another &l" + TimeUtils.formatIntoDetailedString((int)(difference / 1000L)) + "&c."
                        )
                     );
                     event.setCancelled(true);
                  } else {
                     this.abilityHandler.getGlobalCooldowns().put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(5L));
                  }
               }
            }
         }
      }
   }

   @ConstructorProperties({"instance", "abilityHandler"})
   public AbilityListener(Foxtrot instance, AbilityHandler abilityHandler) {
      this.instance = instance;
      this.abilityHandler = abilityHandler;
   }
}
