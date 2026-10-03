package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;
import org.bukkit.ChatColor;

public class TeamFocusColorMap extends PersistMap<ChatColor> {
   public TeamFocusColorMap() {
      super("FocusColor", "FocusColor");
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
      return this.contains(check) ? this.getValue(check) : ChatColor.LIGHT_PURPLE;
   }

   public void setChatColor(UUID update, ChatColor chatColor) {
      this.updateValueAsync(update, chatColor);
   }
}
