package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import net.minecraft.util.com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CustomTimerCreateCommand {
   private static Map<String, Long> customTimers = new ConcurrentHashMap<>();
   private static Set<UUID> sotwEnabled = Sets.newHashSet();

   @Command(names = "customtimer create", permission = "foxtrot.command.customtimer", hidden = true)
   public static void customTimerCreate(CommandSender sender, @Parameter(name = "time") int time, @Parameter(name = "title", wildcard = true) String title) {
      if (time == 0) {
         customTimers.remove(title);
      } else {
         customTimers.put(title, System.currentTimeMillis() + time * 1000);
      }
   }

   @Command(names = {"customtimer cancel", "customtimer stop", "customtimer remove"}, permission = "foxtrot.command.customtimer", hidden = true)
   public static void customTimerCancel(CommandSender sender, @Parameter(name = "title", wildcard = true) String title) {
      String timer = findTimer(title);
      if (timer == null) {
         sender.sendMessage(ChatColor.YELLOW + "No active custom timer was found with that title.");
      } else if (timer.equalsIgnoreCase("&6&lSOTW")) {
         sotwCancel(sender);
      } else {
         customTimers.remove(timer);
         sender.sendMessage(ChatColor.GREEN + "Cancelled custom timer: " + ChatColor.YELLOW + normalizeTimerTitle(timer));
      }
   }

   private static String findTimer(String title) {
      for (String timer : customTimers.keySet()) {
         if (timer.equalsIgnoreCase(title)) {
            return timer;
         }
      }

      String normalizedTitle = normalizeTimerTitle(title);

      for (String timer : customTimers.keySet()) {
         if (normalizeTimerTitle(timer).equalsIgnoreCase(normalizedTitle)) {
            return timer;
         }
      }

      return null;
   }

   private static String normalizeTimerTitle(String title) {
      return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', title)).trim();
   }

   @Command(names = "sotw enable", permission = "")
   public static void sotwEnable(Player sender) {
      if (!isSOTWTimer()) {
         sender.sendMessage(ChatColor.GREEN + "You can't /sotw enable when there is no SOTW timer...");
      } else {
         if (sotwEnabled.add(sender.getUniqueId())) {
            LunarClientListener.updateNametag(sender);
            sender.sendMessage(ChatColor.GREEN + "Successfully disabled your SOTW timer.");
         } else {
            sender.sendMessage(ChatColor.GREEN + "Your SOTW timer was already disabled...");
         }
      }
   }

   @Command(names = {"sotw cancel", "sotw stop"}, permission = "foxtrot.command.sotw.cancel")
   public static void sotwCancel(CommandSender sender) {
      String sotwKey = findSOTWKey();
      Long removed = sotwKey != null ? customTimers.remove(sotwKey) : null;
      if (removed != null && System.currentTimeMillis() < removed) {
         sender.sendMessage(ChatColor.GREEN + "Deactivated the SOTW timer.");

         for (Player loopPlayer : getOnlinePlayersList()) {
            LunarClientListener.updateNametag(loopPlayer);
         }
      } else {
         sender.sendMessage(ChatColor.YELLOW + "SOTW timer is not active.");
      }
   }

   @Command(names = "sotw start", permission = "foxtrot.command.sotw.start")
   public static void sotwStart(CommandSender sender, @Parameter(name = "time") long time) {
      if (time < 0L) {
         sender.sendMessage(ChatColor.YELLOW + "Invalid time!");
      } else {
         String oldKey = findSOTWKey();
         if (oldKey != null) {
            customTimers.remove(oldKey);
         }

         customTimers.put("&6&lSOTW", System.currentTimeMillis() + time);
         sotwEnabled.clear();
         sender.sendMessage(ChatColor.GREEN + "Started the SOTW timer for " + time + "ms.");

         for (Player loopPlayer : getOnlinePlayersList()) {
            if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(loopPlayer.getUniqueId())) {
               Foxtrot.getInstance().getPvPTimerMap().removeTimer(loopPlayer.getUniqueId());
               loopPlayer.sendMessage(ChatColor.GREEN + "Your PvP Timer has been removed due to SOTW starting!");
            }

            LunarClientListener.updateNametag(loopPlayer);
         }
      }
   }

   private static List<Player> getOnlinePlayersList() {
      try {
         Method m = Bukkit.class.getMethod("getOnlinePlayers");
         Object result = m.invoke(null);
         if (result instanceof Collection) {
            return new ArrayList<>((Collection<? extends Player>)result);
         }

         if (result instanceof Player[]) {
            return Arrays.asList((Player[])result);
         }
      } catch (Exception var2) {
      }

      return new ArrayList<>();
   }

   @Command(names = "sotw extend", permission = "foxtrot.command.sotw.extend")
   public static void sotwExtend(CommandSender sender, @Parameter(name = "time") long time) {
      if (time < 0L) {
         sender.sendMessage(ChatColor.YELLOW + "Invalid time!");
      } else {
         String sotwKey = findSOTWKey();
         if (sotwKey == null) {
            sender.sendMessage(ChatColor.YELLOW + "There is currently no active SOTW timer.");
         } else {
            customTimers.put(sotwKey, customTimers.get(sotwKey) + time);
            sender.sendMessage(ChatColor.GREEN + "Extended the SOTW timer by " + time + "ms.");
         }
      }
   }

   @Command(names = "sotw set", permission = "foxtrot.command.sotw.set")
   public static void sotwSet(CommandSender sender, @Parameter(name = "time") long time) {
      if (time < 0L) {
         sender.sendMessage(ChatColor.YELLOW + "Invalid time!");
      } else {
         String sotwKey = findSOTWKey();
         if (sotwKey == null) {
            sender.sendMessage(ChatColor.YELLOW + "There is currently no active SOTW timer.");
         } else {
            customTimers.put(sotwKey, System.currentTimeMillis() + time);
            sender.sendMessage(ChatColor.GREEN + "Set the SOTW timer to " + time + "ms.");
         }
      }
   }

   public static long remainingSOTWTime() {
      String sotwKey = findSOTWKey();
      if (sotwKey == null) {
         return -1L;
      }

      long endsAt = customTimers.get(sotwKey);
      return endsAt - System.currentTimeMillis();
   }

   public static boolean isSOTWTimer() {
      return findSOTWKey() != null;
   }

   private static String findSOTWKey() {
      for (String key : customTimers.keySet()) {
         String normalized = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', key)).trim();
         if (normalized.equalsIgnoreCase("SOTW")) {
            return key;
         }
      }

      return null;
   }

   public static boolean hasSOTWEnabled(UUID uuid) {
      return sotwEnabled.contains(uuid);
   }

   public static boolean isDoublePoints() {
      return customTimers.containsKey("&d&l2x Points");
   }

   public static Map<String, Long> getCustomTimers() {
      return customTimers;
   }

   public static Set<UUID> getSotwEnabled() {
      return sotwEnabled;
   }
}
