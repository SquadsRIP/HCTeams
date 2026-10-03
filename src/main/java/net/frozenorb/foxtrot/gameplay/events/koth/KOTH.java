package net.frozenorb.foxtrot.gameplay.events.koth;

import cc.fyre.modsuite.mod.ModHandler;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import net.frozenorb.foxtrot.gameplay.events.events.EventActivatedEvent;
import net.frozenorb.foxtrot.gameplay.events.events.EventCapturedEvent;
import net.frozenorb.foxtrot.gameplay.events.events.EventDeactivatedEvent;
import net.frozenorb.foxtrot.gameplay.events.koth.events.EventControlTickEvent;
import net.frozenorb.foxtrot.gameplay.events.koth.events.KOTHControlLostEvent;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

public class KOTH implements Event {
   private String name;
   private BlockVector capLocation;
   private String world;
   private int capDistance;
   private int capTime;
   private boolean hidden = false;
   private boolean mini = false;
   boolean active;
   private transient String currentCapper;
   private transient int remainingCapTime;
   private transient boolean terminate;
   public boolean koth = true;
   private EventType type = EventType.KOTH;

   public KOTH(String name, Location location) {
      this.name = name;
      this.capLocation = location.toVector().toBlockVector();
      this.world = location.getWorld().getName();
      this.capDistance = 3;
      this.capTime = 900;
      this.terminate = false;
      Foxtrot.getInstance().getEventHandler().getEvents().add(this);
      Foxtrot.getInstance().getEventHandler().saveEvents();
   }

   public void setLocation(Location location) {
      this.capLocation = location.toVector().toBlockVector();
      this.world = location.getWorld().getName();
      Foxtrot.getInstance().getEventHandler().saveEvents();
   }

   public void setCapDistance(int capDistance) {
      this.capDistance = capDistance;
      Foxtrot.getInstance().getEventHandler().saveEvents();
   }

   public Color getWaypointColor() {
      switch (this.getName()) {
         case "Hell":
         case "Conquest-Red":
            return Color.RED;
         case "Conquest-Yellow":
            return Color.YELLOW;
         case "Conquest-Green":
            return Color.GREEN;
         case "Conquest-Middle":
         case "Citadel":
         case "End":
            return Color.MAGENTA;
         default:
            return Color.blue;
      }
   }

   public ChatColor getWaypointChatColor() {
      switch (this.getName()) {
         case "Hell":
         case "Conquest-Red":
            return ChatColor.RED;
         case "Conquest-Yellow":
            return ChatColor.YELLOW;
         case "Conquest-Green":
            return ChatColor.GREEN;
         case "Conquest-Middle":
         case "Citadel":
         case "End":
            return ChatColor.DARK_PURPLE;
         default:
            return ChatColor.AQUA;
      }
   }

   public void setCapTime(int capTime) {
      int oldCapTime = this.capTime;
      this.capTime = capTime;
      if (this.remainingCapTime > capTime) {
         this.remainingCapTime = capTime;
      } else if (this.remainingCapTime == oldCapTime) {
         this.remainingCapTime = capTime;
      }

      Foxtrot.getInstance().getEventHandler().saveEvents();
   }

   @Override
   public void setHidden(boolean hidden) {
      this.hidden = hidden;
      Foxtrot.getInstance().getEventHandler().saveEvents();
   }

   @Override
   public boolean activate() {
      if (this.active) {
         return false;
      }

      Foxtrot.getInstance().getServer().getPluginManager().callEvent(new EventActivatedEvent(this));
      this.active = true;
      this.currentCapper = null;
      this.remainingCapTime = this.capTime;
      this.terminate = false;
      return true;
   }

   @Override
   public boolean deactivate() {
      if (!this.active) {
         return false;
      }

      Foxtrot.getInstance().getServer().getPluginManager().callEvent(new EventDeactivatedEvent(this));
      this.active = false;
      this.currentCapper = null;
      this.remainingCapTime = this.capTime;
      this.terminate = false;
      return true;
   }

   public void startCapping(Player player) {
      if (this.currentCapper != null) {
         this.resetCapTime();
      }

      this.currentCapper = player.getName();
      this.remainingCapTime = this.capTime;
   }

   public boolean finishCapping() {
      Player capper = Foxtrot.getInstance().getServer().getPlayerExact(this.currentCapper);
      if (capper == null) {
         this.resetCapTime();
         return false;
      } else {
         EventCapturedEvent event = new EventCapturedEvent(this, capper);
         Foxtrot.getInstance().getServer().getPluginManager().callEvent(event);
         if (event.isCancelled()) {
            this.resetCapTime();
            return false;
         } else {
            this.deactivate();
            return true;
         }
      }
   }

