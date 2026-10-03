package net.frozenorb.foxtrot.gameplay.events.region.cavern;

import java.util.Map;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.minecraft.util.com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Cavern {
   private final Map<String, String> ores = Maps.newHashMap();
   private int remaining = 0;

   public void scan() {
      this.ores.clear();
      Claim claim = CavernHandler.getClaim();
      World world = Bukkit.getWorld(claim.getWorld());

      for (int x = claim.getX1(); x <= claim.getX2(); x++) {
         for (int y = claim.getY1(); y <= claim.getY2(); y++) {
            for (int z = claim.getZ1(); z <= claim.getZ2(); z++) {
               Block block = world.getBlockAt(x, y, z);
               if (this.isOre(block.getType())) {
                  this.ores.put(toString(block.getLocation()), block.getType().name());
               }
            }
         }
      }
   }

   public void reset() {
      World world = Bukkit.getWorld(CavernHandler.getClaim().getWorld());

      for (String location : this.ores.keySet()) {
         getBlockAt(world, location).setType(Material.valueOf(this.ores.get(location)));
      }
   }

   public static String toString(Location location) {
      return Integer.toString(location.getBlockX()) + ',' + location.getBlockY() + ',' + location.getBlockZ();
   }

   public static Block getBlockAt(World world, String location) {
      String[] xyz = location.split(",");
      return world.getBlockAt(Integer.parseInt(xyz[0]), Integer.parseInt(xyz[1]), Integer.parseInt(xyz[2]));
   }

   private boolean isOre(Material type) {
      return type == Material.DIAMOND_ORE
         || type == Material.EMERALD_ORE
         || type == Material.COAL_ORE
         || type == Material.IRON_ORE
         || type == Material.REDSTONE_ORE
         || type == Material.GLOWING_REDSTONE_ORE
         || type == Material.LAPIS_ORE
         || type == Material.GOLD_ORE;
   }

   public Map<String, String> getOres() {
      return this.ores;
   }

   public int getRemaining() {
      return this.remaining;
   }

   public void setRemaining(int remaining) {
      this.remaining = remaining;
   }
}
