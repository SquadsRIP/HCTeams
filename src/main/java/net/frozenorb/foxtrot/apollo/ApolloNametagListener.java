package net.frozenorb.foxtrot.apollo;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.module.nametag.Nametag;
import com.lunarclient.apollo.module.nametag.NametagModule;
import com.lunarclient.apollo.module.nametag.NametagVisibilityOverride;
import com.lunarclient.apollo.player.ApolloPlayer;
import com.lunarclient.apollo.recipients.Recipients;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.nametag.FactionNametagFormatter;
import net.frozenorb.foxtrot.team.Team;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ApolloNametagListener implements Listener {
   public static void startTask(Plugin plugin) {
      (new BukkitRunnable() {
         @Override
         public void run() {
            try {
               for (Player player : Bukkit.getOnlinePlayers()) {
                  ApolloNametagListener.updateNametagsForViewer(player);
               }
            } catch (Throwable var3) {
            }
         }
      }).runTaskTimer(plugin, 40L, 40L);
   }

   public static void updateNametagsForViewer(Player player) {
      if (ApolloService.isApolloEnabled()) {
         try {
            Optional optional = Apollo.getPlayerManager().getPlayer(player.getUniqueId());
            if (!optional.isPresent()) {
               return;
            }

            ApolloPlayer apolloPlayer = (ApolloPlayer)optional.get();
            Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId());

            for (Player player2 : Bukkit.getOnlinePlayers()) {
               sendNametagToViewer(player, apolloPlayer, team, player2);
            }
         } catch (Throwable var6) {
         }
      }
   }

   private static void sendNametagToViewer(Player player, ApolloPlayer apolloPlayer, Team team, Player player2) {
      try {
         ArrayList<Component> arrayList = new ArrayList<>();
         String string2 = ChatColor.WHITE.toString();
         if (player2 == player) {
            string2 = ChatColor.GREEN.toString();
         } else {
            Team team2 = Foxtrot.getInstance().getTeamHandler().getTeam(player2.getUniqueId());
            if (team2 != null) {
               string2 = team != null && team.isMember(player2.getUniqueId())
                  ? ChatColor.GREEN.toString()
                  : (
                     team != null && team.getFocusedTeam() == team2
                        ? ChatColor.LIGHT_PURPLE.toString()
                        : (team != null && team2.isAlly(player.getUniqueId()) ? ChatColor.LIGHT_PURPLE.toString() : ChatColor.RED.toString())
                  );
            }
         }

         String staffLine = FactionNametagFormatter.staffRankLine(player2);
         if (staffLine != null) {
            arrayList.add(Component.text(staffLine));
         }

         Team team2 = Foxtrot.getInstance().getTeamHandler().getTeam(player2.getUniqueId());
         if (team2 != null) {
            String string = team2.getName(player);
            String string3 = team2.formatDTR();
            String string4 = ChatColor.GOLD + "[" + string + " " + string3 + ChatColor.GOLD + "]";
            arrayList.add(Component.text(string4));
         }

         arrayList.add(Component.text(string2 + player2.getName()));

         Nametag nametag = Nametag.builder().lines(arrayList).visibilityOverride(NametagVisibilityOverride.NONE).build();
         ((NametagModule)Apollo.getModuleManager().getModule(NametagModule.class))
            .overrideNametag(Recipients.of(Collections.singleton(apolloPlayer)), player2.getUniqueId(), nametag);
      } catch (Throwable var10) {
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onJoin(PlayerJoinEvent playerJoinEvent) {
      final Player player = playerJoinEvent.getPlayer();
      (new BukkitRunnable() {
         @Override
         public void run() {
            if (player.isOnline()) {
               ApolloNametagListener.updateNametagsForViewer(player);

               for (Player player2 : Bukkit.getOnlinePlayers()) {
                  if (player2 != player) {
                     ApolloNametagListener.updateNametagsForViewer(player2);
                  }
               }
            }
         }
      }).runTaskLater(Foxtrot.getInstance(), 60L);
   }
}
