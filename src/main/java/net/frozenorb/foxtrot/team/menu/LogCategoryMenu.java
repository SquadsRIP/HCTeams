package net.frozenorb.foxtrot.team.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import net.frozenorb.foxtrot.team.track.TrackCategory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class LogCategoryMenu extends PaginatedMenu {
   private Team team;
   private TrackCategory trackCategory;
   private Map<TeamActionType, Map<String, Object>> logs;

   @Override
   public String getPrePaginatedTitle(Player player) {
      return "Team Logs";
   }

   @Override
   public Map<Integer, Button> getAllPagesButtons(Player player) {
      Map<Integer, Button> toReturn = new HashMap<>();

      for (final Entry<TeamActionType, Map<String, Object>> entry : this.logs.entrySet()) {
         toReturn.put(toReturn.size(), new Button() {
            @Override
            public String getName(Player player) {
               return ChatColor.DARK_RED + ChatColor.BOLD.toString() + entry.getKey().getFancyName();
            }

            @Override
            public List<String> getDescription(Player player) {
               List<String> toReturnx = new ArrayList<>();
               Map<String, Object> params = entry.getValue();

               for (Entry<String, Object> entry2 : entry.getValue().entrySet()) {
                  toReturnx.add(entry2.getKey() + " <-- Result: " + entry2.getValue());
               }

               return toReturnx;
            }

            @Override
            public Material getMaterial(Player player) {
               return Material.PAPER;
            }
         });
      }

      return toReturn;
   }

   @ConstructorProperties({"team", "trackCategory", "logs"})
   public LogCategoryMenu(Team team, TrackCategory trackCategory, Map<TeamActionType, Map<String, Object>> logs) {
      this.team = team;
      this.trackCategory = trackCategory;
      this.logs = logs;
   }
}
