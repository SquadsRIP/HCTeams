package net.frozenorb.foxtrot.gameplay.totem;

import java.beans.ConstructorProperties;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffectType;

public class Totem {
   private UUID placedBy;
   private Location location;
   private PotionEffectType potionEffectType;
   private long placedAt;
   private long expiresAt;
   private int hitsRemaining;

   @ConstructorProperties({"placedBy", "location", "potionEffectType", "placedAt", "expiresAt", "hitsRemaining"})
   public Totem(UUID placedBy, Location location, PotionEffectType potionEffectType, long placedAt, long expiresAt, int hitsRemaining) {
      this.placedBy = placedBy;
      this.location = location;
      this.potionEffectType = potionEffectType;
      this.placedAt = placedAt;
      this.expiresAt = expiresAt;
      this.hitsRemaining = hitsRemaining;
   }

   public UUID getPlacedBy() {
      return this.placedBy;
   }

   public Location getLocation() {
      return this.location;
   }

   public void setLocation(Location location) {
      this.location = location;
   }

   public PotionEffectType getPotionEffectType() {
      return this.potionEffectType;
   }

   public void setPotionEffectType(PotionEffectType potionEffectType) {
      this.potionEffectType = potionEffectType;
   }

   public long getPlacedAt() {
      return this.placedAt;
   }

   public void setPlacedAt(long placedAt) {
      this.placedAt = placedAt;
   }

   public long getExpiresAt() {
      return this.expiresAt;
   }

   public void setExpiresAt(long expiresAt) {
      this.expiresAt = expiresAt;
   }

   public int getHitsRemaining() {
      return this.hitsRemaining;
   }

   public void setHitsRemaining(int hitsRemaining) {
      this.hitsRemaining = hitsRemaining;
   }
}
