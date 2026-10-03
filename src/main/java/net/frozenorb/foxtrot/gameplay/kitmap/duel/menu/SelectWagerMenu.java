package net.frozenorb.foxtrot.gameplay.kitmap.duel.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.Callback;
import cc.fyre.proton.util.NumberUtils;
import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.menu.ConfirmMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.NullConversationPrefix;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class SelectWagerMenu extends Menu {
   private Callback<Integer> callback;
   private static List<Integer> PURPLE_SLOTS = Arrays.asList(0, 2, 4, 6, 8, 10, 16, 18, 20, 22, 24, 26);
   private static List<Integer> MAGENTA_SLOTS = Arrays.asList(1, 3, 5, 7, 9, 17, 19, 21, 23, 25);

   public SelectWagerMenu(Callback<Integer> callback) {
      this.callback = callback;
   }

   @Override
   public String getTitle(Player player) {
      return "Choose Wager Amount";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      Map<Integer, Button> buttons = new HashMap<>();

      for (int i : PURPLE_SLOTS) {
         buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)10));
      }

      for (int i : MAGENTA_SLOTS) {
         buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)2));
      }

      buttons.put(11, new SelectWagerMenu.WagerAmountButton(1, this.callback));
      buttons.put(12, new SelectWagerMenu.WagerAmountButton(5, this.callback));
      buttons.put(13, new SelectWagerMenu.CustomWagerAmountButton(this.callback));
      buttons.put(14, new SelectWagerMenu.WagerAmountButton(10, this.callback));
      buttons.put(15, new SelectWagerMenu.WagerAmountButton(15, this.callback));
      return buttons;
   }

   private class CustomWagerAmountButton extends Button {
      private Callback<Integer> callback;

      @Override
      public String getName(Player player) {
         return ChatColor.DARK_GREEN.toString() + ChatColor.BOLD + "Custom Amount";
      }

      @Override
      public List<String> getDescription(Player player) {
         List<String> description = new ArrayList<>();
         description.add(ChatColor.GREEN + "Wager a non-specified amount");
         return description;
      }

      @Override
      public Material getMaterial(Player player) {
         return Material.EMERALD;
      }

      @Override
      public ItemStack getButtonItem(Player player) {
         ItemStack itemStack = super.getButtonItem(player);
         itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
         return itemStack;
      }

      @Override
      public void clicked(Player player, int slot, ClickType clickType) {
         player.closeInventory();
         this.createConversation(player);
      }

      private void createConversation(final Player player) {
         ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance())
            .withModality(true)
            .withPrefix(new NullConversationPrefix())
            .withFirstPrompt(new StringPrompt() {
               @Override
               public String getPromptText(ConversationContext context) {
                  return "§aHow many gems do you want to wager? Type an amount to wager, or §cquit§a to cancel.";
               }

               @Override
               public Prompt acceptInput(ConversationContext cc, String s) {
                  if (s.equalsIgnoreCase("quit")) {
                     cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Cancelled.");
                     return Prompt.END_OF_CONVERSATION;
                  }

                  if (NumberUtils.isInteger(s)) {
                     int amount = Integer.parseInt(s);
                     if (amount <= 0) {
                        cc.getForWhom().sendRawMessage(ChatColor.RED + "You cannot wager nothing.");
                        return Prompt.END_OF_CONVERSATION;
                     } else {
                        int gems = (int)Foxtrot.getInstance().getGemMap().getGems(player);
                        if (amount > gems) {
                           cc.getForWhom().sendRawMessage(ChatColor.RED + "You do not have enough gems for this!");
                           return Prompt.END_OF_CONVERSATION;
                        } else {
                           new ConfirmMenu("Are you sure?", success -> {
                              if (success) {
                                 CustomWagerAmountButton.this.callback.callback(amount);
                              } else {
                                 player.closeInventory();
                              }
                           }).openMenu(player);
                           return Prompt.END_OF_CONVERSATION;
                        }
                     }
                  } else {
                     cc.getForWhom().sendRawMessage(ChatColor.RED + "That is not a valid number. Type a number or \"quit\" to cancel.");
                     return Prompt.END_OF_CONVERSATION;
                  }
               }
            })
            .withLocalEcho(false)
            .withEscapeSequence("/no")
            .withTimeout(10)
            .thatExcludesNonPlayersWithMessage("Go away evil console!");
         Conversation con = factory.buildConversation(player);
         player.beginConversation(con);
      }

      @ConstructorProperties("callback")
      public CustomWagerAmountButton(Callback<Integer> callback) {
         this.callback = callback;
      }
   }

   private class WagerAmountButton extends Button {
      private int amount;
      private Callback<Integer> callback;

      @Override
      public String getName(Player player) {
         return ChatColor.GREEN.toString() + ChatColor.BOLD + this.amount + " Gem" + (this.amount != 1 ? "s" : "");
      }

      @Override
      public List<String> getDescription(Player player) {
         return Collections.emptyList();
      }

      @Override
      public int getAmount(Player player) {
         return this.amount;
      }

      @Override
      public Material getMaterial(Player player) {
         return Material.EMERALD;
      }

      @Override
      public void clicked(Player player, int slot, ClickType clickType) {
         int gems = (int)Foxtrot.getInstance().getGemMap().getGems(player);
         if (this.amount > gems) {
            player.sendMessage(ChatColor.RED + "You do not have enough gems for this!");
         } else {
            new ConfirmMenu("Are you sure?", success -> {
               if (success) {
                  this.callback.callback(this.amount);
               } else {
                  player.closeInventory();
               }
            }).openMenu(player);
         }
      }

      @ConstructorProperties({"amount", "callback"})
      public WagerAmountButton(int amount, Callback<Integer> callback) {
         this.amount = amount;
         this.callback = callback;
      }
   }
}
