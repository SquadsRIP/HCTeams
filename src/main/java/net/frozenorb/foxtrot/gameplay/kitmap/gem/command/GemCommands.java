package net.frozenorb.foxtrot.gameplay.kitmap.gem.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.TimeUtils;
import cc.fyre.proton.util.UUIDUtils;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.gameplay.kitmap.gem.menu.GemShopMenu;
import net.frozenorb.foxtrot.persist.maps.GemMap;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.team.upgrade.UpgradeMenu;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.NullConversationPrefix;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GemCommands {
   @Command(names = {"gem help", "gems help", "gemshelp", "gemhelp"}, permission = "")
   public static void help(CommandSender sender) {
      if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
         sender.sendMessage(ChatColor.YELLOW + "This is a KitMap only command.");
      } else {
         String[] msg = new String[]{
            "§8§m-----------------------------------------------------",
            "§c§lGem Help",
            " ",
            "§7/gems §f- View your gem balance.",
            "§7/gems <player> §f- View a player's gem balance.",
            "§7/gemshop §f- Open the gem shop.",
            "§7/gem pay <player> <amount> §f- Send a player gems.",
            "§8§m-----------------------------------------------------"
         };
         sender.sendMessage(msg);
      }
   }

   @Command(names = {"gem", "gems", "gem balance", "gems balance"}, description = "Check another player's balance", permission = "", async = true)
   public static void gems(Player sender, @Parameter(name = "player", defaultValue = "self") Player target) {
      if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
         new UpgradeMenu(Foxtrot.getInstance().getTeamHandler().getTeam(sender)).openMenu(sender);
      } else {
         GemMap gemMap = Foxtrot.getInstance().getGemMap();
         if (sender == target) {
            sender.sendMessage(CC.DARK_GREEN + "Gems: " + CC.GREEN + gemMap.getGems(target));
         } else {
            sender.sendMessage(CC.DARK_GREEN + target.getName() + "'s Gems: " + CC.GREEN + gemMap.getGems(target));
         }
      }
   }

   @Command(names = {"gem shop", "gems shop", "gemshop"}, permission = "")
   public static void gemShop(Player sender) {
      if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
         sender.sendMessage(ChatColor.YELLOW + "This is a KitMap only command.");
      } else if (Foxtrot.getInstance().getInDuelPredicate().test(sender)) {
         sender.sendMessage(ChatColor.YELLOW + "You cannot access the gem shop whilst in a duel!");
      } else if (Foxtrot.getInstance().getMapHandler().getGameHandler() != null
         && Foxtrot.getInstance().getMapHandler().getGameHandler().isOngoingGame()
         && Foxtrot.getInstance().getMapHandler().getGameHandler().getOngoingGame().isPlayingOrSpectating(sender.getUniqueId())) {
         sender.sendMessage(ChatColor.YELLOW + "You cannot buy stuff during a game!");
      } else if (SpawnTagHandler.isTagged(sender)) {
         sender.sendMessage(CC.YELLOW + "You cannot do this while spawn tagged!");
      } else {
         new GemShopMenu().openMenu(sender);
      }
   }

   @Command(names = {"gem repair", "gems repair"}, permission = "")
   public static void repair(Player sender) {
      if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
         sender.sendMessage(ChatColor.YELLOW + "This is a KitMap only command.");
      } else if (Foxtrot.getInstance().getInDuelPredicate().test(sender)) {
         sender.sendMessage(ChatColor.YELLOW + "You cannot access the gem shop whilst in a duel!");
      } else if (!DTRBitmask.SAFE_ZONE.appliesAt(sender.getLocation())) {
         sender.sendMessage(ChatColor.YELLOW + "You must be at Spawn to run this command!");
      } else if (Foxtrot.getInstance().getMapHandler().getGameHandler() != null
         && Foxtrot.getInstance().getMapHandler().getGameHandler().isOngoingGame()
         && Foxtrot.getInstance().getMapHandler().getGameHandler().getOngoingGame().isPlayingOrSpectating(sender.getUniqueId())) {
         sender.sendMessage(ChatColor.YELLOW + "You cannot buy stuff during a game!");
      } else if (SpawnTagHandler.isTagged(sender)) {
         sender.sendMessage(CC.YELLOW + "You cannot do this while spawn tagged!");
      } else if (!Foxtrot.getInstance().getGemMap().removeGems(sender.getUniqueId(), 150L)) {
         sender.sendMessage(CC.YELLOW + "You need 150 Gems to use this!");
      } else {
         for (ItemStack armorContent : sender.getInventory().getArmorContents()) {
            armorContent.setDurability((short)0);
         }

         for (ItemStack content : sender.getInventory().getContents()) {
            if (content.getType() == Material.DIAMOND_SWORD || content.getType() == Material.BOW) {
               content.setDurability((short)0);
            }
         }

         sender.sendMessage(ChatColor.GREEN + "All of your armor");
      }
   }

   @Command(names = {"gem pay", "gems send", "gempay"}, permission = "")
   public static void gemPay(Player sender, @Parameter(name = "target") Player target, @Parameter(name = "amount") int amount) {
      if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
         sender.sendMessage(ChatColor.YELLOW + "This is a KitMap only command.");
      } else if (amount <= 0) {
         sender.sendMessage(CC.YELLOW + "Invalid gem amount.");
      } else if (sender == target) {
         sender.sendMessage(CC.YELLOW + "You cannot pay yourself!");
      } else if (!Foxtrot.getInstance().getGemMap().removeGems(sender.getUniqueId(), amount)) {
         sender.sendMessage(CC.YELLOW + "You do not have enough gems for this!");
      } else {
         Foxtrot.getInstance().getGemMap().addGems(target.getUniqueId(), amount, true);
         sender.sendMessage(CC.GREEN + "You paid " + target.getDisplayName() + " " + CC.DARK_GREEN + amount + CC.GREEN + " gems!");
         target.sendMessage(sender.getDisplayName() + CC.GREEN + " has sent you " + CC.DARK_GREEN + amount + CC.GREEN + " gems!");
      }
   }

   @Command(names = {"gem set", "gems set"}, permission = "op")
   public static void gemsSet(CommandSender sender, @Parameter(name = "target", defaultValue = "self") UUID player, @Parameter(name = "amount") int amount) {
      if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
         sender.sendMessage(ChatColor.YELLOW + "This is a KitMap only command.");
      } else {
         Foxtrot.getInstance().getGemMap().setValue(player, amount);
         Player target = Foxtrot.getInstance().getServer().getPlayer(player);
         if (target != null) {
            target.sendMessage(CC.GREEN + "Your gems have been set to " + amount + ".");
         }

         sender.sendMessage(CC.GREEN + "Set " + UUIDUtils.name(player) + " gems to " + amount);
      }
   }

   @Command(names = {"gem add", "gems add"}, permission = "op")
   public static void gemsAdd(CommandSender sender, @Parameter(name = "target") OfflinePlayer target, @Parameter(name = "amount") int amount) {
      if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
         sender.sendMessage(ChatColor.YELLOW + "This is a KitMap only command.");
      } else {
         GemMap gemMap = Foxtrot.getInstance().getGemMap();
         long newAmount = gemMap.getGems(target.getUniqueId()) + amount;
         gemMap.setValue(target.getUniqueId(), newAmount);
         sender.sendMessage(CC.GREEN + "Added " + amount + " gems to " + target.getName() + " total: " + newAmount);
      }
   }

   @Command(names = {"gem hour", "gem double"}, permission = "foxtrot.gem.double")
   public static void gemHour(Player sender, @Parameter(name = "time") String time) {
      if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
         sender.sendMessage(ChatColor.YELLOW + "This is a KitMap only command.");
      } else {
         final int seconds;
         try {
            seconds = TimeUtils.parseTime(time);
         } catch (IllegalArgumentException e) {
            sender.sendMessage(CC.YELLOW + e.getMessage());
            return;
         }

         if (seconds < 0) {
            sender.sendMessage(ChatColor.YELLOW + "Invalid time!");
         } else {
            ConversationFactory factory = new ConversationFactory(Foxtrot.getInstance())
               .withModality(true)
               .withPrefix(new NullConversationPrefix())
               .withFirstPrompt(new StringPrompt() {
                  @Override
                  public String getPromptText(ConversationContext context) {
                     return "§cType §4§lCONFIRM §cto begin the " + "&2&lDouble Gem".replace('&', '§') + " event! §7(/no to quit.)";
                  }

                  @Override
                  public Prompt acceptInput(ConversationContext cc, String s) {
                     if ("CONFIRM".equalsIgnoreCase(s)) {
                        CustomTimerCreateCommand.getCustomTimers().put("&2&lDouble Gem", System.currentTimeMillis() + seconds * 1000);
                        return Prompt.END_OF_CONVERSATION;
                     } else if (s.equalsIgnoreCase("no")) {
                        cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Cancelled.");
                        return Prompt.END_OF_CONVERSATION;
                     } else {
                        cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Unrecognized response. Type §bCONFIRM§a to confirm or §c/no§a to quit.");
                        return this;
                     }
                  }
               })
               .withLocalEcho(false)
               .withEscapeSequence("/no")
               .withTimeout(10)
               .thatExcludesNonPlayersWithMessage("Go away evil console!");
            Conversation con = factory.buildConversation(sender);
            sender.beginConversation(con);
         }
      }
   }

   @Command(names = {"gem hour stop", "gem double stop"}, permission = "foxtrot.gem.double")
   public static void gemHour(Player sender) {
      if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
         sender.sendMessage(ChatColor.YELLOW + "This is a KitMap only command.");
      } else {
         Long removed = CustomTimerCreateCommand.getCustomTimers().remove("&2&lDouble Gem");
         if (removed != null && System.currentTimeMillis() < removed) {
            sender.sendMessage(ChatColor.GREEN + "Deactivated the double gem timer.");
         } else {
            sender.sendMessage(ChatColor.YELLOW + "Purger timer is not active.");
         }
      }
   }
}
