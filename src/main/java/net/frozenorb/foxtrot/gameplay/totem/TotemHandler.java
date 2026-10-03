package net.frozenorb.foxtrot.gameplay.totem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.totem.listener.TotemListener;
import net.frozenorb.foxtrot.gameplay.totem.service.TotemService;
import org.bson.types.ObjectId;

public class TotemHandler {
   private Foxtrot instance;
   private Map<UUID, Totem> cache = new HashMap<>();
   private Map<ObjectId, Long> onCooldown = new HashMap<>();

   public TotemHandler(Foxtrot instance) {
      this.instance = instance;
      this.instance.getServer().getPluginManager().registerEvents(new TotemListener(), this.instance);
      new TotemService(instance, this).runTaskTimer(this.instance, 20L, 20L);
   }

   public Map<UUID, Totem> getCache() {
      return this.cache;
   }

   public Map<ObjectId, Long> getOnCooldown() {
      return this.onCooldown;
   }
}
