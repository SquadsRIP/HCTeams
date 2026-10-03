package net.frozenorb.foxtrot.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class RealJsonBuilder {
   private JsonObject json = new JsonObject();

   public RealJsonBuilder addProperty(String property, String value) {
      this.json.addProperty(property, value);
      return this;
   }

   public RealJsonBuilder addProperty(String property, Number value) {
      this.json.addProperty(property, value);
      return this;
   }

   public RealJsonBuilder addProperty(String property, Boolean value) {
      this.json.addProperty(property, value);
      return this;
   }

   public RealJsonBuilder addProperty(String property, Character value) {
      this.json.addProperty(property, value);
      return this;
   }

   public RealJsonBuilder add(String property, JsonElement element) {
      this.json.add(property, element);
      return this;
   }

   public JsonObject get() {
      return this.json;
   }
}
