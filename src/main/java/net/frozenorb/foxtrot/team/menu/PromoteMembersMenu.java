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

public class PromoteMembersMenu extends Menu {
   @NonNull
   Team team;

   @Override
   public String getTitle(Player player) {
      return "Members of " + this.team.getName();
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      HashMap<Integer, Button> buttons = new HashMap<>();
      int index = 0;

      for (UUID uuid : this.team.getMembers()) {
         if (!this.team.isOwner(uuid) && !this.team.isCoLeader(uuid)) {
            buttons.put(index, new ChangePromotionStatusButton(uuid, this.team, true));
            index++;
         }
      }

      return buttons;
   }

   @Override
   public boolean isAutoUpdate() {
      return true;
   }

   @ConstructorProperties("team")
   public PromoteMembersMenu(@NonNull Team team) {
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
