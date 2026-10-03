package net.frozenorb.foxtrot.gameplay.events;

import cc.fyre.proton.Proton;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.gameplay.events.dtc.DTC;
import net.frozenorb.foxtrot.gameplay.events.dtc.DTCListener;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.gameplay.events.koth.listeners.KOTHListener;
import net.frozenorb.foxtrot.gameplay.events.koth.minikoth.MiniKOTHHandler;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class EventHandler {
   private final Set<Event> events = new HashSet<>();
   private final Map<EventScheduledTime, String> EventSchedule = new TreeMap<>();
   private boolean scheduleEnabled;
   private MiniKOTHHandler miniKOTHHandler;

   public EventHandler() {
      if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
         this.miniKOTHHandler = new MiniKOTHHandler();
      }

      this.loadEvents();
      this.loadSchedules();
      Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new KOTHListener(), Foxtrot.getInstance());
      Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new DTCListener(), Foxtrot.getInstance());
      Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new EventListener(), Foxtrot.getInstance());
      Proton.getInstance().getCommandHandler().registerParameterType(Event.class, new EventParameterType());
      (new BukkitRunnable() {
         @Override
         public void run() {
            for (Event event : EventHandler.this.events) {
               if (event.isActive()) {
                  event.tick();
               }
            }
         }
      }).runTaskTimer(Foxtrot.getInstance(), 5L, 20L);
      Foxtrot.getInstance().getServer().getScheduler().runTaskTimer(Foxtrot.getInstance(), this::activateKOTHs, 20L, 20L);
   }

   public KOTH getActiveKOTH() {
      return this.events.stream().filter(it -> it instanceof KOTH && it.isActive()).map(it -> (KOTH)it).findFirst().orElse(null);
   }

   public void loadEvents() {
      try {
         File eventsBase = new File(Foxtrot.getInstance().getDataFolder(), "events");
         if (!eventsBase.exists()) {
            eventsBase.mkdir();
         }

         for (EventType eventType : EventType.values()) {
            File subEventsBase = new File(eventsBase, eventType.name().toLowerCase());
            if (!subEventsBase.exists()) {
               subEventsBase.mkdir();
            }

            for (File eventFile : subEventsBase.listFiles()) {
               if (eventFile.getName().endsWith(".json")) {
                  this.events.add(Foxtrot.GSON.fromJson(FileUtils.readFileToString(eventFile), (Type)(eventType == EventType.KOTH ? KOTH.class : DTC.class)));
               }
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      this.events.stream().filter(Event::isActive).findFirst().ifPresent(event -> {
         event.setActive(false);
         Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            if (this.events.stream().noneMatch(Event::isActive)) {
               event.activate();
            }
         }, 300L);
      });
   }

   public void fillSchedule() {
      List<String> allevents = new ArrayList<>();

      for (Event event : this.getEvents()) {
         if (!event.isHidden()
            && !event.getName().equalsIgnoreCase("EOTW")
            && !event.getName().equalsIgnoreCase("Citadel")
            && !event.getName().equalsIgnoreCase("NetherCitadel")) {
            allevents.add(event.getName());
         }
      }

      for (int minute = 0; minute < 60; minute++) {
         for (int hour = 0; hour < 24; hour++) {
            this.EventSchedule
               .put(new EventScheduledTime(Calendar.getInstance().get(6), (Calendar.getInstance().get(11) + hour) % 24, minute), allevents.get(0));
         }
      }
   }

   public void loadSchedules() {
      this.EventSchedule.clear();

      try {
         File eventSchedule = new File(Foxtrot.getInstance().getDataFolder(), "eventSchedule.json");
         if (!eventSchedule.exists()) {
            eventSchedule.createNewFile();
            BasicDBObject schedule = new BasicDBObject();
            int currentDay = Calendar.getInstance().get(6);
            List<String> allevents = new ArrayList<>();

            for (Event event : this.getEvents()) {
               if (!event.isHidden()
                  && !event.getName().equalsIgnoreCase("EOTW")
                  && !event.getName().equalsIgnoreCase("Citadel")
                  && !event.getName().equalsIgnoreCase("NetherCitadel")
                  && !event.getName().contains("Outpost")
                  && (!(event instanceof KOTH) || !((KOTH)event).isMini())) {
                  allevents.add(event.getName());
               }
            }

            for (int dayOffset = 0; dayOffset < 21; dayOffset++) {
               int day = (currentDay + dayOffset) % 365;
               EventScheduledTime[] times = new EventScheduledTime[]{
                  new EventScheduledTime(day, 0, 0),
                  new EventScheduledTime(day, 1, 30),
                  new EventScheduledTime(day, 3, 0),
                  new EventScheduledTime(day, 4, 30),
                  new EventScheduledTime(day, 6, 0),
                  new EventScheduledTime(day, 7, 30),
                  new EventScheduledTime(day, 9, 0),
                  new EventScheduledTime(day, 10, 30),
                  new EventScheduledTime(day, 12, 0),
                  new EventScheduledTime(day, 13, 30),
                  new EventScheduledTime(day, 15, 0),
                  new EventScheduledTime(day, 16, 30),
                  new EventScheduledTime(day, 18, 0),
                  new EventScheduledTime(day, 19, 30),
                  new EventScheduledTime(day, 21, 0),
                  new EventScheduledTime(day, 22, 30)
               };
               if (Foxtrot.getInstance().getServerHandler().isAu()) {
                  times = new EventScheduledTime[]{
                     new EventScheduledTime(day, 0, 0),
                     new EventScheduledTime(day, 2, 0),
                     new EventScheduledTime(day, 4, 0),
                     new EventScheduledTime(day, 6, 0),
                     new EventScheduledTime(day, 8, 0),
                     new EventScheduledTime(day, 10, 0),
                     new EventScheduledTime(day, 12, 0),
                     new EventScheduledTime(day, 14, 0),
                     new EventScheduledTime(day, 16, 0),
                     new EventScheduledTime(day, 18, 0),
                     new EventScheduledTime(day, 20, 0),
                     new EventScheduledTime(day, 22, 0)
                  };
               }

               if (Foxtrot.getInstance().getServerHandler().isTeams()) {
                  times = new EventScheduledTime[]{
                     new EventScheduledTime(day, 12, 0),
                     new EventScheduledTime(day, 15, 0),
                     new EventScheduledTime(day, 18, 0),
                     new EventScheduledTime(day, 21, 0)
                  };
               }

               Collections.shuffle(allevents);
               if (!allevents.isEmpty()) {
                  for (int eventTimeIndex = 0; eventTimeIndex < times.length; eventTimeIndex++) {
                     EventScheduledTime eventTime = times[eventTimeIndex];
                     String eventName = allevents.get(eventTimeIndex % allevents.size());
                     schedule.put(eventTime.toString(), eventName);
                  }
               }
            }

            FileUtils.write(eventSchedule, Foxtrot.GSON.toJson(new JsonParser().parse(schedule.toString())));
         }

         BasicDBObject dbo = (BasicDBObject)JSON.parse(FileUtils.readFileToString(eventSchedule));
         if (dbo != null) {
            for (Entry<String, Object> entry : dbo.entrySet()) {
               EventScheduledTime scheduledTime = EventScheduledTime.parse(entry.getKey());
               this.EventSchedule.put(scheduledTime, String.valueOf(entry.getValue()));
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void saveEvents() {
      try {
         File eventsBase = new File(Foxtrot.getInstance().getDataFolder(), "events");
         if (!eventsBase.exists()) {
            eventsBase.mkdir();
         }

         for (EventType eventType : EventType.values()) {
            File subEventsBase = new File(eventsBase, eventType.name().toLowerCase());
            if (!subEventsBase.exists()) {
               subEventsBase.mkdir();
            }

            for (File eventFile : subEventsBase.listFiles()) {
               eventFile.delete();
            }
         }

         for (Event event : this.events) {
            File eventFile = new File(new File(eventsBase, event.getType().name().toLowerCase()), event.getName() + ".json");
            FileUtils.write(eventFile, Foxtrot.GSON.toJson(event));
            Bukkit.getLogger().info("Writing " + event.getName() + " to " + eventFile.getAbsolutePath());
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public Event getEvent(String name) {
      for (Event event : this.events) {
         if (event.getName().equalsIgnoreCase(name)) {
            return event;
         }
      }

      return null;
   }

   private void activateKOTHs() {
      if (!Foxtrot.getInstance().getServerHandler().isPreEOTW() && !CustomTimerCreateCommand.isSOTWTimer()) {
         for (Event koth : Foxtrot.getInstance().getEventHandler().getEvents()) {
            if (koth.isActive()) {
               return;
            }
         }

         EventScheduledTime scheduledTime = EventScheduledTime.parse(new Date());
         if (Foxtrot.getInstance().getEventHandler().getEventSchedule().containsKey(scheduledTime)) {
            String resolvedName = Foxtrot.getInstance().getEventHandler().getEventSchedule().get(scheduledTime);
            Event resolved = Foxtrot.getInstance().getEventHandler().getEvent(resolvedName);
            if (resolved == null) {
               Foxtrot.getInstance()
                  .getLogger()
                  .warning("The event scheduler has a schedule for an event named " + resolvedName + ", but the event does not exist.");
               return;
            }

            resolved.activate();
         }
      }
   }

   public Set<Event> getEvents() {
      return this.events;
   }

   public Map<EventScheduledTime, String> getEventSchedule() {
      return this.EventSchedule;
   }

   public boolean isScheduleEnabled() {
      return this.scheduleEnabled;
   }

   public void setScheduleEnabled(boolean scheduleEnabled) {
      this.scheduleEnabled = scheduleEnabled;
   }

   public MiniKOTHHandler getMiniKOTHHandler() {
      return this.miniKOTHHandler;
   }
}
