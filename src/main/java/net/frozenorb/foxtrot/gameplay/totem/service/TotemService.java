package net.frozenorb.foxtrot.gameplay.totem.service;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.UUID;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.totem.Totem;
import net.frozenorb.foxtrot.gameplay.totem.TotemHandler;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

public class TotemService extends BukkitRunnable {
   private Foxtrot instance;
   private TotemHandler totemHandler;

   @Override
   public void run() {
      for (Entry<UUID, Totem> entry : new HashMap<>(this.totemHandler.getCache()).entrySet()) {
         if (entry.getValue().getExpiresAt() <= System.currentTimeMillis()) {
            this.totemHandler.getCache().remove(entry.getKey());
            Location location = entry.getValue().getLocation();
            location.getBlock().setType(Material.AIR);
            location.getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
         } else {
            Team placedBy = this.instance.getTeamHandler().getTeam(entry.getValue().getPlacedBy());
            if (placedBy != null) {
               for (Player onlineMember : placedBy.getOnlineMembers()) {
                  if (onlineMember.getLocation().getWorld().getName().equalsIgnoreCase(entry.getValue().getLocation().getWorld().getName())) {
                     onlineMember.addPotionEffect(new PotionEffect(entry.getValue().getPotionEffectType(), 80, 0), true);
                  }
               }
            }
         }
      }
   }

   @ConstructorProperties({"instance", "totemHandler"})
   public TotemService(Foxtrot instance, TotemHandler totemHandler) {
      this.instance = instance;
      this.totemHandler = totemHandler;
   }
}
