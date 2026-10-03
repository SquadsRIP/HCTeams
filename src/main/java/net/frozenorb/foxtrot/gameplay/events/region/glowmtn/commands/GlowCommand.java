package net.frozenorb.foxtrot.gameplay.events.region.glowmtn.commands;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.region.glowmtn.GlowHandler;
import net.frozenorb.foxtrot.gameplay.events.region.glowmtn.GlowMountain;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GlowCommand {
   @Command(names = "glow scan", permission = "op")
   public static void glowScan(Player sender) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(GlowHandler.getGlowTeamName());
      if (team == null) {
         sender.sendMessage(ChatColor.YELLOW + "You must first create the team (" + GlowHandler.getGlowTeamName() + ") and claim it!");
      } else if (team.getClaims().isEmpty()) {
         sender.sendMessage(ChatColor.YELLOW + "You must claim land for '" + GlowHandler.getGlowTeamName() + "' before scanning it!");
      } else {
         if (!Foxtrot.getInstance().getGlowHandler().hasGlowMountain()) {
            Foxtrot.getInstance().getGlowHandler().setGlowMountain(new GlowMountain());
         }

         Foxtrot.getInstance().getGlowHandler().getGlowMountain().scan();
         Foxtrot.getInstance().getGlowHandler().save();
         sender.sendMessage(ChatColor.GREEN + "[Glowstone Mountain] Scanned all glowstone and saved glowstone mountain to file!");
      }
   }

   @Command(names = "glow reset", permission = "op")
   public static void glowReset(Player sender) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(GlowHandler.getGlowTeamName());
      if (team != null && !team.getClaims().isEmpty()) {
         if (!Foxtrot.getInstance().getGlowHandler().hasGlowMountain()) {
            Foxtrot.getInstance().getGlowHandler().ensureGlowMountain();
         }

         if (!Foxtrot.getInstance().getGlowHandler().hasGlowMountain()) {
            sender.sendMessage(ChatColor.YELLOW + "Glowstone mountain scan failed — make sure there are actual glowstone blocks inside the claim.");
         } else {
            int restored = Foxtrot.getInstance().getGlowHandler().getGlowMountain().reset();
            Foxtrot.getInstance().getGlowHandler().setLastReset(System.currentTimeMillis());
            Bukkit.broadcastMessage("");
            Bukkit.broadcastMessage(
               ChatColor.GOLD + "[Glowstone Mountain]" + ChatColor.GREEN + " All glowstone has been reset! (" + restored + " blocks restored)"
            );
            Bukkit.broadcastMessage("");
         }
      } else {
         sender.sendMessage(ChatColor.YELLOW + "Create the team '" + GlowHandler.getGlowTeamName() + "' and claim land for it first!");
      }
   }
}
