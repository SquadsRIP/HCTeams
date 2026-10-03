package net.frozenorb.foxtrot.server.polls.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import cc.fyre.proton.util.ItemBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.polls.Poll;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.NullConversationPrefix;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class PollListMenu extends PaginatedMenu {
   @Override
   public Map<Integer, Button> getAllPagesButtons(Player player) {
      Map<Integer, Button> toReturn = new HashMap<>();

      for (final Poll poll : Foxtrot.getInstance().getPollHandler().getPolls()) {
         toReturn.put(
            toReturn.size(),
            new Button() {
               @Override
               public ItemStack getButtonItem(Player player) {
                  return ItemBuilder.of(Material.WOOL)
                     .data(poll.isActive() ? DyeColor.GREEN.getWoolData() : DyeColor.RED.getWoolData())
                     .name(ChatColor.WHITE + poll.getQuestion())
                     .setLore(Arrays.asList("", ChatColor.GRAY + "Click to view poll details."))
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
                  new PollEditMenu(poll).openMenu(player);
               }
            }
         );
      }

      toReturn.put(
         toReturn.size(),
         new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
               return ItemBuilder.of(Material.NETHER_STAR)
                  .name(ChatColor.GREEN + ChatColor.BOLD.toString() + "Create new poll")
                  .setLore(Arrays.asList("", ChatColor.GRAY + "Click to create a new poll."))
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
            public void clicked(final Player player, int i, ClickType clickType) {
               player.closeInventory();
               ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance())
                  .withModality(true)
                  .withPrefix(new NullConversationPrefix())
                  .withFirstPrompt(new StringPrompt() {
                     @Override
                     public String getPromptText(ConversationContext context) {
                        return "§6Type the question";
                     }

                     @Override
                     public Prompt acceptInput(ConversationContext cc, String s) {
                        player.sendMessage(ChatColor.GOLD + "Created a new poll, question is " + ChatColor.WHITE + s + ChatColor.GOLD + ".");
                        new CreatePollMenu(s, new ArrayList<>()).openMenu(player);
                        return Prompt.END_OF_CONVERSATION;
                     }
                  })
                  .withLocalEcho(false)
                  .withEscapeSequence("/null")
                  .withTimeout(20)
                  .thatExcludesNonPlayersWithMessage("Go away evil console!");
               player.beginConversation(factory.buildConversation(player));
            }
         }
      );
      return toReturn;
   }

   @Override
   public String getPrePaginatedTitle(Player player) {
      return ChatColor.DARK_GRAY + "Poll List";
   }
}
