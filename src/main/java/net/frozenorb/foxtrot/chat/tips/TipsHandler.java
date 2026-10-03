package net.frozenorb.foxtrot.chat.tips;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.chat.tips.task.TipTask;
import net.frozenorb.foxtrot.config.ConfigMessages;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class TipsHandler {
   public static String PREFIX = ChatColor.GRAY + "[" + ChatColor.YELLOW + ChatColor.BOLD + ChatColor.ITALIC + "TIP" + ChatColor.GRAY + "] " + ChatColor.GOLD;
   private Map<Integer, String> tips = new HashMap<>();
   private int interval;
   private TipTask tipTask;
   private File file;
   private FileConfiguration data;

   public TipsHandler(Foxtrot instance) {
      PREFIX = ConfigMessages.message("tips.prefix", "&7[&e&l&oTIP&7] &6");
      this.interval = Math.max(1, instance.getConfiguration("tips.yml").getInt("tips.interval", 300));
      this.tipTask = new TipTask(instance, this);
      this.tipTask.runTaskTimer(instance, 20L * this.interval, 20L * this.interval);
      this.loadTips();
   }

   public void reloadFromConfig() {
      PREFIX = ConfigMessages.message("tips.prefix", "&7[&e&l&oTIP&7] &6");
      int configuredInterval = Math.max(1, Foxtrot.getInstance().getConfiguration("tips.yml").getInt("tips.interval", 300));
      if (configuredInterval != this.interval) {
         this.interval = configuredInterval;
         if (this.tipTask != null) {
            this.tipTask.cancel();
         }

         this.tipTask = new TipTask(Foxtrot.getInstance(), this);
         this.tipTask.runTaskTimer(Foxtrot.getInstance(), 20L * this.interval, 20L * this.interval);
      }
   }

   public void loadTips() {
      this.file = new File(Foxtrot.getInstance().getDataFolder(), "data/tips.yml");
      this.data = YamlConfiguration.loadConfiguration(this.file);
      if (!this.file.exists()) {
         try {
            this.file.createNewFile();
         } catch (IOException e) {
            e.printStackTrace();
         }
      } else if (this.data.get("tips") != null) {
         for (String key : this.data.getConfigurationSection("tips").getKeys(false)) {
            String tip = this.data.getString("tips." + key + ".text");
            this.tips.put(Integer.parseInt(key.replace("id_", "")), tip);
         }
      }
   }

   public void saveTips() {
      this.data.getValues(false).forEach((key, value) -> this.data.set(key, null));

      for (Entry<Integer, String> tipEntry : this.tips.entrySet()) {
         this.data.set("tips.id_" + tipEntry.getKey() + ".text", tipEntry.getValue());
      }

      try {
         this.data.save(this.file);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   @Command(names = "tips setinterval", permission = "op")
   public static void execute(Player player, @Parameter(name = "seconds") int seconds) {
      Foxtrot.getInstance().getTipsHandler().setInterval(seconds);
      Foxtrot.getInstance().getTipsHandler().getTipTask().cancel();
      Foxtrot.getInstance().getTipsHandler().setTipTask(new TipTask(Foxtrot.getInstance(), Foxtrot.getInstance().getTipsHandler()));
      Foxtrot.getInstance().getTipsHandler().getTipTask().runTaskTimer(Foxtrot.getInstance(), 20 * seconds, 20 * seconds);
      player.sendMessage(ChatColor.GOLD + "Set tip interval to " + ChatColor.WHITE + seconds);
   }

   @Command(names = "tips add", permission = "op")
   public static void add(Player player, @Parameter(name = "id") int id, @Parameter(name = "tip", wildcard = true) String tip) {
      if (Foxtrot.getInstance().getTipsHandler().getTips().containsKey(id)) {
         player.sendMessage(ChatColor.YELLOW + "Tip with the ID " + id + " already exists!");
      } else {
         Foxtrot.getInstance().getTipsHandler().getTipTask().getQueue().add(tip);
         Foxtrot.getInstance().getTipsHandler().getTips().put(id, tip);
         player.sendMessage(ChatColor.GOLD + "Added tip with the ID " + ChatColor.WHITE + id + ChatColor.GOLD + ".");
         player.sendMessage("Sample: " + ChatColor.translate(PREFIX + tip));
      }
   }

   @Command(names = "tips delete", permission = "op")
   public static void delete(Player player, @Parameter(name = "id") int id) {
      if (!Foxtrot.getInstance().getTipsHandler().getTips().containsKey(id)) {
         player.sendMessage(ChatColor.YELLOW + "Tip with the ID " + id + " doesn't exist!");
      } else {
         player.sendMessage(ChatColor.GOLD + "Deleted tip with the ID " + ChatColor.WHITE + id + ChatColor.GOLD + ".");
         Foxtrot.getInstance().getTipsHandler().getTips().remove(id);
      }
   }

   public Map<Integer, String> getTips() {
      return this.tips;
   }

   public int getInterval() {
      return this.interval;
   }

   public void setInterval(int interval) {
      this.interval = interval;
   }

   public TipTask getTipTask() {
      return this.tipTask;
   }

   public void setTipTask(TipTask tipTask) {
      this.tipTask = tipTask;
   }

   public File getFile() {
      return this.file;
   }

   public FileConfiguration getData() {
      return this.data;
   }
}
