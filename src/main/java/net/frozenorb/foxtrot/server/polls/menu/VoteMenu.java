package net.frozenorb.foxtrot.server.polls.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import cc.fyre.proton.util.ItemBuilder;
import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.polls.Poll;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class VoteMenu extends PaginatedMenu {
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
                        ChatColor.WHITE
                           + option
                           + ChatColor.GRAY
                           + " ["
                           + VoteMenu.this.poll.getVotes().values().stream().filter(it -> it.equalsIgnoreCase(option)).count()
                           + " Votes]"
                     )
                     .setLore(Arrays.asList("", ChatColor.GRAY + "Click to vote for this poll."))
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
                  player.closeInventory();
                  if (!VoteMenu.this.poll.isActive()) {
                     player.sendMessage(ChatColor.RED + "This poll isn't open at this time!");
                  } else if (VoteMenu.this.poll.getVotes().containsKey(player.getUniqueId())
                     && VoteMenu.this.poll.getVotes().get(player.getUniqueId()).equalsIgnoreCase(option)) {
                     player.sendMessage(ChatColor.RED + "You have already voted for this option!");
                  } else {
                     if (VoteMenu.this.poll.getVotes().containsKey(player.getUniqueId())
                        && !VoteMenu.this.poll.getVotes().get(player.getUniqueId()).equalsIgnoreCase(option)) {
                        VoteMenu.this.poll.getVotes().remove(player.getUniqueId());
                     }

                     player.sendMessage(ChatColor.GREEN + "You successfully voted for " + option + ".");
                     FancyMessage fancyMessage = new FancyMessage(
                        ChatColor.translate(player.getName() + " &ahas voted &f" + option + " &aon the &f" + VoteMenu.this.poll.getQuestion() + " &apoll.")
                     );
                     fancyMessage.tooltip(ChatColor.GREEN + "Click here to vote on this poll");
                     fancyMessage.command("/polls");

                     for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                        fancyMessage.send(onlinePlayer);
                     }

                     VoteMenu.this.poll.getVotes().put(player.getUniqueId(), option);
                  }
               }
            }
         );
      }

      return toReturn;
   }

   @Override
   public boolean isAutoUpdate() {
      return true;
   }

   @Override
   public String getPrePaginatedTitle(Player player) {
      return "Vote on Poll";
   }

   @ConstructorProperties("poll")
   public VoteMenu(Poll poll) {
      this.poll = poll;
   }
}
