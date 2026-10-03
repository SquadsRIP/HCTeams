package net.frozenorb.foxtrot.util;

public class NumberUtil {
   public static String getOrdinal(Integer number) {
      char lastNumber = number.toString().charAt(number.toString().length() - 1);
      if (lastNumber == 1 || number == 1) {
         return number + "st";
      } else if (lastNumber == 2) {
         return number + "nd";
      } else {
         return lastNumber == 3 ? number + "rd" : number + "th";
      }
   }
}
