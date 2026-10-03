package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import java.util.ArrayList;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FlagCommand {
   @Command(names = {"flag list", "flags list"}, permission = "foxtrot.command.flag.list", hidden = true)
   public static void bitmaskList(Player sender) {
      for (DTRBitmask bitmaskType : DTRBitmask.values()) {
         sender.sendMessage(ChatColor.GOLD + bitmaskType.getName() + " (" + bitmaskType.getBitmask() + "): " + ChatColor.YELLOW + bitmaskType.getDescription());
      }
   }

   @Command(names = {"flag info", "flags info"}, permission = "foxtrot.command.flag.info", hidden = true)
   public static void bitmaskInfo(Player sender, @Parameter(name = "team") Team team) {
      sender.sendMessage(ChatColor.YELLOW + "Bitmask flags of " + ChatColor.GOLD + team.getName() + ChatColor.YELLOW + ":");

      for (DTRBitmask bitmaskType : DTRBitmask.values()) {
         if (team.hasDTRBitmask(bitmaskType)) {
            sender.sendMessage(
               ChatColor.GOLD + bitmaskType.getName() + " (" + bitmaskType.getBitmask() + "): " + ChatColor.YELLOW + bitmaskType.getDescription()
            );
         }
      }

      sender.sendMessage(ChatColor.GOLD + "Raw DTR: " + ChatColor.YELLOW + team.getDTR());
   }

   @Command(names = {"flag add", "flags add"}, permission = "foxtrot.command.flag.add", hidden = true)
   public static void bitmaskAdd(Player sender, @Parameter(name = "target") Team team, @Parameter(name = "bitmask") DTRBitmask bitmask) {
      if (team.getOwner() != null) {
         team.setOwner(null);

         for (UUID member : new ArrayList<>(team.getMembers())) {
            team.removeMember(member);
            Foxtrot.getInstance().getTeamHandler().setTeam(member, null);
         }

         team.setDTR(0.0);
         team.flagForSave();
         sender.sendMessage(ChatColor.GREEN + "Converted " + team.getName() + " to a system faction (owner and members removed, DTR reset).");
      }

      if (team.hasDTRBitmask(bitmask)) {
         sender.sendMessage(ChatColor.YELLOW + "This claim already has the bitmask value " + bitmask.getName() + ".");
      } else {
         int dtrInt = (int)team.getDTR();
         team.setDTR(dtrInt + bitmask.getBitmask());
         bitmaskInfo(sender, team);
      }
   }

   @Command(
      names = {"flag setup", "flags setup", "f setupsysfac", "faction setupsysfac", "team setupsysfac", "t setupsysfac"},
      permission = "foxtrot.command.flag.setup",
      hidden = true
   )
   public static void flagSetup(Player sender) {
      String[] roadNames = new String[]{"SouthRoad", "EastRoad", "WestRoad", "NorthRoad", "NetherRoad"};
      ArrayList<String> missingRoads = new ArrayList<>();

      for (String roadName : roadNames) {
         if (!applyFlag(roadName, 1024.0)) {
            missingRoads.add(roadName);
         }
      }

      if (missingRoads.isEmpty()) {
         sender.sendMessage(ChatColor.GREEN + "Detected all roads, added flags.");
      } else {
         sender.sendMessage(
            ChatColor.YELLOW
               + "Added flags to "
               + (roadNames.length - missingRoads.size())
               + "/"
               + roadNames.length
               + " roads. Missing: "
               + String.join(", ", missingRoads)
               + "."
         );
      }

      Foxtrot.getInstance()
         .getEventHandler()
         .getEvents()
         .stream()
         .filter(it -> it.getType() == EventType.KOTH && !it.isHidden() && Foxtrot.getInstance().getTeamHandler().getTeam(it.getName()) != null)
         .forEach(it -> {
            Team faction = Foxtrot.getInstance().getTeamHandler().getTeam(it.getName());
            faction.setDTR(64.0);
            sender.sendMessage(ChatColor.GREEN + "Detected " + it.getName() + " Faction, added flags.");
         });
      applyFlag(sender, "Citadel", 416.0, "Citadel");
      applyFlag(sender, "NetherCitadel", 416.0, "Nether Citadel");
      applyFlag(sender, "Conquest", 2436.0, "Conquest");
      applyFlag(sender, "Glowstone", 16384.0, "Glowstone Mountain");
      applyFlag(sender, "TreasureCove", 16384.0, "Treasure Cove");
      applyFlag(sender, "EndPortal", 8.0, "End Portal");
      applyFlag(sender, "BufferZone", 16384.0, "Buffer Zone");
      applyFlag(sender, "RoadOutpost", DTRBitmask.OUTPOST.getBitmask(), "Road Outpost");
      applyFlag(sender, "EndOutpost", DTRBitmask.OUTPOST.getBitmask(), "End Outpost");
      applyFlag(sender, "NetherOutpost", DTRBitmask.OUTPOST.getBitmask(), "Nether Outpost");
      applyFlag(sender, "Zombie", DTRBitmask.MOUNTAIN.getBitmask(), "Zombie");
      applyFlag(sender, "Spider", DTRBitmask.MOUNTAIN.getBitmask(), "Spider");
      applyFlag(sender, "CaveSpider", DTRBitmask.MOUNTAIN.getBitmask(), "Cave Spider");
      applyFlag(sender, "Skeleton", DTRBitmask.MOUNTAIN.getBitmask(), "Skeleton");
      sender.sendMessage(ChatColor.GREEN + "Completed task for faction bitmask setup.");
   }

   private static boolean applyFlag(String teamName, double dtr) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(teamName);
      if (team == null) {
         return false;
      }

      team.setDTR(dtr);
      return true;
   }

   private static void applyFlag(Player sender, String teamName, double dtr, String displayName) {
      if (applyFlag(teamName, dtr)) {
         sender.sendMessage(ChatColor.GREEN + "Detected " + displayName + " Faction, added flags.");
      }
   }

   @Command(names = {"flag remove", "flags remove"}, permission = "foxtrot.command.flag.remove", hidden = true)
   public static void bitmaskRemove(Player sender, @Parameter(name = "team") Team team, @Parameter(name = "bitmask") DTRBitmask bitmask) {
      if (!team.hasDTRBitmask(bitmask)) {
         sender.sendMessage(ChatColor.YELLOW + "This claim doesn't have the bitmask value " + bitmask.getName() + ".");
      } else {
         int dtrInt = (int)team.getDTR();
         team.setDTR(dtrInt - bitmask.getBitmask());
         bitmaskInfo(sender, team);
      }
   }
}
