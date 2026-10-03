package net.frozenorb.foxtrot.team.menu.manage;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Map;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.menu.manage.button.LogsButton;
import net.frozenorb.foxtrot.team.menu.manage.button.MemberButton;
import net.frozenorb.foxtrot.team.menu.manage.button.PointsButton;
import net.frozenorb.foxtrot.team.menu.manage.button.SettingsButton;
import net.frozenorb.foxtrot.team.menu.manage.button.StatisticsButton;
import net.frozenorb.foxtrot.team.menu.manage.button.UpgradesButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ManageMenu extends Menu {
   private Team team;

   @Override
   public String getTitle(Player player) {
      return "Manage Faction";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      HashMap<Integer, Button> toReturn = new HashMap<>();

      for (int i = 0; i < 45; i++) {
         toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)15, ""));
      }

      toReturn.put(11, new MemberButton(this.team));
      toReturn.put(13, new LogsButton(this.team));
      toReturn.put(15, new PointsButton(this.team));
      toReturn.put(28, new SettingsButton(this.team));
      toReturn.put(30, new UpgradesButton(this.team));
      toReturn.put(34, new StatisticsButton(this.team));
      return toReturn;
   }

   @ConstructorProperties("team")
   public ManageMenu(Team team) {
      this.team = team;
   }
}
