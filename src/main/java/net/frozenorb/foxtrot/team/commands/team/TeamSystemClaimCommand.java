package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import java.util.ArrayList;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.claims.VisualClaim;
import net.frozenorb.foxtrot.team.claims.VisualClaimType;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TeamSystemClaimCommand {
   @Command(names = {"team system claim", "t system claim", "f system claim", "faction system claim", "fac system claim"}, permission = "op")
   public static void systemClaim(Player sender, @Parameter(name = "faction name", wildcard = true) String factionName) {
      if (factionName != null && !factionName.trim().isEmpty()) {
         factionName = factionName.trim();
         Team existing = Foxtrot.getInstance().getTeamHandler().getTeam(factionName);
         if (existing != null) {
            sender.sendMessage(ChatColor.YELLOW + "Faction '" + existing.getName() + "' already exists. Giving you a claim wand to add more claims.");
            giveClaimWand(sender);
            sender.sendMessage(ChatColor.GREEN + "Claim the area, then run:");
            sender.sendMessage(ChatColor.YELLOW + "   /flag add " + existing.getName() + " <flag>");
         } else {
            Team existingTeam = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
            if (existingTeam != null) {
               existingTeam.removeMember(sender.getUniqueId());
               if (existingTeam.getOwner() != null && existingTeam.getOwner().equals(sender.getUniqueId())) {
                  existingTeam.setOwner(null);
               }

               Foxtrot.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), null);
               sender.sendMessage(ChatColor.GRAY + "Auto-removed you from your previous faction.");
            }

            Team createdTeam = new Team(factionName);
            createdTeam.setUniqueId(new ObjectId());
            createdTeam.setName(factionName);
            createdTeam.setDTR(1.0);
            createdTeam.setOwner(sender.getUniqueId());
            createdTeam.addMember(sender.getUniqueId());
            Foxtrot.getInstance().getTeamHandler().setupTeam(createdTeam);
            Foxtrot.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), createdTeam, true);
            Team verifyTeam = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
            if (verifyTeam != null && verifyTeam.getName().equalsIgnoreCase(factionName)) {
               sender.sendMessage(ChatColor.GREEN + "Created system faction '" + ChatColor.WHITE + factionName + ChatColor.GREEN + "'.");
               sender.sendMessage(ChatColor.GREEN + "You have been temporarily added as owner so you can claim land.");
               sender.sendMessage(ChatColor.GRAY + "After claiming, run /flag add to set the flag (this will auto-remove you as owner).");
               giveClaimWand(sender);
               sender.sendMessage(ChatColor.GREEN + "Now claim the area with the wand. After that, run:");
               sender.sendMessage(ChatColor.YELLOW + "   /flag add " + factionName + " <flag>");
               sender.sendMessage(ChatColor.GRAY + "(Use /flag list to see available flags like SAFE_ZONE, ROAD, etc.)");
            } else {
               sender.sendMessage(ChatColor.RED + "ERROR: Failed to add you to the system faction. Tell an admin to check the console.");
               Foxtrot.getInstance()
                  .getLogger()
                  .warning(
                     "[TeamSystemClaimCommand] Failed to add "
                        + sender.getName()
                        + " to system faction '"
                        + factionName
                        + "'. getTeam returned: "
                        + (verifyTeam == null ? "null" : verifyTeam.getName())
                  );
            }
         }
      } else {
         sender.sendMessage(ChatColor.RED + "Usage: /f system claim <faction name>");
      }
   }

   private static void giveClaimWand(final Player sender) {
      sender.getInventory().remove(TeamClaimCommand.SELECTION_WAND);
      int slot = -1;

      for (int i = 0; i < 9; i++) {
         if (sender.getInventory().getItem(i) == null) {
            slot = i;
            break;
         }
      }

      if (slot == -1) {
         sender.getWorld().dropItemNaturally(sender.getLocation(), TeamClaimCommand.SELECTION_WAND.clone());
         sender.sendMessage(ChatColor.YELLOW + "Your hotbar was full, dropped the claim wand at your feet.");
      } else {
         final int finalSlot = slot;
         (new BukkitRunnable() {
            @Override
            public void run() {
               sender.getInventory().setItem(finalSlot, TeamClaimCommand.SELECTION_WAND.clone());
            }
         }).runTaskLater(Foxtrot.getInstance(), 1L);
      }

      new VisualClaim(sender, VisualClaimType.CREATE, true).draw(false);
      if (!VisualClaim.getCurrentMaps().containsKey(sender.getName())) {
         new VisualClaim(sender, VisualClaimType.MAP, true).draw(true);
      }
   }

   @Command(
      names = {"team system claim disband", "t system claim disband", "f system claim disband", "faction system claim disband", "fac system claim disband"},
      permission = "op"
   )
   public static void systemClaimDisband(Player sender, @Parameter(name = "faction name", wildcard = true) String factionName) {
      if (factionName != null && !factionName.trim().isEmpty()) {
         factionName = factionName.trim();
         Team team = Foxtrot.getInstance().getTeamHandler().getTeam(factionName);
         if (team == null) {
            sender.sendMessage(ChatColor.RED + "No faction found with name '" + factionName + "'.");
         } else {
            int claimsRemoved = team.getClaims().size();
            LandBoard.getInstance().clear(team);
            team.getClaims().clear();

            for (UUID member : new ArrayList<>(team.getMembers())) {
               Foxtrot.getInstance().getTeamHandler().setTeam(member, null);
               team.removeMember(member);
            }

            team.disband();
            Foxtrot.getInstance().getTeamHandler().removeTeam(team);
            sender.sendMessage(
               ChatColor.GREEN + "Disbanded system faction '" + ChatColor.WHITE + factionName + ChatColor.GREEN + "' (" + claimsRemoved + " claims removed)."
            );
         }
      } else {
         sender.sendMessage(ChatColor.RED + "Usage: /f system claim disband <faction name>");
      }
   }
}
