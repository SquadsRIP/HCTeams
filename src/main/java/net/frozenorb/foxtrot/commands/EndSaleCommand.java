package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import java.util.ArrayList;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class EndSaleCommand {
   @Command(names = "endsale", permission = "op")
   public static void execute(CommandSender sender, @Parameter(name = "amount|all", wildcard = true) String amountStr) {
      Map<String, Long> timers = CustomTimerCreateCommand.getCustomTimers();
      if (timers.isEmpty()) {
         sender.sendMessage(ChatColor.YELLOW + "There are no active sale timers.");
      } else {
         ArrayList<String> saleTimers = new ArrayList<>();

         for (String string : timers.keySet()) {
            if (!string.equalsIgnoreCase("&a&lSOTW")) {
               saleTimers.add(string);
            }
         }

         if (saleTimers.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "There are no active sale timers (SOTW is not affected by /endsale).");
         } else {
            int count;
            if (amountStr.equalsIgnoreCase("all")) {
               count = saleTimers.size();

               for (String timer : saleTimers) {
                  timers.remove(timer);
               }
            } else {
               try {
                  count = Integer.parseInt(amountStr);
               } catch (NumberFormatException numberFormatException) {
                  sender.sendMessage(ChatColor.RED + "Usage: /endsale <amount|all>");
                  return;
               }

               if (count < 1) {
                  sender.sendMessage(ChatColor.RED + "Amount must be at least 1.");
                  return;
               }

               if (count > saleTimers.size()) {
                  count = saleTimers.size();
               }

               for (int i = 0; i < count; i++) {
                  timers.remove(saleTimers.get(i));
               }
            }

            sender.sendMessage(ChatColor.GREEN + "Ended " + ChatColor.YELLOW + count + ChatColor.GREEN + " sale timer(s).");
         }
      }
   }
}
