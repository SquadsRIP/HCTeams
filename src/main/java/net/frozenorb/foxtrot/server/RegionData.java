package net.frozenorb.foxtrot.server;

import java.beans.ConstructorProperties;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RegionData {
   private RegionType regionType;
   private Team data;

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof RegionData)) {
         return false;
      }

      RegionData other = (RegionData)obj;
      return other.regionType == this.regionType && (this.data == null || other.data.equals(this.data));
   }

   @Override
   public int hashCode() {
      return super.hashCode();
   }

   public String getName(Player player) {
      if (this.data == null) {
         switch (this.regionType) {
            case WARZONE:
               return ChatColor.RED + "WarZone";
            case WILDNERNESS:
               return ChatColor.GRAY + "The Wilderness";
            default:
               return ChatColor.RED + "N/A";
         }
      } else {
         return this.data.getName(player);
      }
   }

   @ConstructorProperties({"regionType", "data"})
   public RegionData(RegionType regionType, Team data) {
      this.regionType = regionType;
      this.data = data;
   }

   public RegionType getRegionType() {
      return this.regionType;
   }

   public Team getData() {
      return this.data;
   }

   public void setRegionType(RegionType regionType) {
      this.regionType = regionType;
   }

   public void setData(Team data) {
      this.data = data;
   }

   @Override
   public String toString() {
      return "RegionData(regionType=" + this.getRegionType() + ", data=" + this.getData() + ")";
   }
}
