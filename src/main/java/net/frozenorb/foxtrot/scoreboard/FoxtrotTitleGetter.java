package net.frozenorb.foxtrot.scoreboard;

import cc.fyre.proton.scoreboard.construct.TitleGetter;
import cc.fyre.universe.UniverseAPI;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.config.ConfigMessages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FoxtrotTitleGetter extends TitleGetter {
   private String title;
   private String text;
   public static boolean domain = false;
   private int length;
   private int flicker;

   public FoxtrotTitleGetter() {
      this.title = this.text = ConfigMessages.raw("scoreboard.animated-title.network-text", "SQUADS").toUpperCase();
      this.text = "SQUADS";
      this.length = 1;
      this.flicker = -1;
      Bukkit.getServer()
         .getScheduler()
         .runTaskTimer(
            Foxtrot.getInstance(),
            () -> {
               StringBuilder title = new StringBuilder();
               boolean finish = this.length > this.text.length();
               if (this.flicker >= 0) {
                  title.append(
                     (this.flicker % 2 == 0 ? color("flicker-color", ChatColor.WHITE) : color("secondary-color", ChatColor.GOLD))
                        + ChatColor.BOLD.toString()
                        + this.text
                  );
                  if (this.flicker == 8) {
                     this.flicker = -1;
                     this.text = domain
                        ? UniverseAPI.getServerName().toUpperCase()
                        : ConfigMessages.raw("scoreboard.animated-title.network-text", "SQUADS").toUpperCase();
                     domain = !domain;
                  } else {
                     this.flicker++;
                  }
               } else if (finish) {
                  title.append(color("primary-color", ChatColor.YELLOW) + ChatColor.BOLD.toString() + this.text);
                  this.length = 1;
                  this.flicker = 0;
               } else {
                  title.append(color("primary-color", ChatColor.YELLOW) + ChatColor.BOLD.toString() + this.text.substring(0, this.length - 1));
                  title.append(color("secondary-color", ChatColor.GOLD) + ChatColor.BOLD.toString() + this.text.substring(this.length - 1));
                  this.length++;
               }

               this.title = title.toString();
            },
            6L,
            6L
         );
   }

   @Override
   public String getTitle(Player player) {
      return this.title;
   }

   private static ChatColor color(String path, ChatColor fallback) {
      try {
         return ChatColor.valueOf(ConfigMessages.raw("scoreboard.animated-title." + path, fallback.name()).toUpperCase());
      } catch (IllegalArgumentException exception) {
         return fallback;
      }
   }
}
