package net.frozenorb.foxtrot.gameplay.content;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.content.listener.ContentListener;

public class ContentHandler {
   private Foxtrot instance;
   private Map<UUID, List<ContentType>> cache = new HashMap<>();

   public boolean isContentType(UUID uuid, ContentType contentType) {
      return this.cache.get(uuid).stream().anyMatch(it -> it.equals(contentType));
   }

   public ContentHandler(Foxtrot instance) {
      this.instance = instance;
      this.instance.getServer().getPluginManager().registerEvents(new ContentListener(this.instance, this), this.instance);
   }

   public Map<UUID, List<ContentType>> getCache() {
      return this.cache;
   }
}
