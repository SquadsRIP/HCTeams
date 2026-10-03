package net.frozenorb.foxtrot.chat.listeners;

import java.util.Map;
import java.util.UUID;
import net.frozenorb.foxtrot.FoxConstants;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.chat.enums.ChatMode;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.commands.team.TeamMuteCommand;
import net.frozenorb.foxtrot.team.commands.team.TeamShadowMuteCommand;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import net.frozenorb.foxtrot.util.CC;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.minecraft.util.com.google.common.collect.ImmutableMap;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.cavepvp.profiles.Profiles;

public class ChatListener implements Listener {
   private String getCustomPrefix(UUID uuid) {
      Map<Integer, UUID> placesMap = Foxtrot.getInstance().getMapHandler().getStatsHandler() != null
         ? Foxtrot.getInstance().getMapHandler().getStatsHandler().getTopKills()
         : null;
      if (placesMap == null) {
         return "";
      }

      int place = placesMap.size() == 3
         ? (placesMap.get(1).equals(uuid) ? 1 : (placesMap.get(2).equals(uuid) ? 2 : (placesMap.get(3).equals(uuid) ? 3 : 99)))
         : 99;
      return place == 99 ? "" : ChatColor.translateAlternateColorCodes('&', place == 1 ? "&2➊" : (place == 2 ? "&a➋" : "&e➌")) + " ";
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onAsyncPlayerChatEarly(AsyncPlayerChatEvent event) {
      ChatMode playerChatMode = Foxtrot.getInstance().getChatModeMap().getChatMode(event.getPlayer().getUniqueId());
      ChatMode forcedChatMode = ChatMode.findFromForcedPrefix(event.getMessage().charAt(0));
      ChatMode finalChatMode;
      if (forcedChatMode != null) {
         finalChatMode = forcedChatMode;
      } else {
         finalChatMode = playerChatMode;
      }

      if (finalChatMode != ChatMode.PUBLIC) {
         event.getPlayer().setMetadata("NoSpamCheck", new FixedMetadataValue(Foxtrot.getInstance(), true));
      }
   }

   @EventHandler(priority = EventPriority.MONITOR)
   public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
      event.getPlayer().removeMetadata("NoSpamCheck", Foxtrot.getInstance());
      Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());
      ChatMode playerChatMode = Foxtrot.getInstance().getChatModeMap().getChatMode(event.getPlayer().getUniqueId());
      ChatMode forcedChatMode = ChatMode.findFromForcedPrefix(event.getMessage().charAt(0));
      if (!Profiles.getInstance().hasStaffChatEnabled(event.getPlayer())) {
         if (forcedChatMode != null) {
            event.setMessage(event.getMessage().substring(1).trim());
         }

         ChatMode finalChatMode;
         if (forcedChatMode != null) {
            finalChatMode = forcedChatMode;
         } else {
            finalChatMode = playerChatMode;
         }

         if (!event.isCancelled() || finalChatMode != ChatMode.PUBLIC) {
            event.setCancelled(true);
            if (finalChatMode != ChatMode.PUBLIC && playerTeam == null) {
               event.getPlayer().sendMessage(ChatColor.RED + "You can't speak in non-public chat if you're not in a team!");
            } else if (finalChatMode == ChatMode.OFFICER
               && !playerTeam.isCaptain(event.getPlayer().getUniqueId())
               && !playerTeam.isCoLeader(event.getPlayer().getUniqueId())
               && !playerTeam.isOwner(event.getPlayer().getUniqueId())) {
               event.getPlayer().sendMessage(ChatColor.RED + "You can't speak in officer chat if you're not an officer!");
            } else if (finalChatMode == ChatMode.PUBLIC && TeamMuteCommand.getTeamMutes().containsKey(event.getPlayer().getUniqueId())) {
               event.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Your team is muted!");
            } else {
               switch (finalChatMode) {
                  case PUBLIC:
                     String publicChatFormat = FoxConstants.publicChatFormat(
                        event.getPlayer(), playerTeam, event.getMessage(), this.getCustomPrefix(event.getPlayer().getUniqueId())
                     );
                     TextComponent component = null;
                     if (event.getPlayer().hasMetadata("stream_link")) {
                        String streamLink = (String)event.getPlayer().getMetadata("stream_link").get(0).value();
                        component = new TextComponent();
                        component.setText(ChatColor.RED + CC.BOLD + "[LIVE] ");
                        component.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(CC.GRAY + "Coming Soon!")}));
                        new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.OPEN_URL, streamLink);
                        component.addExtra(publicChatFormat);
                     }

