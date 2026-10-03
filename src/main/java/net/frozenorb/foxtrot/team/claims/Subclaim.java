package net.frozenorb.foxtrot.team.claims;

import cc.fyre.proton.serialization.LocationSerializer;
import cc.fyre.proton.util.UUIDUtils;
import com.mongodb.BasicDBObject;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import net.minecraft.util.com.google.common.base.Joiner;
import org.bukkit.Location;

public class Subclaim {
   @NonNull
   private Location loc1;
   @NonNull
   private Location loc2;
   @NonNull
   private String name;
   private List<UUID> members = new ArrayList<>();

   public static Subclaim fromJson(BasicDBObject obj) {
      Subclaim c = new Subclaim(
         LocationSerializer.deserialize((BasicDBObject)obj.get("Location1")),
         LocationSerializer.deserialize((BasicDBObject)obj.get("Location2")),
         obj.getString("Name")
      );
      c.setMembers(new ArrayList<>());
      return c;
   }

   public void addMember(UUID member) {
      this.members.add(member);
   }

   public boolean isMember(UUID check) {
      return this.members.contains(check);
   }

   public void removeMember(UUID member) {
      this.members.remove(member);
   }

   public BasicDBObject json() {
      BasicDBObject dbObject = new BasicDBObject();
      dbObject.put("Name", this.name);
      dbObject.put("Members", UUIDUtils.uuidsToStrings(this.members));
      dbObject.put("Location1", LocationSerializer.serialize(this.loc1));
      dbObject.put("Location2", LocationSerializer.serialize(this.loc2));
      return dbObject;
   }

   @Override
   public String toString() {
      return this.loc1.getBlockX()
         + ":"
         + this.loc1.getBlockY()
         + ":"
         + this.loc1.getBlockZ()
         + ":"
         + this.loc2.getBlockX()
         + ":"
         + this.loc2.getBlockY()
         + ":"
         + this.loc2.getBlockZ()
         + ":"
         + this.name
         + ":"
         + Joiner.on(", ").join(this.members);
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      }

      if (!(o instanceof Subclaim)) {
         return false;
      }

      Subclaim other = (Subclaim)o;
      if (!other.canEqual(this)) {
         return false;
      }

      Object this$loc1 = this.getLoc1();
      Object other$loc1 = other.getLoc1();
      if (this$loc1 == null ? other$loc1 == null : this$loc1.equals(other$loc1)) {
         Object this$loc2 = this.getLoc2();
         Object other$loc2 = other.getLoc2();
         if (this$loc2 == null ? other$loc2 == null : this$loc2.equals(other$loc2)) {
            Object this$name = this.getName();
            Object other$name = other.getName();
            if (this$name == null ? other$name == null : this$name.equals(other$name)) {
               Object this$members = this.getMembers();
               Object other$members = other.getMembers();
               return this$members == null ? other$members == null : this$members.equals(other$members);
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
      return other instanceof Subclaim;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $loc1 = this.getLoc1();
      result = result * 59 + ($loc1 == null ? 43 : $loc1.hashCode());
      Object $loc2 = this.getLoc2();
      result = result * 59 + ($loc2 == null ? 43 : $loc2.hashCode());
      Object $name = this.getName();
      result = result * 59 + ($name == null ? 43 : $name.hashCode());
      Object $members = this.getMembers();
      return result * 59 + ($members == null ? 43 : $members.hashCode());
   }

   @ConstructorProperties({"loc1", "loc2", "name"})
   public Subclaim(@NonNull Location loc1, @NonNull Location loc2, @NonNull String name) {
      if (loc1 == null) {
         throw new NullPointerException("loc1");
      }

      if (loc2 == null) {
         throw new NullPointerException("loc2");
      }

      if (name == null) {
         throw new NullPointerException("name");
      }

      this.loc1 = loc1;
      this.loc2 = loc2;
      this.name = name;
   }

   @NonNull
   public Location getLoc1() {
      return this.loc1;
   }

   @NonNull
   public Location getLoc2() {
      return this.loc2;
   }

   @NonNull
   public String getName() {
      return this.name;
   }

   public List<UUID> getMembers() {
      return this.members;
   }

   public void setLoc1(@NonNull Location loc1) {
      if (loc1 == null) {
         throw new NullPointerException("loc1");
      }

      this.loc1 = loc1;
   }

   public void setLoc2(@NonNull Location loc2) {
      if (loc2 == null) {
         throw new NullPointerException("loc2");
      }

      this.loc2 = loc2;
   }

   public void setName(@NonNull String name) {
      if (name == null) {
         throw new NullPointerException("name");
      }

      this.name = name;
   }

   public void setMembers(List<UUID> members) {
      this.members = members;
   }
}
