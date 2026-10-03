package net.frozenorb.foxtrot.util;

import java.beans.ConstructorProperties;

public class Logout {
   private final int taskId;
   private final long logoutTime;

   public int getTaskId() {
      return this.taskId;
   }

   public long getLogoutTime() {
      return this.logoutTime;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof Logout)) {
         return false;
      } else {
         Logout other = (Logout)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            return this.getTaskId() != other.getTaskId() ? false : this.getLogoutTime() == other.getLogoutTime();
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof Logout;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      result = result * 59 + this.getTaskId();
      long $logoutTime = this.getLogoutTime();
      return result * 59 + (int)($logoutTime >>> 32 ^ $logoutTime);
   }

   @Override
   public String toString() {
      return "Logout(taskId=" + this.getTaskId() + ", logoutTime=" + this.getLogoutTime() + ")";
   }

   @ConstructorProperties({"taskId", "logoutTime"})
   public Logout(int taskId, long logoutTime) {
      this.taskId = taskId;
      this.logoutTime = logoutTime;
   }
}
