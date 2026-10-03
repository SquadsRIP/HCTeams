package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import java.io.File;
import java.io.IOException;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class SetDeathbanSpawnCommand {
   @Command(names = "setdeathbanspawn", permission = "op")
   public static void setDeathbanSpawn(Player sender) {
      Location loc = sender.getLocation();
      String worldName = loc.getWorld().getName();
      File file = new File(Foxtrot.getInstance().getDataFolder(), "deathban.yml");
      YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
      config.set("spawn.world", worldName);
      config.set("spawn.x", loc.getX());
      config.set("spawn.y", loc.getY());
      config.set("spawn.z", loc.getZ());
      config.set("spawn.yaw", loc.getYaw());
      config.set("spawn.pitch", loc.getPitch());

      try {
         config.save(file);
      } catch (IOException e) {
         sender.sendMessage(ChatColor.RED + "Failed to save deathban spawn: " + e.getMessage());
         return;
      }

      loc.getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
      sender.sendMessage(ChatColor.GREEN + "Deathban spawn set to " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + " in " + worldName);
   }

   public static Location getDeathbanSpawn() {
      File file = new File(Foxtrot.getInstance().getDataFolder(), "deathban.yml");
      if (!file.exists()) {
         return null;
      }

      YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
      if (!config.contains("spawn.world")) {
         return null;
      }

      String worldName = config.getString("spawn.world");
      World world = Bukkit.getWorld(worldName);
      return world == null
         ? null
         : new Location(
            world,
            config.getDouble("spawn.x"),
            config.getDouble("spawn.y"),
            config.getDouble("spawn.z"),
            (float)config.getDouble("spawn.yaw", 0.0),
            (float)config.getDouble("spawn.pitch", 0.0)
         );
   }
}
