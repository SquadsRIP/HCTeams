package net.frozenorb.foxtrot.util;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;

public class Formats {
   public static String formatNumber(Number number) {
      return NumberFormat.getInstance().format(number);
   }

   public static String formatExperience(int exp) {
      return CC.GOLD + "✪" + CC.YELLOW + formatNumber(exp);
   }

   public static List<String> renderLines(String prefix, String text) {
      ArrayList<String> lines = new ArrayList<>();
      if (!text.contains("%line%")) {
         if (text.length() < 32) {
            lines.add(prefix + ChatColor.translateAlternateColorCodes('&', text));
            return lines;
         }

         StringBuilder line = new StringBuilder(prefix);

         for (String piece : text.split(" ")) {
            if (ChatColor.stripColor(line + " " + piece).length() > 32) {
               lines.add(ChatColor.translateAlternateColorCodes('&', line.toString()));
               line = new StringBuilder(prefix).append(piece);
            } else {
               if (line.length() > prefix.length()) {
                  line.append(" ");
               }

               line.append(piece);
            }
         }

         if (line.length() > 0) {
            lines.add(ChatColor.translateAlternateColorCodes('&', line.toString()));
         }

         return lines;
      } else {
         for (String piece : text.split("%line%")) {
            lines.addAll(renderLines(prefix, piece));
         }

         return lines;
      }
   }
}
