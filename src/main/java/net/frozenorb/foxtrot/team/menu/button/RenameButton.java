package net.frozenorb.foxtrot.team.menu.button;

import cc.fyre.proton.menu.Button;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.commands.TeamManageCommand;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class RenameButton extends Button {
   private Team team;

   @Override
   public void clicked(Player player, int i, ClickType clickType) {
      player.closeInventory();
      TeamManageCommand.renameTeam(player, this.team);
   }

   @Override
   public String getName(Player player) {
      return "§eRename Team";
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
      return Material.SIGN;
   }

   @ConstructorProperties("team")
   public RenameButton(Team team) {
      this.team = team;
   }
}
