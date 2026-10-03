package net.frozenorb.foxtrot.nametag;

import cc.fyre.modsuite.mod.ModHandler;
import cc.fyre.proton.Proton;
import cc.fyre.proton.nametag.construct.NameTagInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class FactionNametagFormatter {
   private FactionNametagFormatter() {
   }

   public static List<String> format(Player target, Player viewer) {
      NameTagInfo info = new FoxtrotNametagProvider().fetchNameTag(target, viewer);
      if (info.equals(Proton.getInstance().getNameTagHandler().getINVISIBLE())) {
         return Collections.emptyList();
      }

      String name = info.getPrefix() + target.getDisguisedName();
      List<String> lines = new ArrayList<>();
      if (ModHandler.INSTANCE.isInModMode(target.getUniqueId())) {
         lines.add(ChatColor.GRAY + "[Mod Mode]");
         lines.add(name);
         return lines;
      }

      if (target.hasMetadata("SHOW_HEARTS")) {
         lines.add(ChatColor.YELLOW + Double.toString(Math.round(target.getHealth()) / 2.0) + " Hearts");
      }

      Foxtrot plugin = Foxtrot.getInstance();
      if (plugin.getBountyManager() != null && plugin.getBountyManager().getBounty(target) != null) {
         lines.add(ChatColor.YELLOW + "Bounty: " + ChatColor.GREEN + plugin.getBountyManager().getBounty(target).getGems() + " Gems");
      }

      Team team = plugin.getTeamHandler().getTeam(target);
      if (team != null) {
         String dtr = team.formatDTR();
         if (plugin.getDTRDisplayMap().isHearts(viewer.getUniqueId())) {
            dtr = team.getDTRColor().toString() + (int)Math.ceil(team.getDTR()) + "❤" + team.getDTRSuffix();
         }

         lines.add(ChatColor.GOLD + "[" + team.getName(viewer) + " " + dtr + ChatColor.GOLD + "]");
      }

      lines.add(name);
      return lines;
   }
}
