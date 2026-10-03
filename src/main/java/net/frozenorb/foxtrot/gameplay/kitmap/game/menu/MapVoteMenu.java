package net.frozenorb.foxtrot.gameplay.kitmap.game.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import net.frozenorb.foxtrot.gameplay.kitmap.game.Game;
import net.frozenorb.foxtrot.gameplay.kitmap.game.arena.GameArena;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class MapVoteMenu extends Menu {
   private Game game;

   @Override
   public String getTitle(Player player) {
      return "Map Votes";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      HashMap<Integer, Button> buttons = new HashMap<>();

      for (Entry<GameArena, AtomicInteger> entry : this.game.getArenaOptions().entrySet()) {
         buttons.put(buttons.size(), new MapVoteMenu.MapButton(entry.getKey(), entry.getValue()));
      }

      return buttons;
   }

   @ConstructorProperties("game")
   public MapVoteMenu(Game game) {
      this.game = game;
   }

   private class MapButton extends Button {
      private GameArena arena;
      private AtomicInteger votes;

      @Override
      public String getName(Player player) {
         return ChatColor.YELLOW.toString() + ChatColor.BOLD + this.arena.getName();
      }

      @Override
      public List<String> getDescription(Player player) {
         ArrayList<String> description = new ArrayList<>();
         description.add(ChatColor.GRAY.toString() + "This map has " + ChatColor.GREEN + this.votes.get() + ChatColor.GRAY + " votes.");
         if (MapVoteMenu.this.game.getPlayerVotes().containsKey(player.getUniqueId())
            && MapVoteMenu.this.game.getPlayerVotes().get(player.getUniqueId()) == this.arena) {
            description.add(ChatColor.GRAY.toString() + "You voted for this map!");
         }

         return description;
      }

      @Override
      public Material getMaterial(Player player) {
         return Material.EMPTY_MAP;
      }

      @Override
      public void clicked(Player player, int slot, ClickType clickType) {
      }

      @ConstructorProperties({"arena", "votes"})
      public MapButton(GameArena arena, AtomicInteger votes) {
         this.arena = arena;
         this.votes = votes;
      }
   }
}
