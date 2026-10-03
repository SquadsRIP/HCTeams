package net.frozenorb.foxtrot.gameplay.events.mini;

import cc.fyre.proton.event.HourEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.mini.type.Mayhem;
import net.frozenorb.foxtrot.gameplay.events.mini.type.MiningFrenzy;
import net.frozenorb.foxtrot.gameplay.events.mini.type.Rage;
import net.frozenorb.foxtrot.gameplay.events.mini.type.Raider;
import net.frozenorb.foxtrot.gameplay.events.mini.type.Traveller;
import net.frozenorb.foxtrot.gameplay.events.mini.type.kitmap.King;
import net.frozenorb.foxtrot.gameplay.events.mini.type.kitmap.Shooter;
import net.frozenorb.foxtrot.gameplay.events.mini.type.kitmap.Stabber;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class MiniEventsHandler implements Listener {
   private Foxtrot instance;
   private List<MiniEvent> events = new ArrayList<>();
   private MiniEvent activeEvent;

   public MiniEventsHandler(Foxtrot instance) {
      this.instance = instance;
      if (this.instance.getMapHandler().isKitMap()) {
         this.events.add(new King());
         this.events.add(new Shooter());
         this.events.add(new Stabber());
      } else {
         this.events.add(new MiningFrenzy());
         this.events.add(new Raider());
      }

      this.events.add(new Traveller());
      this.events.add(new Rage());
      this.events.add(new Mayhem());
      this.instance.getServer().getPluginManager().registerEvents(this, this.instance);
   }

   public MiniEvent findEvent(String id) {
      return this.events.stream().filter(it -> it.getEventID().equalsIgnoreCase(id)).findFirst().orElse(null);
   }

   @EventHandler(priority = EventPriority.LOW)
   private void onHour(HourEvent event) {
      GregorianCalendar calendar = new GregorianCalendar();
      calendar.setTime(new Date(System.currentTimeMillis()));
      int day = calendar.get(7);
      if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
         if (event.getHour() == 16) {
            if (day == 1) {
               this.activeEvent = this.findEvent("Rage");
            }

            if (day == 2) {
               this.activeEvent = this.findEvent("King");
            }

            if (day == 3) {
               this.activeEvent = this.findEvent("Mayhem");
            }

            if (day == 4) {
               this.activeEvent = this.findEvent("Traveller");
            }

            if (day == 5) {
               this.activeEvent = this.findEvent("Stabber");
            }

            if (day == 6) {
               this.activeEvent = this.findEvent("Shooter");
            }
         }
      } else {
         if (day == 1 && event.getHour() == 15) {
            this.activeEvent = this.findEvent("Rage");
         }

         if (day == 2 && event.getHour() == 16) {
            this.activeEvent = this.findEvent("Raider");
         }

         if (day == 3 && event.getHour() == 12) {
            this.activeEvent = this.findEvent("Mayhem");
         }

         if (day == 4 && event.getHour() == 16) {
            this.activeEvent = this.findEvent("Traveller");
         }

         if (day == 5 && event.getHour() == 14) {
            this.activeEvent = this.findEvent("Rage");
         }

         if (this.activeEvent != null) {
            this.activeEvent.activate();
         }
      }
   }

   public void activateRandom() {
      this.activeEvent = this.events.get(ThreadLocalRandom.current().nextInt(this.events.size()));
      this.activeEvent.activate();
   }

   public List<MiniEvent> getEvents() {
      return this.events;
   }

   public MiniEvent getActiveEvent() {
      return this.activeEvent;
   }

   public void setActiveEvent(MiniEvent activeEvent) {
      this.activeEvent = activeEvent;
   }
}
