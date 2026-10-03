package net.frozenorb.foxtrot.gameplay.kitmap.game.arena.select;

import cc.fyre.proton.cuboid.Cuboid;
import cc.fyre.proton.util.ItemBuilder;
import java.beans.ConstructorProperties;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class Selection {
   public static final ItemStack SELECTION_WAND = ItemBuilder.of(Material.GOLD_AXE).name("&aSelection Wand").build();
   public static final String SELECTION_METADATA_KEY = "selection";
   private Location loc1;
   private Location loc2;

   private Selection() {
   }

   public Cuboid getCuboid() {
      return !this.isComplete() ? null : new Cuboid(this.loc1, this.loc2);
   }

   public boolean isComplete() {
      return this.loc1 != null && this.loc2 != null;
   }

   public static Selection getOrCreateSelection(Player player) {
      if (player.hasMetadata("selection")) {
         return (Selection)player.getMetadata("selection").get(0).value();
      }

      Selection selection = new Selection();
      player.setMetadata("selection", new FixedMetadataValue(Foxtrot.getInstance(), selection));
      return selection;
   }

   public Location getLoc1() {
      return this.loc1;
   }

   public Location getLoc2() {
      return this.loc2;
   }

   public void setLoc1(Location loc1) {
      this.loc1 = loc1;
   }

   public void setLoc2(Location loc2) {
      this.loc2 = loc2;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof Selection)) {
         return false;
      } else {
         Selection other = (Selection)o;
         if (!other.canEqual(this)) {
            return false;
         } else {
            Location this$loc1 = this.getLoc1();
            Location other$loc1 = other.getLoc1();
            if (this$loc1 == null ? other$loc1 == null : this$loc1.equals(other$loc1)) {
               Location this$loc2 = this.getLoc2();
               Location other$loc2 = other.getLoc2();
               return this$loc2 == null ? other$loc2 == null : this$loc2.equals(other$loc2);
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof Selection;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Location $loc1 = this.getLoc1();
      result = result * 59 + ($loc1 == null ? 43 : $loc1.hashCode());
      Location $loc2 = this.getLoc2();
      return result * 59 + ($loc2 == null ? 43 : $loc2.hashCode());
   }

   @Override
   public String toString() {
      return "Selection(loc1=" + this.getLoc1() + ", loc2=" + this.getLoc2() + ")";
   }

   @ConstructorProperties({"loc1", "loc2"})
   public Selection(Location loc1, Location loc2) {
      this.loc1 = loc1;
      this.loc2 = loc2;
   }
}
