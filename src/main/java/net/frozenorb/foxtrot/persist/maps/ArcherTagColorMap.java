package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;
import org.bukkit.ChatColor;

public class ArcherTagColorMap extends PersistMap<ChatColor> {
   public ArcherTagColorMap() {
      super("ArcherTagColor", "ArcherTagColor");
   }

   public String getRedisValue(ChatColor chatColor) {
      return chatColor.name();
   }

   public ChatColor getJavaObject(String str) {
      return ChatColor.valueOf(str);
   }

   public Object getMongoValue(ChatColor chatColor) {
      return chatColor;
   }

   public ChatColor getChatColor(UUID check) {
      return this.contains(check) ? this.getValue(check) : ChatColor.YELLOW;
   }

   public void setChatColor(UUID update, ChatColor chatColor) {
      this.updateValueAsync(update, chatColor);
   }
}
