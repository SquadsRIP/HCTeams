package net.frozenorb.foxtrot.gameplay.events.outposts.type;

import java.util.Collections;
import java.util.List;
import net.frozenorb.foxtrot.gameplay.events.outposts.data.Outpost;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class NetherOutpost extends Outpost {
   @Override
   public String getId() {
      return "Nether";
   }

   @Override
   public ChatColor getDisplayColor() {
      return ChatColor.GOLD;
   }

   @Override
   public Material getMaterial() {
      return Material.NETHERRACK;
   }

   @Override
   public int getSlot() {
      return 16;
   }

   @Override
   public List<String> getBenefits() {
      return Collections.singletonList("Double KOTH Points");
   }
}
