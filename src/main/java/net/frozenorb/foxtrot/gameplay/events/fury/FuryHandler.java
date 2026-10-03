package net.frozenorb.foxtrot.gameplay.events.fury;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import net.frozenorb.foxtrot.gameplay.events.conquest.ConquestHandler;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.team.Team;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FuryHandler {
   private final Foxtrot instance;
   public static String PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + ChatColor.BOLD + "FURY" + ChatColor.DARK_GRAY + "]";
   private LinkedHashMap<ObjectId, Integer> teamPoints = new LinkedHashMap<>();
   private boolean active;
   private long lastSwitchTime;
   private FuryCapZone furyCapZone;

   public FuryHandler(Foxtrot instance) {
      this.instance = instance;
      this.active = false;
      this.furyCapZone = FuryCapZone.OVERWORLD;
   }

   public void start() {
      this.getAllCapZones().forEach(it -> {
         if (!it.isHidden()) {
            it.setHidden(true);
         }

         if (it.getCapTime() != 25) {
            it.setCapTime(25);
         }
      });
      this.active = true;
      this.lastSwitchTime = System.currentTimeMillis();
      this.furyCapZone = FuryCapZone.OVERWORLD;
      this.getActiveCapZone().activate();
      this.instance.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + " &eFury &6has started!"));
   }

   public void switchCapZone() {
      this.getActiveCapZone().deactivate();
      this.furyCapZone = Arrays.stream(FuryCapZone.values())
         .filter(it -> it.getOrder() == this.furyCapZone.getOrder() + 1)
         .findFirst()
         .orElse(FuryCapZone.OVERWORLD);
      this.lastSwitchTime = System.currentTimeMillis();
      this.getActiveCapZone().activate();
      this.instance
         .getServer()
         .broadcastMessage(
            ChatColor.translateAlternateColorCodes(
               '&', PREFIX + " &6Switched Capzones to " + this.furyCapZone.getChatColor() + this.furyCapZone.getDisplayName() + "&6."
            )
         );
   }

   public void death(Player player) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
      if (team != null && this.teamPoints.containsKey(team.getUniqueId())) {
         this.teamPoints.put(team.getUniqueId(), Math.max(0, this.teamPoints.get(team.getUniqueId()) - 20));
         this.teamPoints = this.sortByValues(this.teamPoints);
         this.instance
            .getServer()
            .broadcastMessage(
               PREFIX
                  + ChatColor.YELLOW
                  + team.getName()
                  + ChatColor.GOLD
                  + " has lost "
                  + ChatColor.YELLOW
                  + "20 points "
                  + ChatColor.GOLD
                  + "because "
                  + ChatColor.YELLOW
                  + player.getName()
                  + ChatColor.GOLD
                  + " died. "
                  + ChatColor.YELLOW
                  + " ("
                  + this.teamPoints.get(team.getUniqueId())
                  + "/"
                  + 150
                  + ")"
            );
      }
   }

   public void endGame(Team winner) {
      this.active = false;
      this.furyCapZone = FuryCapZone.OVERWORLD;
      this.lastSwitchTime = 0L;
      this.getTeamPoints().clear();
      this.getAllCapZones().forEach(KOTH::deactivate);
      if (winner == null) {
         Foxtrot.getInstance().getServer().broadcastMessage(PREFIX + " " + ChatColor.GOLD + "Fury has ended.");
      } else {
         winner.setFurysCaptured(winner.getFurysCaptured() + 1);
         this.instance
            .getServer()
            .broadcastMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD + ChatColor.BOLD + winner.getName() + ChatColor.GOLD + " has won Conquest!");
      }
   }

   public LinkedHashMap<ObjectId, Integer> sortByValues(Map<ObjectId, Integer> map) {
      LinkedList<Entry<ObjectId, Integer>> list = new LinkedList<>(map.entrySet());
      list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
      LinkedHashMap<ObjectId, Integer> sortedHashMap = new LinkedHashMap<>();

      for (Entry entry : list) {
         sortedHashMap.put((ObjectId)entry.getKey(), (Integer)entry.getValue());
      }

      return sortedHashMap;
   }

   public KOTH getKoth(FuryCapZone furyCapZone) {
      return (KOTH)this.instance.getEventHandler().getEvent("Fury_" + furyCapZone.name());
   }

   public List<KOTH> getAllCapZones() {
      return this.instance
         .getEventHandler()
         .getEvents()
         .stream()
         .filter(it -> it.getType() == EventType.KOTH && it.getName().startsWith("Fury_"))
         .map(it -> (KOTH)it)
         .collect(Collectors.toList());
   }

   public KOTH getActiveCapZone() {
      return this.getKoth(this.furyCapZone);
   }

   public LinkedHashMap<ObjectId, Integer> getTeamPoints() {
      return this.teamPoints;
   }

   public void setTeamPoints(LinkedHashMap<ObjectId, Integer> teamPoints) {
      this.teamPoints = teamPoints;
   }

   public boolean isActive() {
      return this.active;
   }

   public long getLastSwitchTime() {
      return this.lastSwitchTime;
   }

   public FuryCapZone getFuryCapZone() {
      return this.furyCapZone;
   }
}
