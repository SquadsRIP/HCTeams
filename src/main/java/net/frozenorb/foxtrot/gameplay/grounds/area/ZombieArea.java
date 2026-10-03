package net.frozenorb.foxtrot.gameplay.grounds.area;

import java.util.Collections;
import java.util.List;
import net.frozenorb.foxtrot.gameplay.grounds.GroundsArea;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

public class ZombieArea extends GroundsArea {
   @Override
   public String getGroundsID() {
      return "Zombie";
   }

   @Override
   public String getGroundsDisplayName() {
      return ChatColor.GOLD + ChatColor.BOLD.toString() + "Zombie";
   }

   @Override
   public EntityType getSpawnerType() {
      return EntityType.ZOMBIE;
   }

   @Override
   public List<EntityType> getGuardTypes() {
      return Collections.singletonList(EntityType.ZOMBIE);
   }
}
