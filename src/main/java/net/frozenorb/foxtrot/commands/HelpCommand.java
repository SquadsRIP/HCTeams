package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.config.ConfigMessages;
import org.bukkit.entity.Player;

public class HelpCommand {
   @Command(names = {"help", "?", "helpme"}, permission = "")
   public static void execute(Player player) {
      String[] msg = new String[]{
         "§8§m-----------------------------------------------------",
         "§4§lSquads Help",
         "§8§m-----------------------------------------------------",
         "§cGetting Started:",
         "§7/f help §f- Show every faction command.",
         "§7/coords §f- Locations of Spawn, KOTHs, the End and more.",
         "§7/koth §f- Show active and upcoming events.",
         "§7/pvp §f- Manage your PvP timer.",
         "§7/lives §f- Check your lives.",
         "§7/lives use §f- Use a life to respawn while deathbanned.",
         "",
         "§cPlayer Commands:",
         "§7/stats [player] §f- View a player's statistics.",
         "§7/lb [objective] §f- View the leaderboards.",
         "§7/focus <player> §f- Focus a player for your faction.",
         "§7/tl §f- Tell your faction your location.",
         "§7/msg <player> <message> §f- Send a private message.",
         "§7/logout §f- Log out safely.",
         "",
         "§cLinks:",
         "§7/store §f- " + ConfigMessages.link("store", "squadsrip.tebex.io"),
         "§7/discord §f- " + ConfigMessages.link("discord", "discord.gg/squadsrip"),
         "§8§m-----------------------------------------------------"
      };
      player.sendMessage(msg);
   }
}
