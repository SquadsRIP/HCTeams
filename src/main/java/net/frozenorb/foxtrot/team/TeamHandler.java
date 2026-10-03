package net.frozenorb.foxtrot.team;

import cc.fyre.proton.Proton;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.client.MongoCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.team.dtr.DTRBitmaskType;
import net.frozenorb.foxtrot.team.subclaim.SubclaimType;
import net.frozenorb.foxtrot.team.upgrade.UpgradeTask;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TeamHandler {
   private Map<String, Team> teamNameMap = new ConcurrentHashMap<>();
   private Map<ObjectId, Team> teamUniqueIdMap = new ConcurrentHashMap<>();
   private Map<UUID, Team> playerTeamMap = new ConcurrentHashMap<>();
   private boolean rostersLocked = false;
   private static HashSet<Team> powerFactions = new HashSet<>();

   public TeamHandler() {
      powerFactions = new HashSet<>();
      Proton.getInstance().getCommandHandler().registerParameterType(Team.class, new TeamType());
      Proton.getInstance().getCommandHandler().registerParameterType(DTRBitmask.class, new DTRBitmaskType());
      Proton.getInstance().getCommandHandler().registerParameterType(Subclaim.class, new SubclaimType());
      new UpgradeTask().runTaskTimer(Foxtrot.getInstance(), 40L, 40L);
      Proton.getInstance().runRedisCommand(redis -> {
         for (String key : redis.keys("fox_teams.*")) {
            String loadString = redis.get(key);

            try {
               Team team = new Team(key.split("\\.")[1]);
               team.load(loadString);
               this.setupTeam(team);
            } catch (Exception e) {
               e.printStackTrace();
               Foxtrot.getInstance().getLogger().severe("Could not load team from raw string: " + loadString);
            }
         }

         this.rostersLocked = Boolean.valueOf(redis.get("RostersLocked"));
         return null;
      });
      Bukkit.getLogger().info("Creating indexes...");
      MongoCollection playerCollection = Foxtrot.getInstance().getMongoPool().getDatabase(Foxtrot.MONGO_DB_NAME).getCollection("Players");
      playerCollection.createIndex(new BasicDBObject("Team", 1));
      MongoCollection teamCollection = Foxtrot.getInstance().getMongoPool().getDatabase(Foxtrot.MONGO_DB_NAME).getCollection("Teams");
      teamCollection.createIndex(new BasicDBObject("Owner", 1));
      teamCollection.createIndex(new BasicDBObject("CoLeaders", 1));
      teamCollection.createIndex(new BasicDBObject("Captains", 1));
      teamCollection.createIndex(new BasicDBObject("Members", 1));
      teamCollection.createIndex(new BasicDBObject("Name", 1));
      teamCollection.createIndex(new BasicDBObject("PowerFaction", 1));
      Bukkit.getLogger().info("Creating indexes done.");
   }

   public static void addPowerFaction(Team t) {
      powerFactions.add(t);
   }

   public static void removePowerFaction(Team t) {
      powerFactions.remove(t);
   }

   public static boolean isPowerFaction(Team t) {
      return powerFactions.contains(t);
   }

   public List<Team> getTeams() {
      return new ArrayList<>(this.teamNameMap.values());
   }

   public Team getTeam(String teamName) {
      return this.teamNameMap.get(teamName.toLowerCase());
   }

   public Team getTeam(ObjectId teamUUID) {
      return teamUUID == null ? null : this.teamUniqueIdMap.get(teamUUID);
   }

   public Team getTeam(UUID playerUUID) {
      return playerUUID == null ? null : this.playerTeamMap.get(playerUUID);
   }

   public Team getTeam(Player player) {
      return this.getTeam(player.getUniqueId());
   }

   public void setTeam(UUID playerUUID, Team team, boolean update) {
      if (team == null) {
         this.playerTeamMap.remove(playerUUID);
      } else {
         this.playerTeamMap.put(playerUUID, team);
      }

      if (update) {
         Bukkit.getScheduler().runTaskAsynchronously(Foxtrot.getInstance(), () -> {
            DBCollection playersCollection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Players");
            BasicDBObject player = new BasicDBObject("_id", playerUUID.toString().replace("-", ""));
            if (team != null) {
               playersCollection.update(player, new BasicDBObject("$set", new BasicDBObject("Team", team.getUniqueId().toHexString())));
            } else {
               playersCollection.update(player, new BasicDBObject("$set", new BasicDBObject("Team", null)));
            }
         });
      }
   }

   public void setTeam(UUID playerUUID, Team team) {
      this.setTeam(playerUUID, team, true);
   }

   public void setupTeam(Team team) {
      this.setupTeam(team, false);
   }

   public void setupTeam(Team team, boolean update) {
      this.teamNameMap.put(team.getName().toLowerCase(), team);
      this.teamUniqueIdMap.put(team.getUniqueId(), team);

      for (UUID member : team.getMembers()) {
         this.setTeam(member, team, update);
      }
   }

   public void removeTeam(Team team) {
      this.teamNameMap.remove(team.getName().toLowerCase());
      this.teamUniqueIdMap.remove(team.getUniqueId());
      Collection<Player> toRemove = team.getOnlineMembers();

      for (UUID member : team.getMembers()) {
         this.setTeam(member, null);
      }

      toRemove.forEach(LunarClientListener::updateNametag);
   }

   public void recachePlayerTeams() {
      this.playerTeamMap.clear();

      for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
         for (UUID member : team.getMembers()) {
            this.setTeam(member, team);
         }
      }
   }

   public boolean isRostersLocked() {
      return this.rostersLocked;
   }

   public void setRostersLocked(boolean rostersLocked) {
      this.rostersLocked = rostersLocked;
   }

   public static HashSet<Team> getPowerFactions() {
      return powerFactions;
   }
}
