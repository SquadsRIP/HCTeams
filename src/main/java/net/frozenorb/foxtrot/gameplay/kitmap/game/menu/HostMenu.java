package net.frozenorb.foxtrot.gameplay.kitmap.game.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.config.ConfigMessages;
import net.frozenorb.foxtrot.gameplay.kitmap.game.Game;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameType;
import net.frozenorb.foxtrot.util.Formats;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class HostMenu extends Menu {
   @Override
   public String getTitle(Player player) {
      return "Host an Event";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      HashMap<Integer, Button> buttons = new HashMap<>();

      for (GameType gameType : GameType.values()) {
         buttons.put(buttons.size(), new HostMenu.GameTypeButton(gameType));
      }

      return buttons;
   }

   private class GameTypeButton extends Button {
      private final GameType gameType;

      @Override
      public String getName(Player player) {
         return ChatColor.GOLD.toString() + ChatColor.BOLD + this.gameType.getDisplayName();
      }

      @Override
      public List<String> getDescription(Player player) {
         ArrayList<String> description = new ArrayList<>(Formats.renderLines(ChatColor.GRAY.toString(), this.gameType.getDescription()));
         description.add("");
         if (this.gameType.isDisabled()) {
            description.add(ChatColor.GOLD.toString() + ChatColor.BOLD + "DISABLED");
            description.add(ChatColor.GOLD + "This event has been disabled!");
         } else if (this.gameType.canHost(player)) {
            description.add(ChatColor.GREEN + "Click to host this event!");
         } else {
            description.add("");
            description.add(ChatColor.YELLOW + "You must purchase " + this.gameType.getRank() + ChatColor.YELLOW + " to host this event!");
            description.add(ChatColor.GRAY + "You may purchase this rank at " + ConfigMessages.link("store", "Coming Soon!"));
         }

         return description;
      }

      @Override
      public Material getMaterial(Player player) {
         return this.gameType.getIcon().getType();
      }

      @Override
      public void clicked(Player player, int slot, ClickType clickType) {
         if (clickType.isLeftClick()) {
            if (this.gameType.isDisabled()) {
               player.sendMessage(ChatColor.YELLOW + "That event is temporarily disabled!");
               return;
            }

            if (!this.gameType.canHost(player)) {
               player.sendMessage(ChatColor.YELLOW + "You don't have permission to host " + this.gameType.getDisplayName() + " events.");
               return;
            }

            if (!Foxtrot.getInstance().getMapHandler().getGameHandler().canStartGame(player, this.gameType)) {
               return;
            }

            try {
               player.closeInventory();
               Game game = Foxtrot.getInstance().getMapHandler().getGameHandler().startGame(player, this.gameType);
               game.addPlayer(player);
               player.sendMessage(ChatColor.GREEN + "Started " + this.gameType.getDisplayName() + "! You can use /start to forcefully start the event.");
            } catch (IllegalStateException e) {
               player.sendMessage(ChatColor.YELLOW.toString() + e.getMessage());
            }
         }
      }

      @ConstructorProperties("gameType")
      public GameTypeButton(GameType gameType) {
         this.gameType = gameType;
      }
   }
}
