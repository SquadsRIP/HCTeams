package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import java.util.Set;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import net.minecraft.util.com.google.common.collect.ImmutableMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.enchant.data.CustomEnchant;

public class TeamKickCommand {
   @Command(names = {"team kick", "t kick", "f kick", "faction kick", "fac kick"}, permission = "")
   public static void teamKick(Player sender, @Parameter(name = "player") UUID player) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
      if (team == null) {
         sender.sendMessage(ChatColor.GRAY + "You are not in a faction!");
      } else if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId()) && !team.isCaptain(sender.getUniqueId())) {
         sender.sendMessage(ChatColor.DARK_AQUA + "Only faction captains can do this.");
      } else if (!team.isMember(player)) {
         sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " isn't in your faction!");
      } else if (team.isOwner(player)) {
         sender.sendMessage(ChatColor.RED + "You cannot kick the faction leader!");
      } else if (team.isCoLeader(player) && !team.isOwner(sender.getUniqueId())) {
         sender.sendMessage(ChatColor.RED + "Only the owner can kick other co-leaders!");
      } else if (team.isCaptain(player) && !team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
         sender.sendMessage(ChatColor.RED + "Only an owner or co-leader can kick other captains!");
      } else if (DTRHandler.isOnCooldown(team)) {
         sender.sendMessage(ChatColor.RED + "You cannot kick members while your faction's DTR is frozen!");
      } else {
         Player bukkitPlayer = Foxtrot.getInstance().getServer().getPlayer(player);
         if (bukkitPlayer != null && SpawnTagHandler.isTagged(bukkitPlayer)) {
            sender.sendMessage(
               ChatColor.RED
                  + bukkitPlayer.getName()
                  + " is currently combat-tagged! You can forcibly kick "
                  + bukkitPlayer.getName()
                  + " by using '"
                  + ChatColor.YELLOW
                  + "/f forcekick "
                  + bukkitPlayer.getName()
                  + ChatColor.RED
                  + "' which will cost your faction 1 DTR."
            );
         } else {
            team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player) + " was kicked by " + sender.getName() + "!");
            TeamActionTracker.logActionAsync(
               team,
               TeamActionType.MEMBER_KICKED,
               ImmutableMap.of("playerId", player, "kickedById", sender.getUniqueId(), "kickedByName", sender.getName(), "usedForceKick", "false")
            );
            if (team.removeMember(player)) {
               team.disband();
            } else {
               team.flagForSave();
            }

            Foxtrot.getInstance().getTeamHandler().setTeam(player, null);
            if (bukkitPlayer != null) {
               LunarClientListener.updateNametag(bukkitPlayer);

               for (PotionEffectType purchasedEffect : team.getPurchasedEffects()) {
                  if (team.getPurchasedEffects().contains(purchasedEffect)) {
                     Set<CustomEnchant> customEnchantList = Suge.getInstance().getEnchantHandler().findAllCustomEnchants(bukkitPlayer).keySet();
                     if (!customEnchantList.stream()
                        .anyMatch(it -> it.getEffect() != null && it.getEffect().getName().equalsIgnoreCase(purchasedEffect.getName()))) {
                        bukkitPlayer.removePotionEffect(purchasedEffect);
                     }
                  }
               }
            }
         }
      }
   }
}
