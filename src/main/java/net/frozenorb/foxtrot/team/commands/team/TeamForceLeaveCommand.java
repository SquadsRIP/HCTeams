package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import java.util.Set;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.enchant.data.CustomEnchant;

public class TeamForceLeaveCommand {
   @Command(names = {"team forceleave", "t forceleave", "f forceleave", "faction forceleave", "fac forceleave", "t fl", "team fl"}, permission = "")
   public static void forceLeave(Player sender) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
      if (team == null) {
         sender.sendMessage(ChatColor.GRAY + "You are not in a faction!");
      } else if (team.isOwner(sender.getUniqueId()) && team.getSize() > 1) {
         sender.sendMessage(ChatColor.RED + "Please choose a new leader before leaving your faction!");
      } else if (LandBoard.getInstance().getTeam(sender.getLocation()) == team) {
         sender.sendMessage(ChatColor.RED + "You cannot leave your faction while on faction territory.");
      } else {
         if (team.removeMember(sender.getUniqueId())) {
            team.disband();
            Foxtrot.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), null);
            sender.sendMessage(ChatColor.DARK_AQUA + "Successfully left and disbanded faction!");
         } else {
            Foxtrot.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), null);
            team.flagForSave();
            if (SpawnTagHandler.isTagged(sender)) {
               team.setDTR(team.getDTR() - Foxtrot.getInstance().getServerHandler().getDTRLoss(sender));
               team.sendMessage(
                  ChatColor.RED
                     + sender.getName()
                     + " forcibly left the faction. Your faction has lost "
                     + Foxtrot.getInstance().getServerHandler().getDTRLoss(sender)
                     + " DTR."
               );
               sender.sendMessage(
                  ChatColor.RED
                     + "You have forcibly left your faction. Your faction lost "
                     + Foxtrot.getInstance().getServerHandler().getDTRLoss(sender)
                     + " DTR."
               );
            } else {
               team.sendMessage(ChatColor.YELLOW + sender.getName() + " has left the faction.");
               sender.sendMessage(ChatColor.DARK_AQUA + "Successfully left the faction!");
            }
         }

         LunarClientListener.updateNametag(sender);
         Team teamAt = LandBoard.getInstance().getTeam(sender.getLocation());
         if (teamAt.getName().equalsIgnoreCase(team.getName())) {
            for (PotionEffectType purchasedEffect : team.getPurchasedEffects()) {
               if (team.getPurchasedEffects().contains(purchasedEffect)) {
                  Set<CustomEnchant> customEnchantList = Suge.getInstance().getEnchantHandler().findAllCustomEnchants(sender).keySet();
                  if (!customEnchantList.stream()
                     .anyMatch(it -> it.getEffect() != null && it.getEffect().getName().equalsIgnoreCase(purchasedEffect.getName()))) {
                     sender.removePotionEffect(purchasedEffect);
                  }
               }
            }
         }
      }
   }
}
