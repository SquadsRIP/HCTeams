package net.frozenorb.foxtrot.gameplay.events.koth.minikoth;

import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.EventHandler;
import net.frozenorb.foxtrot.gameplay.events.EventScheduledTime;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.minecraft.util.org.apache.commons.io.FileUtils;

public class MiniKOTHHandler {
   private final Map<EventScheduledTime, String> EventSchedule = new TreeMap<>();
   private boolean scheduleEnabled;

   public List<Event> findMiniKOTHs() {
      return Foxtrot.getInstance()
         .getEventHandler()
         .getEvents()
         .stream()
         .filter(it -> it instanceof KOTH)
         .map(it -> (KOTH)it)
         .filter(KOTH::isMini)
         .collect(Collectors.toList());
   }

   public void loadSchedules() {
      this.EventSchedule.clear();

      try {
         File eventSchedule = new File(Foxtrot.getInstance().getDataFolder(), "minieventSchedule.json");
         if (!eventSchedule.exists()) {
            eventSchedule.createNewFile();
            BasicDBObject schedule = new BasicDBObject();
            int currentDay = Calendar.getInstance().get(6);
            ArrayList<String> allevents = new ArrayList<>();

            for (Event event : this.findMiniKOTHs()) {
               if (!event.isHidden()
                  && !event.getName().equalsIgnoreCase("EOTW")
                  && !event.getName().equalsIgnoreCase("Citadel")
                  && !event.getName().equalsIgnoreCase("NetherCitadel")) {
                  allevents.add(event.getName());
               }
            }

            for (int dayOffset = 0; dayOffset < 21; dayOffset++) {
               int day = (currentDay + dayOffset) % 365;
               EventScheduledTime[] times = new EventScheduledTime[]{new EventScheduledTime(day, 14, 0), new EventScheduledTime(day, 19, 0)};
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

         BasicDBObject dbo;
         if ((dbo = (BasicDBObject)JSON.parse(FileUtils.readFileToString(eventSchedule))) != null) {
            for (Entry entry : dbo.entrySet()) {
               EventScheduledTime scheduledTime = EventScheduledTime.parse((String)entry.getKey());
               this.EventSchedule.put(scheduledTime, String.valueOf(entry.getValue()));
            }
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private void activateKOTHs() {
      if (!Foxtrot.getInstance().getServerHandler().isPreEOTW() && !CustomTimerCreateCommand.isSOTWTimer()) {
         EventHandler eventHandler = Foxtrot.getInstance().getEventHandler();
         if (eventHandler != null) {
            for (Event koth : eventHandler.getEvents()) {
               if (koth.isActive()) {
                  return;
               }
            }

            EventScheduledTime scheduledTime = EventScheduledTime.parse(new Date());
            if (this.EventSchedule.containsKey(scheduledTime)) {
               String resolvedName = this.EventSchedule.get(scheduledTime);
               Event resolved = eventHandler.getEvent(resolvedName);
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
}
