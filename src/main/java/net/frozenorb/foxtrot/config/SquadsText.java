package net.frozenorb.foxtrot.config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SquadsText {
   private static final Pattern URL = Pattern.compile(
      "(?i)(?:https?://|www\\.)[^\\s<>\"'§&]+|(?:(?<![\\w.])|(?<=[&§][0-9a-fk-or]))(?:(?:store|ts|www)\\.)?(?:cavepvp\\.(?:org|com|net)|squads\\.(?:org|rip)|discord\\.gg)(?![a-z0-9-])(?:/[^\\s<>\"'§&]*)?"
   );
   // Squads' own links are shown as-is; every other URL (leftover CavePVP links) is scrubbed.
   private static final Pattern OWN_LINK = Pattern.compile("(?i)^(?:https?://)?(?:www\\.)?(?:(?:[a-z0-9-]+\\.)*squads\\.rip|squadsrip\\.tebex\\.io|discord\\.gg/squadsrip|(?:x|twitter)\\.com/@?squadsrip)(?:/.*)?$");
   private static final Pattern BRAND = Pattern.compile("(?i)cavepvp|cave(?!rn|[_ ]?spider| spawner)");

   private SquadsText() {
   }

   public static String display(String var0) {
      if (var0 == null) {
         return null;
      }

      Matcher var1 = URL.matcher(var0);
      StringBuffer var2 = new StringBuffer();

      while (var1.find()) {
         String var3 = "";

         for (String var4 = var1.group();
            var4.length() > 0 && ".,!?:;)".indexOf(var4.charAt(var4.length() - 1)) >= 0;
            var4 = var4.substring(0, var4.length() - 1)
         ) {
            var3 = var4.substring(var4.length() - 1) + var3;
         }

         String var10 = var1.group().substring(0, var1.group().length() - var3.length());
         var1.appendReplacement(var2, Matcher.quoteReplacement((OWN_LINK.matcher(var10).matches() ? var10 : "Coming Soon!") + var3));
      }

      var1.appendTail(var2);
      var0 = var2.toString().replaceAll("Coming Soon!(?:/[^\\s&§]+)+", "Coming Soon!");
      Matcher var8 = BRAND.matcher(var0);
      var2 = new StringBuffer();

      while (var8.find()) {
         String var9 = var8.group();
         String var5 = var9.equals(var9.toUpperCase(Locale.ROOT)) ? "SQUADS" : (Character.isUpperCase(var9.charAt(0)) ? "Squads" : "squads");
         var8.appendReplacement(var2, var5);
      }

      var8.appendTail(var2);
      return var2.toString();
   }

   public static Object value(Object var0) {
      if (var0 instanceof String) {
         return display((String)var0);
      }

      if (var0 instanceof List) {
         ArrayList var4 = new ArrayList();

         for (Object var6 : (List)var0) {
            var4.add(value(var6));
         }

         return var4;
      } else {
         if (!(var0 instanceof Map)) {
            return var0;
         }

         LinkedHashMap var1 = new LinkedHashMap();

         for (Entry var3 : ((Map<?, ?>)var0).entrySet()) {
            var1.put(var3.getKey(), value(var3.getValue()));
         }

         return var1;
      }
   }

   public static void configuration(Object var0, String var1) {
      if (Arrays.asList("branding.yml", "messages.yml", "tips.yml", "trivia.yml", "scoreboard.yml", "disabled-commands.yml").contains(var1)) {
         try {
            Method var2 = var0.getClass().getMethod("getKeys", boolean.class);
            Method var3 = var0.getClass().getMethod("get", String.class);
            Method var4 = var0.getClass().getMethod("set", String.class, Object.class);

            for (Object var6 : (Set)var2.invoke(var0, true)) {
               String var7 = (String)var6;
               if (!var1.equals("disabled-commands.yml") || var7.startsWith("custom-messages.")) {
                  Object var8 = var3.invoke(var0, var7);
                  Object var9 = value(var8);
                  if (var7.equals("branding.network-name")) {
                     var9 = "Squads";
                  }

                  if (var8 instanceof String || var8 instanceof List || var8 instanceof Map) {
                     var4.invoke(var0, var7, var9);
                  }
               }
            }
         } catch (ReflectiveOperationException var10) {
            throw new IllegalStateException("Could not apply Squads cosmetic settings", var10);
         }
      }
   }
}
