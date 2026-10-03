package net.frozenorb.foxtrot.server.idle;

import cc.fyre.piston.PistonConstants;
import net.frozenorb.foxtrot.Foxtrot;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class IdleCheckRunnable extends BukkitRunnable {
   private static final int MINUTES = 15;

   @Override
   public void run() {
      for (Player online : Bukkit.getOnlinePlayers()) {
         if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(online.getUniqueId())
            && !online.hasPermission("basic.idle.bypass")
            && !online.hasMetadata(PistonConstants.FREEZE_METADATA)) {
            EntityPlayer var3 = ((CraftPlayer)online).getHandle();
         }
      }
   }
}
