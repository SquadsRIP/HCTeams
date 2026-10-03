package net.frozenorb.foxtrot.gameplay.kitmap.gemflip.data;

import java.beans.ConstructorProperties;
import org.bukkit.Material;

public enum GemFlipSide {
   HEADS("Heads", "H", Material.DIAMOND),
   TAILS("Tails", "T", Material.EMERALD);

   private final String friendlyName;
   private final String abbreviation;
   private final Material material;

   public GemFlipSide getOpposite() {
      return this == HEADS ? TAILS : HEADS;
   }

   public String getFriendlyName() {
      return this.friendlyName;
   }

   public String getAbbreviation() {
      return this.abbreviation;
   }

   public Material getMaterial() {
      return this.material;
   }

   @ConstructorProperties({"friendlyName", "abbreviation", "material"})
   GemFlipSide(String friendlyName, String abbreviation, Material material) {
      this.friendlyName = friendlyName;
      this.abbreviation = abbreviation;
      this.material = material;
   }
}
