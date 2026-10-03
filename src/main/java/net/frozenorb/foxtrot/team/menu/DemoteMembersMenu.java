package net.frozenorb.foxtrot.team.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.menu.button.ChangePromotionStatusButton;
import org.bukkit.entity.Player;

public class DemoteMembersMenu extends Menu {
   @NonNull
   Team team;

   @Override
   public String getTitle(Player player) {
      return "Demote captains/co-leaders";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      HashMap<Integer, Button> buttons = new HashMap<>();
      int index = 0;

      for (UUID uuid : this.team.getColeaders()) {
         buttons.put(index, new ChangePromotionStatusButton(uuid, this.team, false));
         index++;
      }

      for (UUID uuid : this.team.getCaptains()) {
         buttons.put(index, new ChangePromotionStatusButton(uuid, this.team, false));
         index++;
      }

      return buttons;
   }

   @Override
   public boolean isAutoUpdate() {
      return true;
   }

   @ConstructorProperties("team")
   public DemoteMembersMenu(@NonNull Team team) {
      if (team == null) {
         throw new NullPointerException("team");
      }

      this.team = team;
   }

   @NonNull
   public Team getTeam() {
      return this.team;
   }
}
