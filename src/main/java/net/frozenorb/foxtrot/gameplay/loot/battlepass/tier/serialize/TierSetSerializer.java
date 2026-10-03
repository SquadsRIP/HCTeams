package net.frozenorb.foxtrot.gameplay.loot.battlepass.tier.serialize;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.tier.Tier;

public class TierSetSerializer implements JsonSerializer<Set<Tier>>, JsonDeserializer<Set<Tier>> {
   public JsonElement serialize(Set<Tier> tiers, Type type, JsonSerializationContext jsonSerializationContext) {
      JsonArray array = new JsonArray();

      for (Tier tier : tiers) {
         array.add(tier.getNumber());
      }

      return array;
   }

   public Set<Tier> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
      HashSet<Tier> tiers = new HashSet<>();

      for (JsonElement element : jsonElement.getAsJsonArray()) {
         Tier tier = Foxtrot.getInstance().getBattlePassHandler().getTier(element.getAsInt());
         if (tier != null) {
            tiers.add(tier);
         }
      }

      return tiers;
   }
}
