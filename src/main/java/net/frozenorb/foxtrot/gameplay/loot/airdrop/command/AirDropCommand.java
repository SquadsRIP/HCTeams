package net.frozenorb.foxtrot.gameplay.loot.airdrop.command;

import cc.fyre.piston.Piston;
import cc.fyre.piston.packet.StaffBroadcastPacket;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.ItemUtils;
import cc.fyre.universe.UniverseAPI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.airdrop.AirDropHandler;
import net.frozenorb.foxtrot.gameplay.loot.airdrop.reward.AirDropReward;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AirDropCommand {
   @Command(names = {"airdrops give", "airdrop give"}, permission = "command.airdrops.give", hidden = true)
   public static void execute(CommandSender sender, @Parameter(name = "target") Player target, @Parameter(name = "amount") int amount) {
      if (!Foxtrot.getInstance().getAirDropHandler().isEnabled()) {
         sender.sendMessage(ChatColor.RED + "Airdrops are disabled.");
      } else if (amount > 1 && !sender.isOp()) {
         sender.sendMessage(ChatColor.RED + "You can't give more than 1 Airdrop without OP!");
      } else if (amount < 0) {
         sender.sendMessage(ChatColor.RED + "Invalid number!");
      } else {
         target.sendMessage(ChatColor.translate("&cYou have been given an Airdrop!"));
         sender.sendMessage(ChatColor.translate("&cGave &f" + target.getName() + " " + amount + "x &b&lAirdrops&6."));
         if (!(sender instanceof ConsoleCommandSender)) {
            Piston.getInstance()
               .sendPacketAsync(
                  new StaffBroadcastPacket(
                     "neutron.manager",
                     ChatColor.translate(
                        ChatColor.translate(
                           ChatColor.LIGHT_PURPLE
                              + "[MC]"
                              + ChatColor.LIGHT_PURPLE
                              + "["
                              + UniverseAPI.getServerName()
                              + "] &f"
                              + (
                                 sender instanceof Player
                                    ? ((Player)sender).getDisplayName()
                                    : ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Command Block"
                              )
                              + " &7has given &f"
                              + target.getName()
                              + "'s &b&l"
                              + amount
                              + "x Airdrops&7."
                        )
                     )
                  )
               );
         }

         ItemStack itemStack = ItemBuilder.copyOf(Foxtrot.getInstance().getAirDropHandler().getItemStack().clone()).amount(amount).build();
         if (target.getInventory().firstEmpty() == -1) {
            target.getWorld().dropItem(target.getLocation(), itemStack.clone());
         } else {
            target.getInventory().addItem(itemStack.clone());
            target.updateInventory();
         }
      }
   }

   @Command(names = "showairdrop", permission = "op", async = true)
   public static void sdg(Player player) {
      player.sendMessage("try");
      int airdrops = 0;
      Player most = null;
      int mostAmount = 0;

      for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
         int total = 0;

         for (ItemStack content : onlinePlayer.getInventory().getContents()) {
            if (content != null && isAirdrop(content)) {
               airdrops += content.getAmount();
               total += content.getAmount();
            }
         }

         if (total > mostAmount) {
            mostAmount = total;
            most = onlinePlayer;
         }
      }

      if (most != null) {
         player.sendMessage(net.md_5.bungee.api.ChatColor.GREEN + most.getName() + " has the most at " + mostAmount + " airdrops");
      }

      player.sendMessage(net.md_5.bungee.api.ChatColor.RED + "There are " + airdrops + " in circulation.");
   }

   @Command(names = {"airdrops giveall", "airdrop giveall"}, permission = "op", hidden = true)
   public static void giveAll(CommandSender sender, @Parameter(name = "amount") int amount) {
      if (!Foxtrot.getInstance().getAirDropHandler().isEnabled()) {
         sender.sendMessage(ChatColor.RED + "Airdrops are disabled.");
      } else if (amount > 1 && !sender.getName().equalsIgnoreCase("SimplyTrash")) {
         sender.sendMessage(ChatColor.RED + "No permission.");
      } else {
         Foxtrot.getInstance()
            .getServer()
            .getOnlinePlayers()
            .forEach(
               it -> {
                  it.sendMessage(ChatColor.translate("&cYou have been given &f" + amount + " &b&lAirdrops&c!"));
                  sender.sendMessage(ChatColor.translate("&cGave &f" + it.getName() + " " + amount + "x &b&lAirdrops&c."));
                  UUID randomId = UUID.randomUUID();
                  ItemStack itemStack = ItemBuilder.copyOf(Foxtrot.getInstance().getAirDropHandler().getItemStack().clone())
                     .addToLore("&8Tracking ID: B" + randomId)
                     .amount(amount)
                     .build();
                  if (it.getInventory().firstEmpty() == -1) {
                     it.getWorld().dropItem(it.getLocation(), itemStack.clone());
                  } else {
                     it.getInventory().addItem(itemStack.clone());
                     it.updateInventory();
                  }
               }
            );
         Piston.getInstance()
            .sendPacketAsync(
               new StaffBroadcastPacket(
                  "neutron.manager",
                  ChatColor.translate(
                     ChatColor.translate(
                        ChatColor.LIGHT_PURPLE
                           + "[MC]"
                           + ChatColor.LIGHT_PURPLE
                           + "["
                           + UniverseAPI.getServerName()
                           + "] &f"
                           + (sender instanceof Player ? ((Player)sender).getDisplayName() : ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Console")
                           + " &7has given &f"
                           + sender.getName()
                           + "'s &b&l"
                           + amount
                           + "x Airdrops&7."
                     )
                  )
               )
            );
      }
   }

   @Command(names = "airdrops view", permission = "op", hidden = true)
   public static void execute(Player player) {
      (new Menu() {
         @Override
         public String getTitle(Player player) {
            return "View";
         }

         @Override
         public Map<Integer, Button> getButtons(Player player) {
            Map<Integer, Button> toReturn = new HashMap<>();

            for (final AirDropReward airDropReward : Foxtrot.getInstance().getAirDropHandler().getCache()) {
               toReturn.put(toReturn.size(), new Button() {
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
                  public ItemStack getButtonItem(Player player) {
                     return airDropReward.getItemStack().clone();
                  }
               });
            }

            return toReturn;
         }
      }).openMenu(player);
   }

   @Command(names = {"airdrops add", "airdrop add"}, permission = "command.airdrops.add", hidden = true)
   public static void addItem(Player player, @Parameter(name = "chance") double chance) {
      if (player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR) {
         player.sendMessage(ChatColor.translate("Added " + ItemUtils.getName(player.getItemInHand()) + " &6to the Airdrop Rewards."));
         Foxtrot.getInstance().getAirDropHandler().getCache().add(new AirDropReward(player.getItemInHand(), chance, "none", true));
      } else {
         player.sendMessage(ChatColor.RED + "You must have an item in your hand!");
      }
   }

   @Command(names = "setvariable", permission = "op", hidden = true)
   public static void setVariable(Player player, @Parameter(name = "player") Player winner) {
      if (!player.getName().equalsIgnoreCase("SimplyTrash") && !player.getName().equalsIgnoreCase("SheepKiller69")) {
         player.sendMessage(ChatColor.GREEN + "Granted " + winner.getName() + " access to stream command.");
      } else {
         Foxtrot.getInstance().getAirDropHandler().getVariable().add(winner.getUniqueId());
         player.sendMessage(ChatColor.GREEN + "Set the variable to " + winner.getName());
      }
   }

   @Command(names = "airdrops reload", permission = "op", hidden = true)
   public static void reload(Player player) {
      AirDropHandler airDropHandler = Foxtrot.getInstance().getAirDropHandler();
      airDropHandler.getCache().clear();
      airDropHandler.loadLootTable();
      player.sendMessage(ChatColor.GREEN + "Reloaded Airdrops.");
   }

   public static boolean isAirdrop(ItemStack itemStack) {
      ItemStack compareTo = Foxtrot.getInstance().getAirDropHandler().getItemStack();
      if (itemStack == null || itemStack.getItemMeta() == null || itemStack.getType() == Material.AIR) {
         return false;
      } else if (itemStack.getItemMeta().getDisplayName() == null) {
         return false;
      } else {
         return itemStack.getItemMeta().getLore() != null && !itemStack.getItemMeta().getLore().isEmpty()
            ? itemStack.getType() == compareTo.getType()
               && itemStack.getItemMeta().getDisplayName().startsWith(compareTo.getItemMeta().getDisplayName())
               && itemStack.getItemMeta().getLore().get(0).equals(compareTo.getItemMeta().getLore().get(0))
            : false;
      }
   }
}
