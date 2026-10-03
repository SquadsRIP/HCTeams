package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import com.mongodb.client.MongoCollection;
import java.util.HashMap;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.menu.LogsMenu;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamLogsCommand {
   @Command(names = {"team logs", "t logs", "f logs", "fac logs", "faction logs"}, permission = "")
   public static void execute(Player player, @Parameter(name = "team", defaultValue = "self") Team team) {
      Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player);
      if (playerTeam == null && !player.hasPermission("command.team.logs")) {
         player.sendMessage(ChatColor.GRAY + "You are not on a team!");
      } else if (playerTeam != null && !playerTeam.equals(team) && !player.hasPermission("command.team.logs")) {
         player.sendMessage(ChatColor.YELLOW + "No permission");
      } else {
         HashMap<TeamActionType, Map<String, Object>> info = new HashMap<>();
         MongoCollection<Document> collection = TeamActionTracker.getMongoCollection();

         for (Document document : collection.find(new Document("teamId", team.getUniqueId().toString()))) {
            info.put(TeamActionType.valueOf(document.getString("type")), (Map<String, Object>)document.get("params"));
         }

         new LogsMenu(team, info).openMenu(player);
      }
   }
}
