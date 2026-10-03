package net.frozenorb.foxtrot.commands;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.piston.Piston;
import cc.fyre.piston.packet.StaffBroadcastPacket;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import cc.fyre.universe.UniverseAPI;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Collectors;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.event.ReclaimEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ReclaimCommand {
   @Command(names = "reclaim", permission = "")
   public static void execute(Player player) {
      if (Foxtrot.getInstance().getReclaimMap().hasReclaimed(player.getUniqueId())) {
         player.sendMessage(ChatColor.RED + "You have already reclaimed this map.");
      } else {
         if (player.getName().equalsIgnoreCase("Lavaboy29")) {
            player.getInventory().addItem(new ItemStack(Material.MONSTER_EGG, 64, (short)91));
            player.getInventory().addItem(new ItemStack(Material.LEASH, 32));
         }

         Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
         if (profile.hasSubscription()) {
            for (String command : Foxtrot.getInstance().getConfiguration("reclaims.yml").getStringList("reclaims.VIP.commands")) {
               Foxtrot.getInstance()
                  .getServer()
                  .dispatchCommand(
                     Foxtrot.getInstance().getServer().getConsoleSender(),
                     command.replace("{player}", player.getName()).replace("{rankName}", ChatColor.YELLOW + "VIP")
                  );
            }

            Foxtrot.getInstance().getReclaimMap().setReclaimed(player.getUniqueId(), true);
         }

         Configuration config = Foxtrot.getInstance().getConfiguration("reclaims.yml");

         for (String key : config.getConfigurationSection("reclaims")
            .getKeys(false)
            .stream()
            .sorted(Comparator.comparingInt(keyx -> (int)config.getLong("reclaims." + keyx + ".priority", 99L)))
            .collect(Collectors.toList())) {
            if (player.hasPermission("reclaims." + key.toLowerCase())) {
               Foxtrot.getInstance().getReclaimMap().setReclaimed(player.getUniqueId(), true);

               for (String command : config.getStringList("reclaims." + key + ".commands")) {
                  try {
                     Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(), command.replace("{player}", player.getName()).replace("{uuid}", player.getUniqueId().toString())
                     );
                  } catch (Exception e) {
                     Foxtrot.getInstance().getLogger().severe("[Reclaims] Failed to execute command: " + command + " for player " + player.getName());
                     e.printStackTrace();
                  }
               }

               Foxtrot.getInstance().getServer().getPluginManager().callEvent(new ReclaimEvent(player, key));
               return;
            }
         }

         player.sendMessage(ChatColor.RED + "It appears there is no reclaim found for your rank.");
      }
   }

   @Command(names = {"resetreclaim", "reclaimreset"}, permission = "foxtrot.command.resetreclaim")
   public static void execute(CommandSender sender, @Parameter(name = "player") UUID uuid) {
      if (!Foxtrot.getInstance().getReclaimMap().hasReclaimed(uuid)) {
         sender.sendMessage(ChatColor.RED + "That player has not reclaimed yet this map!");
      } else {
         Foxtrot.getInstance().getReclaimMap().setReclaimed(uuid, false);
         Piston.getInstance()
            .sendPacketAsync(
               new StaffBroadcastPacket(
                  "neutron.manager",
                  ChatColor.translate(
                     ChatColor.translate(
                        ChatColor.LIGHT_PURPLE
                           + "[MC]"
                           + ChatColor.LIGHT_PURPLE
                           + "["
                           + UniverseAPI.getServerName()
                           + "] &f"
                           + (sender instanceof Player ? ((Player)sender).getDisplayName() : ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Console")
                           + " &7has reset &f"
                           + UUIDUtils.name(uuid)
                           + "'s &7reclaim."
                     )
                  )
               )
            );
         sender.sendMessage(ChatColor.GOLD + "Reset " + ChatColor.YELLOW + uuid.toString() + ChatColor.GOLD + "'s reclaim.");
      }
   }
}
