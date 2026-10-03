package net.frozenorb.foxtrot.server.pearl;

import java.util.List;
import net.minecraft.server.v1_7_R4.AxisAlignedBB;
import net.minecraft.server.v1_7_R4.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

/**
 * The server jar's EntityEnderPearl treats slabs, stairs, chests and similar blocks as "pass-through" when it picks the
 * teleport spot, and only checks a 0.55-high box around the pearl. So a pearl thrown at a slab or a crit spot can land
 * the player with their 1.8-high hitbox inside a wall, and the client then pushes them out on the far side.
 *
 * This keeps the destination only if the player's hitbox fits there. Otherwise it tries a small step up (landing on a
 * slab or stair) or step down (head under a ceiling), then backs off along the pearl's path towards the thrower.
 * Returns null when no spot fits, and the pearl is cancelled.
 */
public final class PearlLandingFixer {
   private static final double HALF_WIDTH = 0.3 - 0.001;
   private static final double HEIGHT = 1.8 - 0.002;
   private static final double MAX_STEP_UP = 0.5;
   private static final double MAX_STEP_DOWN = 1.3;
   private static final double BACK_OFF_STEP = 0.25;
   private static final double BACK_OFF_MAX = 2.0;

   private PearlLandingFixer() {
   }

   public static Location findLanding(Player player, Location from, Location to) {
      if (to.getWorld() == null) {
         return null;
      }

      World world = ((CraftWorld)to.getWorld()).getHandle();
      Vector velocity = pearlVelocity(player, from, to);
      boolean rising = velocity.getY() > 0.05;
      Vector back = velocity.clone().multiply(-1);
      if (back.lengthSquared() < 1.0E-6) {
         back = new Vector(0, 0, 0);
      } else {
         back.normalize();
      }

      double maxBackOff = Math.min(BACK_OFF_MAX, to.getWorld().equals(from.getWorld()) ? to.distance(from) : 0.0);

      for (double dist = 0.0; dist <= maxBackOff + 1.0E-6; dist += BACK_OFF_STEP) {
         Location candidate = to.clone().add(back.clone().multiply(dist));
         Double y = fitAt(world, candidate.getX(), candidate.getY(), candidate.getZ(), rising);
         if (y != null) {
            candidate.setY(y);
            return candidate;
         }

         if (back.lengthSquared() == 0.0) {
            break;
         }
      }

      return null;
   }

   private static Double fitAt(World world, double x, double y, double z, boolean rising) {
      List<?> hits = collisions(world, x, y, z);
      if (hits.isEmpty()) {
         return y;
      }

      double maxTop = Double.NEGATIVE_INFINITY;
      double minBottom = Double.POSITIVE_INFINITY;

      for (Object hit : hits) {
         AxisAlignedBB box = (AxisAlignedBB)hit;
         maxTop = Math.max(maxTop, box.e);
         minBottom = Math.min(minBottom, box.b);
      }

      // Only the feet are blocked (slab, stair, snow): stand on top of it. Not for a rising pearl, which would
      // otherwise lift the player through a ceiling slab it entered from below.
      if (!rising && maxTop - y <= MAX_STEP_UP) {
         double up = maxTop + 0.001;
         if (collisions(world, x, up, z).isEmpty()) {
            return up;
         }
      }

      // Only the head is blocked: drop down until it fits. The lowered hitbox covers the whole range it moved
      // through, so this never crosses a block.
      double down = minBottom - HEIGHT - 0.002;
      if (y - down <= MAX_STEP_DOWN && collisions(world, x, down, z).isEmpty()) {
         return down;
      }

      return null;
   }

   private static List<?> collisions(World world, double x, double y, double z) {
      AxisAlignedBB box = AxisAlignedBB.a(x - HALF_WIDTH, y + 0.001, z - HALF_WIDTH, x + HALF_WIDTH, y + HEIGHT, z + HALF_WIDTH);
      return world.a(box);
   }

   private static Vector pearlVelocity(Player player, Location from, Location to) {
      for (MetadataValue value : player.getMetadata("LastEnderPearl")) {
         if (value.value() instanceof EnderPearl) {
            EnderPearl pearl = (EnderPearl)value.value();
            Vector velocity = pearl.getVelocity();
            if (velocity.lengthSquared() > 1.0E-6) {
               return velocity;
            }
         }
      }

      return to.toVector().subtract(from.toVector());
   }
}
