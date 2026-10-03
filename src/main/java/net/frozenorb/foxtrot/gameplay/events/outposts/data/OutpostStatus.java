package net.frozenorb.foxtrot.gameplay.events.outposts.data;

import java.beans.ConstructorProperties;
import org.bukkit.ChatColor;

public enum OutpostStatus {
   CONTROLLED("Controlled", ChatColor.GREEN),
   CONTROLLING("Controlling", ChatColor.AQUA),
   CONTESTED("Contested", ChatColor.YELLOW),
   NEUTRALIZING("Neutralizing", ChatColor.YELLOW),
   NEUTRAL("Neutral", ChatColor.GREEN);

   private final String friendlyName;
   private final ChatColor color;

   public String getDisplayName() {
      return this.color + this.friendlyName;
   }

   @ConstructorProperties({"friendlyName", "color"})
   OutpostStatus(String friendlyName, ChatColor color) {
      this.friendlyName = friendlyName;
      this.color = color;
   }

   public String getFriendlyName() {
      return this.friendlyName;
   }

   public ChatColor getColor() {
      return this.color;
   }
}
