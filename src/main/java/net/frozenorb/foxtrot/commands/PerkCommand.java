package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PerkCommand {
   public static Map<UUID, UUID> requests = new HashMap<>();

   public static void execute(Player player) {
      if (!player.hasPermission("perk.warp.end")) {
         player.sendMessage(ChatColor.RED + "You do not have the /warp End perk!");
         player.sendMessage(ChatColor.RED + "Purchase this perk at Coming Soon!");
      } else if (!CustomTimerCreateCommand.isSOTWTimer()) {
         player.sendMessage(ChatColor.RED + "You can only warp during SOTW Timer!");
      } else {
         player.teleport(Foxtrot.getInstance().getServer().getWorld("world_the_end").getSpawnLocation().clone());
         player.sendMessage(ChatColor.translate("&6You have warped to &5&lThe End&6!"));
      }
   }

   public static void nether(Player player) {
      if (!player.hasPermission("perk.warp.nether")) {
         player.sendMessage(ChatColor.RED + "You do not have the /warp Nether perk!");
         player.sendMessage(ChatColor.RED + "Purchase this perk at Coming Soon!");
      } else if (!CustomTimerCreateCommand.isSOTWTimer()) {
         player.sendMessage(ChatColor.RED + "You can only warp during SOTW Timer!");
      } else {
         player.teleport(Foxtrot.getInstance().getServer().getWorld("world_nether").getSpawnLocation().clone());
         player.sendMessage(ChatColor.translate("&6You have warped to &4&lNether&6!"));
      }
   }

   @Command(names = "tpa", permission = "")
   public static void tpa(Player player, @Parameter(name = "target") Player target) {
      if (!player.hasPermission("perk.warp.tpa")) {
         player.sendMessage(ChatColor.RED + "You do not have the /tpa perk!");
         player.sendMessage(ChatColor.RED + "Purchase this perk at Coming Soon!");
      } else if (!CustomTimerCreateCommand.isSOTWTimer()) {
         player.sendMessage(ChatColor.RED + "You can only teleport during SOTW Timer!");
      } else if (requests.containsKey(player.getUniqueId()) && requests.get(player.getUniqueId()).toString().equalsIgnoreCase(player.getUniqueId().toString())) {
         player.sendMessage(ChatColor.RED + "You already have an outgoing response");
      } else {
         player.sendMessage(ChatColor.translate("&aSent a teleportation request to " + target.getName() + ", expires in &f30 seconds&a."));
         target.sendMessage(ChatColor.translate(player.getName() + " &6has requested to teleport to you."));
         target.sendMessage(ChatColor.translate("&7Type &f/tpaccept &7to let them accept the teleport request."));
         requests.put(target.getUniqueId(), player.getUniqueId());
         Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            UUID var10000 = requests.remove(player.getUniqueId());
         }, 600L);
      }
   }

   @Command(names = {"tpayes", "tpyes", "tpaccept"}, permission = "")
   public static void accept(Player player) {
      if (!CustomTimerCreateCommand.isSOTWTimer()) {
         player.sendMessage(ChatColor.RED + "You can only teleport during SOTW Timer!");
      } else if (!requests.containsKey(player.getUniqueId())) {
         player.sendMessage(ChatColor.RED + "You have no available teleportation requests!");
      } else {
         Player target = Foxtrot.getInstance().getServer().getPlayer(requests.remove(player.getUniqueId()));
         if (target == null) {
            player.sendMessage(ChatColor.RED + "That player is no longer online!");
         } else {
            target.sendMessage(ChatColor.translate(player.getName() + " &6has teleported to you."));
            player.sendMessage(ChatColor.GREEN + "Teleported to " + target.getName() + ".");
            target.teleport(player.getLocation());
         }
      }
   }

   @Command(names = {"tpdeny", "tpadeny"}, permission = "")
   public static void deny(Player player) {
      if (!requests.containsKey(player.getUniqueId())) {
         player.sendMessage(ChatColor.RED + "You have no available teleportation requests!");
      } else {
         requests.remove(player.getUniqueId());
         player.sendMessage(ChatColor.RED + "Denied your recent teleportation request.");
      }
   }
}