   public void resetCapTime() {
      Foxtrot.getInstance().getServer().getPluginManager().callEvent(new KOTHControlLostEvent(this));
      this.currentCapper = null;
      this.remainingCapTime = this.capTime;
      if (this.terminate) {
         this.deactivate();
         Foxtrot.getInstance()
            .getServer()
            .broadcastMessage(ChatColor.GOLD + "[KingOfTheHill] " + ChatColor.BLUE + this.getName() + ChatColor.YELLOW + " has been terminated.");
      }
   }

   @Override
   public void tick() {
      if (this.currentCapper != null) {
         Player capper = Foxtrot.getInstance().getServer().getPlayerExact(this.currentCapper);
         if (capper == null
            || !this.onCap(capper.getLocation())
            || capper.isDead()
            || capper.getGameMode() == GameMode.CREATIVE
            || ModHandler.INSTANCE.isInVanish(capper.getUniqueId())) {
            this.resetCapTime();
         } else if (Foxtrot.getInstance().getTeamHandler().getTeam(capper) == null && this.isMini()) {
            capper.sendMessage(ChatColor.RED + "You must be in a faction to capture a Mini KOTH!");
            this.resetCapTime();
         } else {
            if (this.remainingCapTime % 60 == 0 && this.remainingCapTime > 1 && !this.isHidden()) {
               Team team = Foxtrot.getInstance().getTeamHandler().getTeam(capper);
               if (team != null) {
                  for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                     if (team.isMember(player.getUniqueId()) && capper != player) {
                        player.sendMessage(
                           ChatColor.GOLD
                              + "[KingOfTheHill]"
                              + ChatColor.YELLOW
                              + " Your faction is controlling "
                              + ChatColor.BLUE
                              + this.getName()
                              + ChatColor.YELLOW
                              + "."
                        );
                     }
                  }
               }
            }

            if (this.remainingCapTime % 10 == 0 && this.remainingCapTime > 1 && !this.isHidden()) {
               capper.sendMessage(
                  ChatColor.GOLD + "[KingOfTheHill]" + ChatColor.YELLOW + " Attempting to control " + ChatColor.BLUE + this.getName() + ChatColor.YELLOW + "."
               );
            }

            if (this.remainingCapTime <= 0) {
               this.finishCapping();
            } else {
               Foxtrot.getInstance().getServer().getPluginManager().callEvent(new EventControlTickEvent(this));
            }

            this.remainingCapTime--;
         }
      } else {
         List<Player> onCap = new ArrayList<>();

         for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (this.onCap(player.getLocation()) && Foxtrot.getInstance().getBattlePassHandler() != null) {
               Foxtrot.getInstance().getBattlePassHandler().useProgress(player.getUniqueId(), progress -> {
                  progress.setAttemptCaptureKoth(true);
                  progress.requiresSave();
                  Foxtrot.getInstance().getBattlePassHandler().checkCompletionsAsync(player);
               });
            }

            if (this.onCap(player.getLocation())
               && !player.isDead()
               && player.getGameMode() == GameMode.SURVIVAL
               && !ModHandler.INSTANCE.isInModMode(player.getUniqueId())
               && !Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
               onCap.add(player);
            }
         }

         Collections.shuffle(onCap);
         if (onCap.size() != 0) {
            this.startCapping(onCap.get(0));
         }
      }
   }

   public boolean onCap(Location location) {
      if (!location.getWorld().getName().equalsIgnoreCase(this.world)) {
         return false;
      }

      int xDistance = Math.abs(location.getBlockX() - this.capLocation.getBlockX());
      int yDistance = Math.abs(location.getBlockY() - this.capLocation.getBlockY());
      int zDistance = Math.abs(location.getBlockZ() - this.capLocation.getBlockZ());
      return xDistance <= this.capDistance && yDistance <= 5 && zDistance <= this.capDistance;
   }

   @Override
   public String getName() {
      return this.name;
   }

   public BlockVector getCapLocation() {
      return this.capLocation;
   }

   public String getWorld() {
      return this.world;
   }

   public int getCapDistance() {
      return this.capDistance;
   }

   public int getCapTime() {
      return this.capTime;
   }

   @Override
   public boolean isHidden() {
      return this.hidden;
   }

   public boolean isMini() {
      return this.mini;
   }

   public void setMini(boolean mini) {
      this.mini = mini;
   }

   @Override
   public boolean isActive() {
      return this.active;
   }

   @Override
   public void setActive(boolean active) {
      this.active = active;
   }

   public String getCurrentCapper() {
      return this.currentCapper;
   }

   public int getRemainingCapTime() {
      return this.remainingCapTime;
   }

   public boolean isTerminate() {
      return this.terminate;
   }

   public void setTerminate(boolean terminate) {
      this.terminate = terminate;
   }

   public boolean isKoth() {
      return this.koth;
   }

   @Override
   public EventType getType() {
      return this.type;
   }
}
