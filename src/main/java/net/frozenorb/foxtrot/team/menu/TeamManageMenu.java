package net.frozenorb.foxtrot.team.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Map;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.menu.button.DisbandTeamButton;
import net.frozenorb.foxtrot.team.menu.button.OpenKickMenuButton;
import net.frozenorb.foxtrot.team.menu.button.OpenMuteMenuButton;
import net.frozenorb.foxtrot.team.menu.button.RenameButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class TeamManageMenu extends Menu {
   private Team team;

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      HashMap<Integer, Button> buttons = new HashMap<>();

      for (int i = 0; i < 9; i++) {
         if (i == 1) {
            buttons.put(i, new RenameButton(this.team));
         } else if (i == 3) {
            buttons.put(i, new OpenMuteMenuButton(this.team));
         } else if (i == 5) {
            buttons.put(i, new OpenKickMenuButton(this.team));
         } else if (i == 7) {
            buttons.put(i, new DisbandTeamButton(this.team));
         } else {
            buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)14));
         }
      }

      return buttons;
   }

   @Override
   public String getTitle(Player player) {
      return "Manage " + this.team.getName();
   }

   @ConstructorProperties("team")
   public TeamManageMenu(Team team) {
      this.team = team;
   }
}
