package net.frozenorb.foxtrot.gameplay.events.koth.commands.koth;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.util.TimeUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.util.LocationUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

public class KOTHsCommand {
   private static final String LINE = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-----------------------------------------------------";

   @Command(names = {"koths", "kothlist", "koth locations", "koth coords"}, permission = "")
   public static void koths(Player sender) {
      boolean staff = sender.hasPermission("foxtrot.koth");
      List<KOTH> koths = new ArrayList<>();

      for (Event event : Foxtrot.getInstance().getEventHandler().getEvents()) {
         if (event instanceof KOTH && (staff || !event.isHidden())) {
            koths.add((KOTH)event);
         }
      }

      if (koths.isEmpty()) {
         sender.sendMessage(ChatColor.YELLOW + "There aren't any KOTHs set.");
      } else {
         koths.sort(Comparator.comparing((KOTH koth) -> koth.isHidden()).thenComparing(KOTH::getName, String.CASE_INSENSITIVE_ORDER));
         sender.sendMessage(LINE);
         sender.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "KOTHs " + ChatColor.GRAY + "(" + koths.size() + ")");

         for (KOTH koth : koths) {
            BlockVector cap = koth.getCapLocation();
            sender.sendMessage(
               (koth.isHidden() ? ChatColor.DARK_GRAY + "[H] " : "")
                  + (koth.isActive() ? ChatColor.GREEN : ChatColor.YELLOW)
                  + koth.getName()
                  + (koth.isMini() ? ChatColor.GRAY + " (Mini)" : "")
                  + ChatColor.GRAY
                  + ": "
                  + ChatColor.WHITE
                  + cap.getBlockX()
                  + ", "
                  + cap.getBlockZ()
                  + ChatColor.GRAY
                  + " ("
                  + LocationUtil.realmName(koth.getWorld())
                  + ") "
                  + ChatColor.DARK_GRAY
                  + "- "
                  + ChatColor.GRAY
                  + TimeUtils.formatIntoMMSS(koth.getCapTime())
                  + (koth.isActive() ? ChatColor.GREEN + " (Active)" : "")
            );
         }

         sender.sendMessage(LINE);
      }
   }
}
