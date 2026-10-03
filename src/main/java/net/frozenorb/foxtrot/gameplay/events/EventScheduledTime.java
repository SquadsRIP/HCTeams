package net.frozenorb.foxtrot.gameplay.events;

import java.beans.ConstructorProperties;
import java.util.Calendar;
import java.util.Date;
import net.minecraft.util.com.google.common.base.Objects;
import net.minecraft.util.com.google.common.primitives.Ints;

public class EventScheduledTime implements Comparable<EventScheduledTime> {
   private int day;
   private int hour;
   private int minutes;

   public static EventScheduledTime parse(String input) {
      String[] inputSplit = input.split(" ");
      int days = Integer.parseInt(inputSplit[0]);
      String[] timeSplit = inputSplit[1].split(":");
      int hour = Integer.parseInt(timeSplit[0]);
      int minutes = timeSplit.length > 1 ? Integer.parseInt(timeSplit[1]) : 0;
      return new EventScheduledTime(days, hour, minutes);
   }

   public static EventScheduledTime parse(Date input) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(input);
      int days = calendar.get(6);
      int hour = calendar.get(11);
      int minutes = calendar.get(12);
      return new EventScheduledTime(days, hour, minutes);
   }

   public Date toDate() {
      Calendar activationTime = Calendar.getInstance();
      activationTime.set(6, this.day);
      activationTime.set(11, this.hour);
      activationTime.set(12, this.minutes);
      activationTime.set(13, 0);
      activationTime.set(14, 0);
      return activationTime.getTime();
   }

   @Override
   public boolean equals(Object object) {
      if (!(object instanceof EventScheduledTime)) {
         return false;
      }

      EventScheduledTime other = (EventScheduledTime)object;
      return other.day == this.day && other.hour == this.hour && other.minutes == this.minutes;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(new Object[]{this.day, this.hour, this.minutes});
   }

   public int compareTo(EventScheduledTime other) {
      int result = Ints.compare(this.day, other.day);
      if (result == 0) {
         result = Ints.compare(this.hour, other.hour);
      }

      if (result == 0) {
         result = Ints.compare(this.minutes, other.minutes);
      }

      return result;
   }

   @Override
   public String toString() {
      return this.day + " " + this.hour + ":" + this.minutes;
   }

   @ConstructorProperties({"day", "hour", "minutes"})
   public EventScheduledTime(int day, int hour, int minutes) {
      this.day = day;
      this.hour = hour;
      this.minutes = minutes;
   }

   public int getDay() {
      return this.day;
   }

   public int getHour() {
      return this.hour;
   }

   public int getMinutes() {
      return this.minutes;
   }
}
