package net.frozenorb.foxtrot.gameplay.kitmap.gemflip;

import cc.fyre.piston.util.Cooldown;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.gemflip.data.GemFlipEntry;
import net.frozenorb.foxtrot.gameplay.kitmap.gemflip.data.GemFlipWager;
import org.bukkit.entity.Player;

public class GemFlipHandler {
   private final List<GemFlipEntry> queue = new ArrayList<>(8);
   private final List<GemFlipWager> activeWagers = new ArrayList<>();
   private final Map<UUID, Cooldown> cooldowns = new HashMap<>();
   private long totalWagered = 0L;

   public GemFlipHandler() {
      Foxtrot.getInstance().getServer().getPluginManager().registerEvents(new GemFlipListener(this), Foxtrot.getInstance());
      this.totalWagered = Foxtrot.getInstance().getConfig().getLong("totalWagered", 0L);
   }

   public void onDisable() {
      for (GemFlipEntry entry : this.queue) {
         Player player = entry.getCreator();
         if (player != null) {
            Foxtrot.getInstance().getGemMap().addGems(player.getUniqueId(), entry.getAmount(), true);
         }
      }

      Foxtrot.getInstance().getConfig().set("totalWagered", this.totalWagered);
   }

   public GemFlipEntry getEntry(Player player) {
      return this.queue.stream().filter(entry -> entry.getCreator().getUniqueId().equals(player.getUniqueId())).findFirst().orElse(null);
   }

   public GemFlipWager getActiveWager(Player player) {
      return this.activeWagers
         .stream()
         .filter(wager -> wager.getCreator().getUniqueId().equals(player.getUniqueId()) || wager.getOpponent().getUniqueId().equals(player.getUniqueId()))
         .findFirst()
         .orElse(null);
   }

   public GemFlipEntry removeEntry(Player player) {
      GemFlipEntry entry = this.getEntry(player);
      if (entry == null) {
         return null;
      }

      this.queue.remove(entry);
      return entry;
   }

   public List<GemFlipEntry> getQueue() {
      return this.queue;
   }

   public List<GemFlipWager> getActiveWagers() {
      return this.activeWagers;
   }

   public Map<UUID, Cooldown> getCooldowns() {
      return this.cooldowns;
   }

   public long getTotalWagered() {
      return this.totalWagered;
   }

   public void setTotalWagered(long totalWagered) {
      this.totalWagered = totalWagered;
   }
}
