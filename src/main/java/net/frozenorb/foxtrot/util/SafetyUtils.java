package net.frozenorb.foxtrot.util;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class SafetyUtils {
   private static ArrayList<String> passableBlocks = Lists.newArrayList("AIR", "LONG_GRASS", "DOUBLE_PLANT");

   private static boolean isSafe(Location location) {
      for (int i = 0; i <= 1; i++) {
         if (!isPassable(location.clone().add(0.0, i, 0.0).getBlock())) {
            return false;
         }
      }

      return true;
   }

   public static boolean isSafeInDirection(Location currentLocation, int length, Player player) {
      DirectionUtil direction = DirectionUtil.getDirection(player);
      Location location = currentLocation.clone();
      if (isSafe(location) && direction != null) {
         for (int i = 0; i <= length; i++) {
            if (!isSafe(getBlockInDirection(direction, location))) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static Location getBlockInDirection(DirectionUtil directionUtil, Location location) {
      Location newLocation = location.clone();
      switch (directionUtil) {
         case EAST:
            newLocation.add(1.0, 0.0, 0.0);
            break;
         case WEST:
            newLocation.add(-1.0, 0.0, 0.0);
            break;
         case NORTH:
            newLocation.add(0.0, 0.0, -1.0);
            break;
         case SOUTH:
            newLocation.add(0.0, 0.0, 1.0);
      }

      return newLocation;
   }

   public static List<Location> getSurroundingBlocks(Location location) {
      return Lists.newArrayList(
         location.clone().add(0.0, 0.0, 1.0),
         location.clone().add(1.0, 0.0, 1.0),
         location.clone().add(-1.0, 0.0, 1.0),
         location.clone().add(1.0, 0.0, 0.0),
         location.clone().add(-1.0, 0.0, 0.0),
         location.clone().add(0.0, 0.0, -1.0),
         location.clone().add(1.0, 0.0, -1.0),
         location.clone().add(-1.0, 0.0, -1.0)
      );
   }

   public static boolean isPassable(Block block) {
      return block == null || block.isLiquid() || block.getType() == Material.AIR || passableBlocks.contains(block.getType().name());
   }
}
