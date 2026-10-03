package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import java.util.Arrays;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.VisualClaim;
import net.frozenorb.foxtrot.team.claims.VisualClaimType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class TeamResizeCommand {
   public static final ItemStack SELECTION_WAND = new ItemStack(Material.WOOD_AXE);

   @Command(names = {"team resize", "t resize", "f resize", "faction resize", "fac resize"}, permission = "op")
   public static void teamResize(final Player sender) {
      Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
      if (team == null) {
         sender.sendMessage(ChatColor.RED + "You are not in a faction!");
      } else {
         if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId()) && !team.isCaptain(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only faction captains can do this.");
         } else {
            sender.getInventory().remove(SELECTION_WAND);
            if (team.isRaidable()) {
               sender.sendMessage(ChatColor.YELLOW + "You may not resize land while your faction is raidable!");
               return;
            }

            (new BukkitRunnable() {
               @Override
               public void run() {
                  sender.getInventory().addItem(TeamResizeCommand.SELECTION_WAND.clone());
               }
            }).runTaskLater(Foxtrot.getInstance(), 1L);
            new VisualClaim(sender, VisualClaimType.RESIZE, false).draw(false);
            if (!VisualClaim.getCurrentMaps().containsKey(sender.getName())) {
               new VisualClaim(sender, VisualClaimType.MAP, false).draw(true);
            }
         }
      }
   }

   static {
      ItemMeta meta = SELECTION_WAND.getItemMeta();
      meta.setDisplayName("§a§oResizing Wand");
      meta.setLore(
         Arrays.asList(
            "",
            "§eRight/Left Click§6 Block",
            "§b- §fSelect resize's corners",
            "",
            "§eRight Click §6Air",
            "§b- §fCancel current claim",
            "",
            "§9Shift §eLeft Click §6Block/Air",
            "§b- §fPurchase current claim"
         )
      );
      SELECTION_WAND.setItemMeta(meta);
   }
}
