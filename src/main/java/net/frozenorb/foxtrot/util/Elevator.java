package net.frozenorb.foxtrot.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;

public enum Elevator {
   UP,
   DOWN;

   public Location getCalculatedLocation(Location location, Elevator.Type type) {
      if (this == UP) {
         for (int yLevel = location.getBlockY(); yLevel < 256; yLevel++) {
            if (location.getBlockY() != yLevel) {
               Location first = new Location(location.getWorld(), location.getX(), yLevel, location.getBlockZ());
               Location second = new Location(location.getWorld(), location.getX(), yLevel + 1, location.getBlockZ());
               if (type == Elevator.Type.MINE_CART && this.isValidForMineCart(first, second)) {
                  return new Location(location.getWorld(), location.getBlockX(), yLevel, location.getBlockZ());
               }

               if (type == Elevator.Type.CARPET && this.isValidForCarpet(this, first, second) == Elevator.Cause.ALLOWED) {
                  return new Location(location.getWorld(), location.getBlockX(), yLevel, location.getBlockZ());
               }

               if (type == Elevator.Type.SIGN && this.isValidForSign(this, first, second) == Elevator.Cause.ALLOWED) {
                  return new Location(location.getWorld(), location.getBlockX(), yLevel, location.getBlockZ());
               }
            }
         }

         return null;
      } else {
         for (int yLevel = location.getBlockY(); yLevel > 0; yLevel--) {
            if (location.getBlockY() != yLevel) {
               Location first = new Location(location.getWorld(), location.getX(), yLevel, location.getBlockZ());
               Location second = new Location(location.getWorld(), location.getX(), yLevel + 1, location.getBlockZ());
               if (type == Elevator.Type.MINE_CART && this.isValidForMineCart(first, second)) {
                  return first;
               }

               if (type == Elevator.Type.CARPET && this.isValidForCarpet(DOWN, first, second) == Elevator.Cause.ALLOWED) {
                  return first;
               }

               if (type == Elevator.Type.SIGN && this.isValidForSign(DOWN, first, second) == Elevator.Cause.ALLOWED) {
                  return first;
               }
            }
         }

         return null;
      }
   }

   private boolean isValidForMineCart(Location first, Location second) {
      return first.getBlock().getType() == Material.AIR && second.getBlock().getType() == Material.AIR
         || first.getBlock().getType() == Material.FENCE_GATE && second.getBlock().getType() == Material.FENCE_GATE;
   }

   private Elevator.Cause isValidForCarpet(Elevator direction, Location first, Location second) {
      if (second.getBlock().getType() != null && second.getBlock().getType() != Material.AIR) {
         return Elevator.Cause.SECOND_BLOCK_NOT_AIR;
      }

      if (first.getBlock().getType() != Material.CARPET && first.getBlock().getType() == Material.AIR) {
         return Elevator.Cause.ALLOWED;
      }

      if (first.getBlock().getType() != Material.CARPET) {
         return Elevator.Cause.FIRST_BLOCK_NOT_REQUIRED_ITEM;
      }

      byte data = 0;
      if (direction == UP) {
         data = 14;
      } else if (direction == DOWN) {
         data = 13;
      }

      return first.getBlock().getData() != data ? Elevator.Cause.INVALID_DIRECTION : Elevator.Cause.ALLOWED;
   }

   private Elevator.Cause isValidForSign(Elevator elevator, Location first, Location second) {
      if (second.getBlock().getType() != null && second.getBlock().getType() != Material.AIR) {
         return Elevator.Cause.SECOND_BLOCK_NOT_AIR;
      } else if (!first.getBlock().getType().name().contains("SIGN") && first.getBlock().getType() == Material.AIR) {
         return Elevator.Cause.ALLOWED;
      } else if (!first.getBlock().getType().name().contains("SIGN")) {
         return Elevator.Cause.FIRST_BLOCK_NOT_REQUIRED_ITEM;
      } else {
         Sign sign = (Sign)first.getBlock().getState();
         if (!sign.getLine(0).contains("Elevator")) {
            return Elevator.Cause.NOT_ELEVATOR;
         } else {
            return (elevator == UP ? sign.getLine(1).equalsIgnoreCase("Down") : sign.getLine(1).equalsIgnoreCase("Up"))
               ? Elevator.Cause.ALLOWED
               : Elevator.Cause.INVALID_DIRECTION;
         }
      }
   }

   private enum Cause {
      FIRST_BLOCK_NOT_REQUIRED_ITEM,
      SECOND_BLOCK_NOT_AIR,
      NOT_ELEVATOR,
      INVALID_DIRECTION,
      ALLOWED;
   }

   public enum Type {
      SIGN,
      CARPET,
      MINE_CART;
   }
}
