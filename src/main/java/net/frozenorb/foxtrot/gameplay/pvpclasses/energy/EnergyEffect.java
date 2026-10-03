package net.frozenorb.foxtrot.gameplay.pvpclasses.energy;

import java.beans.ConstructorProperties;
import net.frozenorb.foxtrot.util.PotionUtil;
import net.frozenorb.foxtrot.util.RomanUtil;
import org.bukkit.potion.PotionEffect;

public class EnergyEffect {
   private int energy;
   private PotionEffect potionEffect;

   public static EnergyEffect fromEnergy(int energy) {
      return new EnergyEffect(energy, null);
   }

   public static EnergyEffect fromPotion(PotionEffect potionEffect) {
      return new EnergyEffect(-1, potionEffect);
   }

   public static EnergyEffect fromPotionAndEnergy(PotionEffect potionEffect, int energy) {
      return new EnergyEffect(energy, potionEffect);
   }

   public String getFancyName() {
      return PotionUtil.getColor(this.potionEffect.getType())
         + PotionUtil.getName(this.potionEffect.getType())
         + " "
         + RomanUtil.toRoman(this.potionEffect.getAmplifier() + 1);
   }

   @ConstructorProperties({"energy", "potionEffect"})
   public EnergyEffect(int energy, PotionEffect potionEffect) {
      this.energy = energy;
      this.potionEffect = potionEffect;
   }

   public int getEnergy() {
      return this.energy;
   }

   public PotionEffect getPotionEffect() {
      return this.potionEffect;
   }
}
