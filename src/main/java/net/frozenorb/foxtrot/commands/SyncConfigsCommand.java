package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SyncConfigsCommand {
   @Command(names = "syncconfigs", permission = "op")
   public static void execute(CommandSender sender) {
      File sourceDir = new File("/config/HCTeams");
      File targetDir = Foxtrot.getInstance().getDataFolder();
      if (!sourceDir.exists()) {
         sender.sendMessage(ChatColor.RED + "Source directory /config/HCTeams does not exist!");
      } else {
         File[] files = sourceDir.listFiles((dir, name) -> name.endsWith(".yml"));
         if (files != null && files.length != 0) {
            if (!targetDir.exists()) {
               targetDir.mkdirs();
            }

            int copied = 0;

            for (File source : files) {
               try {
                  File target = new File(targetDir, source.getName());
                  Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
                  copied++;
                  sender.sendMessage(ChatColor.GREEN + "Copied " + source.getName());
               } catch (Exception e) {
                  sender.sendMessage(ChatColor.RED + "Failed to copy " + source.getName() + ": " + e.getMessage());
               }
            }

            sender.sendMessage(ChatColor.GREEN + "Copied " + copied + " config files from /config/HCTeams/ to plugins/HCTeams/");
            sender.sendMessage(ChatColor.YELLOW + "Now run /hcteams reload to apply the changes.");
         } else {
            sender.sendMessage(ChatColor.RED + "No .yml files found in /config/HCTeams/");
         }
      }
   }
}
