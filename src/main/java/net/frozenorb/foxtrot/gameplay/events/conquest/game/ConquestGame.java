package net.frozenorb.foxtrot.gameplay.events.conquest.game;

import cc.fyre.proton.util.UUIDUtils;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import net.frozenorb.foxtrot.gameplay.events.conquest.ConquestHandler;
import net.frozenorb.foxtrot.gameplay.events.conquest.enums.ConquestCapzone;
import net.frozenorb.foxtrot.gameplay.events.events.EventCapturedEvent;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.gameplay.events.koth.events.EventControlTickEvent;
import net.frozenorb.foxtrot.gameplay.events.koth.events.KOTHControlLostEvent;
import net.frozenorb.foxtrot.team.Team;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ConquestGame implements Listener {
   private LinkedHashMap<ObjectId, Integer> teamPoints = new LinkedHashMap<>();

   public ConquestGame() {
      Foxtrot.getInstance().getServer().getPluginManager().registerEvents(this, Foxtrot.getInstance());

      for (Event event : Foxtrot.getInstance().getEventHandler().getEvents()) {
         if (event.getType() == EventType.KOTH) {
            KOTH koth = (KOTH)event;
            if (koth.getName().startsWith("Conquest-")) {
               if (!koth.isHidden()) {
                  koth.setHidden(true);
               }

               if (koth.getCapTime() != 30) {
                  koth.setCapTime(30);
               }

               koth.activate();
            }
         }
      }

      Foxtrot.getInstance()
         .getServer()
         .broadcastMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD + "Conquest has started! Use /conquest for more information.");
      Foxtrot.getInstance().getConquestHandler().setGame(this);
   }

   public void endGame(Team winner) {
      if (winner == null) {
         Foxtrot.getInstance().getServer().broadcastMessage(ConquestHandler.PREFIX + " " + ChatColor.GOLD + "Conquest has ended.");
      } else {
         winner.setConquestsCapped(winner.getConquestsCapped() + 1);
         Foxtrot.getInstance()
            .getServer()
            .broadcastMessage(
               ConquestHandler.PREFIX + " " + ChatColor.GOLD.toString() + ChatColor.BOLD + winner.getName() + ChatColor.GOLD + " has won Conquest!"
            );
      }

      for (Event koth : Foxtrot.getInstance().getEventHandler().getEvents()) {
         if (koth.getName().startsWith("Conquest-")) {
            koth.deactivate();
         }
      }

      HandlerList.unregisterAll(this);
      Foxtrot.getInstance().getConquestHandler().setGame(null);
   }

   @EventHandler
   public void onKOTHCaptured(final EventCapturedEvent event) {
      if (event.getEvent().getName().startsWith("Conquest-")) {
         Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());
         ConquestCapzone capZone = ConquestCapzone.valueOf(event.getEvent().getName().replace("Conquest-", "").toUpperCase());
         if (team == null) {
            (new BukkitRunnable() {
               @Override
               public void run() {
                  if (Foxtrot.getInstance().getConquestHandler().getGame() != null) {
                     event.getEvent().activate();
                  }
               }
            }).runTaskLater(Foxtrot.getInstance(), 10L);
         } else {
            int points = 1;
            if (this.teamPoints.containsKey(team.getUniqueId())) {
               if (!capZone.getName().equalsIgnoreCase("Sky") && !capZone.getName().equalsIgnoreCase("Middle")) {
                  this.teamPoints.put(team.getUniqueId(), this.teamPoints.get(team.getUniqueId()) + 1);
               } else {
                  this.teamPoints.put(team.getUniqueId(), this.teamPoints.get(team.getUniqueId()) + 2);
                  points = 2;
               }
            } else if (!capZone.getName().equalsIgnoreCase("Sky") && !capZone.getName().equalsIgnoreCase("Middle")) {
               this.teamPoints.put(team.getUniqueId(), 1);
            } else {
               this.teamPoints.put(team.getUniqueId(), 2);
               points = 2;
            }

            this.teamPoints = sortByValues(this.teamPoints);
            Foxtrot.getInstance()
               .getServer()
               .broadcastMessage(
                  ConquestHandler.PREFIX
                     + " "
                     + ChatColor.GOLD
                     + team.getName()
                     + ChatColor.GOLD
                     + " captured "
                     + capZone.getChatColor()
                     + capZone.getName()
                     + ChatColor.GOLD
                     + " and earned "
                     + ChatColor.WHITE.toString()
                     + points
                     + " point(s)"
                     + ChatColor.GOLD
                     + "!"
                     + ChatColor.AQUA
                     + " ("
                     + this.teamPoints.get(team.getUniqueId())
                     + "/"
                     + ConquestHandler.getPointsToWin()
                     + ")"
               );
            if (this.teamPoints.get(team.getUniqueId()) >= ConquestHandler.getPointsToWin()) {
               this.endGame(team);
               Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + event.getPlayer().getName() + " koth 7");
            } else {
               (new BukkitRunnable() {
                  @Override
                  public void run() {
                     if (Foxtrot.getInstance().getConquestHandler().getGame() != null) {
                        event.getEvent().activate();
                     }
                  }
               }).runTaskLater(Foxtrot.getInstance(), 10L);
            }
         }
      }
   }

   @EventHandler
   public void onKOTHControlLost(KOTHControlLostEvent event) {
      if (event.getKOTH().getName().startsWith("Conquest-")) {
         Team team = Foxtrot.getInstance().getTeamHandler().getTeam(UUIDUtils.uuid(event.getKOTH().getCurrentCapper()));
         ConquestCapzone capzone = ConquestCapzone.valueOf(event.getKOTH().getName().replace("Conquest-", "").toUpperCase());
         if (team != null) {
            team.sendMessage(
               ConquestHandler.PREFIX
                  + ChatColor.GOLD
                  + " "
                  + event.getKOTH().getCurrentCapper()
                  + " was knocked off of "
                  + capzone.getChatColor()
                  + capzone.getName()
                  + ChatColor.GOLD
                  + "!"
            );
         }
      }
   }

   @EventHandler
   public void onKOTHControlTick(EventControlTickEvent event) {
      if (event.getKOTH().getName().startsWith("Conquest-") && event.getKOTH().getRemainingCapTime() % 5 == 0) {
         ConquestCapzone capzone = ConquestCapzone.valueOf(event.getKOTH().getName().replace("Conquest-", "").toUpperCase());
         Player capper = Foxtrot.getInstance().getServer().getPlayerExact(event.getKOTH().getCurrentCapper());
         if (capper != null) {
            capper.sendMessage(
               ConquestHandler.PREFIX
                  + " "
                  + ChatColor.GOLD
                  + "Attempting to capture "
                  + capzone.getChatColor()
                  + capzone.getName()
                  + ChatColor.GOLD
                  + "!"
                  + ChatColor.AQUA
                  + " ("
                  + event.getKOTH().getRemainingCapTime()
                  + "s)"
            );
         }
      }
   }

   @EventHandler
   public void onPlayerDeath(PlayerDeathEvent event) {
      if (!Foxtrot.getInstance().getDeathbanArenaHandler().isDeathbanArena(event.getEntity())) {
         this.death(event.getEntity());
      }
   }

   public void death(Player player) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);
      if (team != null && this.teamPoints.containsKey(team.getUniqueId())) {
         this.teamPoints.put(team.getUniqueId(), Math.max(0, this.teamPoints.get(team.getUniqueId()) - 20));
         this.teamPoints = sortByValues(this.teamPoints);
         team.sendMessage(
            ConquestHandler.PREFIX
               + ChatColor.GOLD
               + " Your team has lost "
               + 20
               + " points because of "
               + player.getName()
               + "'s death!"
               + ChatColor.AQUA
               + " ("
               + this.teamPoints.get(team.getUniqueId())
               + "/"
               + ConquestHandler.getPointsToWin()
               + ")"
         );
      }
   }

   private static LinkedHashMap<ObjectId, Integer> sortByValues(Map<ObjectId, Integer> map) {
      LinkedList<Entry<ObjectId, Integer>> list = new LinkedList<>(map.entrySet());
      Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
      LinkedHashMap<ObjectId, Integer> sortedHashMap = new LinkedHashMap<>();

      for (Entry<ObjectId, Integer> entry : list) {
         sortedHashMap.put(entry.getKey(), entry.getValue());
      }

      return sortedHashMap;
   }

   public LinkedHashMap<ObjectId, Integer> getTeamPoints() {
      return this.teamPoints;
   }
}
