package net.frozenorb.foxtrot.server.polls.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import cc.fyre.proton.util.ItemBuilder;
import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenorb.foxtrot.server.polls.Poll;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class PollResultsMenu extends PaginatedMenu {
   private Poll poll;

   @Override
   public Map<Integer, Button> getAllPagesButtons(Player player) {
      Map<Integer, Button> toReturn = new HashMap<>();

      for (final String option : this.poll.getOptions()) {
         toReturn.put(
            toReturn.size(),
            new Button() {
               @Override
               public ItemStack getButtonItem(Player player) {
                  return ItemBuilder.of(Material.PAPER)
                     .name(
                        option
                           + " "
                           + ChatColor.GRAY
                           + "["
                           + PollResultsMenu.this.poll.getVotes().values().stream().filter(it -> it.equalsIgnoreCase(option)).count()
                           + "]"
                     )
                     .build();
               }

               @Override
               public String getName(Player player) {
                  return null;
               }

               @Override
               public List<String> getDescription(Player player) {
                  return null;
               }

               @Override
               public Material getMaterial(Player player) {
                  return null;
               }

               @Override
               public void clicked(Player player, int i, ClickType clickType) {
                  new PollEditMenu(PollResultsMenu.this.poll);
               }
            }
         );
      }

      return toReturn;
   }

   @Override
   public String getPrePaginatedTitle(Player player) {
      return ChatColor.DARK_GRAY + "Poll Result";
   }

   @ConstructorProperties("poll")
   public PollResultsMenu(Poll poll) {
      this.poll = poll;
   }
}
