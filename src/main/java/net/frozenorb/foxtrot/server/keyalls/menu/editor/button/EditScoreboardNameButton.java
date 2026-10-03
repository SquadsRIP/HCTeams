package net.frozenorb.foxtrot.server.keyalls.menu.editor.button;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.List;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.keyalls.KeyAll;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class EditScoreboardNameButton extends Button {
   private Menu menu;
   private KeyAll keyAll;

   @Override
   public String getName(Player player) {
      return ChatColor.GOLD + ChatColor.BOLD.toString() + "Scoreboard Display Name";
   }

   @Override
   public List<String> getDescription(Player player) {
      return Arrays.asList(
         "", ChatColor.translate("&6&l┃ &fCurrent: &e" + this.keyAll.getDisplayName()), "", ChatColor.GREEN + "Click to modify this key-all's scoreboard name"
      );
   }

   @Override
   public Material getMaterial(Player player) {
      return Material.COMMAND;
   }

   @Override
   public void clicked(Player player, int slot, ClickType clickType) {
      player.closeInventory();
      ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance())
         .withFirstPrompt(new EditScoreboardNameButton.DisplayNamePrompt(this.menu, this.keyAll))
         .withLocalEcho(false);
      player.beginConversation(factory.buildConversation(player));
   }

   @ConstructorProperties({"menu", "keyAll"})
   public EditScoreboardNameButton(Menu menu, KeyAll keyAll) {
      this.menu = menu;
      this.keyAll = keyAll;
   }

   public static class DisplayNamePrompt extends StringPrompt {
      private Menu menu;
      private KeyAll keyAll;

      @Override
      public String getPromptText(ConversationContext conversationContext) {
         return ChatColor.YELLOW
            + "Please type the new display name for the scoreboard keyall. Type "
            + ChatColor.RED
            + "cancel"
            + ChatColor.YELLOW
            + " to cancel.";
      }

      @Override
      public Prompt acceptInput(ConversationContext conversationContext, String input) {
         Player sender = (Player)conversationContext.getForWhom();
         if (input.equalsIgnoreCase("cancel")) {
            this.menu.openMenu(sender);
            conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "Key-All scoreboard display name process cancelled.");
            return END_OF_CONVERSATION;
         } else {
            this.menu.openMenu(sender);
            this.keyAll.setScoreboardDisplay(input);
            Foxtrot.getInstance().getKeyAllHandler().getCache().replace(input, this.keyAll);
            conversationContext.getForWhom()
               .sendRawMessage(ChatColor.translate("&6Set the scoreboard display-name for &f" + this.keyAll.getId() + " &6to &f" + input));
            return Prompt.END_OF_CONVERSATION;
         }
      }

      @ConstructorProperties({"menu", "keyAll"})
      public DisplayNamePrompt(Menu menu, KeyAll keyAll) {
         this.menu = menu;
         this.keyAll = keyAll;
      }

      public Menu getMenu() {
         return this.menu;
      }

      public KeyAll getKeyAll() {
         return this.keyAll;
      }
   }
}
