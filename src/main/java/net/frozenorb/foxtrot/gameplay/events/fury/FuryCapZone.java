package net.frozenorb.foxtrot.gameplay.events.fury;

import java.beans.ConstructorProperties;
import org.bukkit.ChatColor;

public enum FuryCapZone {
   OVERWORLD("Overworld", ChatColor.DARK_GREEN, 1),
   END("End", ChatColor.DARK_PURPLE, 2),
   NETHER("Nether", ChatColor.YELLOW, 3);

   String displayName;
   ChatColor chatColor;
   int order;

   @ConstructorProperties({"displayName", "chatColor", "order"})
   FuryCapZone(String displayName, ChatColor chatColor, int order) {
      this.displayName = displayName;
      this.chatColor = chatColor;
      this.order = order;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public ChatColor getChatColor() {
      return this.chatColor;
   }

   public int getOrder() {
      return this.order;
   }
}
