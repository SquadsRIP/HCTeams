package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;

public class ChatMessagesMap extends PersistMap<Integer> {
   public ChatMessagesMap() {
      super("ChatMessages", "ChatMessages", false);
   }

   public String getRedisValue(Integer messages) {
      return String.valueOf(messages);
   }

   public Integer getJavaObject(String str) {
      return Integer.parseInt(str);
   }

   public Object getMongoValue(Integer messages) {
      return messages;
   }

   public int getMessages(UUID check) {
      return this.contains(check) ? this.getValue(check) : 0;
   }

   public synchronized void increment(UUID update) {
      this.updateValueAsync(update, this.getMessages(update) + 1);
   }
}
