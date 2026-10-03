package net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.Challenge;

public class ChallengeSetSerializer implements JsonSerializer<Set<Challenge>>, JsonDeserializer<Set<Challenge>> {
   public JsonElement serialize(Set<Challenge> challenges, Type type, JsonSerializationContext jsonSerializationContext) {
      JsonArray array = new JsonArray();

      for (Challenge challenge : challenges) {
         JsonObject object = new JsonObject();
         object.addProperty("id", challenge.getId());
         object.addProperty("daily", challenge.isDaily());
         array.add(object);
      }

      return array;
   }

   public Set<Challenge> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      HashSet<Challenge> challenges = new HashSet<>();

      for (JsonElement element : jsonElement.getAsJsonArray()) {
         JsonObject jsonObject = element.getAsJsonObject();
         String challengeId = jsonObject.get("id").getAsString();
         Challenge challenge = jsonObject.get("daily").getAsBoolean()
            ? Foxtrot.getInstance().getBattlePassHandler().getDailyChallenges().getChallenge(challengeId)
            : Foxtrot.getInstance().getBattlePassHandler().getChallenge(challengeId);
         if (challenge != null) {
            challenges.add(challenge);
         }
      }

      return challenges;
   }
}
