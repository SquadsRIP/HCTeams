package net.frozenorb.foxtrot.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class CuboidRegion implements Iterable<Location> {
   private int x1;
   private int x2;
   private int z1;
   private int z2;
   private int y1;
   private int y2;
   private String world;
   private String name;
   private ArrayList<String> tags = new ArrayList<>();

   public CuboidRegion(String name, Location corner1, Location corner2) {
      this(
         name,
         corner1.getWorld().getName(),
         corner1.getBlockX(),
         corner1.getBlockY(),
         corner1.getBlockZ(),
         corner2.getBlockX(),
         corner2.getBlockY(),
         corner2.getBlockZ()
      );
   }

   public CuboidRegion(String name, String world, int x1, int y1, int z1, int x2, int y2, int z2) {
      this.world = world;
      this.name = name;
      this.x1 = Math.min(x1, x2);
      this.x2 = Math.max(x1, x2);
      this.y1 = Math.min(y1, y2);
      this.y2 = Math.max(y1, y2);
      this.z1 = Math.min(z1, z2);
      this.z2 = Math.max(z1, z2);
   }

   public String getName() {
      return this.name;
   }

   public ArrayList<String> getTags() {
      return this.tags;
   }

   public void addTag(String tag) {
      this.tags.add(tag.toLowerCase());
   }

   public boolean hasTag(String name) {
      return this.tags.contains(name.toLowerCase());
   }

   public void removeTag(String tag) {
      this.tags.remove(tag.toLowerCase());
   }

   public Location getMinimumPoint() {
      return new Location(Bukkit.getWorld(this.world), Math.min(this.x1, this.x2), Math.min(this.y1, this.y2), Math.min(this.z1, this.z2));
   }

   public Location getMaximumPoint() {
      return new Location(Bukkit.getWorld(this.world), Math.max(this.x1, this.x2), Math.max(this.y1, this.y2), Math.max(this.z1, this.z2));
   }

   public void setName(String name) {
      this.name = name;
   }

   public boolean contains(int x, int y, int z) {
      return x >= this.x1 && x <= this.x2 && y >= this.y1 && y <= this.y2 && z >= this.z1 && z <= this.z2;
   }

   public boolean contains(Location l) {
      return !this.world.equals(l.getWorld().getName()) ? false : this.contains(l.getBlockX(), l.getBlockY(), l.getBlockZ());
   }

   public boolean contains(Block b) {
      return this.contains(b.getLocation());
   }

   public boolean contains(Player p) {
      return this.contains(p.getLocation());
   }

   public HashSet<Player> getPlayers() {
      HashSet<Player> players = new HashSet<>();

      for (Player p : Bukkit.getWorld(this.world).getPlayers()) {
         if (this.contains(p)) {
            players.add(p);
         }
      }

      return players;
   }

   public void setLocation(Location loc1, Location loc2) {
      this.setLocation(loc1.getWorld().getName(), loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ(), loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ());
   }

   public void setLocation(String world, int x1, int y1, int z1, int x2, int y2, int z2) {
      this.world = world;
      this.x1 = Math.min(x1, x2);
      this.x2 = Math.max(x1, x2);
      this.y1 = Math.min(y1, y2);
      this.y2 = Math.max(y1, y2);
      this.z1 = Math.min(z1, z2);
      this.z2 = Math.max(z1, z2);
   }

   @Override
   public int hashCode() {
      return this.name.hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof CuboidRegion ? ((CuboidRegion)obj).getName().equals(this.getName()) : super.equals(obj);
   }

   @Override
   public String toString() {
      return this.name;
   }

   @Override
   public Iterator<Location> iterator() {
      return new CuboidRegion.CuboidIterator(this.world, this.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
   }

   public class CuboidIterator implements Iterator<Location> {
      private int baseX;
      private int baseY;
      private int baseZ;
      private int x;
      private int y;
      private int z;
      private int sizeX;
      private int sizeY;
      private int sizeZ;
      private String world;

      public CuboidIterator(String world, int x1, int y1, int z1, int x2, int y2, int z2) {
         this.world = world;
         this.baseX = x1;
         this.baseY = y1;
         this.baseZ = z1;
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

      public Location next() {
         Location b = new Location(Bukkit.getWorld(this.world), this.baseX + this.x, this.baseY + this.y, this.baseZ + this.z);
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
