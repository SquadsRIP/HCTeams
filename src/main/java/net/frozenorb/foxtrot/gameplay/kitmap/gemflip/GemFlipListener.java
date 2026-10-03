package net.frozenorb.foxtrot.gameplay.kitmap.gemflip;

import java.beans.ConstructorProperties;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.gemflip.data.GemFlipEntry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerKickEvent;

public class GemFlipListener implements Listener {
   private final GemFlipHandler gemFlipSystem;

   @EventHandler(priority = EventPriority.LOWEST)
   public void onKick(PlayerKickEvent event) {
      this.handleLeave(event);
   }

   private void handleLeave(PlayerEvent event) {
      Player player = event.getPlayer();
      this.gemFlipSystem.getCooldowns().remove(player.getUniqueId());
      GemFlipEntry entry = this.gemFlipSystem.removeEntry(player);
      if (entry != null) {
         Foxtrot.getInstance().getGemMap().addGems(player.getUniqueId(), entry.getAmount(), true);
      }
   }

   @ConstructorProperties("gemFlipSystem")
   public GemFlipListener(GemFlipHandler gemFlipSystem) {
      this.gemFlipSystem = gemFlipSystem;
   }
}
