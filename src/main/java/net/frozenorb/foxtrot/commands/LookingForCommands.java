package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.util.TimeUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.menu.LookingForFactionMenu;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LookingForCommands {
   public static final long LFC_COOLDOWN = TimeUnit.MINUTES.toMillis(15L);
   public static final long LFF_COOLDOWN = TimeUnit.MINUTES.toMillis(5L);
   // /lfc is limited per faction, so members can't take turns spamming it.
   private static final Map<ObjectId, Long> lfcCooldowns = new HashMap<>();
   private static final Map<UUID, Long> lffCooldowns = new HashMap<>();

   @Command(names = {"lfc", "lookingforcampers"}, permission = "")
   public static void lookingForCampers(Player sender) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
      if (team == null) {
         sender.sendMessage(ChatColor.RED + "You must be in a faction to use this command.");
         return;
      }

      long remaining = remaining(lfcCooldowns.get(team.getUniqueId()));
      if (remaining > 0L) {
         sender.sendMessage(ChatColor.RED + "Your faction can use /lfc again in " + ChatColor.BOLD + formatRemaining(remaining) + ChatColor.RED + ".");
         return;
      }

      lfcCooldowns.put(team.getUniqueId(), System.currentTimeMillis() + LFC_COOLDOWN);
      int online = team.getOnlineMemberAmount();
      String dtr = team.formatDTR();

      for (Player player : chatRecipients(sender)) {
         player.sendMessage("");
         player.sendMessage(
            ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "LFC" + ChatColor.DARK_GRAY + "] "
               + team.getName(player) + ChatColor.YELLOW + " is " + ChatColor.GOLD + ChatColor.BOLD + "Looking for Campers!"
         );
         player.sendMessage(
            ChatColor.GRAY + " » " + ChatColor.YELLOW + "Online: " + ChatColor.WHITE + online + "/" + team.getMembers().size()
               + ChatColor.GRAY + " | " + ChatColor.YELLOW + "DTR: " + dtr
               + ChatColor.GRAY + " | " + ChatColor.YELLOW + "Contact: " + ChatColor.WHITE + sender.getName()
         );
         player.sendMessage("");
      }
   }

   @Command(names = {"lff", "lookingforfaction", "lookingforteam", "lft"}, permission = "")
   public static void lookingForFaction(Player sender) {
      if (canUseLff(sender)) {
         new LookingForFactionMenu().openMenu(sender);
      }
   }

   public static boolean canUseLff(Player sender) {
      if (Foxtrot.getInstance().getTeamHandler().getTeam(sender) != null) {
         sender.sendMessage(ChatColor.RED + "You are already in a faction. Use /lfc to look for campers instead.");
         return false;
      }

      long remaining = remaining(lffCooldowns.get(sender.getUniqueId()));
      if (remaining > 0L) {
         sender.sendMessage(ChatColor.RED + "You can use /lff again in " + ChatColor.BOLD + formatRemaining(remaining) + ChatColor.RED + ".");
         return false;
      }

      return true;
   }

   public static void broadcastLff(Player sender, Collection<String> classes) {
      lffCooldowns.put(sender.getUniqueId(), System.currentTimeMillis() + LFF_COOLDOWN);
      String classList = ChatColor.WHITE + String.join(ChatColor.GRAY + ", " + ChatColor.WHITE, classes);

      for (Player player : chatRecipients(sender)) {
         player.sendMessage("");
         player.sendMessage(
            ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "LFF" + ChatColor.DARK_GRAY + "] "
               + ChatColor.WHITE + sender.getName() + ChatColor.YELLOW + " is " + ChatColor.GREEN + ChatColor.BOLD + "Looking for a Faction!"
         );
         player.sendMessage(ChatColor.GRAY + " » " + ChatColor.YELLOW + "Plays: " + classList);
         player.sendMessage("");
      }
   }

   // Everyone with global chat on (/togglechat), plus the sender so they always see their own ad.
   private static Collection<Player> chatRecipients(Player sender) {
      Collection<Player> recipients = new ArrayList<>();

      for (Player player : Bukkit.getOnlinePlayers()) {
         if (player == sender || Foxtrot.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(player.getUniqueId())) {
            recipients.add(player);
         }
      }

      return recipients;
   }

   private static long remaining(Long expiresAt) {
      return expiresAt == null ? 0L : expiresAt - System.currentTimeMillis();
   }

   private static String formatRemaining(long millis) {
      return TimeUtils.formatIntoDetailedString((int)Math.max(1L, (millis + 999L) / 1000L));
   }
}
