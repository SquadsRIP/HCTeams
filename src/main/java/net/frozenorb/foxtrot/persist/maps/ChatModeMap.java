package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.chat.enums.ChatMode;
import net.frozenorb.foxtrot.persist.PersistMap;

public class ChatModeMap extends PersistMap<ChatMode> {
   public ChatModeMap() {
      super("ChatModes", "ChatMode");
   }

   public String getRedisValue(ChatMode chatMode) {
      return chatMode.name();
   }

   public ChatMode getJavaObject(String str) {
      return ChatMode.valueOf(str);
   }

   public Object getMongoValue(ChatMode chatMode) {
      return chatMode.name();
   }

   public ChatMode getChatMode(UUID check) {
      return this.contains(check) ? this.getValue(check) : ChatMode.PUBLIC;
   }

   public void setChatMode(UUID update, ChatMode chatMode) {
      this.updateValueAsync(update, chatMode);
   }
}
