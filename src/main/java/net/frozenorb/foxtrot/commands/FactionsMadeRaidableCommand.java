package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.TimeUtils;
import cc.fyre.proton.util.UUIDUtils;
import cc.fyre.proton.util.paginate.PaginatedOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FactionsMadeRaidableCommand {
   @Command(names = {"fmr", "factionsraidable", "factionsmaderaidable"}, async = true, permission = "command.fmr")
   public static void execute(
      Player player,
      @Parameter(name = "team") Team team,
      @Parameter(name = "page", defaultValue = "1") Integer page,
      @Parameter(name = "time", defaultValue = "0") String timeValue
   ) {
      if (Foxtrot.getInstance().getServerHandler().isTeams()) {
         player.sendMessage(ChatColor.YELLOW + "This feature is disabled on " + Foxtrot.getInstance().getServerHandler().getServerName() + ".");
      } else {
         ArrayList<Bson> filters = new ArrayList<>();
         MongoCollection collection = Foxtrot.getInstance().getMongoPool().getDatabase(Foxtrot.MONGO_DB_NAME).getCollection("TeamActions");
         filters.add(
            Filters.and(Filters.eq("teamId", team.getUniqueId().toString()), Filters.eq("type", TeamActionType.MADE_FACTION_RAIDABLE.getInternalName()))
         );
         long time = 0L;
         if (!timeValue.equals("0")) {
            try {
               time = TimeUtils.parseTime(timeValue) * 1000L;
            } catch (Exception ex) {
               player.sendMessage(ChatColor.YELLOW + "Invalid time format.");
               return;
            }
         }

         if (time != 0L) {
            filters.add(Filters.gt("time", new Date(System.currentTimeMillis() - time)));
         }

         ArrayList documents = new ArrayList();
         collection.find(Filters.and(filters)).sort(Sorts.descending("time")).iterator().forEachRemaining(documents::add);
         if (documents.isEmpty()) {
            if (time != 0L) {
               player.sendMessage(
                  ChatColor.YELLOW
                     + team.getName()
                     + " has not made any factions raidable in the last "
                     + TimeUtils.formatIntoDetailedString((int)(time / 1000L))
                     + "."
               );
            } else {
               player.sendMessage(ChatColor.YELLOW + team.getName() + " has not made any factions raidable.");
            }
         } else {
            player.sendMessage(ChatColor.YELLOW + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 53));
            (new PaginatedOutput<Document>() {
                  @Override
                  public String getHeader(int i, int i1) {
                     return ChatColor.GOLD
                        + "You are viewing page "
                        + ChatColor.WHITE
                        + i
                        + "/"
                        + i1
                        + "\n"
                        + ChatColor.YELLOW
                        + ChatColor.STRIKETHROUGH
                        + StringUtils.repeat("-", 53);
                  }

                  public String format(Document data, int i) {
                     BasicDBObject params = BasicDBObject.parse(((Document)data.get("params")).toJson());
                     Team teamx = params.containsKey("factionRaidable")
                        ? Foxtrot.getInstance().getTeamHandler().getTeam(new ObjectId(params.getString("factionRaidable")))
                        : null;
                     String teamName = teamx != null ? teamx.getName() : "Unknown";
                     return ChatColor.GREEN
                        + UUIDUtils.name(UUID.fromString(params.getString("killerId")))
                        + ChatColor.YELLOW
                        + " made "
                        + ChatColor.YELLOW
                        + teamName
                        + ChatColor.GOLD
                        + ChatColor.BOLD
                        + " RAIDABLE "
                        + ChatColor.YELLOW
                        + "by killing "
                        + ChatColor.YELLOW
                        + UUIDUtils.name(UUID.fromString(params.getString("playerId")))
                        + " "
                        + ChatColor.DARK_GREEN
                        + ChatColor.BOLD
                        + "["
                        + TimeUtils.formatIntoCalendarString(data.getDate("time"))
                        + "]";
                  }
               })
               .display(player, page, documents);
            player.sendMessage(ChatColor.YELLOW + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-", 53));
         }
      }
   }
}
