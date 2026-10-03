package net.frozenorb.foxtrot.gameplay.loot.treasurechest.menu;

import cc.fyre.proton.Proton;
import cc.fyre.proton.hologram.construct.Hologram;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.treasurechest.TreasureChest;
import net.frozenorb.foxtrot.gameplay.loot.treasurechest.listener.TreasureChestListener;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class TreasureChestMenu extends Menu {
   @Override
   public String getTitle(Player player) {
      return "Treasure Chests";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      Map<Integer, Button> toReturn = new HashMap<>();

      for (final TreasureChest treasureChest : Foxtrot.getInstance().getTreasureChestHandler().getTreasureChests()) {
         final int chests = treasureChest.getCache().getOrDefault(player.getUniqueId(), 0);
         toReturn.put(
            treasureChest.getSlot(),
            new Button() {
               @Override
               public String getName(Player player) {
                  return ChatColor.translate(treasureChest.getDisplayName());
               }

               @Override
               public List<String> getDescription(Player player) {
                  List<String> toReturnx = new ArrayList<>();
                  toReturnx.add("");
                  toReturnx.add(ChatColor.translate("&4&l┃ &fAmount: &c" + chests));
                  toReturnx.add("");
                  toReturnx.add(ChatColor.GRAY + "Right Click to view all possible loot");
                  if (chests == 0) {
                     toReturnx.add(ChatColor.RED + "Purchase a " + ChatColor.translate(treasureChest.getDisplayName()) + ChatColor.RED + " at Coming Soon!!");
                  } else {
                     toReturnx.add(ChatColor.GREEN + "Left Click to open a " + ChatColor.translate(treasureChest.getDisplayName()) + ChatColor.GREEN + "!");
                  }

                  return toReturnx;
               }

               @Override
               public void clicked(Player player, int slot, ClickType clickType) {
                  if (clickType.isShiftClick()) {
                     player.closeInventory();
                     (new Menu() {
                           @Override
                           public String getTitle(Player player) {
                              return "Omega Loot";
                           }

                           @Override
                           public Map<Integer, Button> getButtons(Player player) {
                              Map<Integer, Button> toReturn = new HashMap<>();
                              new ArrayList<>(treasureChest.getRewards())
                                 .stream()
                                 .filter(it -> it.getItemStack() != null)
                                 .forEach(it -> toReturn.put(toReturn.size(), new Button() {
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
                                       return it.getItemStack().clone();
                                    }
                                 }));
                              return toReturn;
                           }
                        })
                        .openMenu(player);
                  } else if (treasureChest.getCache().containsKey(player.getUniqueId()) && treasureChest.getCache().get(player.getUniqueId()) > 0) {
                     TreasureChestListener treasureChestListener = Foxtrot.getInstance().getTreasureChestHandler().getTreasureChestListener();
                     if (treasureChestListener.getCurrentPlayer() != null) {
                        player.closeInventory();
                        player.sendMessage(ChatColor.RED + "A player is already opening one!");
                     } else {
                        final Location centralLocation = treasureChest.getCentralLocation();
                        if (centralLocation != null) {
                           List<Hologram> holograms = new ArrayList<>(Proton.getInstance().getHologramHandler().getCache().values());
                           holograms.stream()
                              .filter(it -> it.getLines().contains(ChatColor.translate("&f&ki&4&l TREASURE CHESTS &f&ki")))
                              .forEach(Hologram::delete);
                           treasureChest.getCache().put(player.getUniqueId(), treasureChest.getCache().get(player.getUniqueId()) - 1);
                           centralLocation.getBlock().setType(Material.AIR);
                           treasureChestListener.setEnding(false);
                           treasureChestListener.setChestsOpened(0);
                           treasureChestListener.getRewardsWon().clear();
                           treasureChestListener.getOpenedChests().clear();
                           treasureChestListener.setCurrentlyOpening(treasureChest);
                           treasureChestListener.setCurrentPlayer(player.getUniqueId());
                           String color = "&c";
                           if (treasureChest.getId().equalsIgnoreCase("Illuminated")) {
                              color = "&b";
                           }

                           for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                              onlinePlayer.sendMessage(ChatColor.translate(""));
                              onlinePlayer.sendMessage(ChatColor.translate(treasureChest.getDisplayName()));
                              onlinePlayer.sendMessage(
                                 ChatColor.translate("&f" + player.getName() + " &7is opening a &4&l" + treasureChest.getDisplayName() + "&7!")
                              );
                              onlinePlayer.sendMessage(
                                 ChatColor.translate(
                                    color
                                       + "Purchase "
                                       + ChatColor.stripColor(ChatColor.translate(treasureChest.getDisplayName()))
                                       + " on our store at &fComing Soon!"
                                       + color
                                       + "!"
                                 )
                              );
                              onlinePlayer.sendMessage(ChatColor.translate(""));
                           }

                           (new BukkitRunnable() {
                                 @Override
                                 public void run() {
                                    TreasureChestListener listener = Foxtrot.getInstance().getTreasureChestHandler().getTreasureChestListener();
                                    if (listener.getCurrentPlayer() != null && !listener.isEnding()) {
                                       for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                                          if (!onlinePlayer.getUniqueId().toString().equalsIgnoreCase(listener.getCurrentPlayer().toString())
                                             && DTRBitmask.SAFE_ZONE.appliesAt(onlinePlayer.getLocation())
                                             && onlinePlayer.getWorld().getName().equalsIgnoreCase("Spawn")
                                             && onlinePlayer.getLocation().distance(centralLocation.clone()) <= 4.0) {
                                             onlinePlayer.setVelocity(onlinePlayer.getLocation().getDirection().multiply(-1.75));
                                          }
                                       }
                                    } else {
                                       this.cancel();
                                    }
                                 }
                              })
                              .runTaskTimer(Foxtrot.getInstance(), 20L, 20L);
                           player.teleport(centralLocation.clone());
                           Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                              for (Location chest : treasureChest.getChests()) {
                                 chest.getBlock().setType(Material.CHEST);
                                 chest.getWorld().playEffect(chest, Effect.EXPLOSION_HUGE, 1, 1);
                                 chest.getWorld().playSound(chest, Sound.ANVIL_LAND, 1.0F, 1.0F);
                              }
                           }, 80L);
                        }
                     }
                  } else {
                     player.sendMessage(ChatColor.RED + "You don't have any Treasure Chests! You can purchase one at Coming Soon!");
                  }
               }

               @Override
               public Material getMaterial(Player player) {
                  return treasureChest.getMaterial();
               }
            }
         );
      }

      return toReturn;
   }

   @Override
   public int size(Player player) {
      return 27;
   }

   @Override
   public boolean isPlaceholder() {
      return true;
   }
}
