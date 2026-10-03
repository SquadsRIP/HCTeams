package net.frozenorb.foxtrot.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Cuboid implements Iterable<Block>, Cloneable, ConfigurationSerializable {
   protected final String worldName;
   protected final int x1;
   protected final int y1;
   protected final int z1;
   protected final int x2;
   protected final int y2;
   protected final int z2;

   public Cuboid(Location l1, Location l2) {
      if (!l1.getWorld().equals(l2.getWorld())) {
         throw new IllegalArgumentException("Locations must be on the same world");
      }

      this.worldName = l1.getWorld().getName();
      this.x1 = Math.min(l1.getBlockX(), l2.getBlockX());
      this.y1 = Math.min(l1.getBlockY(), l2.getBlockY());
      this.z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
      this.x2 = Math.max(l1.getBlockX(), l2.getBlockX());
      this.y2 = Math.max(l1.getBlockY(), l2.getBlockY());
      this.z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());
   }

   public Cuboid(Location l1) {
      this(l1, l1);
   }

   public Cuboid(Cuboid other) {
      this(other.getWorld().getName(), other.x1, other.y1, other.z1, other.x2, other.y2, other.z2);
   }

   public Cuboid(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
      this.worldName = world.getName();
      this.x1 = Math.min(x1, x2);
      this.x2 = Math.max(x1, x2);
      this.y1 = Math.min(y1, y2);
      this.y2 = Math.max(y1, y2);
      this.z1 = Math.min(z1, z2);
      this.z2 = Math.max(z1, z2);
   }

   private Cuboid(String worldName, int x1, int y1, int z1, int x2, int y2, int z2) {
      this.worldName = worldName;
      this.x1 = Math.min(x1, x2);
      this.x2 = Math.max(x1, x2);
      this.y1 = Math.min(y1, y2);
      this.y2 = Math.max(y1, y2);
      this.z1 = Math.min(z1, z2);
      this.z2 = Math.max(z1, z2);
   }

   public Cuboid(Map<String, Object> map) {
      this.worldName = (String)map.get("worldName");
      this.x1 = (Integer)map.get("x1");
      this.x2 = (Integer)map.get("x2");
      this.y1 = (Integer)map.get("y1");
      this.y2 = (Integer)map.get("y2");
      this.z1 = (Integer)map.get("z1");
      this.z2 = (Integer)map.get("z2");
   }

   @Override
   public Map<String, Object> serialize() {
      HashMap<String, Object> map = new HashMap<>();
      map.put("worldName", this.worldName);
      map.put("x1", this.x1);
      map.put("y1", this.y1);
      map.put("z1", this.z1);
      map.put("x2", this.x2);
      map.put("y2", this.y2);
      map.put("z2", this.z2);
      return map;
   }

   public Location getLowerNE() {
      return new Location(this.getWorld(), this.x1, this.y1, this.z1);
   }

   public Location getUpperSW() {
      return new Location(this.getWorld(), this.x2, this.y2, this.z2);
   }

   public List<Block> getBlocks() {
      Iterator<Block> blockI = this.iterator();
      ArrayList<Block> copy = new ArrayList<>();

      while (blockI.hasNext()) {
         copy.add(blockI.next());
      }

      return copy;
   }

   public Location getCenter() {
      int x1 = this.getUpperX() + 1;
      int y1 = this.getUpperY() + 1;
      int z1 = this.getUpperZ() + 1;
      return new Location(
         this.getWorld(),
         this.getLowerX() + (x1 - this.getLowerX()) / 2.0,
         this.getLowerY() + (y1 - this.getLowerY()) / 2.0,
         this.getLowerZ() + (z1 - this.getLowerZ()) / 2.0
      );
   }

   public World getWorld() {
      World world = Bukkit.getWorld(this.worldName);
      if (world == null) {
         throw new IllegalStateException("World '" + this.worldName + "' is not loaded");
      } else {
         return world;
      }
   }

   public int getSizeX() {
      return this.x2 - this.x1 + 1;
   }

   public int getSizeY() {
      return this.y2 - this.y1 + 1;
   }

   public int getSizeZ() {
      return this.z2 - this.z1 + 1;
   }

   public int getLowerX() {
      return this.x1;
   }

   public int getLowerY() {
      return this.y1;
   }

   public int getLowerZ() {
      return this.z1;
   }

   public int getUpperX() {
      return this.x2;
   }

   public int getUpperY() {
      return this.y2;
   }

   public int getUpperZ() {
      return this.z2;
   }

   public Block[] corners() {
      Block[] res = new Block[8];
      World w = this.getWorld();
      res[0] = w.getBlockAt(this.x1, this.y1, this.z1);
      res[1] = w.getBlockAt(this.x1, this.y1, this.z2);
      res[2] = w.getBlockAt(this.x1, this.y2, this.z1);
      res[3] = w.getBlockAt(this.x1, this.y2, this.z2);
      res[4] = w.getBlockAt(this.x2, this.y1, this.z1);
      res[5] = w.getBlockAt(this.x2, this.y1, this.z2);
      res[6] = w.getBlockAt(this.x2, this.y2, this.z1);
      res[7] = w.getBlockAt(this.x2, this.y2, this.z2);
      return res;
   }

   public Cuboid expand(Cuboid.CuboidDirection dir, int amount) {
      switch (dir) {
         case North:
            return new Cuboid(this.worldName, this.x1 - amount, this.y1, this.z1, this.x2, this.y2, this.z2);
         case East:
            return new Cuboid(this.worldName, this.x1, this.y1, this.z1 - amount, this.x2, this.y2, this.z2);
         case South:
            return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2 + amount, this.y2, this.z2);
         case West:
            return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, this.z2 + amount);
         case Up:
            return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2 + amount, this.z2);
         case Down:
            return new Cuboid(this.worldName, this.x1, this.y1 - amount, this.z1, this.x2, this.y2, this.z2);
         default:
            throw new IllegalArgumentException("Invalid direction " + dir);
      }
   }

   public Cuboid shift(Cuboid.CuboidDirection dir, int amount) {
      return this.expand(dir, amount).expand(dir.opposite(), -amount);
   }

   public Cuboid outset(Cuboid.CuboidDirection dir, int amount) {
      Cuboid c;
      switch (dir) {
         case Horizontal:
            c = this.expand(Cuboid.CuboidDirection.North, amount)
               .expand(Cuboid.CuboidDirection.South, amount)
               .expand(Cuboid.CuboidDirection.East, amount)
               .expand(Cuboid.CuboidDirection.West, amount);
            break;
         case Vertical:
            c = this.expand(Cuboid.CuboidDirection.Down, amount).expand(Cuboid.CuboidDirection.Up, amount);
            break;
         case Both:
            c = this.outset(Cuboid.CuboidDirection.Horizontal, amount).outset(Cuboid.CuboidDirection.Vertical, amount);
            break;
         default:
            throw new IllegalArgumentException("Invalid direction " + dir);
      }

      return c;
   }

   public Cuboid inset(Cuboid.CuboidDirection dir, int amount) {
      return this.outset(dir, -amount);
   }

   public boolean contains(int x, int y, int z) {
      return x >= this.x1 && x <= this.x2 && y >= this.y1 && y <= this.y2 && z >= this.z1 && z <= this.z2;
   }

   public boolean contains(Block b) {
      return this.contains(b.getLocation());
   }

   public boolean contains(Location l) {
      return !this.worldName.equals(l.getWorld().getName()) ? false : this.contains(l.getBlockX(), l.getBlockY(), l.getBlockZ());
   }

   public int getVolume() {
      return this.getSizeX() * this.getSizeY() * this.getSizeZ();
   }

   public byte getAverageLightLevel() {
      long total = 0L;
      int n = 0;

      for (Block b : this) {
         if (b.isEmpty()) {
            total += b.getLightLevel();
            n++;
         }
      }

      return n > 0 ? (byte)(total / n) : 0;
   }

   public Cuboid contract() {
      return this.contract(Cuboid.CuboidDirection.Down)
         .contract(Cuboid.CuboidDirection.South)
         .contract(Cuboid.CuboidDirection.East)
         .contract(Cuboid.CuboidDirection.Up)
         .contract(Cuboid.CuboidDirection.North)
         .contract(Cuboid.CuboidDirection.West);
   }

   public Cuboid contract(Cuboid.CuboidDirection dir) {
      Cuboid face = this.getFace(dir.opposite());
      switch (dir) {
         case North:
            while (face.containsOnly(0) && face.getLowerX() > this.getLowerX()) {
               face = face.shift(Cuboid.CuboidDirection.North, 1);
            }

            return new Cuboid(this.worldName, this.x1, this.y1, this.z1, face.getUpperX(), this.y2, this.z2);
         case East:
            while (face.containsOnly(0) && face.getLowerZ() > this.getLowerZ()) {
               face = face.shift(Cuboid.CuboidDirection.East, 1);
            }

            return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, face.getUpperZ());
         case South:
            while (face.containsOnly(0) && face.getUpperX() < this.getUpperX()) {
               face = face.shift(Cuboid.CuboidDirection.South, 1);
            }

            return new Cuboid(this.worldName, face.getLowerX(), this.y1, this.z1, this.x2, this.y2, this.z2);
         case West:
            while (face.containsOnly(0) && face.getUpperZ() < this.getUpperZ()) {
               face = face.shift(Cuboid.CuboidDirection.West, 1);
            }

            return new Cuboid(this.worldName, this.x1, this.y1, face.getLowerZ(), this.x2, this.y2, this.z2);
         case Up:
            while (face.containsOnly(0) && face.getUpperY() < this.getUpperY()) {
               face = face.shift(Cuboid.CuboidDirection.Up, 1);
            }

            return new Cuboid(this.worldName, this.x1, face.getLowerY(), this.z1, this.x2, this.y2, this.z2);
         case Down:
            while (face.containsOnly(0) && face.getLowerY() > this.getLowerY()) {
               face = face.shift(Cuboid.CuboidDirection.Down, 1);
            }

            return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, face.getUpperY(), this.z2);
         default:
            throw new IllegalArgumentException("Invalid direction " + dir);
      }
   }

   public Cuboid getFace(Cuboid.CuboidDirection dir) {
      switch (dir) {
         case North:
            return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x1, this.y2, this.z2);
         case East:
            return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, this.z1);
         case South:
            return new Cuboid(this.worldName, this.x2, this.y1, this.z1, this.x2, this.y2, this.z2);
         case West:
            return new Cuboid(this.worldName, this.x1, this.y1, this.z2, this.x2, this.y2, this.z2);
         case Up:
            return new Cuboid(this.worldName, this.x1, this.y2, this.z1, this.x2, this.y2, this.z2);
         case Down:
            return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y1, this.z2);
         default:
            throw new IllegalArgumentException("Invalid direction " + dir);
      }
   }

   public boolean containsOnly(int blockId) {
      for (Block b : this) {
         if (b.getTypeId() != blockId) {
            return false;
         }
      }

      return true;
   }

   public Cuboid getBoundingCuboid(Cuboid other) {
      if (other == null) {
         return this;
      }

      int xMin = Math.min(this.getLowerX(), other.getLowerX());
      int yMin = Math.min(this.getLowerY(), other.getLowerY());
      int zMin = Math.min(this.getLowerZ(), other.getLowerZ());
      int xMax = Math.max(this.getUpperX(), other.getUpperX());
      int yMax = Math.max(this.getUpperY(), other.getUpperY());
      int zMax = Math.max(this.getUpperZ(), other.getUpperZ());
      return new Cuboid(this.worldName, xMin, yMin, zMin, xMax, yMax, zMax);
   }

   public Block getRelativeBlock(int x, int y, int z) {
      return this.getWorld().getBlockAt(this.x1 + x, this.y1 + y, this.z1 + z);
   }

   public Block getRelativeBlock(World w, int x, int y, int z) {
      return w.getBlockAt(this.x1 + x, this.y1 + y, this.z1 + z);
   }

   public List<Chunk> getChunks() {
      ArrayList<Chunk> res = new ArrayList<>();
      World w = this.getWorld();
      int x1 = this.getLowerX() & -16;
      int x2 = this.getUpperX() & -16;
      int z1 = this.getLowerZ() & -16;
      int z2 = this.getUpperZ() & -16;

      for (int x = x1; x <= x2; x += 16) {
         for (int z = z1; z <= z2; z += 16) {
            res.add(w.getChunkAt(x >> 4, z >> 4));
         }
      }

      return res;
   }

   @Override
   public Iterator<Block> iterator() {
      return new Cuboid.CuboidIterator(this.getWorld(), this.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
   }

   public Cuboid clone() {
      return new Cuboid(this);
   }

   @Override
   public String toString() {
      return new String("Cuboid: " + this.worldName + "," + this.x1 + "," + this.y1 + "," + this.z1 + "=>" + this.x2 + "," + this.y2 + "," + this.z2);
   }

   public List<Block> getWalls() {
      ArrayList<Block> blocks = new ArrayList<>();
      Location min = new Location(this.getWorld(), this.x1, this.y1, this.z1);
      Location max = new Location(this.getWorld(), this.x2, this.y2, this.z2);
      int minX = min.getBlockX();
      int minY = min.getBlockY();
      int minZ = min.getBlockZ();
      int maxX = max.getBlockX();
      int maxY = max.getBlockY();
      int maxZ = max.getBlockZ();

      for (int x = minX; x <= maxX; x++) {
         for (int y = minY; y <= maxY; y++) {
            Location minLoc = new Location(this.getWorld(), x, y, minZ);
            Location maxLoc = new Location(this.getWorld(), x, y, maxZ);
            blocks.add(minLoc.getBlock());
            blocks.add(maxLoc.getBlock());
         }
      }

      for (int y = minY; y <= maxY; y++) {
         for (int z = minZ; z <= maxZ; z++) {
            Location minLoc = new Location(this.getWorld(), minX, y, z);
            Location maxLoc = new Location(this.getWorld(), maxX, y, z);
            blocks.add(minLoc.getBlock());
            blocks.add(maxLoc.getBlock());
         }
      }

      return blocks;
   }

   public List<Block> getFaces() {
      ArrayList<Block> blocks = new ArrayList<>();
      Location min = new Location(this.getWorld(), this.x1, this.y1, this.z1);
      Location max = new Location(this.getWorld(), this.x2, this.y2, this.z2);
      int minX = min.getBlockX();
      int minY = min.getBlockY();
      int minZ = min.getBlockZ();
      int maxX = max.getBlockX();
      int maxY = max.getBlockY();
      int maxZ = max.getBlockZ();

      for (int x = minX; x <= maxX; x++) {
         for (int y = minY; y <= maxY; y++) {
            blocks.add(new Location(this.getWorld(), x, y, minZ).getBlock());
            blocks.add(new Location(this.getWorld(), x, y, maxZ).getBlock());
         }
      }

      for (int y = minY; y <= maxY; y++) {
         for (int z = minZ; z <= maxZ; z++) {
            blocks.add(new Location(this.getWorld(), minX, y, z).getBlock());
            blocks.add(new Location(this.getWorld(), maxX, y, z).getBlock());
         }
      }

      for (int z = minZ; z <= maxZ; z++) {
         for (int x = minX; x <= maxX; x++) {
            blocks.add(new Location(this.getWorld(), x, minY, z).getBlock());
            blocks.add(new Location(this.getWorld(), x, maxY, z).getBlock());
         }
      }

      return blocks;
   }

   public enum CuboidDirection {
      North,
      East,
      South,
      West,
      Up,
      Down,
      Horizontal,
      Vertical,
      Both,
      Unknown;

      public Cuboid.CuboidDirection opposite() {
         switch (this) {
            case North:
               return South;
            case East:
               return West;
            case South:
               return North;
            case West:
               return East;
            case Up:
               return Down;
            case Down:
               return Up;
            case Horizontal:
               return Vertical;
            case Vertical:
               return Horizontal;
            case Both:
               return Both;
            default:
               return Unknown;
         }
      }
   }

   public class CuboidIterator implements Iterator<Block> {
      private World w;
      private int baseX;
      private int baseY;
      private int baseZ;
      private int x;
      private int y;
      private int z;
      private int sizeX;
      private int sizeY;
      private int sizeZ;

      public CuboidIterator(World w, int x1, int y1, int z1, int x2, int y2, int z2) {
         this.w = w;
         this.baseX = Math.min(x1, x2);
         this.baseY = Math.min(y1, y2);
         this.baseZ = Math.min(z1, z2);
         this.sizeX = Math.abs(x2 - x1) + 1;
         this.sizeY = Math.abs(y2 - y1) + 1;
         this.sizeZ = Math.abs(z2 - z1) + 1;
         this.z = 0;
         this.y = 0;
         this.x = 0;
      }

      @Override
      public boolean hasNext() {
         return this.x < this.sizeX && this.y < this.sizeY && this.z < this.sizeZ;
      }

      public Block next() {
         Block b = this.w.getBlockAt(this.baseX + this.x, this.baseY + this.y, this.baseZ + this.z);
         if (++this.x >= this.sizeX) {
            this.x = 0;
            if (++this.y >= this.sizeY) {
               this.y = 0;
               this.z++;
            }
         }

         return b;
      }

      @Override
      public void remove() {
      }
   }
}
