package net.frozenorb.foxtrot.gameplay.events.region.glowmtn;

import java.util.HashSet;
import java.util.Set;
import net.frozenorb.foxtrot.team.claims.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;

public class GlowMountain {
   private final Set<BlockVector> glowstone = new HashSet<>();
   private int remaining = 0;

   public void scan() {
      this.glowstone.clear();
      Claim claim = GlowHandler.getClaim();
      World world = Bukkit.getWorld(claim.getWorld());

      for (int x = claim.getX1(); x < claim.getX2(); x++) {
         for (int y = claim.getY1(); y < claim.getY2(); y++) {
            for (int z = claim.getZ1(); z < claim.getZ2(); z++) {
               Block block = world.getBlockAt(x, y, z);
               if (block.getType() == Material.GLOWSTONE) {
                  this.glowstone.add(block.getLocation().toVector().toBlockVector());
               }
            }
         }
      }

      this.remaining = this.glowstone.size();
   }

   public int reset() {
      int restored = 0;
      this.remaining = this.glowstone.size();
      World world = Bukkit.getWorld(GlowHandler.getClaim().getWorld());
      if (world == null) {
         return 0;
      }

      for (BlockVector vector : this.glowstone) {
         Block block = world.getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
         if (block.getType() != Material.GLOWSTONE) {
            block.setType(Material.GLOWSTONE);
            restored++;
         }
      }

      return restored;
   }

   public Set<BlockVector> getGlowstone() {
      return this.glowstone;
   }

   public int getRemaining() {
      return this.remaining;
   }

   public void setRemaining(int remaining) {
      this.remaining = remaining;
   }
}
