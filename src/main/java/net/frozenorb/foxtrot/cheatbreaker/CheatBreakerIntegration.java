package net.frozenorb.foxtrot.cheatbreaker;

import java.util.logging.Level;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.plugin.Plugin;

public final class CheatBreakerIntegration {
   private final Foxtrot plugin;
   private CheatBreakerService service;

   public CheatBreakerIntegration(Foxtrot plugin) {
      this.plugin = plugin;
   }

   public void reload() {
      this.close();
      Plugin apiPlugin = this.plugin.getServer().getPluginManager().getPlugin("CheatBreakerAPI");
      if (apiPlugin != null && apiPlugin.getConfig().getBoolean("kick.enabled", true)) {
         apiPlugin.getConfig().set("kick.enabled", false);
         apiPlugin.saveConfig();
         this.plugin.getLogger().info("[CheatBreaker] Disabled the API's client requirement; all clients may join.");
      }

      if (this.plugin.getConfig().getBoolean("cheatbreaker.enabled", true)) {
         if (apiPlugin != null && apiPlugin.isEnabled()) {
            try {
               this.service = new CheatBreakerService(this.plugin);
               this.service.start();
               this.plugin.getLogger().info("[CheatBreaker] Team view, staff modules, waypoints and faction nametags enabled.");
            } catch (RuntimeException | LinkageError error) {
               this.close();
               this.plugin.getLogger().log(Level.WARNING, "[CheatBreaker] Could not enable integration; use API 3.4.0.", error);
            }
         } else {
            this.plugin.getLogger().info("[CheatBreaker] Install CheatBreakerAPI to enable the client integration.");
         }
      }
   }

   public void close() {
      if (this.service != null) {
         this.service.close();
         this.service = null;
      }
   }
}
