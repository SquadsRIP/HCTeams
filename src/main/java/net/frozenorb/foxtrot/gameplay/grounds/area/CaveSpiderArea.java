package net.frozenorb.foxtrot.gameplay.grounds.area;

import java.util.Collections;
import java.util.List;
import net.frozenorb.foxtrot.gameplay.grounds.GroundsArea;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

public class CaveSpiderArea extends GroundsArea {
   @Override
   public String getGroundsID() {
      return "CaveSpider";
   }

   @Override
   public String getGroundsDisplayName() {
      return ChatColor.GOLD + ChatColor.BOLD.toString() + "Cave Spider";
   }

   @Override
   public EntityType getSpawnerType() {
      return EntityType.CAVE_SPIDER;
   }

   @Override
   public List<EntityType> getGuardTypes() {
      return Collections.singletonList(EntityType.CAVE_SPIDER);
   }
}
