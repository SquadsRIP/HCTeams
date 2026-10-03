package net.frozenorb.foxtrot.gameplay.events.outposts.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.outposts.data.Outpost;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.ProgressBarUtil;
import net.minecraft.util.com.google.common.util.concurrent.AtomicDouble;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class OutpostMenu extends Menu {
   @Override
   public String getTitle(Player player) {
      return "Outpost";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      Map<Integer, Button> toReturn = new HashMap<>();

      for (final Outpost outpost : Foxtrot.getInstance().getOutpostHandler().getOutposts()) {
         toReturn.put(
            outpost.getSlot(),
            new Button() {
               @Override
               public String getName(Player player) {
                  return outpost.getDisplayName().contains("Outpost") ? outpost.getDisplayName() : outpost.getDisplayName() + " Outpost";
               }

               @Override
               public List<String> getDescription(Player player) {
                  List<String> toReturnx = new ArrayList<>();
                  ChatColor displayColor = outpost.getDisplayColor();
                  toReturnx.add("");
                  toReturnx.add(ChatColor.translate(displayColor + "&lCaptured Benefits"));

                  for (String benefit : outpost.getBenefits()) {
                     toReturnx.add(ChatColor.translate(displayColor + "┃ &f" + benefit));
                  }

                  toReturnx.add("");
                  Team team = outpost.findTeam();
                  toReturnx.add(ChatColor.translate(displayColor + "&lInformation"));
                  toReturnx.add(ChatColor.translate(displayColor + "┃ &fStatus: " + outpost.getStatus().getDisplayName()));
                  if (team != null && team.getHQ() != null) {
                     toReturnx.add(
                        ChatColor.translate(
                           displayColor
                              + "┃ &fLocation: "
                              + displayColor
                              + team.getHQ().getBlockX()
                              + ", "
                              + team.getHQ().getBlockZ()
                              + ChatColor.GRAY
                              + " ["
                              + WordUtils.capitalize(team.getHQ().getWorld().getEnvironment().name().replace("_", " "))
                              + "]"
                        )
                     );
                  }

                  Team controller = outpost.findController();
                  toReturnx.add(
                     ChatColor.translate(displayColor + "┃ &fController: " + displayColor + (controller != null ? controller.getName(player) : "&cNone"))
                  );
                  List<Team> attackers = outpost.findAttackers();
                  if (attackers.isEmpty()) {
                     toReturnx.add(ChatColor.translate(displayColor + "┃ &fAttackers: &cNone"));
                  } else {
                     toReturnx.add(
                        ChatColor.translate(
                           displayColor
                              + "┃ &fAttacker"
                              + (attackers.size() == 1 ? "" : "s")
                              + ": "
                              + displayColor
                              + attackers.stream().map(faction -> "&c" + faction.getName()).collect(Collectors.joining(","))
                        )
                     );
                  }

                  AtomicDouble percentage = outpost.getPercentage();
                  toReturnx.add(
                     ChatColor.translate(
                        displayColor
                           + "┃ &fPercentage: "
                           + displayColor
                           + (percentage.get() == 0.0 ? ChatColor.RED : ChatColor.GREEN)
                           + String.format("%.2f", percentage.get())
                           + "%"
                     )
                  );
                  toReturnx.add(
                     ChatColor.translate(
                        displayColor
                           + "┃ &fProgress: &7["
                           + ProgressBarUtil.getProgressBar(Math.min(outpost.getPercentage().intValue(), 100), 100, 20)
                           + " &7]"
                     )
                  );
                  toReturnx.add("");
                  toReturnx.add(ChatColor.GREEN + "Click to focus the Outpost faction.");
                  return toReturnx;
               }

               @Override
               public Material getMaterial(Player player) {
                  return outpost.getMaterial();
               }

               @Override
               public void clicked(Player player, int slot, ClickType clickType) {
                  player.chat("/f focus " + outpost.getFactionName());
               }
            }
         );
      }

      return toReturn;
   }

   @Override
   public int size(Player player) {
      return 27;
   }

   @Override
   public boolean isPlaceholder() {
      return true;
   }
}
