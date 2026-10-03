package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class ResetRedeemCommand {
   @Command(names = "resetredeem", permission = "op")
   public static void resetRedeem(CommandSender sender, @Parameter(name = "player|all", wildcard = true) String target) {
      if (target == null || target.trim().isEmpty()) {
         sender.sendMessage(ChatColor.RED + "Usage: /resetredeem <player|all>");
      } else if (target.equalsIgnoreCase("all")) {
         RedeemCommand.clearRedeemed();
         File file = new File(Foxtrot.getInstance().getDataFolder(), "redeem-data.yml");
         if (file.exists()) {
            file.delete();
         }

         sender.sendMessage(ChatColor.GREEN + "Reset redeem status for ALL players.");
      } else {
         Player targetPlayer = Bukkit.getPlayer(target);
         if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player not found: " + target);
         } else {
            UUID uuid = targetPlayer.getUniqueId();
            RedeemCommand.removeRedeemed(uuid);
            removeFromDataFile(uuid);
            sender.sendMessage(ChatColor.GREEN + "Reset redeem status for " + targetPlayer.getName() + ".");
            targetPlayer.sendMessage(ChatColor.GREEN + "Your redeem status has been reset! You can /redeem again.");
         }
      }
   }

   private static void removeFromDataFile(UUID uuid) {
      File file = new File(Foxtrot.getInstance().getDataFolder(), "redeem-data.yml");
      if (file.exists()) {
         YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
         data.set(uuid.toString(), null);

         try {
            data.save(file);
         } catch (IOException e) {
            Foxtrot.getInstance().getLogger().warning("Failed to save redeem-data.yml: " + e.getMessage());
         }
      }
   }
}
