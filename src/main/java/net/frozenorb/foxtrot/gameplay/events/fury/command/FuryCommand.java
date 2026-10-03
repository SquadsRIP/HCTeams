package net.frozenorb.foxtrot.gameplay.events.fury.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FuryCommand {
   @Command(names = "fury start", permission = "op")
   public static void execute(Player player) {
      if (Foxtrot.getInstance().getFuryHandler().isActive()) {
         player.sendMessage(ChatColor.RED + "Fury is already active!");
      } else {
         Foxtrot.getInstance().getFuryHandler().start();
      }
   }

   @Command(names = "fury stop", permission = "op")
   public static void stop(Player player) {
      if (!Foxtrot.getInstance().getFuryHandler().isActive()) {
         player.sendMessage(ChatColor.RED + "Fury isn't currently active!");
      } else {
         Foxtrot.getInstance().getFuryHandler().endGame(null);
      }
   }

   @Command(names = "fury setpoints", permission = "op")
   public static void setPoints(Player player, @Parameter(name = "team") Team team, @Parameter(name = "amount") int amount) {
      if (!Foxtrot.getInstance().getFuryHandler().isActive()) {
         player.sendMessage(ChatColor.RED + "Fury isn't currently active!");
      } else {
         player.sendMessage(ChatColor.translate("&6You have set &f" + team.getName() + "'s points &6to &f" + amount + "&6."));
         Foxtrot.getInstance().getFuryHandler().getTeamPoints().replace(team.getUniqueId(), amount);
      }
   }

   @Command(names = "fury swap", permission = "op")
   public static void swap(Player player) {
      if (!Foxtrot.getInstance().getFuryHandler().isActive()) {
         player.sendMessage(ChatColor.RED + "Fury isn't currently active!");
      } else {
         Foxtrot.getInstance().getFuryHandler().switchCapZone();
      }
   }
}
