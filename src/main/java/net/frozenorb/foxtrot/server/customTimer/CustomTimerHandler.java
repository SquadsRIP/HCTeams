package net.frozenorb.foxtrot.server.customTimer;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.server.customTimer.task.CustomTimerTask;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class CustomTimerHandler {
   private String activeTimer = "";
   private File file = new File(Foxtrot.getInstance().getDataFolder(), "data/custom-timers.yml");
   private FileConfiguration data = YamlConfiguration.loadConfiguration(this.file);

   public CustomTimerHandler() {
      new CustomTimerTask().runTaskTimer(Foxtrot.getInstance(), 120L, 120L);
      if (!this.file.exists()) {
         try {
            this.file.createNewFile();
         } catch (IOException e) {
            e.printStackTrace();
         }
      } else if (this.data.get("timers") != null) {
         Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            for (String configEntry : this.data.getConfigurationSection("timers").getKeys(false)) {
               CustomTimerCreateCommand.getCustomTimers().put(configEntry, this.data.getLong("timers." + configEntry));
            }
         }, 30L);
      }
   }

   public void saveData() {
      Map<String, Object> configValues = this.data.getValues(false);

      for (Entry<String, Object> entry : configValues.entrySet()) {
         this.data.set(entry.getKey(), null);
      }

      for (Entry<String, Long> mapEntry : CustomTimerCreateCommand.getCustomTimers().entrySet()) {
         this.data.set("timers." + mapEntry.getKey(), mapEntry.getValue());
      }

      try {
         this.data.save(this.file);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public String getActiveTimer() {
      return this.activeTimer;
   }

   public void setActiveTimer(String activeTimer) {
      this.activeTimer = activeTimer;
   }
}
