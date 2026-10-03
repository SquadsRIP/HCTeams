package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.frozenorb.foxtrot.server.HelpfulColor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ArcherColorCommand {
   @Command(names = "armorcolor", permission = "command.archercolor")
   public static void execute(Player player, @Parameter(name = "color") String color) {
      HelpfulColor helpfulColor = Arrays.stream(HelpfulColor.values()).filter(it -> it.name().equalsIgnoreCase(color)).findFirst().orElse(null);
      if (helpfulColor == null) {
         player.sendMessage(ChatColor.YELLOW + "Color not found! Type /listcolors to view all available colors.");
      } else {
         List<ItemStack> armor = Arrays.stream(player.getInventory().getArmorContents())
            .filter(Objects::nonNull)
            .filter(it -> it.hasItemMeta() && it.getItemMeta() instanceof LeatherArmorMeta)
            .collect(Collectors.toList());
         if (armor.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "You have no leather armor on!");
         } else {
            armor.forEach(it -> {
               LeatherArmorMeta meta = (LeatherArmorMeta)it.getItemMeta();
               meta.setColor(helpfulColor.getColor());
               it.setItemMeta(meta);
            });
            player.sendMessage(ChatColor.GREEN + "Updated your leather armor color to " + helpfulColor.name() + "!");
            player.updateInventory();
         }
      }
   }

   @Command(names = "listcolors", permission = "command.archercolor")
   public static void list(Player player) {
      player.sendMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Available Colors");

      for (HelpfulColor value : HelpfulColor.values()) {
         player.sendMessage(ChatColor.WHITE + "- " + ChatColor.YELLOW + WordUtils.capitalizeFully(value.name()));
      }
   }
}
