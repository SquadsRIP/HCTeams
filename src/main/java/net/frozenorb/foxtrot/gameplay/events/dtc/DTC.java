package net.frozenorb.foxtrot.gameplay.events.dtc;

import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import net.frozenorb.foxtrot.gameplay.events.events.EventActivatedEvent;
import net.frozenorb.foxtrot.gameplay.events.events.EventCapturedEvent;
import net.frozenorb.foxtrot.gameplay.events.events.EventDeactivatedEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

public class DTC implements Event {
   private String name;
   private BlockVector capLocation;
   private String world;
   private boolean hidden = false;
   boolean active;
   private int currentPoints = 0;
   private final int startingPoints = 175;
   private long lastBlockBreak = -1L;
   private long lastPointIncrease = -1L;
   private EventType type = EventType.DTC;

   public DTC(String name, Location location) {
      this.name = name;
      this.capLocation = location.toVector().toBlockVector();
      this.world = location.getWorld().getName();
      this.currentPoints = 175;
      Foxtrot.getInstance().getEventHandler().getEvents().add(this);
      Foxtrot.getInstance().getEventHandler().saveEvents();
   }

   public void setLocation(Location location) {
      this.capLocation = location.toVector().toBlockVector();
      this.world = location.getWorld().getName();
      Foxtrot.getInstance().getEventHandler().saveEvents();
   }

   @Override
   public void tick() {
      if (this.currentPoints != 175) {
         if (175 <= this.currentPoints) {
            this.currentPoints = 175;
         }

         long timeSinceLastBlockBreak = System.currentTimeMillis() - this.lastBlockBreak;
         long timeSinceLastPointIncrease = System.currentTimeMillis() - this.lastPointIncrease;
         if (TimeUnit.SECONDS.toMillis(15L) <= timeSinceLastBlockBreak && TimeUnit.SECONDS.toMillis(15L) <= timeSinceLastPointIncrease) {
            this.currentPoints++;
            this.lastPointIncrease = System.currentTimeMillis();
            if (this.currentPoints % 10 == 0) {
               Bukkit.broadcastMessage(
                  ChatColor.translateAlternateColorCodes('&', "&6[DTC] &e" + this.getName() + " &6is regenerating &9[" + this.getCurrentPoints() + "]")
               );
            }
         }
      }
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
      this.currentPoints = 175;
      return true;
   }

   @Override
   public boolean deactivate() {
      if (!this.active) {
         return false;
      }

      Foxtrot.getInstance().getServer().getPluginManager().callEvent(new EventDeactivatedEvent(this));
      this.active = false;
      this.currentPoints = 175;
      return true;
   }

   public void blockBroken(Player player) {
      if (--this.currentPoints <= 0) {
         Foxtrot.getInstance().getServer().getPluginManager().callEvent(new EventCapturedEvent(this, player));
         this.deactivate();
      }

      if (this.currentPoints % 10 == 0) {
         Bukkit.broadcastMessage(
            ChatColor.translateAlternateColorCodes('&', "&6[DTC] &e" + this.getName() + " &6is being broken &9[" + this.getCurrentPoints() + "]")
         );
      }

      this.lastBlockBreak = System.currentTimeMillis();
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

   @Override
   public boolean isHidden() {
      return this.hidden;
   }

   @Override
   public boolean isActive() {
      return this.active;
   }

   @Override
   public void setActive(boolean active) {
      this.active = active;
   }

   public int getCurrentPoints() {
      return this.currentPoints;
   }

   public int getStartingPoints() {
      return 175;
   }

   public long getLastBlockBreak() {
      return this.lastBlockBreak;
   }

   public long getLastPointIncrease() {
      return this.lastPointIncrease;
   }

   @Override
   public EventType getType() {
      return this.type;
   }
}
