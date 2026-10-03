package net.frozenorb.foxtrot.spongetrader;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

public final class TraderPosition {
   private final UUID worldId;
   private final double x;
   private final double y;
   private final double z;
   private final float yaw;

   public TraderPosition(Location location) {
      this(location.getWorld().getUID(), location.getX(), location.getY(), location.getZ(), location.getYaw());
   }

   private TraderPosition(UUID worldId, double x, double y, double z, float yaw) {
      if (Double.isFinite(x)
         && Double.isFinite(y)
         && Double.isFinite(z)
         && Float.isFinite(yaw)
         && !(Math.abs(x) > 3.0E7)
         && !(Math.abs(z) > 3.0E7)
         && !(y < 0.0)
         && !(y >= 256.0)) {
         this.worldId = worldId;
         this.x = x;
         this.y = y;
         this.z = z;
         this.yaw = yaw;
      } else {
         throw new IllegalArgumentException("Invalid sponge trader coordinates");
      }
   }

   public static TraderPosition read(ConfigurationSection section) {
      if (section == null) {
         return null;
      }

      for (String key : new String[]{"x", "y", "z", "yaw"}) {
         if (!section.isSet(key) || !(section.get(key) instanceof Number)) {
            throw new IllegalArgumentException("Missing or invalid trader coordinate: " + key);
         }
      }

      return new TraderPosition(
         UUID.fromString(section.getString("world", "")),
         section.getDouble("x"),
         section.getDouble("y"),
         section.getDouble("z"),
         (float)section.getDouble("yaw")
      );
   }

   public void write(ConfigurationSection section) {
      section.set("world", this.worldId.toString());
      section.set("x", this.x);
      section.set("y", this.y);
      section.set("z", this.z);
      section.set("yaw", this.yaw);
   }

   public boolean matches(World world, int chunkX, int chunkZ) {
      return world.getUID().equals(this.worldId) && chunkX == (int)Math.floor(this.x) >> 4 && chunkZ == (int)Math.floor(this.z) >> 4;
   }

   public Location resolve() {
      World world = Bukkit.getWorld(this.worldId);
      return world == null ? null : new Location(world, this.x, this.y, this.z, this.yaw, 0.0F);
   }
}
