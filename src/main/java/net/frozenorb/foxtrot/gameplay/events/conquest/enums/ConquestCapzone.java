package net.frozenorb.foxtrot.gameplay.events.conquest.enums;

import java.beans.ConstructorProperties;
import org.bukkit.ChatColor;

public enum ConquestCapzone {
   RED(ChatColor.YELLOW, "Red"),
   BLUE(ChatColor.BLUE, "Blue"),
   GREEN(ChatColor.GREEN, "Green"),
   YELLOW(ChatColor.YELLOW, "Yellow");

   private ChatColor chatColor;
   private String name;

   @ConstructorProperties({"chatColor", "name"})
   ConquestCapzone(ChatColor chatColor, String name) {
      this.chatColor = chatColor;
      this.name = name;
   }

   public ChatColor getChatColor() {
      return this.chatColor;
   }

   public String getName() {
      return this.name;
   }
}
