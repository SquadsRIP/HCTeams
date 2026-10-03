package net.frozenorb.foxtrot.util;

import cc.fyre.proton.cuboid.Cuboid.CuboidDirection;
import net.frozenorb.foxtrot.listener.PortalTrapListener;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class Portal {
   private PortalDirection direction;
   private cc.fyre.proton.cuboid.Cuboid portal;

   public Portal(Block block, PortalDirection direction) {
      if (block.getType() == Material.PORTAL) {
         this.direction = direction;
         if (direction != null) {
            if (direction == PortalDirection.NORTH_SOUTH) {
               int minX = block.getX();
               int maxX = block.getX();
               int minY = block.getY() - 1;
               int maxZ = Integer.MIN_VALUE;

               for (int i = 0; i < Integer.MAX_VALUE; i++) {
                  Block next = block.getRelative(BlockFace.SOUTH, i);
                  if (next.getType() != Material.PORTAL) {
                     maxZ = next.getRelative(BlockFace.SOUTH).getRelative(BlockFace.DOWN).getZ();
                     maxZ = maxZ < 0 ? maxZ + 1 : maxZ - 1;
                     break;
                  }
               }

               int minZ = Integer.MAX_VALUE;

               for (int i = 0; i < Integer.MAX_VALUE; i++) {
                  Block next = block.getRelative(BlockFace.NORTH, i);
                  if (next.getType() != Material.PORTAL) {
                     minZ = next.getRelative(BlockFace.NORTH).getRelative(BlockFace.DOWN).getZ();
                     minZ = minZ < 0 ? minZ - 1 : minZ + 1;
                     break;
                  }
               }

               int maxY = Integer.MIN_VALUE;

               for (int i = 0; i < Integer.MAX_VALUE; i++) {
                  Block next = block.getRelative(BlockFace.UP, i);
                  if (next.getType() != Material.PORTAL) {
                     maxY = next.getY();
                     break;
                  }
               }

               this.portal = new cc.fyre.proton.cuboid.Cuboid(block.getWorld(), minX, minY, minZ, maxX, maxY, maxZ);
            } else {
               int minZ = block.getZ();
               int maxZ = block.getZ();
               int minY = block.getY() - 1;
               int maxX = Integer.MIN_VALUE;

               for (int i = 0; i < Integer.MAX_VALUE; i++) {
                  Block next = block.getRelative(BlockFace.EAST, i);
                  if (next.getType() != Material.PORTAL) {
                     maxX = next.getRelative(BlockFace.EAST).getRelative(BlockFace.DOWN).getX();
                     maxX = maxX < 0 ? maxX + 1 : maxX - 1;
                     break;
                  }
               }

               int minX = Integer.MAX_VALUE;

               for (int i = 0; i < Integer.MAX_VALUE; i++) {
                  Block next = block.getRelative(BlockFace.WEST, i);
                  if (next.getType() != Material.PORTAL) {
                     minX = next.getRelative(BlockFace.WEST).getRelative(BlockFace.DOWN).getX();
                     minX = minX < 0 ? minX - 1 : minX + 1;
                     break;
                  }
               }

               int maxY = Integer.MIN_VALUE;

               for (int i = 0; i < Integer.MAX_VALUE; i++) {
                  Block next = block.getRelative(BlockFace.UP, i);
                  if (next.getType() != Material.PORTAL) {
                     maxY = next.getY();
                     break;
                  }
               }

               this.portal = new cc.fyre.proton.cuboid.Cuboid(block.getWorld(), minX, minY, minZ, maxX, maxY, maxZ);
            }
         }
      }
   }

   public void show(cc.fyre.proton.cuboid.Cuboid cuboid) {
      for (Block block : cuboid.corners()) {
         block.setType(Material.STONE);
      }
   }

   public void patchOverworld() {
      for (Block block : this.direction == PortalDirection.NORTH_SOUTH
         ? this.portal.expand(CuboidDirection.SOUTH, 1).expand(CuboidDirection.NORTH, 1)
         : this.portal.expand(CuboidDirection.EAST, 1).expand(CuboidDirection.WEST, 1)) {
         if (!this.portal.contains(block)) {
            for (BlockFace f : PortalTrapListener.FACES) {
               Block relative = block.getRelative(f);
               if (relative.getType() == Material.PORTAL && block.getType() != Material.AIR) {
                  Claim bClaim = LandBoard.getInstance().getClaim(block.getLocation());
                  if (bClaim == null || bClaim.contains(relative)) {
                     block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
                     block.setTypeIdAndData(Material.AIR.getId(), (byte)0, false);
                  }
                  break;
               }
            }
         }
      }
   }

   public void patchNether() {
      cc.fyre.proton.cuboid.Cuboid expanded = this.direction == PortalDirection.NORTH_SOUTH
         ? this.portal.expand(CuboidDirection.SOUTH, 4).expand(CuboidDirection.NORTH, 4).expand(CuboidDirection.EAST, 4).expand(CuboidDirection.WEST, 4)
         : this.portal.expand(CuboidDirection.EAST, 4).expand(CuboidDirection.WEST, 4).expand(CuboidDirection.SOUTH, 4).expand(CuboidDirection.NORTH, 4);
      cc.fyre.proton.cuboid.Cuboid down = expanded.getFace(CuboidDirection.DOWN);

      for (Block block : expanded) {
         if (!block.getChunk().isLoaded()) {
            block.getChunk().load();
         }

         if (!this.portal.contains(block) && block.getType() != Material.AIR & !down.contains(block)) {
            block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
            block.setTypeIdAndData(Material.AIR.getId(), (byte)0, false);
         }
      }

      for (Block block : down) {
         if (!block.getChunk().isLoaded()) {
            block.getChunk().load();
         }

         if (block.getType() != Material.OBSIDIAN) {
            block.setTypeIdAndData(Material.OBSIDIAN.getId(), (byte)0, false);
            block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
         }
      }
   }
}
