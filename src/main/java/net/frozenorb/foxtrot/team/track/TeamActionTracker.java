package net.frozenorb.foxtrot.team.track;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.client.MongoCollection;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.minecraft.util.com.google.common.base.Charsets;
import net.minecraft.util.com.google.common.io.Files;
import org.bson.Document;
import org.bukkit.Bukkit;

public final class TeamActionTracker {
   private static boolean databaseLogEnabled = true;
   private static final MongoCollection<Document> mongoCollection = Foxtrot.getInstance()
      .getMongoPool()
      .getDatabase(Foxtrot.MONGO_DB_NAME)
      .getCollection("TeamActions");
   private static final File logFileRoot = new File(new File("foxlogs"), "teamactiontracker");

   public static void logActionAsync(Team team, TeamActionType actionType, Map<String, Object> params) {
      if (!team.isLoading()) {
         Bukkit.getScheduler().runTaskAsynchronously(Foxtrot.getInstance(), () -> {
            logActionToFile(team, actionType, params);
            if (databaseLogEnabled && actionType.isLoggedToDatabase()) {
               logActionToDatabase(team, actionType, params);
            }
         });
      }
   }

   private static void logActionToFile(Team team, TeamActionType actionType, Map<String, Object> params) {
      File teamLogFolder = new File(logFileRoot, team.getUniqueId().toString());
      File teamLogFile = new File(teamLogFolder, (actionType.isLoggedToDatabase() ? "general" : "misc") + ".log");

      try {
         StringBuilder logLine = new StringBuilder();
         logLine.append('[');
         logLine.append(DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
         logLine.append(", ");
         logLine.append(actionType.getInternalName());
         logLine.append("] ");
         params.forEach((key, value) -> {
            logLine.append(key);
            logLine.append(": ");
            logLine.append(value);
            logLine.append(' ');
         });
         logLine.append('\n');
         teamLogFile.getParentFile().mkdirs();
         teamLogFile.createNewFile();
         Files.append(logLine.toString(), teamLogFile, Charsets.UTF_8);
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   private static void logActionToDatabase(Team team, TeamActionType actionType, Map<String, Object> params) {
      BasicDBObject entry = new BasicDBObject();
      entry.put("teamId", team.getUniqueId().toString());
      entry.put("teamName", team.getName());
      entry.put("time", new Date());
      entry.put("type", actionType.name());
      BasicDBObject paramsJson = new BasicDBObject();
      params.forEach((key, value) -> paramsJson.put(key, value.toString()));
      entry.put("params", paramsJson);
      entry.put("teamAfterAction", team.toJSON());
      DB db = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME);
      db.getCollection("TeamActions").insert(entry);
   }

   public static boolean isDatabaseLogEnabled() {
      return databaseLogEnabled;
   }

   public static void setDatabaseLogEnabled(boolean databaseLogEnabled) {
      TeamActionTracker.databaseLogEnabled = databaseLogEnabled;
   }

   public static MongoCollection<Document> getMongoCollection() {
      return mongoCollection;
   }
}
