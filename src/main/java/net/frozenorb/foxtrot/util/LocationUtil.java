package net.frozenorb.foxtrot.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.util.BlockVector;

public class LocationUtil {
   public static Location fromVector(World world, BlockVector vector) {
      return vector == null ? null : new Location(world, vector.getX(), vector.getY(), vector.getZ());
   }

   public static String realmName(String worldName) {
      if (worldName == null) {
         return "Unknown";
      } else if (worldName.contains("MiniKOTH")) {
         return "Mini KOTH";
      } else {
         World world = Bukkit.getWorld(worldName);
         if (world == null) {
            return worldName;
         } else if (world.getEnvironment() == Environment.NETHER) {
            return "Nether";
         } else {
            return world.getEnvironment() == Environment.THE_END ? "End" : "Overworld";
         }
      }
   }

   public static String realmName(World world) {
      return world == null ? "Unknown" : realmName(world.getName());
   }
}
