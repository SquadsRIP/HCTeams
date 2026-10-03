package net.frozenorb.foxtrot.server.keyalls.menu.editor.button;

import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.util.DurationWrapper;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.Date;
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

public class EditEndButton extends Button {
   private Menu parentMenu;
   private KeyAll keyAll;

   @Override
   public String getName(Player player) {
      return ChatColor.GOLD + ChatColor.BOLD.toString() + "End Date";
   }

   @Override
   public List<String> getDescription(Player player) {
      return Arrays.asList(
         "",
         ChatColor.translate("&6&l┃ &fCurrent: &e" + new Date(this.keyAll.getEnd()).toLocaleString()),
         "",
         ChatColor.GREEN + "Click to modify this key-all's end date."
      );
   }

   @Override
   public Material getMaterial(Player player) {
      return Material.REDSTONE;
   }

   @Override
   public void clicked(Player player, int slot, ClickType clickType) {
      player.closeInventory();
      ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance())
         .withFirstPrompt(new EditEndButton.DatePrompt(this.parentMenu, this.keyAll))
         .withLocalEcho(false);
      player.beginConversation(factory.buildConversation(player));
   }

   @ConstructorProperties({"parentMenu", "keyAll"})
   public EditEndButton(Menu parentMenu, KeyAll keyAll) {
      this.parentMenu = parentMenu;
      this.keyAll = keyAll;
   }

   public static class DatePrompt extends StringPrompt {
      private Menu menu;
      private KeyAll keyAll;

      @Override
      public String getPromptText(ConversationContext conversationContext) {
         return ChatColor.YELLOW + "Please type the end duration. Type " + ChatColor.RED + "cancel" + ChatColor.YELLOW + " to cancel.";
      }

      @Override
      public Prompt acceptInput(ConversationContext conversationContext, String input) {
         Player sender = (Player)conversationContext.getForWhom();
         if (input.equalsIgnoreCase("cancel")) {
            this.menu.openMenu(sender);
            conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "Process cancelled.");
            return END_OF_CONVERSATION;
         } else {
            this.menu.openMenu(sender);
            DurationWrapper durationWrapper = NeutronConstants.findDurationWrapper(input);
            if (durationWrapper.isPermanent()) {
               conversationContext.getForWhom().sendRawMessage(ChatColor.RED + "Date process cancelled as you typed an invalid start date.");
               return END_OF_CONVERSATION;
            } else {
               this.keyAll.setEnd(System.currentTimeMillis() + durationWrapper.getDuration());
               Foxtrot.getInstance().getKeyAllHandler().getCache().replace(input, this.keyAll);
               conversationContext.getForWhom()
                  .sendRawMessage(
                     ChatColor.translate("&6Set the end date for &f" + this.keyAll.getId() + " &6to &f" + new Date(this.keyAll.getEnd()).toLocaleString())
                  );
               return Prompt.END_OF_CONVERSATION;
            }
         }
      }

      @ConstructorProperties({"menu", "keyAll"})
      public DatePrompt(Menu menu, KeyAll keyAll) {
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
