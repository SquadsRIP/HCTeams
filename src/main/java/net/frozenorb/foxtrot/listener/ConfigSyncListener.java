package net.frozenorb.foxtrot.listener;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;

public class ConfigSyncListener implements Listener {
   @EventHandler(priority = EventPriority.LOWEST)
   public void onPluginEnable(PluginEnableEvent event) {
      if (event.getPlugin().getName().equals("HCTeams")) {
         File sourceDir = new File("/config/HCTeams");
         File targetDir = Foxtrot.getInstance().getDataFolder();
         if (sourceDir.exists()) {
            if (!targetDir.exists()) {
               targetDir.mkdirs();
            }

            File[] files;
            if ((files = sourceDir.listFiles((dir, name) -> name.endsWith(".yml"))) != null) {
               for (File source : files) {
                  try {
                     File target = new File(targetDir, source.getName());
                     Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                  } catch (Exception e) {
                     System.out.println("[HCTeams] Failed to sync config " + source.getName() + ": " + e.getMessage());
                  }
               }

               System.out.println("[HCTeams] Synced " + files.length + " config files from /config/HCTeams/ to plugins/HCTeams/");
            }
         }
      }
   }
}
