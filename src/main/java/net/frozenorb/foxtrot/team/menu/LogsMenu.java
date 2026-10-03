package net.frozenorb.foxtrot.team.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import net.frozenorb.foxtrot.team.track.TrackCategory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class LogsMenu extends Menu {
   private Team team;
   private Map<TeamActionType, Map<String, Object>> logs;

   @Override
   public String getTitle(Player player) {
      return "Team Logs";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      Map<Integer, Button> toReturn = new HashMap<>();

      for (int i = 0; i < 45; i++) {
         toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)15, ""));
      }

      for (final TrackCategory value : TrackCategory.values()) {
         if (value != TrackCategory.STAFF) {
            toReturn.put(value.getSlot(), new Button() {
               @Override
               public String getName(Player player) {
                  return ChatColor.DARK_RED + ChatColor.BOLD.toString() + value.getDisplayName();
               }

               @Override
               public List<String> getDescription(Player player) {
                  int logCount = LogsMenu.this.logs.values().size();
                  List<String> toReturnx = new ArrayList<>(value.getLore());
                  toReturnx.add("");
                  toReturnx.add(ChatColor.translate("&4&l┃ &fLogs: &c" + logCount));
                  toReturnx.add("");
                  if (value == TrackCategory.ALL) {
                     toReturnx.add(ChatColor.RED + "Click to view all logs");
                  } else {
                     toReturnx.add(ChatColor.RED + "Click to view all logs in this category");
                  }

                  return toReturnx;
               }

               @Override
               public byte getDamageValue(Player player) {
                  return value.getData();
               }

               @Override
               public Material getMaterial(Player player) {
                  return value.getMaterial();
               }

               @Override
               public void clicked(Player player, int slot, ClickType clickType) {
                  Map<TeamActionType, Map<String, Object>> params = new HashMap<>();
                  LogsMenu.this.logs.entrySet().stream().filter(it -> value == TrackCategory.ALL || it.getKey().getTrackCategory() == value).forEach(it -> {
                     Map var10000 = params.put(it.getKey(), it.getValue());
                  });
                  new LogCategoryMenu(LogsMenu.this.team, value, params).openMenu(player);
               }
            });
         }
      }

      return toReturn;
   }

   @ConstructorProperties({"team", "logs"})
   public LogsMenu(Team team, Map<TeamActionType, Map<String, Object>> logs) {
      this.team = team;
      this.logs = logs;
   }
}
