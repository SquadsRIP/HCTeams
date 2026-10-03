package net.frozenorb.foxtrot.util;

import java.beans.ConstructorProperties;
import org.bukkit.potion.PotionEffectType;

public class EffectSnapshot {
   private PotionEffectType effectType;
   private int amplifier;
   private int duration;

   @ConstructorProperties({"effectType", "amplifier", "duration"})
   public EffectSnapshot(PotionEffectType effectType, int amplifier, int duration) {
      this.effectType = effectType;
      this.amplifier = amplifier;
      this.duration = duration;
   }

   public PotionEffectType getEffectType() {
      return this.effectType;
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public int getDuration() {
      return this.duration;
   }

   public void setEffectType(PotionEffectType effectType) {
      this.effectType = effectType;
   }

   public void setAmplifier(int amplifier) {
      this.amplifier = amplifier;
   }

   public void setDuration(int duration) {
      this.duration = duration;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof EffectSnapshot)) {
         return false;
      } else {
         EffectSnapshot other = (EffectSnapshot)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            PotionEffectType this$effectType = this.getEffectType();
            PotionEffectType other$effectType = other.getEffectType();
            if (this$effectType == null ? other$effectType == null : this$effectType.equals(other$effectType)) {
               return this.getAmplifier() != other.getAmplifier() ? false : this.getDuration() == other.getDuration();
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof EffectSnapshot;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      PotionEffectType $effectType = this.getEffectType();
      result = result * 59 + ($effectType == null ? 43 : $effectType.hashCode());
      result = result * 59 + this.getAmplifier();
      return result * 59 + this.getDuration();
   }

   @Override
   public String toString() {
      return "EffectSnapshot(effectType=" + this.getEffectType() + ", amplifier=" + this.getAmplifier() + ", duration=" + this.getDuration() + ")";
   }
}
