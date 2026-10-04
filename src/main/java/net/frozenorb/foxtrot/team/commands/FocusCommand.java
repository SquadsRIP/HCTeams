package net.frozenorb.foxtrot.team.commands;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import java.awt.Color;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.frozenorb.foxtrot.apollo.ApolloService;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import net.frozenorb.foxtrot.listener.StaffUtilsListener;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FocusCommand {
   @Command(names = "focus", description = "Focus a player", permission = "")
   public static void focus(Player sender, @Parameter(name = "player") Player target) {
      Team senderTeam = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
      Team targetTeam = Foxtrot.getInstance().getTeamHandler().getTeam(target);
      if (senderTeam == null) {
         sender.sendMessage(ChatColor.RED + "You are not in a faction!");
      } else if (senderTeam == targetTeam) {
         sender.sendMessage(ChatColor.YELLOW + "You cannot target a player on your faction.");
      } else {
         senderTeam.setFocused(target.getUniqueId());
         String focusMsg = Foxtrot.getInstance().getConfig().getString("focusMessage", "&d<target> &ehas been focused by &d<player>&e.");
         focusMsg = focusMsg.replace("<target>", target.getName()).replace("<player>", sender.getName());
         senderTeam.sendMessage(ChatColor.translateAlternateColorCodes('&', focusMsg));

         for (Player onlinePlayer : getOnlinePlayersList()) {
            if (senderTeam.isMember(onlinePlayer.getUniqueId())) {
               LunarClientListener.updateNametag(onlinePlayer);
            }
         }
      }
   }

   @Command(names = "unfocus", description = "Remove player focus", permission = "")
   public static void unfocus(Player sender) {
      Team senderTeam = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
      if (senderTeam == null) {
         sender.sendMessage(ChatColor.RED + "You are not in a faction!");
      } else {
         UUID focused = senderTeam.getFocused();
         if (focused == null) {
            sender.sendMessage(ChatColor.YELLOW + "You are not focusing anyone!");
         } else {
            senderTeam.setFocused(null);
            String unfocusMsg = Foxtrot.getInstance().getConfig().getString("unfocusMessage", "&d<target> &ehas been unfocused by &d<player>&e.");
            unfocusMsg = unfocusMsg.replace("<target>", Proton.getInstance().getUuidCache().name(focused)).replace("<player>", sender.getName());
            senderTeam.sendMessage(ChatColor.translateAlternateColorCodes('&', unfocusMsg));

            for (Player onlinePlayer : getOnlinePlayersList()) {
               if (senderTeam.isMember(onlinePlayer.getUniqueId())) {
                  LunarClientListener.updateNametag(onlinePlayer);
               }
            }
         }
      }
   }

   @Command(names = {"team focus", "t focus", "f focus", "faction focus", "fac focus"}, permission = "")
   public static void execute(Player player, @Parameter(name = "team") String focusString) {
      Player target = Foxtrot.getInstance().getServer().getPlayer(focusString);
      Team focusTeam = target != null ? Foxtrot.getInstance().getTeamHandler().getTeam(target) : Foxtrot.getInstance().getTeamHandler().getTeam(focusString);
      if (focusTeam == null && focusString.equalsIgnoreCase("koth") && Foxtrot.getInstance().getEventHandler().getActiveKOTH() != null) {
         player.chat("/f focus " + Foxtrot.getInstance().getEventHandler().getActiveKOTH().getName());
      } else {
         if (focusTeam == null && StaffUtilsListener.lastPlayerHit.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.YELLOW + "No team or member with the name " + focusString + " found. Attempting to focus the last player you hit...");
            focusTeam = Foxtrot.getInstance().getTeamHandler().getTeam(StaffUtilsListener.lastPlayerHit.get(player.getUniqueId()));
         }

         if (focusTeam == null) {
            player.sendMessage(ChatColor.YELLOW + "No team or member with the name " + focusString + " found.");
         } else {
            Team team2 = Foxtrot.getInstance().getTeamHandler().getTeam(player);
            if (team2 == null) {
               player.sendMessage(ChatColor.YELLOW + "You are not on a team!");
            } else if (focusTeam == team2) {
               player.sendMessage(ChatColor.YELLOW + "You can't focus yourself!");
            } else {
               if (team2.getFocusWaypoint() != null) {
                  team2.getOnlineMembers().forEach(it -> ApolloService.removeWaypoint(it, team2.getFocusWaypoint()));
                  team2.setFocusWaypoint(null);
               }

               Team passFocusedTeam = team2.getFocusedTeam();
               team2.setFocusedTeam(focusTeam == team2.getFocusedTeam() ? null : focusTeam);
               if (team2.getFocusedTeam() != null && team2.getFocusedTeam().getHQ() != null) {
                  Location location = team2.getFocusedTeam().getHQ();
                  team2.setFocusWaypoint(
                     new LCWaypoint(ChatColor.YELLOW + team2.getFocusedTeam().getName() + "'s HQ" + ChatColor.WHITE, location, Color.RED.getRGB(), true)
                  );
                  team2.getOnlineMembers().forEach(it -> ApolloService.sendWaypoint(it, team2.getFocusWaypoint()));
               }

               if (passFocusedTeam != null) {
                  passFocusedTeam.getOnlineMembers().forEach(LunarClientListener::updateNametag);
               }

               focusTeam.getOnlineMembers().forEach(LunarClientListener::updateNametag);
               String teamFocusMsg = Foxtrot.getInstance().getConfig().getString("teamFocusMessage", "&eTeam &d<team> &ehas been <action> by &d<player>&e.");
               teamFocusMsg = teamFocusMsg.replace("<team>", focusTeam.getName())
                  .replace("<action>", team2.getFocusedTeam() == null ? "unfocused" : "focused")
                  .replace("<player>", player.getName());
               team2.sendMessage(ChatColor.translateAlternateColorCodes('&', teamFocusMsg));
            }
         }
      }
   }

   @Command(names = {"team unfocus", "t unfocus", "f unfocus", "faction unfocus", "fac unfocus"}, permission = "")
   public static void unfocusTeam(Player player) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
      if (team == null) {
         player.sendMessage(ChatColor.YELLOW + "You are not on a team!");
      } else {
         Team focusedTeam = team.getFocusedTeam();
         if (focusedTeam == null) {
            player.sendMessage(ChatColor.YELLOW + "You don't have any teams focused!");
         } else {
            if (team.getFocusWaypoint() != null) {
               team.getOnlineMembers().forEach(it -> ApolloService.removeWaypoint(it, team.getFocusWaypoint()));
            }

            team.setFocusedTeam(null);
            team.setFocusWaypoint(null);
            focusedTeam.getOnlineMembers().forEach(LunarClientListener::updateNametag);
            String teamUnfocusMsg = Foxtrot.getInstance().getConfig().getString("teamUnfocusMessage", "&eTeam &d<team> &ehas been unfocused by &d<player>&e.");
            teamUnfocusMsg = teamUnfocusMsg.replace("<team>", focusedTeam.getName()).replace("<player>", player.getName());
            team.sendMessage(ChatColor.translateAlternateColorCodes('&', teamUnfocusMsg));
         }
      }
   }

   private static List<Player> getOnlinePlayersList() {
      try {
         Method m = Bukkit.class.getMethod("getOnlinePlayers");
         Object result = m.invoke(null);
         if (result instanceof Collection) {
            return new ArrayList<>((Collection<? extends Player>)result);
         }

         if (result instanceof Player[]) {
            return Arrays.asList((Player[])result);
         }
      } catch (Exception var2) {
      }

      return new ArrayList<>();
   }
}
