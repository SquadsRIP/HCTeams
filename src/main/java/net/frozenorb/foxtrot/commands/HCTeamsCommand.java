package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import java.io.IOException;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.config.ConfigMessages;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;

public class HCTeamsCommand {
   @Command(names = {"hcteams", "squads", "teams"}, permission = "hcteams.command.reload")
   public static void root(CommandSender sender) {
      sender.sendMessage(ConfigMessages.message("messages.reload.usage", "&eUsage: /hcteams reload"));
   }

   @Command(names = {"hcteams reload", "squads reload", "teams reload"}, permission = "hcteams.command.reload")
   public static void reload(CommandSender sender) {
      long startedAt = System.currentTimeMillis();

      try {
         Foxtrot.getInstance().reloadPluginConfiguration();
         sender.sendMessage(
            ConfigMessages.message(
               "messages.reload.success",
               "&aReloaded all HCTeams configuration files in %time%ms. Startup-only changes require a restart.",
               "%time%",
               Long.toString(System.currentTimeMillis() - startedAt)
            )
         );
      } catch (IOException | InvalidConfigurationException exception) {
         sender.sendMessage(
            ConfigMessages.message(
               "messages.reload.failure",
               "&eThe configuration files could not be reloaded: %error%",
               "%error%",
               exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage()
            )
         );
         Foxtrot.getInstance().getLogger().warning("Configuration reload rejected: " + exception.getMessage());
      }
   }
}
