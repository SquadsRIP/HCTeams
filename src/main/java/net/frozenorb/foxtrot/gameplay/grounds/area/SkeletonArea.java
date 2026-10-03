package net.frozenorb.foxtrot.gameplay.grounds.area;

import java.util.Collections;
import java.util.List;
import net.frozenorb.foxtrot.gameplay.grounds.GroundsArea;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

public class SkeletonArea extends GroundsArea {
   @Override
   public String getGroundsID() {
      return "Skeleton";
   }

   @Override
   public String getGroundsDisplayName() {
      return ChatColor.GOLD + ChatColor.BOLD.toString() + "Skeleton";
   }

   @Override
   public EntityType getSpawnerType() {
      return EntityType.SKELETON;
   }

   @Override
   public List<EntityType> getGuardTypes() {
      return Collections.singletonList(EntityType.SKELETON);
   }
}
