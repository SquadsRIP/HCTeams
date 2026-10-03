package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;
import net.frozenorb.foxtrot.persist.PersistMap;
import org.bukkit.ChatColor;

public class FocusColorMap extends PersistMap<ChatColor> {
   public FocusColorMap() {
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
      return this.contains(check) ? this.getValue(check) : ChatColor.DARK_PURPLE;
   }

   public void setChatColor(UUID update, ChatColor chatColor) {
      this.updateValueAsync(update, chatColor);
   }
}
