package net.frozenorb.foxtrot.gameplay.boosters.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.scoreboard.construct.ScoreFunction;
import cc.fyre.proton.util.UUIDUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.boosters.Booster;
import net.frozenorb.foxtrot.gameplay.boosters.NetworkBoosterHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public class BoosterMenu extends Menu {
   @Override
   public String getTitle(Player player) {
      return "Network Boosters";
   }

   @Override
   public Map<Integer, Button> getButtons(Player player) {
      Map<Integer, Button> toReturn = new HashMap<>();

      for (int i = 0; i < 27; i++) {
         toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)14, ""));
      }

      final NetworkBoosterHandler networkBoosterHandler = Foxtrot.getInstance().getNetworkBoosterHandler();
      final Map<Booster, Integer> boosterMap = new HashMap<>(networkBoosterHandler.getBoosterBalances().getOrDefault(player.getUniqueId(), new HashMap<>()));

      for (final Booster boostersType : networkBoosterHandler.getBoostersTypes()) {
         if (boostersType.getId().equalsIgnoreCase("Frenzy")) {
            final int boosters = boosterMap.getOrDefault(boostersType, 0);
            toReturn.put(
               boostersType.getSlot(),
               new Button() {
                  @Override
                  public String getName(Player player) {
                     return boostersType.getItemDisplay().getItemMeta().getDisplayName();
                  }

                  @Override
                  public List<String> getDescription(Player player) {
                     List<String> toReturnx = new ArrayList<>(boostersType.getItemDisplay().getItemMeta().getLore());
                     toReturnx.add("");
                     if (boostersType.isActive() && boostersType.getActivatedBy() != null) {
                        String activatedBy = UUIDUtils.name(networkBoosterHandler.getActiveBoosters().get(boostersType));
                        toReturnx.add(ChatColor.translate("&4&l┃ &fStatus: &aActive"));
                        toReturnx.add(ChatColor.translate("&4&l┃ &fActivated By: &c" + activatedBy));
                        toReturnx.add(
                           ChatColor.translate(
                              "&4&l┃ &fExpires In: &c"
                                 + ScoreFunction.TIME_FANCY
                                    .apply((float)(boostersType.getActivatedAt() + TimeUnit.HOURS.toMillis(1L) - System.currentTimeMillis()) / 1000.0F)
                           )
                        );
                     } else if (boostersType.isOnCooldown()) {
                        toReturnx.add(ChatColor.translate("&4&l┃ &fStatus: &eOn Cooldown"));
                        toReturnx.add(
                           ChatColor.translate(
                              "&4&l┃ &fRemaining: &a"
                                 + ScoreFunction.TIME_FANCY
                                    .apply((float)(boostersType.getLastDeactivateAt() + TimeUnit.HOURS.toMillis(1L) - System.currentTimeMillis()) / 1000.0F)
                           )
                        );
                     } else {
                        toReturnx.add(ChatColor.translate("&4&l┃ &fStatus: &cInactive"));
                     }

                     toReturnx.add(ChatColor.translate("&4&l┃ &fBalance: &c" + boosters));
                     toReturnx.add("");
                     toReturnx.add(ChatColor.GREEN + "Click to place a booster in the queue");
                     int queued = boostersType.inQueue();
                     toReturnx.add(ChatColor.WHITE.toString() + queued + " Boosters currently are in queue");
                     return toReturnx;
                  }

                  @Override
                  public byte getDamageValue(Player player) {
                     return boostersType.getItemDisplay().getData().getData();
                  }

                  @Override
                  public Material getMaterial(Player player) {
                     return boostersType.getItemDisplay().getType();
                  }

                  @Override
                  public void clicked(Player player, int slot, ClickType clickType) {
                     if (boosters <= 0) {
                        player.sendMessage(ChatColor.RED + "You do not have this booster! You can purchase boosters at Coming Soon!");
                     } else {
                        List<Booster> queuedBoosters = networkBoosterHandler.getBoostersQueued().getOrDefault(player.getUniqueId(), new ArrayList<>());
                        if (queuedBoosters.contains(boostersType)) {
                           player.sendMessage(ChatColor.RED + "You already have that booster in the queue!");
                        } else {
                           queuedBoosters.add(boostersType);
                           if (boosterMap.get(boostersType) == 1) {
                              boosterMap.remove(boostersType);
                           } else {
                              boosterMap.replace(boostersType, boosters - 1);
                           }

                           networkBoosterHandler.getBoosterBalances().replace(player.getUniqueId(), boosterMap);
                           networkBoosterHandler.getBoostersQueued().put(player.getUniqueId(), queuedBoosters);
                           player.sendMessage(ChatColor.GREEN + "Placed your booster in the queue!");
                           player.closeInventory();
                        }
                     }
                  }
               }
            );
         }
      }

      return toReturn;
   }

   @Override
   public boolean isAutoUpdate() {
      return true;
   }
}
