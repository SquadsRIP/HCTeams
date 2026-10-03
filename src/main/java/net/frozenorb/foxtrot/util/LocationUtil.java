package net.frozenorb.foxtrot.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BlockVector;

public class LocationUtil {
   public static Location fromVector(World world, BlockVector vector) {
      return vector == null ? null : new Location(world, vector.getX(), vector.getY(), vector.getZ());
   }
}
