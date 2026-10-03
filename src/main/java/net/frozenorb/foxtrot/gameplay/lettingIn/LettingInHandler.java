package net.frozenorb.foxtrot.gameplay.lettingIn;

import java.util.HashMap;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.lettingIn.listener.LettingInListener;
import org.bson.types.ObjectId;

public class LettingInHandler {
   private Foxtrot instance;
   private Map<ObjectId, Integer> cache = new HashMap<>();

   public LettingInHandler(Foxtrot instance) {
      this.instance = instance;
      this.instance.getServer().getPluginManager().registerEvents(new LettingInListener(this.instance, this), this.instance);
   }

   public Map<ObjectId, Integer> getCache() {
      return this.cache;
   }
}
