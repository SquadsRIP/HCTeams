package net.frozenorb.foxtrot.persist.maps;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;
import org.bson.types.ObjectId;

public class ChatSpyMap extends PersistMap<List<ObjectId>> {
   public ChatSpyMap() {
      super("ChatSpy", "ChatSpy");
   }

   public String getRedisValue(List<ObjectId> teams) {
      StringBuilder stringBuilder = new StringBuilder();

      for (ObjectId team : teams) {
         stringBuilder.append(team).append(",");
      }

      if (stringBuilder.length() > 1) {
         stringBuilder.setLength(stringBuilder.length() - 1);
      }

      return stringBuilder.toString();
   }

   public List<ObjectId> getJavaObject(String str) {
      ArrayList<ObjectId> results = new ArrayList<>();

      for (String split : str.split(",")) {
         if (!split.equals("")) {
            results.add(new ObjectId(split));
         }
      }

      return results;
   }

   public Object getMongoValue(List<ObjectId> teams) {
      return teams;
   }

   public List<ObjectId> getChatSpy(UUID check) {
      return this.contains(check) ? this.getValue(check) : new ArrayList<>();
   }

   public void setChatSpy(UUID update, List<ObjectId> teams) {
      this.updateValueAsync(update, teams);
   }
}
