package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SOTWSaleCommand {
   public static final String SALE_TIMER_TITLE = "&a&lSOTW SALE";

   @Command(names = "sotwsale", permission = "foxtrot.command.sotwsale")
   public static void sotwSale(CommandSender sender, @Parameter(name = "time", wildcard = true) String timeArg) {
      if (timeArg == null || timeArg.isEmpty()) {
         sender.sendMessage(ChatColor.RED + "Usage: /sotwsale <time> (e.g. 1h, 30m, 1d, 45s)");
         sender.sendMessage(ChatColor.RED + "       /sotwsale stop");
      } else if (!timeArg.equalsIgnoreCase("stop") && !timeArg.equalsIgnoreCase("cancel") && !timeArg.equalsIgnoreCase("end")) {
         long durationMillis = parseTime(timeArg);
         if (durationMillis <= 0L) {
            sender.sendMessage(ChatColor.RED + "Invalid time format: " + timeArg);
            sender.sendMessage(ChatColor.RED + "Use formats like: 1h, 30m, 1d, 45s, or 90 (seconds)");
         } else {
            int durationSeconds = (int)(durationMillis / 1000L);
            Foxtrot.getInstance()
               .getServer()
               .dispatchCommand(Foxtrot.getInstance().getServer().getConsoleSender(), "customtimer create " + durationSeconds + " " + "&a&lSOTW SALE");
            sender.sendMessage(ChatColor.GREEN + "Started SOTW Sale for " + formatDuration(durationMillis) + ".");
         }
      } else {
         Foxtrot.getInstance().getServer().dispatchCommand(Foxtrot.getInstance().getServer().getConsoleSender(), "customtimer create 0 &a&lSOTW SALE");
         sender.sendMessage(ChatColor.GREEN + "SOTW Sale has been stopped.");
      }
   }

   private static long parseTime(String input) {
      if (input != null && !input.isEmpty()) {
         try {
            int seconds = Integer.parseInt(input.trim());
            return seconds * 1000L;
         } catch (NumberFormatException seconds) {
            Pattern pattern = Pattern.compile("(\\d+)\\s*([hmdsHMSD])", 2);
            Matcher matcher = pattern.matcher(input.trim());
            long totalMillis = 0L;
            boolean found = false;

            while (matcher.find()) {
               found = true;
               int value = Integer.parseInt(matcher.group(1));
               char unit = Character.toLowerCase(matcher.group(2).charAt(0));
               switch (unit) {
                  case 'd':
                     totalMillis += value * 24L * 60L * 60L * 1000L;
                     break;
                  case 'h':
                     totalMillis += value * 60L * 60L * 1000L;
                     break;
                  case 'm':
                     totalMillis += value * 60L * 1000L;
                     break;
                  case 's':
                     totalMillis += value * 1000L;
               }
            }

            return !found ? 0L : totalMillis;
         }
      } else {
         return 0L;
      }
   }

   private static String formatDuration(long millis) {
      long seconds = millis / 1000L;
      long days = seconds / 86400L;
      long var11;
      long hours = (var11 = seconds % 86400L) / 3600L;
      long minutes = (seconds = var11 % 3600L) / 60L;
      seconds %= 60L;
      StringBuilder sb = new StringBuilder();
      if (days > 0L) {
         sb.append(days).append("d ");
      }

      if (hours > 0L) {
         sb.append(hours).append("h ");
      }

      if (minutes > 0L) {
         sb.append(minutes).append("m ");
      }

      if (seconds > 0L) {
         sb.append(seconds).append("s");
      }

      return sb.toString().trim();
   }
}
