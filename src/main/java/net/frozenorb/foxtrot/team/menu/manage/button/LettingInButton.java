package net.frozenorb.foxtrot.team.menu.manage.button;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.minecraft.util.com.google.common.primitives.Ints;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.NullConversationPrefix;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class LettingInButton extends Button {
   private Team team;
   private Menu menu;

   @Override
   public String getName(Player player) {
      return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Letting In";
   }

   @Override
   public List<String> getDescription(Player player) {
      List<String> toReturn = new ArrayList<>();
      toReturn.add(ChatColor.GRAY + "Signal to everyone that you're");
      toReturn.add(ChatColor.GRAY + "letting players into your base!");
      toReturn.add("");
      toReturn.add(ChatColor.translate("&4&l┃ &fStatus: &cDisabled"));
      toReturn.add("");
      toReturn.add(ChatColor.GREEN + "Click to signal players that you're letting in");
      return toReturn;
   }

   @Override
   public Material getMaterial(Player player) {
      return Material.WOOD;
   }

   @Override
   public void clicked(final Player player, int slot, ClickType clickType) {
      player.closeInventory();
      player.beginConversation(
         new ConversationFactory(Foxtrot.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {
            @Override
            public String getPromptText(ConversationContext context) {
               return ChatColor.translate("&cType your base height in chat");
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
               Integer number = Ints.tryParse(s);
               if (number == null) {
                  cc.getForWhom().sendRawMessage(ChatColor.RED + s + " is not a number.");
                  return END_OF_CONVERSATION;
               } else {
                  Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                     player.chat("/lettingin " + number);
                     LettingInButton.this.menu.openMenu(player);
                  }, 2L);
                  return Prompt.END_OF_CONVERSATION;
               }
            }
         }).withLocalEcho(false).withEscapeSequence("quit").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!").buildConversation(player)
      );
   }

   @ConstructorProperties({"team", "menu"})
   public LettingInButton(Team team, Menu menu) {
      this.team = team;
      this.menu = menu;
   }
}