                     for (Player player : event.getRecipients()) {
                        if (playerTeam == null) {
                           if (!TeamShadowMuteCommand.getTeamShadowMutes().containsKey(event.getPlayer().getUniqueId())
                              && (event.getPlayer().isOp() || Foxtrot.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(player.getUniqueId()))) {
                              if (component != null) {
                                 player.spigot().sendMessage(component);
                              } else {
                                 player.sendMessage(publicChatFormat);
                              }
                           }
                        } else if (playerTeam.isMember(player.getUniqueId())) {
                           if (component != null) {
                              player.spigot().sendMessage(component);
                           } else {
                              player.sendMessage(
                                 publicChatFormat.replace(
                                    ChatColor.GOLD + "[" + Foxtrot.getInstance().getServerHandler().getDefaultRelationColor(),
                                    ChatColor.GOLD + "[" + ChatColor.DARK_GREEN
                                 )
                              );
                           }
                        } else if (playerTeam.isAlly(player.getUniqueId())) {
                           if (component != null) {
                              player.spigot().sendMessage(component);
                           } else {
                              player.sendMessage(
                                 publicChatFormat.replace(
                                    ChatColor.GOLD + "[" + Foxtrot.getInstance().getServerHandler().getDefaultRelationColor(),
                                    ChatColor.GOLD + "[" + Team.ALLY_COLOR
                                 )
                              );
                           }
                        } else if (!TeamShadowMuteCommand.getTeamShadowMutes().containsKey(event.getPlayer().getUniqueId())
                           && (event.getPlayer().isOp() || Foxtrot.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(player.getUniqueId()))) {
                           if (component != null) {
                              player.spigot().sendMessage(component);
                           } else {
                              player.sendMessage(publicChatFormat);
                           }
                        }
                     }

                     Foxtrot.getInstance().getServer().getConsoleSender().sendMessage(publicChatFormat);
                     break;
                  case ALLIANCE:
                     String allyChatFormat = FoxConstants.allyChatFormat(event.getPlayer(), event.getMessage());
                     String allyChatSpyFormat = FoxConstants.allyChatSpyFormat(playerTeam, event.getPlayer(), event.getMessage());

                     for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                        if (!playerTeam.isMember(player.getUniqueId()) && !playerTeam.isAlly(player.getUniqueId())) {
                           if (Foxtrot.getInstance().getChatSpyMap().getChatSpy(player.getUniqueId()).contains(playerTeam.getUniqueId())) {
                              player.sendMessage(allyChatSpyFormat);
                           }
                        } else {
                           player.sendMessage(allyChatFormat);
                        }
                     }

                     for (ObjectId allyId : playerTeam.getAllies()) {
                        Team ally = Foxtrot.getInstance().getTeamHandler().getTeam(allyId);
                        if (ally != null) {
                           TeamActionTracker.logActionAsync(
                              ally,
                              TeamActionType.ALLY_CHAT_MESSAGE,
                              ImmutableMap.<String, Object>builder()
                                 .put("allyTeamId", playerTeam.getUniqueId())
                                 .put("allyTeamName", playerTeam.getName())
                                 .put("playerId", event.getPlayer().getUniqueId())
                                 .put("playerName", event.getPlayer().getName())
                                 .put("message", event.getMessage())
                                 .build()
                           );
                        }
                     }

                     TeamActionTracker.logActionAsync(
                        playerTeam,
                        TeamActionType.ALLY_CHAT_MESSAGE,
                        ImmutableMap.of("playerId", event.getPlayer().getUniqueId(), "playerName", event.getPlayer().getName(), "message", event.getMessage())
                     );
                     Foxtrot.getInstance()
                        .getServer()
                        .getLogger()
                        .info("[Ally Chat] [" + playerTeam.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
                     break;
                  case TEAM:
                     String teamChatFormat = FoxConstants.teamChatFormat(event.getPlayer(), event.getMessage());
                     String teamChatSpyFormat = FoxConstants.teamChatSpyFormat(playerTeam, event.getPlayer(), event.getMessage());

                     for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                        if (playerTeam.isMember(player.getUniqueId())) {
                           player.sendMessage(teamChatFormat);
                        } else if (Foxtrot.getInstance().getChatSpyMap().getChatSpy(player.getUniqueId()).contains(playerTeam.getUniqueId())) {
                           player.sendMessage(teamChatSpyFormat);
                        }
                     }

                     TeamActionTracker.logActionAsync(
                        playerTeam,
                        TeamActionType.TEAM_CHAT_MESSAGE,
                        ImmutableMap.of("playerId", event.getPlayer().getUniqueId(), "playerName", event.getPlayer().getName(), "message", event.getMessage())
                     );
                     Foxtrot.getInstance()
                        .getServer()
                        .getLogger()
                        .info("[Team Chat] [" + playerTeam.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
                     break;
                  case OFFICER:
                     String officerChatFormat = FoxConstants.officerChatFormat(event.getPlayer(), event.getMessage());

                     for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                        if (playerTeam.isCaptain(player.getUniqueId())
                           || playerTeam.isCoLeader(player.getUniqueId())
                           || playerTeam.isOwner(player.getUniqueId())) {
                           player.sendMessage(officerChatFormat);
                        }
                     }

                     TeamActionTracker.logActionAsync(
                        playerTeam,
                        TeamActionType.OFFICER_CHAT_MESSAGE,
                        ImmutableMap.of("playerId", event.getPlayer().getUniqueId(), "playerName", event.getPlayer().getName(), "message", event.getMessage())
                     );
                     Foxtrot.getInstance()
                        .getServer()
                        .getLogger()
                        .info("[Officer Chat] [" + playerTeam.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
               }
            }
         }
      }
   }
}
