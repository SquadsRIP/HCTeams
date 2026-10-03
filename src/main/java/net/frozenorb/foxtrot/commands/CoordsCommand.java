package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

public class CoordsCommand {
   private static final String LINE = "§8§m-----------------------------------------------------";

   @Command(names = {"coords", "coordinates", "locations"}, permission = "")
   public static void coords(Player player) {
      List<String> msg = new ArrayList<>();
      msg.add(LINE);
      msg.add("§4§lSquads Coordinates");
      msg.add(LINE);
      Location spawn = Foxtrot.getInstance().getServerHandler().getSpawnLocation();
      msg.add("§cSpawn: §f" + spawn.getBlockX() + ", " + spawn.getBlockZ() + " §7(Overworld)");
      String portals = Foxtrot.getInstance().getMapHandler().getEndPortalLocation();
      if (portals != null && !portals.trim().isEmpty() && !portals.equals("N/A")) {
         msg.add("§cEnd Portals: §f" + portals + " §7(in each quadrant)");
      }

      Team glowstone = Foxtrot.getInstance().getTeamHandler().getTeam("Glowstone");
      if (glowstone != null && !glowstone.getClaims().isEmpty()) {
         Claim claim = glowstone.getClaims().get(0);
         Location min = claim.getMinimumPoint();
         Location max = claim.getMaximumPoint();
         msg.add(
            "§cGlowstone Mountain: §f"
               + (min.getBlockX() + max.getBlockX()) / 2
               + ", "
               + (min.getBlockZ() + max.getBlockZ()) / 2
               + " §7("
               + worldName(min.getWorld().getName())
               + ")"
         );
      }

      List<KOTH> koths = new ArrayList<>();

      for (Event event : Foxtrot.getInstance().getEventHandler().getEvents()) {
         if (event instanceof KOTH && !event.isHidden() && !((KOTH)event).isMini()) {
            koths.add((KOTH)event);
         }
      }

      if (!koths.isEmpty()) {
         koths.sort(Comparator.comparing(KOTH::getName, String.CASE_INSENSITIVE_ORDER));
         msg.add("");
         msg.add("§cKOTHs:");

         for (KOTH koth : koths) {
            BlockVector cap = koth.getCapLocation();
            msg.add(
               "§7" + koth.getName() + ": §f" + cap.getBlockX() + ", " + cap.getBlockZ() + " §7(" + worldName(koth.getWorld()) + ")" + (koth.isActive() ? " §a(Active)" : "")
            );
         }
      }

      List<String> extra = Foxtrot.getInstance().getConfig().getStringList("coords.extra");
      if (!extra.isEmpty()) {
         msg.add("");

         for (String line : extra) {
            msg.add(ChatColor.translateAlternateColorCodes('&', line));
         }
      }

      msg.add(LINE);
      player.sendMessage(msg.toArray(new String[0]));
   }

   private static String worldName(String world) {
      if (world == null) {
         return "Unknown";
      } else if (world.equalsIgnoreCase(Foxtrot.getInstance().getServerHandler().getMapWorld().getName()) || world.equalsIgnoreCase("world")) {
         return "Overworld";
      } else if (world.equalsIgnoreCase("world_nether")) {
         return "Nether";
      } else {
         return world.equalsIgnoreCase("world_the_end") ? "End" : world;
      }
   }
}
