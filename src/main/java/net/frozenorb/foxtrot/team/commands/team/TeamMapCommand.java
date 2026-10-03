package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.team.claims.VisualClaim;
import net.frozenorb.foxtrot.team.claims.VisualClaimType;
import org.bukkit.entity.Player;

public class TeamMapCommand {
   @Command(names = {"team map", "t map", "f map", "faction map", "fac map", "map"}, permission = "")
   public static void teamMap(Player sender) {
      new VisualClaim(sender, VisualClaimType.MAP, false).draw(false);
   }
}
