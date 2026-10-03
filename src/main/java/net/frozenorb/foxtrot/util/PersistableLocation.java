package net.frozenorb.foxtrot.util;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.util.com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class PersistableLocation implements ConfigurationSerializable, Cloneable {
   private Location location;
   private World world;
   private String worldName;
   private double x;
   private double y;
   private double z;
   private float yaw;
   private float pitch;

   public PersistableLocation(Location location) {
      Preconditions.checkNotNull(location, "Location cannot be null");
      Preconditions.checkNotNull(location.getWorld(), "Locations' world cannot be null");
      this.world = location.getWorld();
      this.worldName = this.world.getName();
      this.x = location.getX();
      this.y = location.getY();
      this.z = location.getZ();
      this.yaw = location.getYaw();
      this.pitch = location.getPitch();
   }

   public PersistableLocation(World world, double x, double y, double z) {
      this.worldName = world.getName();
      this.x = x;
      this.y = y;
      this.z = z;
      this.yaw = 0.0F;
      this.pitch = 0.0F;
   }

   public PersistableLocation(String worldName, double x, double y, double z) {
      this.worldName = worldName;
      this.x = x;
      this.y = y;
      this.z = z;
      this.yaw = 0.0F;
      this.pitch = 0.0F;
   }

   public PersistableLocation(Map<String, Object> map) {
      this.worldName = (String)map.get("worldName");
      Object o = map.get("x");
      this.x = o instanceof String ? Double.parseDouble((String)o) : (Double)o;
      o = map.get("y");
      this.y = o instanceof String ? Double.parseDouble((String)o) : (Double)o;
      o = map.get("z");
      this.z = o instanceof String ? Double.parseDouble((String)o) : (Double)o;
      this.yaw = Float.parseFloat((String)map.get("yaw"));
      this.pitch = Float.parseFloat((String)map.get("pitch"));
   }

   @Override
   public Map<String, Object> serialize() {
      LinkedHashMap<String, Object> map = new LinkedHashMap<>();
      map.put("worldName", this.worldName);
      map.put("x", this.x);
      map.put("y", this.y);
      map.put("z", this.z);
      map.put("yaw", Float.toString(this.yaw));
      map.put("pitch", Float.toString(this.pitch));
      return map;
   }

   public World getWorld() {
      Preconditions.checkNotNull(this.worldName, "World name cannot be null");
      this.world = Bukkit.getWorld(this.worldName);
      return this.world;
   }

   public void setWorld(World world) {
      this.worldName = world.getName();
      this.world = world;
   }

   public Location getLocation() {
      this.location = new Location(this.getWorld(), this.x, this.y, this.z, this.yaw, this.pitch);
      return this.location;
   }

   public PersistableLocation clone() throws CloneNotSupportedException {
      try {
         return (PersistableLocation)super.clone();
      } catch (CloneNotSupportedException ex) {
         ex.printStackTrace();
         throw new RuntimeException();
      }
   }

   public String getWorldName() {
      return this.worldName;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public float getYaw() {
      return this.yaw;
   }

   public float getPitch() {
      return this.pitch;
   }

   public void setLocation(Location location) {
      this.location = location;
   }

   public void setWorldName(String worldName) {
      this.worldName = worldName;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      }

      if (!(o instanceof PersistableLocation)) {
         return false;
      }

      PersistableLocation other = (PersistableLocation)o;
      if (!other.canEqual(this)) {
         return false;
      }

      Location this$location = this.getLocation();
      Location other$location = other.getLocation();
      if (this$location == null ? other$location == null : this$location.equals(other$location)) {
         World this$world = this.getWorld();
         World other$world = other.getWorld();
         if (this$world == null ? other$world == null : this$world.equals(other$world)) {
            String this$worldName = this.getWorldName();
            String other$worldName = other.getWorldName();
            if (this$worldName == null ? other$worldName == null : this$worldName.equals(other$worldName)) {
               if (Double.compare(this.getX(), other.getX()) != 0) {
                  return false;
               } else if (Double.compare(this.getY(), other.getY()) != 0) {
                  return false;
               } else if (Double.compare(this.getZ(), other.getZ()) != 0) {
                  return false;
               } else {
                  return Float.compare(this.getYaw(), other.getYaw()) != 0 ? false : Float.compare(this.getPitch(), other.getPitch()) == 0;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof PersistableLocation;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Location $location = this.getLocation();
      result = result * 59 + ($location == null ? 43 : $location.hashCode());
      World $world = this.getWorld();
      result = result * 59 + ($world == null ? 43 : $world.hashCode());
      String $worldName = this.getWorldName();
      result = result * 59 + ($worldName == null ? 43 : $worldName.hashCode());
      long $x = Double.doubleToLongBits(this.getX());
      result = result * 59 + (int)($x >>> 32 ^ $x);
      long $y = Double.doubleToLongBits(this.getY());
      result = result * 59 + (int)($y >>> 32 ^ $y);
      long $z = Double.doubleToLongBits(this.getZ());
      result = result * 59 + (int)($z >>> 32 ^ $z);
      result = result * 59 + Float.floatToIntBits(this.getYaw());
      return result * 59 + Float.floatToIntBits(this.getPitch());
   }

   @Override
   public String toString() {
      return "PersistableLocation(location="
         + this.getLocation()
         + ", world="
         + this.getWorld()
         + ", worldName="
         + this.getWorldName()
         + ", x="
         + this.getX()
         + ", y="
         + this.getY()
         + ", z="
         + this.getZ()
         + ", yaw="
         + this.getYaw()
         + ", pitch="
         + this.getPitch()
         + ")";
   }

   public void setX(double x) {
      this.x = x;
   }

   public void setY(double y) {
      this.y = y;
   }

   public void setZ(double z) {
      this.z = z;
   }

   public void setYaw(float yaw) {
      this.yaw = yaw;
   }

   public void setPitch(float pitch) {
      this.pitch = pitch;
   }
}
