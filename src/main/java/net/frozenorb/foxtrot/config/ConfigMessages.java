package net.frozenorb.foxtrot.config;

import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public final class ConfigMessages {
   private ConfigMessages() {
   }

   public static String raw(String path, String fallback) {
      FileConfiguration config = source(path);
      String value = config.getString(path);
      return SquadsText.display(value == null ? fallback : value);
   }

   public static String link(String var0, String var1) {
      return "Coming Soon!";
   }

   public static String networkName() {
      return "Squads";
   }

   public static String serverName() {
      return raw("serverName", "Teams");
   }

   public static String message(String path, String fallback, String... replacements) {
      String value = applyGlobalPlaceholders(raw(path, fallback));
      if (replacements.length % 2 != 0) {
         throw new IllegalArgumentException("Message replacements must be supplied as placeholder/value pairs");
      }

      for (int i = 0; i < replacements.length; i += 2) {
         value = value.replace(replacements[i], replacements[i + 1]);
      }

      return SquadsText.display(ChatColor.translateAlternateColorCodes('&', value));
   }

   public static List<String> lines(String path, List<String> fallback, String... replacements) {
      List<String> configured = source(path).getStringList(path);
      List<String> source = configured.isEmpty() ? fallback : configured;
      ArrayList<String> result = new ArrayList<>(source.size());

      for (String line : source) {
         String value = applyGlobalPlaceholders(line);
         if (replacements.length % 2 != 0) {
            throw new IllegalArgumentException("Message replacements must be supplied as placeholder/value pairs");
         }

         for (int i = 0; i < replacements.length; i += 2) {
            value = value.replace(replacements[i], replacements[i + 1]);
         }

         result.add(ChatColor.translateAlternateColorCodes('&', value));
      }

      return result;
   }

   private static String applyGlobalPlaceholders(String value) {
      return SquadsText.display(
         value.replace("%network%", networkName())
            .replace("%server%", serverName())
            .replace("%website%", link("website", "Coming Soon!"))
            .replace("%store%", link("store", "Coming Soon!"))
            .replace("%stats%", link("stats", "Coming Soon!"))
            .replace("%discord%", link("discord", "Coming Soon!"))
            .replace("%support%", link("support", "Coming Soon!"))
      );
   }

   private static FileConfiguration source(String path) {
      if (path.startsWith("messages.") || path.startsWith("broadcasts.")) {
         return Foxtrot.getInstance().getConfiguration("messages.yml");
      } else {
         return path.startsWith("tips.") ? Foxtrot.getInstance().getConfiguration("tips.yml") : Foxtrot.getInstance().getConfiguration("branding.yml");
      }
   }
}
