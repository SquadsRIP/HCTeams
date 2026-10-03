package net.frozenorb.foxtrot.util;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.block.Block;

public final class BlockUtil {
   public static List<Block> getBlocksAroundCenter(Location loc, int radius) {
      ArrayList<Block> blocks = new ArrayList<>();

      for (int x = loc.getBlockX() - radius; x <= loc.getBlockX() + radius; x++) {
         for (int y = loc.getBlockY() - radius; y <= loc.getBlockY() + radius; y++) {
            for (int z = loc.getBlockZ() - radius; z <= loc.getBlockZ() + radius; z++) {
               Location l = new Location(loc.getWorld(), x, y, z);
               if (l.distance(loc) <= radius) {
                  blocks.add(l.getBlock());
               }
            }
         }
      }

      return blocks;
   }

   private BlockUtil() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
