package net.frozenorb.foxtrot.gameplay.kitmap.gemflip.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.gemflip.GemFlipHandler;
import net.frozenorb.foxtrot.gameplay.kitmap.gemflip.data.GemFlipEntry;
import net.frozenorb.foxtrot.gameplay.kitmap.gemflip.data.GemFlipWager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class WagerListMenu extends Menu {
   private GemFlipHandler gemFlipHandler = Foxtrot.getInstance().getGemFlipHandler();

   @Override
   public String getTitle(Player player) {
      return "Wagers";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      Map<Integer, Button> toReturn = new HashMap<>();

      for (int i = 0; i < 8; i++) {
         if (i + 1 <= this.gemFlipHandler.getQueue().size()) {
            final GemFlipEntry entry = this.gemFlipHandler.getQueue().get(i);
            if (entry != null) {
               final Player creator = entry.getCreator();
               toReturn.put(
                  i,
                  new Button() {
                     @Override
                     public String getName(Player player) {
                        return ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + creator.getName();
                     }

                     @Override
                     public List<String> getDescription(Player player) {
                        List<String> toReturnx = new ArrayList<>();
                        toReturnx.add("");
                        toReturnx.add(ChatColor.translate("&2&l❙ &fWager: &a" + entry.getAmount()));
                        toReturnx.add(ChatColor.translate("&2&l❙ &fChosen Side: &a" + entry.getChosenSide().getFriendlyName()));
                        toReturnx.add("");
                        toReturnx.add(ChatColor.GREEN + "Click to accept this gem flip wager.");
                        return toReturnx;
                     }

                     @Override
                     public Material getMaterial(Player player) {
                        return Material.SKULL_ITEM;
                     }

                     @Override
                     public byte getDamageValue(Player player) {
                        return (byte)SkullType.PLAYER.ordinal();
                     }

                     @Override
                     public ItemStack getButtonItem(Player player) {
                        return ItemBuilder.copyOf(super.getButtonItem(player)).skull(creator.getName()).build();
                     }

                     @Override
                     public void clicked(Player player, int slot, ClickType clickType) {
                        Player opponent = entry.getCreator();
                        if (opponent != null && opponent.isOnline()) {
                           if (opponent.getUniqueId().equals(player.getUniqueId())) {
                              player.sendMessage(ChatColor.RED + "You cannot make a wager against yourself.");
                           } else {
                              GemFlipEntry entryx = WagerListMenu.this.gemFlipHandler.getEntry(opponent);
                              if (entryx != null && !entryx.isStarted()) {
                                 long amount = entryx.getAmount();
                                 if (Foxtrot.getInstance().getGemMap().getGems(player.getUniqueId()) < amount) {
                                    player.sendMessage(ChatColor.RED + "Insufficient Gems!");
                                 } else {
                                    WagerListMenu.this.gemFlipHandler.getQueue().remove(entryx);
                                    entryx.setStarted(true);
                                    Foxtrot.getInstance().getGemMap().removeGems(player.getUniqueId(), amount);
                                    entryx.getCreator().sendMessage(ChatColor.translate(player.getName() + " &ahas accepted your wager!"));
                                    player.sendMessage(ChatColor.translate("&aYou have accepted &f" + entryx.getCreator().getName() + "'s &awager!"));
                                    Foxtrot.getInstance()
                                       .getGemFlipHandler()
                                       .setTotalWagered(Foxtrot.getInstance().getGemFlipHandler().getTotalWagered() + entryx.getAmount() * 2L);
                                    GemFlipWager wager = new GemFlipWager(
                                       entryx.getAmount() * 2L, entryx.getCreator(), player, entryx.getChosenSide(), entryx.getChosenSide().getOpposite()
                                    );
                                    wager.start();
                                 }
                              } else {
                                 player.sendMessage(ChatColor.RED + "This wager is no longer available.");
                              }
                           }
                        }
                     }
                  }
               );
            }
         }
      }

      toReturn.put(
         8,
         new Button() {
            @Override
            public String getName(Player player) {
               return ChatColor.translate("&2&lGem Flip");
            }

            @Override
            public List<String> getDescription(Player player) {
               return Arrays.asList(
                  "",
                  ChatColor.GREEN + "How to wager?",
                  ChatColor.translate("&2&l┃ &f/gf wager <amount> - &aAdd a wager"),
                  ChatColor.translate("&2&l┃ &f/gf cancel - &aRemove a wager"),
                  "",
                  ChatColor.GREEN + "Click an active wager to place your bet."
               );
            }

            @Override
            public Material getMaterial(Player player) {
               return Material.EMERALD;
            }
         }
      );
      return toReturn;
   }

   @Override
   public boolean isAutoUpdate() {
      return true;
   }
}
