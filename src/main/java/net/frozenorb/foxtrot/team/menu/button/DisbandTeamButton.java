package net.frozenorb.foxtrot.team.menu.button;

import cc.fyre.proton.menu.Button;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.commands.ForceDisbandCommand;
import net.frozenorb.foxtrot.team.menu.ConfirmMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class DisbandTeamButton extends Button {
   private Team team;

   @Override
   public void clicked(Player player, int i, ClickType clickType) {
      new ConfirmMenu("Disband?", b -> {
         if (b) {
            ForceDisbandCommand.forceDisband(player, this.team);
         }
      }).openMenu(player);
   }

   @Override
   public String getName(Player player) {
      return "§c§lDisband Team";
   }

   @Override
   public List<String> getDescription(Player player) {
      return new ArrayList<>();
   }

   @Override
   public byte getDamageValue(Player player) {
      return 0;
   }

   @Override
   public Material getMaterial(Player player) {
      return Material.TNT;
   }

   @ConstructorProperties("team")
   public DisbandTeamButton(Team team) {
      this.team = team;
   }
}
