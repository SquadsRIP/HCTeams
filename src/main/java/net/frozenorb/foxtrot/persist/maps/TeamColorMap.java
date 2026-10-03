package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;
import org.bukkit.ChatColor;

public class TeamColorMap extends PersistMap<ChatColor> {
   public TeamColorMap() {
      super("TeamColor", "TeamColor");
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
      return this.contains(check) ? this.getValue(check) : ChatColor.DARK_GREEN;
   }

   public void setChatColor(UUID update, ChatColor chatColor) {
      this.updateValueAsync(update, chatColor);
   }
}
