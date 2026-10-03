package net.frozenorb.foxtrot.util;

import org.bukkit.entity.Player;

public enum DirectionUtil {
   NORTH(0),
   SOUTH(180),
   EAST(90),
   WEST(270);

   private int degrees;

   DirectionUtil(int degrees) {
      this.degrees = degrees;
   }

   public int getDegrees() {
      return this.degrees;
   }

   public static DirectionUtil getDirection(Player player) {
      float yaw = player.getLocation().getYaw();
      if (yaw < 0.0F) {
         yaw += 360.0F;
      }

      if (yaw >= 315.0F || yaw < 45.0F) {
         return SOUTH;
      } else if (yaw < 135.0F) {
         return WEST;
      } else if (yaw < 225.0F) {
         return NORTH;
      } else {
         return yaw < 315.0F ? EAST : SOUTH;
      }
   }
}
