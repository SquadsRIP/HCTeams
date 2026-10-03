package net.frozenorb.foxtrot.tab;

import cc.fyre.proton.tab.construct.TabLayout;
import org.bukkit.ChatColor;

final class LegacyTabText {
   static final int MAX_WIDTH = 86;

   private LegacyTabText() {
   }

   static String mapKit(boolean modern, int protection, int sharpness) {
      return "Prot " + protection + ", Sharp " + sharpness;
   }

   static String teamEntry(boolean modern, String name, String color, int online, String dtr) {
      String details = ChatColor.WHITE + " (" + online + ")" + ChatColor.GRAY + (modern ? " | " : "|") + dtr;

      while (name.length() > 1 && ((color + name + details).length() > 29 || !modern && width(color + name + details) > 86)) {
         name = name.substring(0, name.length() - 1);
      }

      return color + name + details;
   }

   static String center(String text) {
      text = ChatColor.translateAlternateColorCodes('&', text);
      int spaces = Math.max(0, (86 - width(text)) / 8);
      StringBuilder result = new StringBuilder();

      for (int i = 0; i < spaces; i++) {
         result.append(' ');
      }

      return result.append(text).toString();
   }

   static void fitLayout(TabLayout layout) {
      if (!layout.is18()) {
         String[] names = layout.getTabNames();

         for (int i = 0; i < names.length; i++) {
            names[i] = fit(names[i]);
         }
      }
   }

   static String fit(String text) {
      text = ChatColor.translateAlternateColorCodes('&', text);
      if (width(text) <= 86) {
         return text;
      }

      int pixels = 0;
      boolean bold = false;
      StringBuilder result = new StringBuilder();
      int i = 0;

      while (i < text.length()) {
         if (text.charAt(i) == 167 && i + 1 < text.length()) {
            char code = Character.toLowerCase(text.charAt(i + 1));
            bold = nextBold(bold, code);
            result.append(text, i, i + 2);
            i += 2;
         } else {
            int codePoint = text.codePointAt(i);
            int advance = advance(codePoint) + (bold && codePoint != 32 ? 1 : 0);
            if (pixels + advance > 77) {
               break;
            }

            result.appendCodePoint(codePoint);
            pixels += advance;
            i += Character.charCount(codePoint);
         }
      }

      return result.append("...").toString();
   }

   static int width(String text) {
      int pixels = 0;
      boolean bold = false;
      int i = 0;

      while (i < text.length()) {
         if (text.charAt(i) == 167 && i + 1 < text.length()) {
            bold = nextBold(bold, Character.toLowerCase(text.charAt(i + 1)));
            i += 2;
         } else {
            int codePoint = text.codePointAt(i);
            pixels += advance(codePoint) + (bold && codePoint != 32 ? 1 : 0);
            i += Character.charCount(codePoint);
         }
      }

      return pixels;
   }

   private static boolean nextBold(boolean bold, char code) {
      if (code == 'l') {
         return true;
      } else {
         return code != 'r' && "0123456789abcdef".indexOf(code) < 0 ? bold : false;
      }
   }

   private static int advance(int codePoint) {
      if (codePoint == 32) {
         return 4;
      } else if ("!.,:;i|".indexOf(codePoint) >= 0) {
         return 2;
      } else if ("'`l".indexOf(codePoint) >= 0) {
         return 3;
      } else if ("\"()[]{}It".indexOf(codePoint) >= 0) {
         return 4;
      } else if ("<>fk".indexOf(codePoint) >= 0) {
         return 5;
      } else if (codePoint != 64 && codePoint != 126) {
         return codePoint >= 32 && codePoint <= 126 ? 6 : 9;
      } else {
         return 7;
      }
   }
}
