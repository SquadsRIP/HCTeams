package net.frozenorb.foxtrot.gameplay.extra.settings;

import cc.fyre.neutron.util.ColorUtil;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class NameTagColorMenu extends Menu {
   private NameTagType nameTagType;

   @Override
   public String getTitle(Player player) {
      return "Please choose a color";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      Map<Integer, Button> toReturn = new HashMap<>();

      for (final ChatColor chatColor : ChatColor.values()) {
         if (chatColor.ordinal() <= 15) {
            toReturn.put(
               toReturn.size(),
               new Button() {
                  @Override
                  public String getName(Player player) {
                     return chatColor + chatColor.name();
                  }

                  @Override
                  public List<String> getDescription(Player player) {
                     List<String> toReturnx = new ArrayList<>();
                     toReturnx.add("");
                     toReturnx.add(ChatColor.GOLD + "Display: " + chatColor + "Player123");
                     return toReturnx;
                  }

                  @Override
                  public Material getMaterial(Player player) {
                     return Material.INK_SACK;
                  }

                  @Override
                  public byte getDamageValue(Player player) {
                     return ColorUtil.COLOR_MAP.get(chatColor).getDyeData();
                  }

                  @Override
                  public void clicked(Player player, int slot, ClickType clickType) {
                     switch (NameTagColorMenu.this.nameTagType) {
                        case TEAM:
                           Foxtrot.getInstance().getTeamColorMap().setChatColor(player.getUniqueId(), chatColor);
                           break;
                        case ENEMY:
                           Foxtrot.getInstance().getEnemyColorMap().setChatColor(player.getUniqueId(), chatColor);
                           break;
                        case ARCHER_TAGS:
                           Foxtrot.getInstance().getArcherTagColorMap().setChatColor(player.getUniqueId(), chatColor);
                           break;
                        case TEAM_FOCUS:
                           Foxtrot.getInstance().getTeamFocusColorMap().setChatColor(player.getUniqueId(), chatColor);
                           break;
                        case FOCUS:
                           Foxtrot.getInstance().getFocusColorMap().setChatColor(player.getUniqueId(), chatColor);
                     }

                     new NameTagMenu().openMenu(player);
                     if (!CustomTimerCreateCommand.isSOTWTimer()) {
                        Foxtrot.getInstance()
                           .getServer()
                           .getScheduler()
                           .runTaskAsynchronously(Foxtrot.getInstance(), () -> LunarClientListener.updateNametag(player));
                     }
                  }
               }
            );
         }
      }

      return toReturn;
   }

   @Override
   public void onClose(Player player) {
      Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> new NameTagMenu().openMenu(player), 1L);
   }

   @ConstructorProperties("nameTagType")
   public NameTagColorMenu(NameTagType nameTagType) {
      this.nameTagType = nameTagType;
   }

   public NameTagType getNameTagType() {
      return this.nameTagType;
   }
}
