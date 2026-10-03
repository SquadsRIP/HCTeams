package net.frozenorb.foxtrot.team.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.UUIDUtils;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.menu.button.KickPlayerButton;
import org.bukkit.entity.Player;

public class KickPlayersMenu extends Menu {
   @NonNull
   Team team;

   @Override
   public String getTitle(Player player) {
      return "Players in " + this.team.getName();
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      HashMap<Integer, Button> buttons = new HashMap<>();
      int index = 0;
      ArrayList<UUID> uuids = new ArrayList<>();
      uuids.addAll(this.team.getMembers());
      Collections.sort(uuids, (u1, u2) -> UUIDUtils.name(u1).toLowerCase().compareTo(UUIDUtils.name(u2).toLowerCase()));

      for (UUID uuid : uuids) {
         buttons.put(index, new KickPlayerButton(uuid, this.team));
         index++;
      }

      return buttons;
   }

   @Override
   public boolean isAutoUpdate() {
      return true;
   }

   @ConstructorProperties("team")
   public KickPlayersMenu(@NonNull Team team) {
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
