package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.neutron.Neutron;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.EOTWCommand;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import net.minecraft.util.com.google.common.collect.ImmutableMap;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamCreateCommand {
   public static final Pattern ALPHA_NUMERIC = Pattern.compile("[^a-zA-Z0-9]");
   private static final Set<String> disallowedTeamNames = new HashSet<>(Arrays.asList("list", "glowstone", "self", "outpost", "treasure"));

   @Command(names = {"team create", "t create", "f create", "faction create", "fac create"}, permission = "")
   public static void teamCreate(Player sender, @Parameter(name = "team") String team) {
      if (Foxtrot.getInstance().getTeamHandler().getTeam(sender) != null) {
         sender.sendMessage(ChatColor.GRAY + "You're already in a faction!");
      } else if (team.length() > 16) {
         sender.sendMessage(ChatColor.YELLOW + "Maximum faction name size is 16 characters!");
      } else if (team.length() < 3) {
         sender.sendMessage(ChatColor.YELLOW + "Minimum faction name size is 3 characters!");
      } else if (disallowedTeamNames.stream().anyMatch(it -> it.equalsIgnoreCase(team)) && !sender.isOp()) {
         sender.sendMessage(ChatColor.YELLOW + "You can not create a faction with that name!");
      } else if (Foxtrot.getInstance().getTeamHandler().getTeam(team) != null) {
         sender.sendMessage(ChatColor.GRAY + "That faction already exists!");
      } else if (ALPHA_NUMERIC.matcher(team).find()) {
         sender.sendMessage(ChatColor.YELLOW + "Faction names must be alphanumeric!");
      } else if (EOTWCommand.realFFAStarted() && !sender.isOp()) {
         sender.sendMessage(ChatColor.YELLOW + "You can't create a faction during FFA.");
      } else if (Foxtrot.getInstance().getServerHandler().isEOTW() && !sender.isOp()) {
         sender.sendMessage(ChatColor.YELLOW + "No factions!");
      } else {
         Team createdTeam = new Team(team);
         TeamActionTracker.logActionAsync(
            createdTeam, TeamActionType.PLAYER_CREATE_TEAM, ImmutableMap.of("playerId", sender.getUniqueId(), "playerName", sender.getName())
         );
         createdTeam.setUniqueId(new ObjectId());
         createdTeam.setOwner(sender.getUniqueId());
         createdTeam.setName(team);
         createdTeam.setDTR(createdTeam.getMaxDTR());
         Foxtrot.getInstance().getQuestHandler().completeQuest(sender, "CreateOrJoin");
         Foxtrot.getInstance().getTeamHandler().setupTeam(createdTeam);
         LunarClientListener.updateNametag(sender);
         Foxtrot.getInstance()
            .getServer()
            .dispatchCommand(
               Foxtrot.getInstance().getServer().getConsoleSender(),
               "sbc &fFaction &4"
                  + createdTeam.getName()
                  + " &fhas been &4created &fby "
                  + Neutron.getInstance().getProfileHandler().fromUuid(sender.getUniqueId()).getFancyName()
                  + "&f."
            );
      }
   }
}
