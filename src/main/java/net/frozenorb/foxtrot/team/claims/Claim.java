package net.frozenorb.foxtrot.team.claims;

import cc.fyre.proton.serialization.LocationSerializer;
import com.mongodb.BasicDBObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class Claim implements Iterable<Coordinate> {
   private String world;
   private int x1;
   private int y1;
   private int z1;
   private int x2;
   private int y2;
   private int z2;
   private String name;

   public static Claim fromJson(BasicDBObject obj) {
      Claim c = new Claim(
         LocationSerializer.deserialize((BasicDBObject)obj.get("Location1")), LocationSerializer.deserialize((BasicDBObject)obj.get("Location2"))
      );
      c.setName(obj.getString("Name"));
      return c;
   }

   public Claim(Location corner1, Location corner2) {
      this(
         corner1.getWorld().getName(),
         corner1.getBlockX(),
         corner1.getBlockY(),
         corner1.getBlockZ(),
         corner2.getBlockX(),
         corner2.getBlockY(),
         corner2.getBlockZ()
      );
   }

   public Claim(Claim copyFrom) {
      this.world = copyFrom.world;
      this.x1 = copyFrom.x1;
      this.y1 = copyFrom.y1;
      this.z1 = copyFrom.z1;
      this.x2 = copyFrom.x2;
      this.y2 = copyFrom.y2;
      this.z2 = copyFrom.z2;
      this.name = copyFrom.name;
   }

   public Claim(String world, int x1, int y1, int z1, int x2, int y2, int z2) {
      this.world = world;
      this.x1 = Math.min(x1, x2);
      this.x2 = Math.max(x1, x2);
      this.y1 = Math.min(y1, y2);
      this.y2 = Math.max(y1, y2);
      this.z1 = Math.min(z1, z2);
      this.z2 = Math.max(z1, z2);
   }

   public BasicDBObject json() {
      BasicDBObject dbObject = new BasicDBObject();
      dbObject.put("Name", this.name);
      World world = Foxtrot.getInstance().getServer().getWorld(this.getWorld());
      dbObject.put("Location1", LocationSerializer.serialize(new Location(world, this.x1, this.y1, this.z1)));
      dbObject.put("Location2", LocationSerializer.serialize(new Location(world, this.x2, this.y2, this.z2)));
      return dbObject;
   }

   public static int getPrice(Claim claim, Team team, boolean buying) {
      int x = Math.abs(claim.x1 - claim.x2);
      int z = Math.abs(claim.z1 - claim.z2);
      int blocks = x * z;
      int done = 0;
      double mod = 0.4;
      double curPrice = 0.0;

      while (blocks > 0) {
         blocks--;
         done++;
         curPrice += mod;
         if (done == 250) {
            done = 0;
            mod += 0.4;
         }
      }

      curPrice *= 0.8F;
      if (buying && team != null) {
         curPrice += 500 * team.getClaims().size();
      }

      return (int)curPrice;
   }

   @Override
   public boolean equals(Object object) {
      if (!(object instanceof Claim)) {
         return false;
      }

      Claim claim = (Claim)object;
      return claim.getMaximumPoint().equals(this.getMaximumPoint()) && claim.getMinimumPoint().equals(this.getMinimumPoint());
   }

   public Location getMinimumPoint() {
      return new Location(
         Foxtrot.getInstance().getServer().getWorld(this.world), Math.min(this.x1, this.x2), Math.min(this.y1, this.y2), Math.min(this.z1, this.z2)
      );
   }

   public Location getMaximumPoint() {
      return new Location(
         Foxtrot.getInstance().getServer().getWorld(this.world), Math.max(this.x1, this.x2), Math.max(this.y1, this.y2), Math.max(this.z1, this.z2)
      );
   }

   public boolean contains(int x, int y, int z, String world) {
      return y >= this.y1 && y <= this.y2 && this.contains(x, z, world);
   }

   public boolean contains(int x, int z, String world) {
      return world != null && !world.equalsIgnoreCase(this.world) ? false : x >= this.x1 && x <= this.x2 && z >= this.z1 && z <= this.z2;
   }

   public boolean contains(Location location) {
      return this.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getName());
   }

   public boolean contains(Block block) {
      return this.contains(block.getLocation());
   }

   public boolean contains(Player player) {
      return this.contains(player.getLocation());
   }

   public Set<Player> getPlayers() {
      Set<Player> players = new HashSet<>();

      for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
         if (this.contains(player)) {
            players.add(player);
         }
      }

      return players;
   }

   @Override
   public int hashCode() {
      return this.getMaximumPoint().hashCode() + this.getMinimumPoint().hashCode();
   }

   @Override
   public String toString() {
      Location corner1 = this.getMinimumPoint();
      Location corner2 = this.getMaximumPoint();
      return corner1.getBlockX()
         + ":"
         + corner1.getBlockY()
         + ":"
         + corner1.getBlockZ()
         + ":"
         + corner2.getBlockX()
         + ":"
         + corner2.getBlockY()
         + ":"
         + corner2.getBlockZ()
         + ":"
         + this.name
         + ":"
         + this.world;
   }

   public String getFriendlyName() {
      return "("
         + this.world
         + ", "
         + this.x1
         + ", "
         + this.y1
         + ", "
         + this.z1
         + ") - ("
         + this.world
         + ", "
         + this.x2
         + ", "
         + this.y2
         + ", "
         + this.z2
         + ")";
   }

   public Claim expand(Claim.CuboidDirection dir, int amount) {
      switch (dir) {
         case North:
            return new Claim(this.world, this.x1 - amount, this.y1, this.z1, this.x2, this.y2, this.z2);
         case South:
            return new Claim(this.world, this.x1, this.y1, this.z1, this.x2 + amount, this.y2, this.z2);
         case East:
            return new Claim(this.world, this.x1, this.y1, this.z1 - amount, this.x2, this.y2, this.z2);
         case West:
            return new Claim(this.world, this.x1, this.y1, this.z1, this.x2, this.y2, this.z2 + amount);
         case Down:
            return new Claim(this.world, this.x1, this.y1 - amount, this.z1, this.x2, this.y2, this.z2);
         case Up:
            return new Claim(this.world, this.x1, this.y1, this.z1, this.x2, this.y2 + amount, this.z2);
         default:
            throw new IllegalArgumentException("Invalid direction " + dir);
      }
   }

   public Claim outset(Claim.CuboidDirection dir, int amount) {
      Claim claim;
      switch (dir) {
         case Horizontal:
            claim = this.expand(Claim.CuboidDirection.North, amount)
               .expand(Claim.CuboidDirection.South, amount)
               .expand(Claim.CuboidDirection.East, amount)
               .expand(Claim.CuboidDirection.West, amount);
            break;
         case Vertical:
            claim = this.expand(Claim.CuboidDirection.Down, amount).expand(Claim.CuboidDirection.Up, amount);
            break;
         case Both:
            claim = this.outset(Claim.CuboidDirection.Horizontal, amount).outset(Claim.CuboidDirection.Vertical, amount);
            break;
         default:
            throw new IllegalArgumentException("Invalid direction " + dir);
      }

      return claim;
   }

   public boolean isWithin(int x, int z, int radius, String world) {
      return this.outset(Claim.CuboidDirection.Both, radius).contains(x, z, world);
   }

   public void setLocations(Location loc1, Location loc2) {
      this.x1 = Math.min(loc1.getBlockX(), loc2.getBlockX());
      this.x2 = Math.max(loc1.getBlockX(), loc2.getBlockX());
      this.y1 = Math.min(loc1.getBlockY(), loc2.getBlockY());
      this.y2 = Math.max(loc1.getBlockY(), loc2.getBlockY());
      this.z1 = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
      this.z2 = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
   }

   public Location[] getCornerLocations() {
      World world = Foxtrot.getInstance().getServer().getWorld(this.world);
      return new Location[]{
         new Location(world, this.x1, this.y1, this.z1),
         new Location(world, this.x2, this.y1, this.z2),
         new Location(world, this.x1, this.y1, this.z2),
         new Location(world, this.x2, this.y1, this.z1)
      };
   }

   @Override
   public Iterator<Coordinate> iterator() {
      return new Claim.BorderIterator(this.x1, this.z1, this.x2, this.z2);
   }

   public void setWorld(String world) {
      this.world = world;
   }

   public void setX1(int x1) {
      this.x1 = x1;
   }

   public void setY1(int y1) {
      this.y1 = y1;
   }

   public void setZ1(int z1) {
      this.z1 = z1;
   }

   public void setX2(int x2) {
      this.x2 = x2;
   }

   public void setY2(int y2) {
      this.y2 = y2;
   }

   public void setZ2(int z2) {
      this.z2 = z2;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Claim() {
   }

   public String getWorld() {
      return this.world;
   }

   public int getX1() {
      return this.x1;
   }

   public int getY1() {
      return this.y1;
   }

   public int getZ1() {
      return this.z1;
   }

   public int getX2() {
      return this.x2;
   }

   public int getY2() {
      return this.y2;
   }

   public int getZ2() {
      return this.z2;
   }

   public String getName() {
      return this.name;
   }

   public enum BorderDirection {
      POS_X,
      POS_Z,
      NEG_X,
      NEG_Z;
   }

   public class BorderIterator implements Iterator<Coordinate> {
      private int x;
      private int z;
      private boolean next = true;
      private Claim.BorderDirection dir = Claim.BorderDirection.POS_Z;
      int maxX = Claim.this.getMaximumPoint().getBlockX();
      int maxZ = Claim.this.getMaximumPoint().getBlockZ();
      int minX = Claim.this.getMinimumPoint().getBlockX();
      int minZ = Claim.this.getMinimumPoint().getBlockZ();

      public BorderIterator(int x1, int z1, int x2, int z2) {
         this.x = Math.min(x1, x2);
         this.z = Math.min(z1, z2);
      }

      @Override
      public boolean hasNext() {
         return this.next;
      }

      public Coordinate next() {
         if (this.dir == Claim.BorderDirection.POS_Z) {
            if (++this.z == this.maxZ) {
               this.dir = Claim.BorderDirection.POS_X;
            }
         } else if (this.dir == Claim.BorderDirection.POS_X) {
            if (++this.x == this.maxX) {
               this.dir = Claim.BorderDirection.NEG_Z;
            }
         } else if (this.dir == Claim.BorderDirection.NEG_Z) {
            if (--this.z == this.minZ) {
               this.dir = Claim.BorderDirection.NEG_X;
            }
         } else if (this.dir == Claim.BorderDirection.NEG_X && --this.x == this.minX) {
            this.next = false;
         }

         return new Coordinate(this.x, this.z);
      }

      @Override
      public void remove() {
      }
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
   }
}
